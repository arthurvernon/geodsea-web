package com.geodsea.pub.service;

import com.geodsea.epsg.CrsTransformService;
import com.geodsea.pub.domain.*;
import com.geodsea.pub.repository.*;
import com.geodsea.pub.security.SecurityUtils;
import com.geodsea.pub.service.util.TripCreateChecks;
import com.geodsea.pub.service.util.TripUpdateChecks;
import com.vividsolutions.jts.geom.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.*;

/**
 * Service to manage trips.
 * <h3>Way Points</h3>
 * <p>Way points in the database are stored as X/Y values. The ordering of coordinates is unfortunately
 * inconsistent with the EPSG database settings han hence every time a Geography is persisted or retrieved
 * the X/Y coordinated need to be switched.</p>
 * <p>
 * As this is done by this class within a transaction context, getters must be declared as a read-only transaction
 * to ensure that the cached value is left unchanged. If this is not done then each time a trip is re-read from
 * the cache, the X/Y coordindates will be switched again, leading to an incorrect way point being provided on
 * every second read of the same trip.
 * </p>
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

    @Inject
    private CrsTransformService crsTransformService;

    private final Logger log = LoggerFactory.getLogger(TripService.class);

    /**
     * Add a trip plan to the database.
     * <p>
     * Note: The X/Y coordinates for the way points are swapped before the trip is saved.
     * </p>
     *
     * @param trip
     * @return
     */
    public Trip addTripPlan(Trip trip) {
        trip.setWayPoints(crsTransformService.swapXAndY(trip.getWayPoints()));

        return tripRepository.save(trip);
    }

    /**
     * Update an existing trip plan.
     * <p>
     * Note: The X/Y coordinates for the way points are swapped before the trip is saved.
     * </p>
     *
     * @param tripId
     * @param skipperId
     * @param headline
     * @param scheduledStartTime
     * @param scheduledEndTime
     * @param summary
     * @param wayPoints
     * @param fuelOnBoard
     * @param peopleOnBoard
     * @throws ActionRefusedException
     */
    @PreAuthorize("isAuthenticated()")
    public void updatePlan(long tripId, long skipperId, String headline, Date scheduledStartTime, Date scheduledEndTime,
                           String summary, LineString wayPoints, Integer fuelOnBoard, Integer peopleOnBoard)
            throws ActionRefusedException {
        TripSkipper trip = tripSkipperRepository.findOne(tripId);

        if (trip == null)
            throw new ActionRefusedException(ErrorCode.NO_SUCH_TRIP, "No trip: " + tripId);

        Skipper skipper = skipperRepository.getOne(skipperId);
        checkPersonIsSkipper(skipperId, skipper, trip.getVessel().getId(), trip.getVessel());

        wayPoints = crsTransformService.swapXAndY(wayPoints);

        trip.setSkipper(skipper);
        trip.setHeadline(headline);
        trip.setScheduledStartTime(scheduledStartTime);
        trip.setScheduledEndTime(scheduledEndTime);
        trip.setSummary(summary);
        trip.setWayPoints(wayPoints);
        trip.setFuelOnBoard(fuelOnBoard);
        trip.setPeopleOnBoard(peopleOnBoard);

        validateTrip(trip,TripUpdateChecks.class);

        trip = tripRepository.save(trip);

        // if way points are defined then we can include the Monitor
        if (wayPoints != null && !wayPoints.isEmpty()) {
//            Point point = (Point) wayPoints.getGeometryN(0);
            maintainSRO(trip, wayPoints);
        }

    }

    private void validateTrip(Trip trip, Class<?>... groups) throws ConstraintViolationException {
        Set<ConstraintViolation<Trip>> constraintViolations = validator.validate(trip, groups);
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
     * <p>
     * Note: The X/Y coordinates for the way points are swapped before the trip is saved.
     * </p>
     *
     * @param vesselId  the ID of the vessel being taken on the trip.
     * @param skipperId        the Id of the person (skipper) in charge of the vessel
     * @param headline         a summary of the purpose of the trip, shared with SRO
     * @param scheduledEndTime planned end time for the trip
     * @param summary          a personal summary of the trip, not shared with SRO
     * @param wayPoints        optional way points for journey, beginning with the starting location
     * @param fuel             number of litres of fuel
     * @param people           number of people on board
     * @return a newly created trip
     */
    @PreAuthorize("isAuthenticated()")
    public Trip createTripPlan(long vesselId, long skipperId, String headline, Date scheduledStartTime, Date scheduledEndTime,
                               String summary, LineString wayPoints, int fuel, int people) throws ActionRefusedException {
        Skipper skipper = skipperRepository.findOne(skipperId);
        Vessel vessel = vesselRepository.findOne(vesselId);
        checkPersonIsSkipper(skipperId, skipper, vesselId, vessel);

        wayPoints = crsTransformService.swapXAndY(wayPoints);

        TripSkipper trip = new TripSkipper(vessel, skipper, headline, scheduledStartTime, scheduledEndTime, summary,
                wayPoints, fuel, people);

        validateTrip(trip, TripCreateChecks.class);

        // allow the skipper to see what is going on.
        trip = tripRepository.save(trip);
        monitorRepository.save(new Monitor(trip, trip.getSkipper().getPerson()));

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
     * @param trip     the trip to monitor
     * @param geometry the location to attempt
     */
    private void maintainSRO(Trip trip, Geometry geometry) {


        // 1. if the trip has commenced then leave an existing monitor as is.
        Monitor monitor = monitorRepository.findRescueMonitoringTrip(trip);
        if (monitor != null && trip.getActualStartTime() != null)
            return;

        log.debug("checking geometry: " + geometry + "- SRID " + geometry.getSRID());
        // if there are no candidates then there is no one to keep track of the vessel
        List<Rescue> rescueList = rescueRepository.getRescueOrganisationsForLocation(geometry);
        if (rescueList.size() == 0) {
            log.info("No sea rescue organisation covers " + geometry);
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

        // Select the most appropriate
        Rescue firstSRO = rescueList.size() > 1 ? narrowSelection(geometry, rescueList) : rescueList.get(0);
        trip.setRescue(firstSRO);
        tripRepository.save(trip);

        // Set up a monitor that has no expiry date.
        // it will expire on completion of the trip.
        monitor = new Monitor(trip, firstSRO.getOrganisation());
        monitorRepository.save(monitor);
    }

    private Rescue narrowSelection(Geometry geometry, List<Rescue> options) {
        options.get(0).getZone().getZone().getArea();

        // Arrange rescue organisations by size, smallest first
        Collections.sort(options, new Comparator<Rescue>() {
            @Override
            public int compare(Rescue o1, Rescue o2) {
                return Double.compare(o1.getZone().getZone().getArea(), o2.getZone().getZone().getArea());
            }
        });

        GeometryFactory factory = geometry.getFactory();
        Point point = factory.createPoint(geometry.getCoordinate());

        for (Rescue rescue : options) {
            if (point.within(rescue.getZone().getZone()))
                return rescue;
        }

        log.debug("No rescue organisation (out of " + options.size() +
                " which overlap the trip) covers the starting point: " + point.toString() + ". Use the closest");

        // Find the closest rescue organisation, up to 50 km away from the starting point.
        List<Rescue> closest = rescueRepository.getClosestRescueOrganisation(point, options, 50000);

        if (closest == null || closest.size() == 0)
            log.info("No rescue organisation monitors a zone within 50km of the starting point: " + point.toString());

        return closest.get(0);
    }

    private boolean sroIsInList(Participant participant, List<Rescue> rescueList) {
        for (Rescue rescue : rescueList)
            if (rescue.getOrganisation().getId().equals(participant.getId()))
                return true;
        return false;
    }

    /**
     * <p>
     * Note: The X/Y coordinates for the way points are swapped before the trip is returned.
     * </p>
     * <p>
     * See note about the necessity of read-only transactions to preserve the integrity of the way point data.
     * </p>
     *
     * @param id
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public Trip getTrip(long id) {
        Trip trip = tripRepository.findOne(id);
        if (trip != null && trip.getWayPoints() != null) {
            log.debug("Way points in the database: " + trip.getWayPoints() + " SRID:" + trip.getWayPoints().getSRID());
            trip.setWayPoints(crsTransformService.swapXAndY(trip.getWayPoints()));
            log.debug("Reversed way points: " + trip.getWayPoints() + " SRID:" + trip.getWayPoints().getSRID());
        }
        return trip;
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
     * <p>
     * Note: The X/Y coordinates for the way points are swapped before the trip is returned.
     * </p>
     * <p>
     * See note about the necessity of read-only transactions to preserve the integrity of the way point data.
     * </p>
     *
     * @param tripId
     * @param participant
     * @return a non-null trip
     * @throws ActionRefusedException
     */
    @Transactional(readOnly = true)
    private Trip lookupTripAndCheckMonitor(long tripId, String participant) throws ActionRefusedException {
        Trip trip = tripRepository.findOne(tripId);

        if (trip == null)
            throw new IllegalArgumentException("No such trip: " + tripId);

        Monitor monitor = monitorRepository.findByParticipantLoginAndTripId(participant, tripId);
        if (monitor == null) {
            log.warn("User: {} not permitted to access trip: {}", participant, tripId);
            throw new ActionRefusedException(ErrorCode.PERMISSION_DENIED, "Participant" + participant + " is not permitted to access Trip: " + tripId);
        }

        trip.setWayPoints(crsTransformService.swapXAndY(trip.getWayPoints()));

        return trip;
    }

    /**
     * <p>
     * Note: The X/Y coordinates for the way points are swapped before the trip is returned.
     * </p>
     * <p>
     * See note about the necessity of read-only transactions to preserve the integrity of the way point data.
     * </p>
     *
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    @Transactional(readOnly = true)
    public List<TripSkipper> getTripsForSkipper() {
        List<TripSkipper> trips = tripSkipperRepository.getBySkipperPersonLogin(SecurityUtils.getCurrentLogin());
        for (TripSkipper trip : trips)
            trip.setWayPoints(crsTransformService.swapXAndY(trip.getWayPoints()));

        return trips;
    }
}
