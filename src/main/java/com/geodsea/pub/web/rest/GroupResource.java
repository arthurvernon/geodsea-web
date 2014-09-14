package com.geodsea.pub.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.geodsea.pub.domain.*;
import com.geodsea.pub.service.ActionRefusedException;
import com.geodsea.pub.service.ErrorCode;
import com.geodsea.pub.service.GroupService;
import com.geodsea.pub.web.rest.dto.GroupDTO;
import com.geodsea.pub.web.rest.dto.MemberDTO;
import com.geodsea.pub.web.rest.dto.UserDTO;
import com.geodsea.pub.web.rest.mapper.Mapper;
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
 * REST controller for managing Group.
 */
@RestController
@RequestMapping("/app")
public class GroupResource extends ParticipantResource {

    private final Logger log = LoggerFactory.getLogger(GroupResource.class);

    @Inject
    private GroupService groupService;

    /**
     * Create a new group with the user nominated or the current user if no contact person login is specified.
     * POST  /rest/groups -> Create a new group.
     */
    @RequestMapping(value = "/rest/groups",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> createOrUpdate(@RequestBody GroupDTO groupDTO, HttpServletRequest request,
                                            HttpServletResponse response) {

        // group must be specified
        if (groupDTO == null) {
            log.info("Attempt to register a null group");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // setup the group with the person as the contact person with the status of inactive, but
        // assigned the role of manager
        if (StringUtils.isBlank(groupDTO.getEmail())) {
            log.info("No contact email address specified");
            return new ResponseEntity<String>(ErrorCode.MISSING_EMAIL, HttpStatus.FORBIDDEN);
        }

        String groupName = groupDTO.getLogin();

        if (StringUtils.isBlank(groupDTO.getLogin())) {
            log.info("No group name specified");
            return new ResponseEntity<String>(ErrorCode.MISSING_GROUP_NAME, HttpStatus.FORBIDDEN);
        }
        if (groupDTO.getId() != null) {
            try {
                groupService.updateGroup(groupDTO.getId(), groupDTO.getEmail(), groupDTO.getContactLogin());
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (ActionRefusedException ex) {
                return new ResponseEntity<String>(ex.getCode(), HttpStatus.FORBIDDEN);
            }
        } else {
            try {
                UserDTO userDTO = groupDTO.getContactPerson();
                String userName = null;
                if (userDTO != null)
                    userName = userDTO.getLogin();
                groupService.createGroup(groupDTO.getLogin(), groupDTO.getEmail(), userName, createBaseUrl(request));
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
     * GET  /rest/groups/:id/members -> get the members of the specified group.
     */
    @RequestMapping(value = "/rest/groups/{id}/members",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getMembers(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get members of Group : {}", id);
        try {

            // permission denied or no such group
            List<Member> members = groupService.getMembers(id);
            List<MemberDTO> dtos = new ArrayList<MemberDTO>(members.size());
            for (Member member : members)
                dtos.add(Mapper.member(member));
            return new ResponseEntity<>(dtos, HttpStatus.OK);

        }
        catch (ActionRefusedException ex)
        {
            if (ErrorCode.PERMISSION_DENIED.equals(ex.getCode()))
                return new ResponseEntity<String>(ex.getCode(), HttpStatus.NOT_FOUND);
            else
                return new ResponseEntity<String>(ex.getCode(), HttpStatus.FORBIDDEN);
        }
    }


    /**
     * GET  /rest/groups -> get all the groups.
     */
    @RequestMapping(value = "/rest/groups",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<GroupDTO> getAll() {
        log.debug("REST request to get all Groups");

        List<Group> groups = groupService.getAllGroups();
        List<GroupDTO> dtos = new ArrayList<GroupDTO>();
        for (Group g : groups)
            dtos.add(Mapper.group(g));

        return dtos;
    }


    /**
     * GET  /rest/groups/:id -> get the "id" group.
     */
    @RequestMapping(value = "/rest/groups/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<GroupDTO> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get Group : {}", id);
        Group group = groupService.lookupGroup(id);
        if (group == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(Mapper.group(group), HttpStatus.OK);
    }

    /**
     * DELETE  /rest/groups/:id -> delete the "id" group.
     */
    @RequestMapping(value = "/rest/groups/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Group : {}", id);
        groupService.deleteGroup(id);
    }
}
