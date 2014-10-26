package com.geodsea.pub.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.geodsea.pub.domain.Licensor;
import com.geodsea.pub.domain.Zone;
import com.geodsea.pub.repository.LicensorRepository;
import com.geodsea.pub.repository.PersonRepository;
import com.geodsea.pub.service.GisService;
import com.geodsea.pub.service.LicenseService;
import com.geodsea.common.dto.LicensorDTO;
import com.geodsea.common.dto.VesselDTO;
import com.geodsea.pub.web.rest.mapper.Mapper;
import com.geodsea.ws.LicenseResponse;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.io.ParseException;
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
 * REST controller for managing Licensor.
 */
@RestController
@RequestMapping("/app")
public class LicensorResource {

    private final Logger log = LoggerFactory.getLogger(LicensorResource.class);

    @Inject
    private LicensorRepository licensorRepository;

    @Inject
    private LicenseService licenseService;

    @Inject
    private PersonRepository personRepository;


    /**
     * POST  /rest/licensors -> Create a new licensor.
     */
    @RequestMapping(value = "/rest/licensors",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> create(@RequestBody LicensorDTO dto) {
        log.debug("REST request to create/save Licensor : {}", dto);

        MultiPolygon polygon = null;
        try {
            Geometry geometry = GisService.createFromWKT(dto.getZoneWKT());
            if (geometry instanceof MultiPolygon)
                polygon = (MultiPolygon) geometry;
            else {
                log.warn("Require a MultiPolygon, not a " + geometry.getGeometryType());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (ParseException ex) {
            log.warn("Invalid zone definition: " + ex.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Zone zone = new Zone(dto.getZoneTitle(), polygon);
        licenseService.addOrUpdateLicensor(dto.getId(), dto.getId(), dto.getWebServiceURL(), zone);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /**
     * GET  /rest/licensors -> get all the licensors.
     */
    @RequestMapping(value = "/rest/licensors",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<LicensorDTO> getAll() {
        log.debug("REST request to get all Licensors");
        List<Licensor> licensors = licensorRepository.findAll();

        List<LicensorDTO> dtoList = new ArrayList<LicensorDTO>();
        for (Licensor l : licensors)
            dtoList.add(Mapper.licensor(l));
        log.debug("Located " + dtoList.size() + " licensors");
        return dtoList;
    }

    /**
     * GET  /rest/licensors/:id -> get the "id" licensor.
     */
    @RequestMapping(value = "/rest/licensors/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LicensorDTO> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get Licensor : {}", id);
        Licensor licensor = licensorRepository.findOne(id);
        if (licensor == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        LicensorDTO dto = Mapper.licensor(licensor);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }



    /**
     * DELETE  /rest/licensors/:id -> delete the "id" licensor.
     */
    @RequestMapping(value = "/rest/licensors/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Licensor : {}", id);
        licensorRepository.delete(id);
    }


    /**
     * Get the licence details as supplied by the licensing agency.
     * GET /rest/licensor/:id/registration/:registration
     */
    @RequestMapping(value = "/rest/licensors/{licensorId}/registration/{registration}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<VesselDTO> getLicensorLocalToUser(@PathVariable Long licensorId, @PathVariable String registration, HttpServletResponse response) {

        log.debug("REST request to get Vessel license details");

        Licensor licensor = licensorRepository.findOne(licensorId);
        if (licensor == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        LicenseResponse licenseResponse = licenseService.lookupLicense(licensor, registration);

        if (licenseResponse != null) {
            // TODO store owner details and the like from the response
            // TODO provide association from vessel to owner & participant
            // TODO verify that the person requesting is an owner of the vessel
            // TODO fill out the details before returning

            return new ResponseEntity<VesselDTO>(Mapper.vessel(licenseResponse.getVessel()), HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
