package com.geodsea.pub.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.geodsea.pub.domain.Member;
import com.geodsea.pub.repository.MemberRepository;
import com.geodsea.pub.service.ActionRefusedException;
import com.geodsea.pub.service.ErrorCode;
import com.geodsea.pub.service.GroupService;
import com.geodsea.pub.web.rest.dto.MemberDTO;
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
import java.util.List;

/**
 * REST controller for managing Member.
 */
@RestController
@RequestMapping("/app")
public class MemberResource {

    private final Logger log = LoggerFactory.getLogger(MemberResource.class);

    @Inject
    private GroupService groupService;

    /**
     * POST  /rest/members -> Create a new member.
     */
    @RequestMapping(value = "/rest/members",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> create(@RequestBody MemberDTO member) {
        log.debug("REST request to save Member : {}", member);
        if (member.getId() == null)
            try {
                groupService.addMember(member.getGroup().getLogin(), member.getParticipant().getLogin(),
                        member.isActive(), member.isManager(), member.getMemberSince(), member.getMemberUntil());
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (ActionRefusedException e) {
                return new ResponseEntity<String>(e.getCode(), HttpStatus.OK);
            }
        else
            try {
                groupService.updateMember(member.getId(), member.isActive(), member.isManager(), member.getMemberSince(),
                        member.getMemberUntil());
                return new ResponseEntity<>(HttpStatus.OK);
            } catch (ActionRefusedException e) {
                return new ResponseEntity<String>(e.getCode(), HttpStatus.FORBIDDEN);
            }
    }

    /**
     * GET  /rest/members -> get all the members.
     */
    @RequestMapping(value = "/rest/members",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<MemberDTO> getAll() {
        log.debug("REST request to get all Members");
        List<Member> members = groupService.getAllMembers();
        List<MemberDTO> dtos = new ArrayList<MemberDTO>(members.size());
        for (Member member : members)
            dtos.add(Mapper.member(member));
        return dtos;
    }

    /**
     * GET  /rest/members/:id -> get the "id" member.
     */
    @RequestMapping(value = "/rest/members/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get Member : {}", id);
        try {
            Member member = groupService.getMember(id);
            return new ResponseEntity<MemberDTO>(Mapper.member(member), HttpStatus.OK);
        } catch (ActionRefusedException e) {
            if (ErrorCode.NO_SUCH_MEMBER.equals(e.getCode()))
                return new ResponseEntity<String>(e.getCode(), HttpStatus.NOT_FOUND);
            else
                return new ResponseEntity<String>(e.getCode(), HttpStatus.FORBIDDEN);
        }
    }

    /**
     * DELETE  /rest/members/:id -> delete the "id" member.
     */
    @RequestMapping(value = "/rest/members/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> delete(@PathVariable Long id) {
        log.debug("REST request to delete Member : {}", id);
        try {
            groupService.deleteMember(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ActionRefusedException e) {
            if (ErrorCode.NO_SUCH_MEMBER.equals(e.getCode()))
                return new ResponseEntity<String>(e.getCode(), HttpStatus.NOT_FOUND);
            else
                return new ResponseEntity<String>(e.getCode(), HttpStatus.FORBIDDEN);
        }
    }
}
