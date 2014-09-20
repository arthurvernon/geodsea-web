package com.geodsea.pub.service;

import com.geodsea.pub.domain.*;
import com.geodsea.pub.repository.*;
import com.geodsea.pub.service.util.TripSubmitChecks;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * TODO rename this TripService
 */
@Service
@Transactional
public class SkipperService extends BaseService {

    @Inject
    Validator validator;

    @Inject
    private TripRepository tripRepository;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private VesselRepository vesselRepository;

    @Inject
    private RescueRepository rescueRepository;

    @Inject
    private MonitorRepository monitorRepository;

    private final Logger log = LoggerFactory.getLogger(SkipperService.class);

    public Trip addTripPlan(Trip trip) {
        return tripRepository.save(trip);
    }

    /**
     * @param vesselId  the ID of the vessel being taken on the trip.
     * @param personId the Id of the person (skipper) in charge of the vessel
     * @param headline  a summary of the purpose of the trip, shared with SRO
     * @param startTime planned start time for the trip
     * @param endTime   planned end time for the trip
     * @param summary   a personal summary of the trip, not shared with SRO
     * @param wayPoints optional way points for journey, beginning with the starting location
     * @param fuel      number of litres of fuel
     * @param people    number of people on board
     * @return a newly created trip
     */
    public Trip createTripPlan(long vesselId, long personId, String headline, Date startTime, Date endTime,
                               String summary, MultiPoint wayPoints, int fuel, int people) {
        Person skipper = personRepository.getOne(personId);
        if (skipper == null)
            throw new IllegalArgumentException("No person (skipper) with ID: " + personId);

        if (!skipper.isEnabled()) {
            String msg = "Skipper's user account: " + skipper.getLogin() + " is disabled";
            log.warn(msg);
            throw new IllegalStateException(msg);
        }

        Vessel vessel = vesselRepository.findOne(vesselId);
        if (skipper == null)
            throw new IllegalArgumentException("No vessel with ID: " + vesselId);

        Trip trip = new Trip(vessel, skipper, headline, startTime, endTime, summary, wayPoints, fuel, people);

        Set<ConstraintViolation<Trip>> constraintViolations = validator.validate(trip, TripSubmitChecks.class);

        if (constraintViolations.size() > 0) {
            if (log.isDebugEnabled())
                for (ConstraintViolation<Trip> cv : constraintViolations)
                    log.debug(cv.getMessage());
            throw new ConstraintViolationException(constraintViolations);
        }

        trip = tripRepository.save(trip);

        monitorRepository.save(new Monitor(trip, trip.getPerson()));

        // if way points are defined then we can include the Monitor
        if (wayPoints != null && !wayPoints.isEmpty()) {
            Point point = (Point) wayPoints.getGeometryN(0);
            establishSRO(trip, point);
        }

        // allow the skipper to monitor the trip
        return trip;
    }

    /**
     * Setup a sea rescue organisation based upon the location reported.
     *
     * @param trip  the trip to monitor
     * @param point the location to attempt
     */
    private void establishSRO(Trip trip, Point point) {
        List<Rescue> sroList = rescueRepository.getRescueOrganisationsForLocation(point);

        // just take the first if more than one
        if (sroList.size() > 0) {
            Rescue firstSRO = sroList.get(0);

            trip.setRescue(firstSRO);

            tripRepository.save(trip);

            // Set up a monitor that has no expiry date.
            // it will expire on completion of the trip.
            Monitor monitor = new Monitor(trip, firstSRO.getOrgansation());
            monitorRepository.save(monitor);
        } else
            log.info("No sea rescue organisation covers " + point);
    }

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

    public void reportLocation(long tripId, LocationTime locationTime) {
        if (locationTime == null)
            return;

        Trip trip = tripRepository.findOne(tripId);

        if (trip == null)
            throw new IllegalArgumentException("No such trip: " + tripId);

        // if no SRO has been established then do it now
        if (trip.getRescue() == null)
            establishSRO(trip,locationTime.getLocation());

        trip.add(locationTime);
//        locationTimeRepository.save(locationTime);
        tripRepository.save(trip);
    }

    public void reportLocations(long tripId, List<LocationTime> locationTimes) {
        if (locationTimes == null || locationTimes.size() == 0)
            return;

        Trip trip = tripRepository.findOne(tripId);

        if (trip == null)
            throw new IllegalArgumentException("No such trip: " + tripId);

        // if no SRO has been established then do it now
        if (trip.getRescue() == null)
            establishSRO(trip, locationTimes.get(0).getLocation());

        trip.addAll(locationTimes);
        tripRepository.save(trip);

    }

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
    public List<LocationTime> getLocationTimes(String user, long tripId) throws ActionRefusedException {
        Trip trip = lookupTripAndCheckMonitor(tripId, user);
        return trip.getLocationTimes();
    }


    /**
     * Get the latest reported location (if any) in relation to the trip.
     *
     * @param tripId
     * @return
     */
    @Transactional(rollbackFor = ActionRefusedException.class)
    public LocationTime getLatestLocationTime(String user, long tripId) throws ActionRefusedException {
        Trip trip = lookupTripAndCheckMonitor(tripId, user);
        final List<LocationTime> locations = trip.getLocationTimes();
        if (locations == null || locations.size() == 0)
            return null;
        else
            return locations.get(locations.size() - 1);
    }

    private Trip lookupTripAndCheckMonitor(long tripId, String participant) throws ActionRefusedException {
        Trip trip = tripRepository.findOne(tripId);

        if (trip == null)
            throw new IllegalArgumentException("No such trip: " + tripId);

        Monitor monitor = monitorRepository.findByParticipantLogin(participant);
        if (monitor == null) {
            log.warn("User: {} not permitted to access trip: {}", participant, tripId);
            throw new ActionRefusedException(ErrorCode.PERMISSION_DENIED, "Participant" + participant + " is not permitted to access Trip: " + tripId);
        }
        return trip;
    }

}
