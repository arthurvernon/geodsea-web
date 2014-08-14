package com.geodsea.pub.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.geodsea.pub.domain.Licensor;
import com.geodsea.pub.repository.LicensorRepository;
import com.geodsea.pub.service.LicenseService;
import com.geodsea.pub.web.rest.dto.LicensorDTO;
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
     * GET  /rest/userlicensor/:username  -> get the "id" licensor.
     *
     */
    @RequestMapping(value = "/rest/userlicensor/{username}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<LicensorDTO> getLicensorLocalToUser(@PathVariable String username, HttpServletResponse response) {
        log.debug("REST request to get Licensor closest to user: {}", username);

//        if (licensor == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
        LicensorDTO dto = new LicensorDTO((long)1, (long)5,
                "Dept of Transport WA",
                "http://www.transport.wa.gov.au", "Western Australia");
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
}
