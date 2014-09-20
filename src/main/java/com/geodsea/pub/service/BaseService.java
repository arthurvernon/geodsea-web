package com.geodsea.pub.service;

import com.geodsea.pub.domain.Person;
import com.geodsea.pub.repository.PersonRepository;
import com.geodsea.pub.security.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Frequently used utility methods for a service
 */
public class BaseService {

    @Autowired
    protected PersonRepository personRepository;


    /**
     *
     * @return the person or null if no user is logged on.
     */
    public Person getPersonForPrincipal() {
        String username = SecurityUtils.getCurrentLogin();
        return getPersonForLogin(username);
    }

    public Person getPersonForLogin(String login)
    {
        return personRepository.getByLogin(login);
    }

}
