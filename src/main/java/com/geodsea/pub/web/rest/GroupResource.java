package com.geodsea.pub.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.geodsea.pub.domain.*;
import com.geodsea.pub.repository.GroupRepository;
import com.geodsea.pub.repository.ParticipantRepository;
import com.geodsea.pub.repository.PersonRepository;
import com.geodsea.pub.security.SecurityUtils;
import com.geodsea.pub.service.ParticipantService;
import com.geodsea.pub.web.rest.dto.GroupDTO;
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
import java.util.Locale;

/**
 * REST controller for managing Group.
 */
@RestController
@RequestMapping("/app")
public class GroupResource extends ParticipantResource {

    private final Logger log = LoggerFactory.getLogger(GroupResource.class);

    @Inject
    private GroupRepository groupRepository;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private ParticipantRepository participantRepository;

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

        // with a non-null participant identifier
        String groupParticipantName = groupDTO.getLogin();
        if (groupParticipantName == null || groupParticipantName.trim().length() == 0)
        {
            log.info("Attempt to register a group with no login name");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        boolean creating = false;
        Group group = null;
        if (groupDTO.getId() != null) {

            // ID indicates that we are updating an existing group
            group = groupRepository.findOne(groupDTO.getId());
            if (group == null) {
                log.info("No such group: {}", groupDTO.getId());
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            else
                log.debug("Updating existing group {}", groupDTO.getLogin());
        } else {
            // if this is a new group then there should no be a participant with the same name
            Participant p = participantRepository.getParticipantByParticipantName(groupParticipantName);
            if (p != null) {
                log.info("An participant exists with the name {}", groupParticipantName);
                return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
            }

            // OK so we can setup the group.
            group = new Group();
            group.setParticipantName(groupParticipantName);
            ParticipantService.addRegistrationToken(group);
            creating = true;
        }

        // use the userID to verify if the contact has changed.
        UserDTO userDTO = groupDTO.getContactPerson();
        String contactParticipantName = null;
        if (userDTO == null || (userDTO.getLogin() == null))
            contactParticipantName = SecurityUtils.getCurrentLogin();
        else
            contactParticipantName = userDTO.getLogin();

        Person contactPerson = personRepository.getUserByParticipantName(contactParticipantName);
        if (contactPerson == null || ! contactPerson.isEnabled()) {
            log.info("Not accepting group where " +
                    (contactPerson == null? " no such person" : "user's account is disabled"));
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

            // setup the group with the person as the contact person with the status of inactive, but
            // assigned the role of manager
        if (StringUtils.isBlank(groupDTO.getEmail())) {
            log.info("No contact email address specified");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        group.setEmail(groupDTO.getEmail());
        group.setEnabled(groupDTO.isEnabled());
        group.setContactPerson(contactPerson);

        if (creating)
            group.addMember(Member.inactiveManager(contactPerson, group));

        groupRepository.save(group);

        if(creating)
        {
            log.debug("Sending group registration email to {}", group.getEmail());
            final Locale locale = Locale.forLanguageTag(contactPerson.getLangKey());
            String content = createHtmlContentFromTemplate(group, locale, request, response);
            mailService.sendActivationEmail(group.getEmail(), content, locale);
        }
        log.debug("REST saved Group : {}", group);
        return new ResponseEntity<>(HttpStatus.CREATED);
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

        List<Group> groups = groupRepository.findAll();
        List<GroupDTO> dtos = new ArrayList<GroupDTO>();
        for (Group g : groups)
            dtos.add(toDto(g));

        return dtos;
    }

    /**
     * Create a group including the contact person, but without the roles of the contact person.
     *
     * @param group
     * @return
     */
    private GroupDTO toDto(Group group) {

        GroupDTO dto = new GroupDTO(group.getId(), group.isEnabled(), group.getParticipantName(), group.getEmail(),
                Mapper.user(group.getContactPerson(), null));
        return dto;
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
        Group group = groupRepository.findOne(id);
        if (group == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(toDto(group), HttpStatus.OK);
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
        groupRepository.delete(id);
    }
}
