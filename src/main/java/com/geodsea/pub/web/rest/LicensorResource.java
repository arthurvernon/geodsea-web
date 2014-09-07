package com.geodsea.pub.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.geodsea.pub.domain.Address;
import com.geodsea.pub.domain.Licensor;
import com.geodsea.pub.domain.Person;
import com.geodsea.pub.domain.Zone;
import com.geodsea.pub.repository.LicensorRepository;
import com.geodsea.pub.repository.PersonRepository;
import com.geodsea.pub.service.ActionRefusedException;
import com.geodsea.pub.service.GisService;
import com.geodsea.pub.service.LicenseService;
import com.geodsea.pub.web.rest.dto.LicensorDTO;
import com.geodsea.ws.LicenseResponse;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTWriter;
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

    @Inject
    private GisService gisService;


    /**
     * POST  /rest/licensors -> Create a new licensor.
     */
    @RequestMapping(value = "/rest/licensors",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> create(@RequestBody LicensorDTO dto) {
        log.debug("REST request to create/save Licensor : {}", dto);

        Polygon polygon = null;
        try {
            Geometry geometry = gisService.createFromWKT(dto.getZoneWKT());
            if (geometry instanceof Polygon)
                polygon = (Polygon) geometry;
            else {
                log.warn("Require a Polygon, not a " + geometry.getGeometryType());
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (ParseException ex) {
            log.warn("Invalid zone definition: " + ex.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Zone zone = new Zone(dto.getZoneTitle(), polygon);
        licenseService.addOrUpdateLicensor(dto.getId(), dto.getOrganisationId(), dto.getWebServiceURL(), zone);
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
            dtoList.add(createLicensorDTO(l));
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

        LicensorDTO dto = createLicensorDTO(licensor);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    private LicensorDTO createLicensorDTO(Licensor licensor) {
        return new LicensorDTO(licensor.getId(), licensor.getOrgansation().getId(),
                licensor.getOrgansation().getPublishedName(),
                licensor.getLicenceWsURL(), licensor.getZone().getZoneTitle(),
                gisService.toWKT(licensor.getZone().getZone()));
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
     * Get the licencing agency that covers the user's home address.
     * GET  /rest/userlicensor/:username  -> get the "id" licensor.
     */
    @RequestMapping(value = "/rest/userlicensor/{username}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LicensorDTO> getLicensorLocalToUser(@PathVariable String username, HttpServletResponse response) {
        log.debug("REST request to get Licensor closest to user: {}", username);


        Person person = personRepository.getUserByParticipantName(username);
        Address address = person.getAddress();
        if (address == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        List<Licensor> licensors = licenseService.getLocalLicensor(person.getAddress());

        if (licensors == null || licensors.size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }


        Licensor l = licensors.get(0);
        LicensorDTO licensorDTO = createLicensorDTO(l);

        return new ResponseEntity<>(licensorDTO, HttpStatus.OK);
    }

    /**
     * Get the licence details as supplied by the licensing agency.
     * GET /rest/licensor/:id/registration/:registration
     */
    @RequestMapping(value = "/rest/licensors/{licensorId}/registration/{registration}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LicenseResponse> getLicensorLocalToUser(@PathVariable Long licensorId, @PathVariable String registration, HttpServletResponse response) {

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

            return new ResponseEntity<LicenseResponse>(licenseResponse, HttpStatus.OK);
        }
        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}
