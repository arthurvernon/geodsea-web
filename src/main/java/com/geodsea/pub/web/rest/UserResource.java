package com.geodsea.pub.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.geodsea.pub.domain.Address;
import com.geodsea.pub.domain.Licensor;
import com.geodsea.pub.domain.Person;
import com.geodsea.pub.repository.PersonRepository;
import com.geodsea.pub.security.AuthoritiesConstants;
import com.geodsea.pub.service.LicenseService;
import com.geodsea.pub.web.rest.dto.LicensorDTO;
import com.geodsea.pub.web.rest.mapper.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/app")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Inject
    private PersonRepository personRepository;

    @Inject
    private LicenseService licenseService;

    /**
     * GET  /rest/users/:login -> get the "login" user.
     */
    @RequestMapping(value = "/rest/users/{login}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public Person getUser(@PathVariable String login, HttpServletResponse response) {
        log.debug("REST request to get User : {}", login);
        Person person = personRepository.getUserByParticipantName(login);
        if (person == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
        return person;
    }

    /**
     * Get the licencing agency that covers the user's home address.
     * GET  /rest/users/:username/licensor  -> get the "id" licensor.
     */
    @RequestMapping(value = "/rest/users/{username}/licensor",
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
        LicensorDTO licensorDTO = Mapper.licensor(l);

        return new ResponseEntity<>(licensorDTO, HttpStatus.OK);
    }

}
