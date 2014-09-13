package com.geodsea.pub.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.geodsea.pub.domain.Skipper;
import com.geodsea.pub.repository.SkipperRepository;
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
 * REST controller for managing Skipper.
 */
@RestController
@RequestMapping("/app")
public class SkipperResource {

    private final Logger log = LoggerFactory.getLogger(SkipperResource.class);

    @Inject
    private SkipperRepository skipperRepository;

    /**
     * POST  /rest/skippers -> Create a new skipper.
     */
    @RequestMapping(value = "/rest/skippers",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody Skipper skipper) {
        log.debug("REST request to save Skipper : {}", skipper);
        skipperRepository.save(skipper);
    }

    /**
     * GET  /rest/skippers -> get all the skippers.
     */
    @RequestMapping(value = "/rest/skippers",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Skipper> getAll() {
        log.debug("REST request to get all Skippers");
        return skipperRepository.findAll();
    }

    /**
     * GET  /rest/skippers/:id -> get the "id" skipper.
     */
    @RequestMapping(value = "/rest/skippers/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Skipper> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get Skipper : {}", id);
        Skipper skipper = skipperRepository.findOne(id);
        if (skipper == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(skipper, HttpStatus.OK);
    }

    /**
     * DELETE  /rest/skippers/:id -> delete the "id" skipper.
     */
    @RequestMapping(value = "/rest/skippers/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Skipper : {}", id);
        skipperRepository.delete(id);
    }
}
