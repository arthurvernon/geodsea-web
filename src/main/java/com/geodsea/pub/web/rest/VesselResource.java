package com.geodsea.pub.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.geodsea.pub.domain.Vessel;
import com.geodsea.pub.service.ActionRefusedException;
import com.geodsea.pub.service.VesselService;
import com.geodsea.pub.web.rest.dto.VesselAddDTO;
import com.geodsea.pub.web.rest.dto.VesselDTO;
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
import java.util.Collection;
import java.util.List;

/**
 * REST controller for managing Boat.
 */
@RestController
@RequestMapping("/app")
public class VesselResource {

    private final Logger log = LoggerFactory.getLogger(VesselResource.class);

    @Inject
    private VesselService vesselService;


    /**
     * POST  /rest/vessels -> Create a new boat.
     */
    @RequestMapping(value = "/rest/vessels",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> create(@RequestBody VesselAddDTO vesselAdd) {

        Vessel vessel = Mapper.vessel(vesselAdd);
        if (vesselAdd.getId() == null) {
            log.debug("REST request to add a Vessel : {}", vessel);
            try {
                vessel = vesselService.registerVessel(vessel, vesselAdd.getOwners(), vesselAdd.getSkippers());
                log.debug("Created a vessel with id: " + vessel.getId());
            } catch (ActionRefusedException e) {
                return new ResponseEntity<String>(e.getCode(), HttpStatus.NOT_MODIFIED);
            }
        } else {
            log.debug("REST request to update a Vessel : {}", vessel);
            try {
                vessel = vesselService.updateVessel(vessel);
                log.debug("Saved a vessel with id: " + vessel.getId());
            } catch (ActionRefusedException e) {
                return new ResponseEntity<String>(e.getCode(), HttpStatus.NOT_MODIFIED);
            }
        }
        return new ResponseEntity<VesselDTO>(Mapper.vessel(vessel), HttpStatus.OK);
    }

    /**
     * Retrieve all the vessels that this person is permitted to see.
     * <p>
     * Administrators can view all vessels, but are unlikely to call this method direct.
     * Owners and skippers may retrieve vessel details at any time.
     * Rescue organisations can view vessels they are monitoring.
     * </p>
     * GET  /rest/vessels -> get all the vessels.
     */
    @RequestMapping(value = "/rest/vessels",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getAll() {
        log.debug("REST request to get all Vessels");
        try {
            Collection<Vessel> vessels = vesselService.retrieveVesselsUserMaySee();
            log.debug("REST request to get all Vessels returned " + vessels.size() + " vessels.");
            List<VesselDTO> dtos = new ArrayList<VesselDTO>();
            for (Vessel vessel : vessels)
                dtos.add(Mapper.vessel(vessel));
            return new ResponseEntity<>(dtos, HttpStatus.OK);
        } catch (ActionRefusedException e) {
            return new ResponseEntity<String>(e.getCode(), HttpStatus.FORBIDDEN);
        }
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
        Vessel vessel = vesselService.retrieveVessel(id);
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
    public ResponseEntity<String> delete(@PathVariable Long id) {
        log.debug("REST request to delete Vessel : {}", id);
        try {
            vesselService.deleteVessel(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ActionRefusedException e) {
            return new ResponseEntity<String>(e.getCode(), HttpStatus.NOT_MODIFIED);
        }
    }
}
