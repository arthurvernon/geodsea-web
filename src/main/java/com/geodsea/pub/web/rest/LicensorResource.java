package com.geodsea.pub.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.geodsea.pub.domain.Address;
import com.geodsea.pub.domain.Licensor;
import com.geodsea.pub.domain.Person;
import com.geodsea.pub.repository.LicensorRepository;
import com.geodsea.pub.repository.PersonRepository;
import com.geodsea.pub.service.LicenseService;
import com.geodsea.pub.web.rest.dto.LicensorDTO;
import com.geodsea.ws.LicenseResponse;
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
    public void create(@RequestBody LicensorDTO dto) {
        log.debug("REST request to create/save Licensor : {}", dto);

        licenseService.addOrUpdateLicensor(dto.getId(), dto.getParticipantGroupId(), dto.getWebServiceURL(), dto.getRegion());
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
            dtoList.add(new LicensorDTO(l.getId(), l.getParticipant().getId(), l.getParticipant().getPublishedName(),
                    l.getLicenceWsURL(), l.getRegion()));
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
        LicensorDTO dto = new LicensorDTO(licensor.getId(), licensor.getParticipant().getId(),
                licensor.getParticipant().getPublishedName(),
                licensor.getLicenceWsURL(), licensor.getRegion());
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
     * Get the licencing agency that covers the user's home address.
     * GET  /rest/userlicensor/:username  -> get the "id" licensor.
     *
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
        LicensorDTO licensorDTO = new LicensorDTO(l.getId(), l.getParticipant().getId(), l.getParticipant().getPublishedName(),
                l.getLicenceWsURL(), l.getRegion());

        return new ResponseEntity<>(licensorDTO, HttpStatus.OK);
    }

    /**
     * Get the licence details as supplied by the licensing agency.
     * GET /rest/licensor/:id/registration/:registration
     *
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

        // TODO store owner details and the like from the response
        // TODO provide association from vessel to owner & participant
        // TODO verify that the person requesting is an owner of the vessel
        // TODO fill out the details before returning


        return new ResponseEntity<>(licenseResponse, HttpStatus.OK);
    }

}
