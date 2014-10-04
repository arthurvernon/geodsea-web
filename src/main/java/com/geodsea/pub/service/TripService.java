package com.geodsea.pub.service;

import com.geodsea.pub.domain.*;
import com.geodsea.pub.repository.*;
import com.geodsea.pub.security.SecurityUtils;
import com.geodsea.pub.service.util.TripSubmitChecks;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Service to manage trips.
 */
@Service
@Transactional(rollbackFor = {ActionRefusedException.class})
public class TripService extends BaseService {

    @Inject
    Validator validator;

    @Inject
    private TripRepository tripRepository;

    @Inject
    private TripSkipperRepository tripSkipperRepository;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private VesselRepository vesselRepository;

    @Inject
    private RescueRepository rescueRepository;

    @Inject
    private MonitorRepository monitorRepository;

    @Inject
    private SkipperRepository skipperRepository;

    private final Logger log = LoggerFactory.getLogger(TripService.class);

    public Trip addTripPlan(Trip trip) {
        return tripRepository.save(trip);
    }

    @PreAuthorize("isAuthenticated()")
    public void updatePlan(long tripId, long skipperId, String headline, Date scheduledStartTime, Date scheduledEndTime,
                           String summary, MultiPoint wayPoints, Integer fuelOnBoard, Integer peopleOnBoard)
            throws ActionRefusedException {
        TripSkipper trip = tripSkipperRepository.findOne(tripId);

        if (trip == null)
            throw new ActionRefusedException(ErrorCode.NO_SUCH_TRIP, "No trip: " + tripId);

        Skipper skipper = skipperRepository.getOne(skipperId);
        checkPersonIsSkipper(skipperId, skipper, trip.getVessel().getId(), trip.getVessel());

        trip.setPerson(skipper.getPerson());
        trip.setHeadline(headline);
        trip.setScheduledStartTime(scheduledStartTime);
        trip.setScheduledEndTime(scheduledEndTime);
        trip.setSummary(summary);
        trip.setFuelOnBoard(fuelOnBoard);
        trip.setPeopleOnBoard(peopleOnBoard);

        validateTrip(trip);

        trip = tripRepository.save(trip);

        // if way points are defined then we can include the Monitor
        if (wayPoints != null && !wayPoints.isEmpty()) {
            Point point = (Point) wayPoints.getGeometryN(0);
            maintainSRO(trip, point);
        }

    }

    private void validateTrip(Trip trip) throws ConstraintViolationException {
        Set<ConstraintViolation<Trip>> constraintViolations = validator.validate(trip, TripSubmitChecks.class);
        if (constraintViolations.size() > 0) {
            if (log.isDebugEnabled())
                for (ConstraintViolation<Trip> cv : constraintViolations)
                    log.debug(cv.getMessage());
            throw new ConstraintViolationException(constraintViolations);
        }
    }

    /**
     * A plan may be created without necessarily specifying all the information that would be required to actually
     * commence a journey.
     *
     * @param vesselId  the ID of the vessel being taken on the trip.
     * @param skipperId  the Id of the person (skipper) in charge of the vessel
     * @param headline  a summary of the purpose of the trip, shared with SRO
     * @param scheduledEndTime   planned end time for the trip
     * @param summary   a personal summary of the trip, not shared with SRO
     * @param wayPoints optional way points for journey, beginning with the starting location
     * @param fuel      number of litres of fuel
     * @param people    number of people on board
     * @return a newly created trip
     */
    @PreAuthorize("isAuthenticated()")
    public Trip createTripPlan(long vesselId, long skipperId, String headline, Date scheduledStartTime, Date scheduledEndTime,
                               String summary, MultiPoint wayPoints, int fuel, int people) throws ActionRefusedException {
        Skipper skipper = skipperRepository.findOne(skipperId);
        Vessel vessel = vesselRepository.findOne(vesselId);
        checkPersonIsSkipper(skipperId, skipper, vesselId, vessel);

        TripSkipper trip = new TripSkipper(vessel, skipper.getPerson(), headline, scheduledStartTime, scheduledEndTime, summary,
                wayPoints, fuel, people);

        validateTrip(trip);

        // allow the skipper to see what is going on.
        trip = tripRepository.save(trip);
        monitorRepository.save(new Monitor(trip, trip.getPerson()));

        // if way points are defined then we can include the Monitor
        if (wayPoints != null && !wayPoints.isEmpty()) {
            Point point = (Point) wayPoints.getGeometryN(0);
            maintainSRO(trip, point);
        }

        return trip;
    }


    /**
     * The vessel and person must exist, the person must be enabled and must also be an authorised skipper of the vessel.
     *
     * @param skipperId Id of the person requested.
     * @param skipper optional person retrieved from the database
     * @param vesselId supplied vessel ID
     * @param vessel   optional vessel retrieved from the database
     * @throws ActionRefusedException
     */
    private void checkPersonIsSkipper(long skipperId, Skipper skipper, long vesselId, Vessel vessel) throws ActionRefusedException {
        if (skipper == null)
            throw new ActionRefusedException(ErrorCode.NO_SUCH_SKIPPER, "No skipper with ID: " + skipperId);
        if (vessel == null)
            throw new ActionRefusedException(ErrorCode.NO_SUCH_VESSEL, "No vessel: " + vesselId);

        Person person = skipper.getPerson();
        if (!person.isEnabled())
            throw new ActionRefusedException(ErrorCode.USER_DISABLED,
                    "Skipper's user account: " + person.getLogin() + " is disabled");

        if (skipperRepository.getSkipperByPersonAndVessel(person, vessel) == null)
            throw new ActionRefusedException(ErrorCode.NOT_SKIPPER_OF_VESSEL, "Person: " + person.getLogin()
                    + " is not a skipper of vessel: " + vessel.getId());
    }

    /**
     * Setup a sea rescue organisation based upon the location reported.
     *
     * @param trip  the trip to monitor
     * @param point the location to attempt
     */
    private void maintainSRO(Trip trip, Point point) {

        // 1. if the trip has commenced then leave an existing monitor as is.
        Monitor monitor = monitorRepository.findRescueMonitoringTrip(trip);
        if (monitor != null && trip.getActualStartTime() != null)
            return;

        // if there are no candidates then there is no one to keep track of the vessel
        List<Rescue> rescueList = rescueRepository.getRescueOrganisationsForLocation(point);
        if (rescueList.size() == 0) {
            log.info("No sea rescue organisation covers " + point);
            return;
        }

        // Where an monitor exists then ensure that it remains a candidate
        // If not then delete it. Go on to create a new monitor
        if (monitor != null) {
            if (!sroIsInList(monitor.getParticipant(), rescueList))
                monitorRepository.delete(monitor);
            else
                return;
        }

        // Select the first SRO if more than one
        Rescue firstSRO = rescueList.get(0);
        trip.setRescue(firstSRO);
        tripRepository.save(trip);

        // Set up a monitor that has no expiry date.
        // it will expire on completion of the trip.
        monitor = new Monitor(trip, firstSRO.getOrganisation());
        monitorRepository.save(monitor);
    }

    private boolean sroIsInList(Participant participant, List<Rescue> rescueList) {
        for (Rescue rescue : rescueList)
            if (rescue.getOrganisation().getId().equals(participant.getId()))
                return true;
        return false;
    }

    @PreAuthorize("isAuthenticated()")
    public Trip getTrip(long id) {
        return tripRepository.findOne(id);
    }

    /**
     * Notification that the specified trip has commenced.
     * <p>
     * Without a geographical location
     * </p>
     *
     * @param tripId  ID of an existing trip.
     * @param started optional start date/time. If not specified the trip will be assumed to have started now
     */
    public void commenceTrip(long tripId, Date started) {
        Trip trip = tripRepository.findOne(tripId);

        if (trip == null)
            throw new IllegalArgumentException("No such trip: " + tripId);

        trip.setActualStartTime(new Date());
        tripRepository.save(trip);
    }

    @PreAuthorize("isAuthenticated()")
    public void reportLocation(long tripId, LocationTime locationTime) {
        if (locationTime == null)
            return;

        TripSkipper trip = tripSkipperRepository.findOne(tripId);

        if (trip == null)
            throw new IllegalArgumentException("No such trip: " + tripId);

        // if no SRO has been established then do it now
        if (trip.getRescue() == null)
            maintainSRO(trip, locationTime.getLocation());

        trip.add(locationTime);
        tripRepository.save(trip);
    }

    @PreAuthorize("isAuthenticated()")
    public void reportLocations(long tripId, List<LocationTime> locationTimes) {
        if (locationTimes == null || locationTimes.size() == 0)
            return;

        TripSkipper trip = tripSkipperRepository.findOne(tripId);

        if (trip == null)
            throw new IllegalArgumentException("No such trip: " + tripId);

        // if no SRO has been established then do it now
        if (trip.getRescue() == null)
            maintainSRO(trip, locationTimes.get(0).getLocation());

        trip.addAll(locationTimes);
        tripRepository.save(trip);

    }

    @PreAuthorize("isAuthenticated()")
    public void deleteTrip(long tripId) {
        Trip trip = tripRepository.findOne(tripId);

        if (trip == null)
            throw new IllegalArgumentException("No such trip: " + tripId);

        tripRepository.delete(trip);
    }


    /**
     * Get the track history for this trip.
     *
     * @param tripId
     * @return
     */
    @Transactional(rollbackFor = ActionRefusedException.class)
    @PreAuthorize("isAuthenticated()")
    public List<LocationTime> getLocationTimes(String user, long tripId) throws ActionRefusedException {
        Trip trip = lookupTripAndCheckMonitor(tripId, user);
        if (trip instanceof TripSkipper)
            return ((TripSkipper) trip).getLocationTimes();
        else
            return null;
    }


    /**
     * Get the latest reported location (if any) in relation to the trip.
     *
     * @param tripId
     * @return
     */
    @Transactional(rollbackFor = ActionRefusedException.class)
    @PreAuthorize("isAuthenticated()")
    public LocationTime getLatestLocationTime(String user, long tripId) throws ActionRefusedException {
        Trip trip = lookupTripAndCheckMonitor(tripId, user);
        if (trip instanceof TripSkipper) {
            final List<LocationTime> locations = ((TripSkipper) trip).getLocationTimes();
            if (locations == null || locations.size() == 0)
                return null;
            else
                return locations.get(locations.size() - 1);
        } else
            return null;
    }

    /**
     * @param tripId
     * @param participant
     * @return a non-null trip
     * @throws ActionRefusedException
     */
    private Trip lookupTripAndCheckMonitor(long tripId, String participant) throws ActionRefusedException {
        Trip trip = tripRepository.findOne(tripId);

        if (trip == null)
            throw new IllegalArgumentException("No such trip: " + tripId);

        Monitor monitor = monitorRepository.findByParticipantLoginAndTripId(participant, tripId);
        if (monitor == null) {
            log.warn("User: {} not permitted to access trip: {}", participant, tripId);
            throw new ActionRefusedException(ErrorCode.PERMISSION_DENIED, "Participant" + participant + " is not permitted to access Trip: " + tripId);
        }
        return trip;
    }

    @PreAuthorize("isAuthenticated()")
    public List<TripSkipper> getTripsForSkipper() {
        return tripSkipperRepository.getByPersonLogin(SecurityUtils.getCurrentLogin());
    }
}
