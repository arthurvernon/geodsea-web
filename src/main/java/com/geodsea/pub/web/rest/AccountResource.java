package com.geodsea.pub.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.geodsea.pub.domain.Address;
import com.geodsea.pub.domain.Authority;
import com.geodsea.pub.domain.PersistentToken;
import com.geodsea.pub.domain.Person;
import com.geodsea.pub.repository.PersistentTokenRepository;
import com.geodsea.pub.repository.PersonRepository;
import com.geodsea.pub.security.SecurityUtils;
import com.geodsea.pub.service.ActionRefusedException;
import com.geodsea.pub.service.GisService;
import com.geodsea.pub.service.MailService;
import com.geodsea.pub.service.UserService;
import com.geodsea.pub.web.rest.dto.*;
import com.vividsolutions.jts.geom.Point;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.context.SpringWebContext;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/app")
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private ApplicationContext applicationContext;

    @Inject
    private SpringTemplateEngine templateEngine;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private UserService userService;

    @Inject
    private GisService gisService;

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Inject
    private MailService mailService;

    /**
     * POST  /rest/register -> register the user.
     */
    @RequestMapping(value = "/rest/register",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> registerAccount(@RequestBody UserDTO userDTO, HttpServletRequest request,
                                             HttpServletResponse response) {
        Person person = personRepository.getUserByParticipantName(userDTO.getLogin());
        if (person != null) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        } else {
            Point point = gisService.createPointFromLatLong(userDTO.getPoint().getLat(), userDTO.getPoint().getLon());
            Address address = new Address(userDTO.getAddress(), point);
            person = userService.createUserInformation(userDTO.getLogin(), userDTO.getPassword(),
                    userDTO.getFirstName(), userDTO.getLastName(),
                    userDTO.getEmail().toLowerCase(),
                    address,
                    userDTO.getLangKey());
            final Locale locale = Locale.forLanguageTag(person.getLangKey());
            String content = createHtmlContentFromTemplate(person, locale, request, response);
            mailService.sendActivationEmail(person.getEmail(), content, locale);
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
    }

    /**
     * GET  /rest/activate -> activate the registered user.
     */
    @RequestMapping(value = "/rest/activate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> activateAccount(@RequestParam(value = "key") String key) {
        Person person = userService.activateRegistration(key);
        if (person == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>(person.getParticipantName(), HttpStatus.OK);
    }


    /**
     * GET  /rest/authenticate -> check if the user is authenticated, and return its login.
     */
    @RequestMapping(value = "/rest/authenticate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public String isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return request.getRemoteUser();
    }

    /**
     * GET  /rest/account -> get the current user.
     */
    @RequestMapping(value = "/rest/account",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
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
                new UserDTO(
                        person.getParticipantName(),
                        null,
                        person.getFirstName(),
                        person.getLastName(),
                        person.getEmail(),
                        person.getLangKey(),
                        person.getTelephone(),
                        person.getQuestion(),
                        person.getAnswer(),
                        person.getAddress() != null ? person.getAddress().getFormatted() : null,
                        null, null,
                        roles),
                HttpStatus.OK);
    }

    /**
     * POST  /rest/account -> update the current user information.
     */
    @RequestMapping(value = "/rest/account",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void saveAccount(@RequestBody UserDTO userDTO) {
        Address address = null;
        if (userDTO.getPoint() != null) {
            Point point = gisService.createPointFromLatLong(userDTO.getPoint().getLat(), userDTO.getPoint().getLon());
            address = new Address(userDTO.getAddress(), point);
        }
        userService.updateUserInformation(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail(),
                userDTO.getTelephone(), userDTO.getQuestion(), userDTO.getAnswer(), address);

    }

    /**
     * POST  /rest/change_password -> changes the current user's password
     */
    @RequestMapping(value = "/rest/account/change_password",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeDTO change) {
        if (StringUtils.isEmpty(change.getNewPassword())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            userService.changePassword(change.getOldPassword(), change.getNewPassword());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (ActionRefusedException ex)
        {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }


    /**
     * GET  /rest/account/sessions -> get the current open sessions.
     */
    @RequestMapping(value = "/rest/account/sessions",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<PersistentToken>> getCurrentSessions() {
        Person person = personRepository.getUserByParticipantName(SecurityUtils.getCurrentLogin());
        if (person == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(
                persistentTokenRepository.findByPerson(person),
                HttpStatus.OK);
    }

    /**
     * DELETE  /rest/account/sessions?series={series} -> invalidate an existing session.
     * <p/>
     * - You can only delete your own sessions, not any other user's session
     * - If you delete one of your existing sessions, and that you are currently logged in on that session, you will
     * still be able to use that session, until you quit your browser: it does not work in real time (there is
     * no API for that), it only removes the "remember me" cookie
     * - This is also true if you invalidate your current session: you will still be able to use it until you close
     * your browser or that the session times out. But automatic login (the "remember me" cookie) will not work
     * anymore.
     * There is an API to invalidate the current session, but there is no API to check which session uses which
     * cookie.
     */
    @RequestMapping(value = "/rest/account/sessions/{series}",
            method = RequestMethod.DELETE)
    @Timed
    public void invalidateSession(@PathVariable String series) throws UnsupportedEncodingException {
        String decodedSeries = URLDecoder.decode(series, "UTF-8");
        Person person = personRepository.getUserByParticipantName(SecurityUtils.getCurrentLogin());
        List<PersistentToken> persistentTokens = persistentTokenRepository.findByPerson(person);
        for (PersistentToken persistentToken : persistentTokens) {
            if (StringUtils.equals(persistentToken.getSeries(), decodedSeries)) {
                persistentTokenRepository.delete(decodedSeries);
            }
        }
    }

    /**
     * GET  /rest/question-> activate the registered user.
     */
    @RequestMapping(value = "/rest/question/user/{user}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<QuestionDTO> getSecretQuestion(@PathVariable String user) {
        Person person = personRepository.getUserByParticipantName(user);
        if (person == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<QuestionDTO>(new QuestionDTO(person.getQuestion()), HttpStatus.OK);
    }

    /**
     * GET  /rest/question-> activate the registered user.
     */
    @RequestMapping(value = "/rest/question/answer",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> checkAnswer(@RequestBody AnswerDTO answer) {
        if (answer == null || answer.incomplete())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        Person person = personRepository.getUserByParticipantName(answer.getUsername());
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
    public ResponseEntity<?> resetPassword(@RequestBody ResetDTO reset) {
        if (reset.incomplete())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        try {
            userService.resetPassword(reset.getUsername(), reset.getQuestion(), reset.getAnswer(), reset.getPassword());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (ActionRefusedException ex){
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }


    private String createHtmlContentFromTemplate(final Person person, final Locale locale, final HttpServletRequest request,
                                                 final HttpServletResponse response) {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("user", person);
        variables.put("baseUrl", request.getScheme() + "://" +   // "http" + "://
                request.getServerName() +       // "myhost"
                ":" + request.getServerPort());
        IWebContext context = new SpringWebContext(request, response, servletContext,
                locale, variables, applicationContext);
        return templateEngine.process(MailService.EMAIL_ACTIVATION_PREFIX + MailService.TEMPLATE_SUFFIX, context);
    }
}
