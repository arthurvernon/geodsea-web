package com.geodsea.pub.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.geodsea.pub.domain.Trip;
import com.geodsea.pub.domain.TripSkipper;
import com.geodsea.pub.service.ActionRefusedException;
import com.geodsea.pub.service.SkipperService;
import com.geodsea.pub.web.rest.dto.TripSkipperDTO;
import com.geodsea.pub.web.rest.mapper.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for managing Trips .
 */
@RestController
@RequestMapping("/app")
public class TripResource {

    private final Logger log = LoggerFactory.getLogger(TripResource.class);

    @Inject
    private SkipperService skipperService;

    /**
     * POST  /rest/trips -> Create a new trip.
     * TODO allow entry of way points.
     *
     * @return the ID of the trip (200 OK) or error code (409 CONFLICT)
     */
    @RequestMapping(value = "/rest/skipper/trips",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> create(@RequestBody TripSkipperDTO trip) {
        log.debug("REST request to save Trip : {}", trip);

        if (trip.getId() == null) {
            try {
                Trip created = skipperService.createTripPlan(trip.getVessel().getId(), trip.getSkipper().getId(),
                        trip.getHeadline(), trip.getScheduledStartTime(), trip.getScheduledEndTime(), trip.getSummary(), null,
                        trip.getFuelOnBoard(), trip.getPeopleOnBoard());
                return new ResponseEntity<Long>(created.getId(), HttpStatus.OK);
            } catch (ActionRefusedException e) {
                return new ResponseEntity<String>(e.getCode(), HttpStatus.CONFLICT);
            }
        } else {
            try {
                skipperService.updatePlan(trip.getId(), trip.getSkipper().getId(), trip.getHeadline(),
                        trip.getScheduledStartTime(), trip.getScheduledEndTime(), trip.getSummary(), null,
                        trip.getFuelOnBoard(), trip.getPeopleOnBoard());
                return new ResponseEntity<Long>(trip.getId(), HttpStatus.OK);
            } catch (ActionRefusedException e) {
                return new ResponseEntity<String>(e.getCode(), HttpStatus.CONFLICT);
            }

        }
    }

    /**
     * GET  /rest/trips -> get all the trips.
     */
    @RequestMapping(value = "/rest/skipper/trips",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<TripSkipperDTO> getAll() {
        log.debug("REST request to get all Trips");
        List<TripSkipper> list = skipperService.getTripsForSkipper();

        List<TripSkipperDTO> dtoList = new ArrayList<TripSkipperDTO>();
        for (TripSkipper item : list)
            dtoList.add(Mapper.tripSkipper(item));
        return dtoList;
    }

    /**
     * GET  /rest/trips/:id -> get the "id" trip.
     */
    @RequestMapping(value = "/rest/skipper/trips/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TripSkipperDTO> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get Trip : {}", id);
        Trip trip = skipperService.getTrip(id);
        if (trip == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (! (trip instanceof TripSkipper))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);


        return new ResponseEntity<TripSkipperDTO>(Mapper.tripSkipper((TripSkipper) trip), HttpStatus.OK);
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
        skipperService.deleteTrip(id);
    }
}
