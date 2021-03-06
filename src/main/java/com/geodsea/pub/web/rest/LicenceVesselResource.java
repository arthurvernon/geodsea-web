package com.geodsea.pub.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.geodsea.pub.domain.LicenseVessel;
import com.geodsea.pub.repository.LicenseVesselRepository;
import com.geodsea.pub.repository.LicensorRepository;
import com.geodsea.pub.service.LicenseService;
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
public class LicenceVesselResource {

    private static final String PATH = "/rest/licensevessels";

    private final Logger log = LoggerFactory.getLogger(LicenceVesselResource.class);

    @Inject
    private LicenseVesselRepository licenseVesselRepository;

    @Inject
    private LicensorRepository licensorRepository;

    @Inject
    private LicenseService licenseService;

    /**
     * Manually create a registration for a vessel
     * POST  /rest/licensevessels -> Create a new registration.
     */
    @RequestMapping(value = PATH,
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LicenseVessel> create(@PathVariable Long licensorId, @RequestBody LicenseVessel vessel) {
        log.debug("REST request to save registration : {}", vessel);
        LicenseVessel saved = licenseVesselRepository.save(vessel);
        log.debug("Created a vessel with id: " + saved.getId());
        return new ResponseEntity<LicenseVessel>(saved, HttpStatus.OK);
    }

    /**
     * GET  /rest/licensevessels -> get all the vessel licenses.
     */
    @RequestMapping(value = PATH,
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<LicenseVessel> getAll() {
        log.debug("REST request to get all Vessels");
        List<LicenseVessel> licenseVessels = licenseVesselRepository.findAll();
        log.debug("REST request to get all Vessels licenses returned " + licenseVessels.size() + " licenseVessels.");

        return licenseVessels;
    }


    /**
     * GET  /rest/licensevessels/:id -> get the "id" vessel license.
     */
    @RequestMapping(value = PATH +"/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LicenseVessel> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get Vessel license: {}", id);
        LicenseVessel licenseVessel = licenseVesselRepository.findOne(id);
        if (licenseVessel == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(licenseVessel, HttpStatus.OK);
    }



    /**
     * DELETE  /rest/licensevessels/:id -> delete the "id" vessel license.
     */
    @RequestMapping(value = PATH + "/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Vessel license : {}", id);
        licenseVesselRepository.delete(id);
    }

}
