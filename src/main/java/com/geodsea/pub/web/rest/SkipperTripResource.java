package com.geodsea.pub.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.geodsea.common.type.FeatureType;
import com.geodsea.epsg.CrsTransformService;
import com.geodsea.pub.domain.Trip;
import com.geodsea.pub.domain.TripSkipper;
import com.geodsea.pub.service.ActionRefusedException;
import com.geodsea.pub.service.ErrorCode;
import com.geodsea.pub.service.GisService;
import com.geodsea.pub.service.TripService;
import com.geodsea.common.dto.ErrorsDTO;
import com.geodsea.common.dto.SkipperTripDTO;
import com.geodsea.pub.web.rest.mapper.Mapper;
import com.vividsolutions.jts.geom.LineString;
import org.geojson.Feature;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for managing Trips .
 */
@RestController
@RequestMapping("/app")
public class SkipperTripResource {

    private final Logger log = LoggerFactory.getLogger(SkipperTripResource.class);

    @Inject
    private TripService tripService;

    @Inject
    private CrsTransformService crsTransformService;

    @Inject
    private GisService gisService;

    /**
     * POST  /rest/trips -> Create a new trip.
     *
     * @return the ID of the trip (200 OK) or error code (409 CONFLICT)
     */
    @RequestMapping(value = "/rest/skipper/trips",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> create(@RequestBody SkipperTripDTO trip) {
        log.debug("REST request to create or update a Trip : {}", trip);

        try {

            // Convert to WGS 84 and then swap lat and long around to align with database storage using X and Y
            // coordinates.
            LineString lineString = gisService.toLineString(trip.getWayPoints());
            if (lineString != null) {
                lineString = (LineString) crsTransformService.transform(lineString, CrsTransformService.CRS_CODE_4326);
            }

            if (trip.getId() == null) {
                try {
                    if (trip.getVessel() == null)
                        return new ResponseEntity<>(ErrorCode.VESSEL_NOT_SPECIFIED, HttpStatus.BAD_REQUEST);
                    if (trip.getSkipper() == null)
                        return new ResponseEntity<>(ErrorCode.SKIPPER_NOT_SPECIFIED, HttpStatus.BAD_REQUEST);
                    if (trip.getPeopleOnBoard() == null)
                        return new ResponseEntity<>(ErrorCode.PEOPLE_ON_BOARD_NOT_SPECIFIED, HttpStatus.BAD_REQUEST);

                    Trip created = tripService.createTripPlan(
                            trip.getVessel().getId(),
                            trip.getSkipper().getId(),
                            trip.getHeadline(),
                            trip.getScheduledStart_dt(),
                            trip.getScheduledEnd_dt(),
                            trip.getSummary(),
                            lineString,
                            trip.getFuelOnBoard(),
                            trip.getPeopleOnBoard());
                    return new ResponseEntity<Long>(created.getId(), HttpStatus.OK);
                } catch (ActionRefusedException e) {
                    return new ResponseEntity<String>(e.getCode(), HttpStatus.CONFLICT);
                }
            } else {
                try {
                    tripService.updatePlan(trip.getId(), trip.getSkipper().getId(), trip.getHeadline(),
                            trip.getScheduledStart_dt(), trip.getScheduledEnd_dt(), trip.getSummary(), lineString,
                            trip.getFuelOnBoard(), trip.getPeopleOnBoard());
                    return new ResponseEntity<Long>(trip.getId(), HttpStatus.OK);
                } catch (ActionRefusedException e) {
                    return new ResponseEntity<String>(e.getCode(), HttpStatus.BAD_REQUEST);
                }

            }
        }
        catch (ConstraintViolationException ex)
        {
            return new ResponseEntity<ErrorsDTO>(Mapper.errors(ex), HttpStatus.CONFLICT);
        } catch (TransformException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (FactoryException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * GET  /rest/trips -> get all the trips.
     */
    @RequestMapping(value = "/rest/skipper/trips",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<SkipperTripDTO> getAll() {
        log.debug("REST request to get all Trips for the user (skipper)");
        List<TripSkipper> list = tripService.getTripsForSkipper();

        List<SkipperTripDTO> dtoList = new ArrayList<SkipperTripDTO>();
        for (TripSkipper item : list)
            dtoList.add(Mapper.tripSkipper(item));
        return dtoList;
    }

    /**
     * GET  /rest/skipper/trips/active -> get the active trip for this user (skipper).
     */
    @RequestMapping(value = "/rest/skipper/trips/active",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SkipperTripDTO> getActiveTripForSkipper() {
        log.debug("REST request to get the current trip for the skipper");
        TripSkipper tripSkipper = tripService.getActiveTripForSkipper();
        if (tripSkipper == null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        else
            return new ResponseEntity<SkipperTripDTO>(Mapper.tripSkipper(tripSkipper), HttpStatus.OK);
    }

        /**
         * GET  /rest/trips/:id -> get the "id" trip.
         */
    @RequestMapping(value = "/rest/skipper/trips/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SkipperTripDTO> get(@PathVariable Long id, @RequestParam(value = "srid", required = false) Integer srid)  {
        log.debug("REST request to get Trip : {} SRID: {}", id, srid);

        Trip trip = tripService.getTrip(id);
        if (trip == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (!(trip instanceof TripSkipper))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        TripSkipper tripSkipper = (TripSkipper) trip;

        // Match the projection on the user interface with any waypoints defined.
        if (tripSkipper.getWayPoints() == null)
            return new ResponseEntity<SkipperTripDTO>(Mapper.tripSkipper(tripSkipper), HttpStatus.OK);

        if (srid != null && tripSkipper.getWayPoints().getSRID() != srid) {
            // if the SRID of the data in the database (currently 4326) is not the same as the client
            // then convert the way points
            try {
                if (log.isDebugEnabled())
                    log.debug("Attempting to transform " + trip.getWayPoints() + " SRID:" + trip.getWayPoints().getSRID() +
                    " to SRID: "+ srid);

                LineString ls = (LineString) crsTransformService.transform(trip.getWayPoints(), srid);

                if (log.isDebugEnabled())
                    log.debug("Successfully transformed " + trip.getWayPoints() + " SRID:" + trip.getWayPoints().getSRID() +
                            " to " + ls + " SRID: "+ srid);

                Feature feature = Mapper.feature(ls, FeatureType.WAY_POINTS, null);
                return new ResponseEntity<SkipperTripDTO>(Mapper.tripSkipper(tripSkipper, feature), HttpStatus.OK);
            } catch (FactoryException e) {
                log.error(e.getMessage(), e);
            } catch (TransformException e) {
                log.error(e.getMessage(), e);
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            return new ResponseEntity<SkipperTripDTO>(Mapper.tripSkipper(tripSkipper), HttpStatus.OK);
        }
    }

    /**
     * DELETE  /rest/trips/:id -> delete the "id" trip.
     */
    @RequestMapping(value = "/rest/skipper/trips/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Trip : {}", id);
        tripService.deleteTrip(id);
    }

}
