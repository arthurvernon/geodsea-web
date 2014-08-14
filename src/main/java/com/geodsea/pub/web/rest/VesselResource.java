package com.geodsea.pub.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.geodsea.pub.domain.Vessel;
import com.geodsea.pub.domain.type.VesselType;
import com.geodsea.pub.repository.VesselRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * REST controller for managing Boat.
 */
@RestController
@RequestMapping("/app")
public class VesselResource {

    private final Logger log = LoggerFactory.getLogger(VesselResource.class);

    @Inject
    private VesselRepository vesselRepository;

    /**
     * POST  /rest/vessels -> Create a new boat.
     */
    @RequestMapping(value = "/rest/vessels",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Vessel> create(@RequestBody Vessel vessel) {
        log.debug("REST request to save Vessel : {}", vessel);
        Vessel saved = vesselRepository.save(vessel);
        log.debug("Created/Saved a vessel with id: " + saved.getId());
        return new ResponseEntity<Vessel>(saved, HttpStatus.OK);
    }

    /**
     * GET  /rest/vessels -> get all the vessels.
     */
    @RequestMapping(value = "/rest/vessels",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Vessel> getAll() {
        log.debug("REST request to get all Vessels");
        List<Vessel> vessels = vesselRepository.findAll();
        log.debug("REST request to get all Vessels returned " + vessels.size() + " vessels.");

        return vessels;
    }

    /**
     * GET  /rest/vessels/:id -> get the "id" boat.
     */
    @RequestMapping(value = "/rest/vessels/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Vessel> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get Boat : {}", id);
        Vessel vessel = vesselRepository.findOne(id);
        if (vessel == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(vessel, HttpStatus.OK);
    }

    /**
     * DELETE  /rest/vessels/:id -> delete the "id" boat.
     */
    @RequestMapping(value = "/rest/vessels/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Vessel : {}", id);
        vesselRepository.delete(id);
    }
}
