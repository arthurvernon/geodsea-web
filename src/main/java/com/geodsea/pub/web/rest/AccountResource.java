package com.geodsea.pub.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.geodsea.common.dto.*;
import com.geodsea.pub.domain.*;
import com.geodsea.pub.repository.ParticipantRepository;
import com.geodsea.pub.repository.PersistentTokenRepository;
import com.geodsea.pub.repository.PersonRepository;
import com.geodsea.pub.security.SecurityUtils;
import com.geodsea.pub.service.*;
import com.geodsea.pub.web.rest.mapper.Mapper;
import com.vividsolutions.jts.geom.Point;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
@Api(value = "Account", description = "User Account API")
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    @Inject
    private ParticipantService participantService;

    @Inject
    private UserService userService;


    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Inject
    private MailService mailService;

    /**
     * POST  /register -> register the user.
     */
    @RequestMapping(value = "/register",
            method = RequestMethod.POST,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    @ApiOperation(value = "Register User", notes = "Create a user account that is yet to be enabled")
    public ResponseEntity<?> registerAccount(@Valid @RequestBody UserDTO userDTO, HttpServletRequest request) {

        if (participantService.nameInUse(userDTO.getLogin()))
            // ensure that there is no other participant (person or group) with the same name
            return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("login already in use");
        else {

            if (participantService.emailInUse(userDTO.getEmail())) {
                return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("e-mail address already in use");
            }

            Point point = GisService.createPointFromLatLong(userDTO.getPoint().getLat(), userDTO.getPoint().getLon());
            Address address = new Address(userDTO.getAddress(), point);
            Person person = userService.createUserInformation(userDTO.getLogin(), userDTO.getPassword(),
                userDTO.getFirstName(), userDTO.getLastName(),
                userDTO.getEmail().toLowerCase(),
                address,
                userDTO.getLangKey(), userDTO.getQuestion(), userDTO.getAnswer());

            // "http" "://" "myhost" ":" "80"
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();

            mailService.sendActivationEmail(person, baseUrl);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }
    /**
     * GET  /activate -> activate the registered user.
     */
    @RequestMapping(value = "/activate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "Activate Account", notes = "Submit a registration key to activate a user account")
    public ResponseEntity<String> activateAccount(@RequestParam(value = "key") String key) {
        Participant participant = participantService.activateRegistration(key);
        if (participant == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(participant.getLogin(), HttpStatus.OK);
    }

    /**
     * GET  /authenticate -> check if the user is authenticated, and return its login.
     */
    @RequestMapping(value = "/authenticate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "Authenticated User", notes = "Provide the name of the user who is currently logged on")
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * GET  /account -> get the current user.
     */
    @RequestMapping(value = "/account",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "Get User", notes = "Get the details of the user who is currently logged on")
    public ResponseEntity<UserDTO> getAccount() {
        Person person = userService.getUserWithAuthorities();
        if (person == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        List<String> roles = new ArrayList<>();
        for (Authority authority : person.getAuthorities()) {
            roles.add(authority.getName());
        }
        return new ResponseEntity<>(
                Mapper.user(person, roles),
                HttpStatus.OK);
    }

    /**
     * POST  /account -> update the current user information.
     */
    @RequestMapping(value = "/account",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "Update User", notes = "Update the details of the user including his address")
    public ResponseEntity<String> saveAccount(@RequestBody UserDTO userDTO) {
        Participant userHavingThisLogin = participantService.retrieveParticipantByLogin(userDTO.getLogin());
        if (userHavingThisLogin != null && !userHavingThisLogin.getLogin().equals(SecurityUtils.getCurrentLogin())) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Address address = null;
        if (userDTO.getPoint() != null) {
            Point point = GisService.createPointFromLatLong(userDTO.getPoint().getLat(), userDTO.getPoint().getLon());
            address = new Address(userDTO.getAddress(), point);
        }
        userService.updateUserInformation(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
                userDTO.getTelephone(), userDTO.getQuestion(), userDTO.getAnswer(), address);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    /**
     * POST  /change_password -> changes the current user's password
     */
    @RequestMapping(value = "/account/change_password",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "Change Password", notes = "Update the user's password.")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeDTO change) {

        final String pw = change.getNewPassword();
        if (StringUtils.isEmpty(pw) || pw.length() < 5 || pw.length() > 50) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try {
            userService.changePassword(change.getOldPassword(), change.getNewPassword());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ActionRefusedException ex) {
            return new ResponseEntity<String>(ex.getCode(), HttpStatus.CONFLICT);
        }
    }

    /**
     * GET  /account/sessions -> get the current open sessions.
     */
    @RequestMapping(value = "/account/sessions",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "Session Info", notes = "Retrieve details of the active sessions for the user.")
    public ResponseEntity<List<PersistentToken>> getCurrentSessions() {
        Person person = userService.retrievePersonByLogin(SecurityUtils.getCurrentLogin());
        if (person == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(
                persistentTokenRepository.findByPerson(person),
                HttpStatus.OK);
    }

    /**
     * DELETE  /account/sessions?series={series} -> invalidate an existing session.
     *
     * - You can only delete your own sessions, not any other user's session
     * - If you delete one of your existing sessions, and that you are currently logged in on that session, you will
     *   still be able to use that session, until you quit your browser: it does not work in real time (there is
     *   no API for that), it only removes the "remember me" cookie
     * - This is also true if you invalidate your current session: you will still be able to use it until you close
     *   your browser or that the session times out. But automatic login (the "remember me" cookie) will not work
     *   anymore.
     *   There is an API to invalidate the current session, but there is no API to check which session uses which
     *   cookie.
     */
    @RequestMapping(value = "/account/sessions/{series}",
            method = RequestMethod.DELETE)
    @Timed
    @ApiOperation(value = "Invalidate Session", notes = "Remove any existing token for the current session.")
    public void invalidateSession(@PathVariable String series) throws UnsupportedEncodingException {
        String decodedSeries = URLDecoder.decode(series, "UTF-8");
        Person person = userService.retrievePersonByLogin(SecurityUtils.getCurrentLogin());
        List<PersistentToken> persistentTokens = persistentTokenRepository.findByPerson(person);
        for (PersistentToken persistentToken : persistentTokens) {
            if (StringUtils.equals(persistentToken.getSeries(), decodedSeries)) {
                persistentTokenRepository.delete(decodedSeries);
            }
        }
    }

    /**
     * GET  /rest/question-> get the question to pose to the user.
     */
    @RequestMapping(value = "/rest/question/user/{user}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "Get Question", notes = "Obtain a question to answer in order to reset the user's password.")
    public ResponseEntity<QuestionDTO> getSecretQuestion(@PathVariable String user) {
        Person person = userService.retrievePersonByLogin(user);
        if (person == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<QuestionDTO>(new QuestionDTO(person.getQuestion()), HttpStatus.OK);
    }

    /**
     * GET  /rest/question/answer-> check the answer presented by the user to the question.
     */
    @RequestMapping(value = "/rest/question/answer",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "Check Answer", notes = "Check the answer to the question and if correct, the user can then reset the password.")
    public ResponseEntity<?> checkAnswer(@RequestBody AnswerDTO answer) {
        if (answer == null || answer.incomplete())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Person person = userService.retrievePersonByLogin(answer.getUsername());
        if (person == null) {
            log.debug("no such person");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (answer.getQuestion().equals(person.getQuestion()) &&
                answer.getAnswer().equalsIgnoreCase(person.getAnswer()))
            return new ResponseEntity<>(null, HttpStatus.OK);
        else {
            log.debug("Unexpected question or answer");
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Reset the current user's password if the question and corresponding answers are correct.
     * POST  /rest/question/reset->
     */
    @RequestMapping(value = "/rest/question/reset",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "Reset Password", notes = "Submit the correct answer and the new password for the user.")
    public ResponseEntity<?> resetPassword(@RequestBody ResetDTO reset) {
        if (reset.incomplete())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        try {
            userService.resetPassword(reset.getUsername(), reset.getQuestion(), reset.getAnswer(), reset.getPassword());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ActionRefusedException ex) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }


}
