package com.geodsea.pub.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.geodsea.pub.domain.Boat;
import com.geodsea.pub.repository.BoatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * REST controller for managing Boat.
 */
@RestController
@RequestMapping("/app")
public class BoatResource {

    private final Logger log = LoggerFactory.getLogger(BoatResource.class);

    @Inject
    private BoatRepository boatRepository;

    /**
     * POST  /rest/boats -> Create a new boat.
     */
    @RequestMapping(value = "/rest/boats",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody Boat boat) {
        log.debug("REST request to save Boat : {}", boat);
       Boat b = boatRepository.save(boat);
        log.debug("Created a boat with id: " + b.getId());
    }

    /**
     * GET  /rest/boats -> get all the boats.
     */
    @RequestMapping(value = "/rest/boats",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Boat> getAll() {
        log.debug("REST request to get all Boats");
        return boatRepository.findAll();
    }

    /**
     * GET  /rest/boats/:id -> get the "id" boat.
     */
    @RequestMapping(value = "/rest/boats/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boat> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get Boat : {}", id);
        Boat boat = boatRepository.findOne(id);
        if (boat == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(boat, HttpStatus.OK);
    }

    /**
     * DELETE  /rest/boats/:id -> delete the "id" boat.
     */
    @RequestMapping(value = "/rest/boats/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Boat : {}", id);
        boatRepository.delete(id);
    }
}
