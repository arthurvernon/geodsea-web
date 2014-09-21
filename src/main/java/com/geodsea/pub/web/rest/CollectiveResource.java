package com.geodsea.pub.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.geodsea.pub.domain.Organisation;
import com.geodsea.pub.service.ActionRefusedException;
import com.geodsea.pub.service.ErrorCode;
import com.geodsea.pub.service.GroupService;
import com.geodsea.pub.web.rest.dto.*;
import com.geodsea.pub.web.rest.mapper.Mapper;
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
 * REST controller for managing Group.
 */
@RestController
@RequestMapping("/app")
public class CollectiveResource extends ParticipantResource {

    private final Logger log = LoggerFactory.getLogger(CollectiveResource.class);

    @Inject
    private GroupService groupService;


    /**
     * Get the groups that this user is a manager of
     * GET app/rest/organisations/user/manages -> get the groups that this user manages
     */
    @RequestMapping(value = "/rest/organsations/user/manages",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<OrganisationDTO>> getLicensorLocalToUser(HttpServletResponse response) {
        log.debug("REST request to get organisations managed by the user");
        List<Organisation> organisations = groupService.getManagedOrganisations();
        List<OrganisationDTO> dtos = new ArrayList<OrganisationDTO>();
        for (Organisation org: organisations)
            dtos.add(Mapper.organisation(org));
        log.debug("REST number of organisations managed by the user: " + dtos.size());
        return new ResponseEntity<List<OrganisationDTO>>(dtos, HttpStatus.OK);
    }

}
