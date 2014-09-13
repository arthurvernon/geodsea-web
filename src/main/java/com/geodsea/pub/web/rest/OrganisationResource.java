package com.geodsea.pub.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.geodsea.pub.domain.Organisation;
import com.geodsea.pub.repository.OrganisationRepository;
import com.geodsea.pub.web.rest.dto.OrganisationDTO;
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
 * REST controller for managing Organisation.
 */
@RestController
@RequestMapping("/app")
public class OrganisationResource {

    private final Logger log = LoggerFactory.getLogger(OrganisationResource.class);

    @Inject
    private OrganisationRepository organisationRepository;

    /**
     * POST  /rest/organisations -> Create a new organisation.
     */
    @RequestMapping(value = "/rest/organisations",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody Organisation organisation) {
        log.debug("REST request to save Organisation : {}", organisation);
        organisationRepository.save(organisation);
    }

    /**
     * GET  /rest/organisations -> get all the organisations.
     */
    @RequestMapping(value = "/rest/organisations",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Organisation> getAll() {
        log.debug("REST request to get all Organisations");
        return organisationRepository.findAll();
    }

    /**
     * GET  /rest/organisations/:id -> get the "id" organisation.
     */
    @RequestMapping(value = "/rest/organisations/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Organisation> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get Organisation : {}", id);
        Organisation organisation = organisationRepository.findOne(id);
        if (organisation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(organisation, HttpStatus.OK);
    }

    /**
     * DELETE  /rest/organisations/:id -> delete the "id" organisation.
     */
    @RequestMapping(value = "/rest/organisations/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Organisation : {}", id);
        organisationRepository.delete(id);
    }
}
