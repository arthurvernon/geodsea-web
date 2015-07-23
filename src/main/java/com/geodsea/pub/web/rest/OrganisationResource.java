package com.geodsea.pub.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.geodsea.pub.domain.Address;
import com.geodsea.pub.domain.Collective;
import com.geodsea.pub.domain.Organisation;
import com.geodsea.pub.service.ActionRefusedException;
import com.geodsea.pub.service.ErrorCode;
import com.geodsea.pub.service.GisService;
import com.geodsea.pub.service.GroupService;
import com.geodsea.common.dto.OrganisationDTO;
import com.geodsea.common.dto.ParticipantDTO;
import com.geodsea.pub.web.rest.mapper.Mapper;
import com.vividsolutions.jts.geom.Point;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * REST controller for managing Organisation.
 */
@RestController
@RequestMapping("/app")
public class OrganisationResource {

    private final Logger log = LoggerFactory.getLogger(OrganisationResource.class);


    @Inject
    private GroupService groupService;


    /**
     * POST  /rest/organisations -> Create a new organisation.
     */
    @RequestMapping(value = "/rest/organisations",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> create(@RequestBody OrganisationDTO orgDTO, HttpServletRequest request) {
        log.debug("REST request to save Organisation : {}", orgDTO);

        // group must be specified
        if (orgDTO == null) {
            log.info("Attempt to register a null group");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // setup the group with the person as the contact person with the status of inactive, but
        // assigned the role of manager
        if (StringUtils.isBlank(orgDTO.getEmail())) {
            log.info("No contact email address specified");
            return new ResponseEntity<String>(ErrorCode.MISSING_EMAIL, HttpStatus.FORBIDDEN);
        }

        String groupName = orgDTO.getLogin();

        if (StringUtils.isBlank(groupName)) {
            log.info("No group name specified");
            return new ResponseEntity<String>(ErrorCode.MISSING_GROUP_NAME, HttpStatus.FORBIDDEN);
        }
        if (orgDTO.getId() != null) {
            try {
                groupService.updateOrganisation(orgDTO.getId(), orgDTO.getEmail(), orgDTO.getContact().getLogin(),
                        orgDTO.getName(), orgDTO.getLangKey(), orgDTO.isEnabled(), orgDTO.getWebsiteURL());
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (ActionRefusedException ex) {
                return new ResponseEntity<String>(ex.getCode(), HttpStatus.FORBIDDEN);
            }
        } else {
            try {
                ParticipantDTO contactDTO = orgDTO.getContact();
                String userName = null;
                if (contactDTO != null)
                    userName = contactDTO.getLogin();
                Point point = GisService.createPointFromLatLong(orgDTO.getPoint().getLat(), orgDTO.getPoint().getLon());
                Address address = new Address(orgDTO.getAddress(), point);

                Collective collective = groupService.createOrganisation(orgDTO.getLogin(), orgDTO.getLangKey(), orgDTO.getName(),
                        orgDTO.getEmail(), userName, orgDTO.isEnabled(), orgDTO.getWebsiteURL(), address, orgDTO.getTelephone());

                // send the email if not automatically enabled by an administrator.
                if (!collective.isEnabled()) {
                    // "http" "://" "myhost" ":" "80"
                    String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
                    groupService.sendRegistrationEmail(collective, baseUrl);
                }
                return new ResponseEntity<>(HttpStatus.CREATED);
            } catch (ActionRefusedException ex) {
                if (ErrorCode.USERNAME_ALREADY_EXISTS.equals(ex.getCode()))
                    return new ResponseEntity<String>(ex.getCode(), HttpStatus.NOT_MODIFIED);
                else
                    return new ResponseEntity<String>(ex.getCode(), HttpStatus.FORBIDDEN);
            }
        }
    }

    /**
     * GET  /rest/organisations -> get all the organisations.
     */
    @RequestMapping(value = "/rest/organisations",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<OrganisationDTO> getAll() {
        log.debug("REST request to get all Organisations");
        List<Organisation> organisations = groupService.getAllOrganisations();
        List<OrganisationDTO> dtos = new ArrayList<OrganisationDTO>(organisations.size());
        for (Organisation o : organisations)
            dtos.add(Mapper.organisation(o));
        return dtos;
    }

    /**
     * GET  /rest/organisations/:id -> get the "id" organisation.
     */
    @RequestMapping(value = "/rest/organisations/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OrganisationDTO> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get Organisation : {}", id);
        Organisation organisation = groupService.lookupOrganisation(id);
        if (organisation == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(Mapper.organisation(organisation), HttpStatus.OK);
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
        groupService.deleteOrganisation(id);
    }
}
