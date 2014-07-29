package com.geodsea.pub.service;

import com.geodsea.pub.domain.Person;
import com.geodsea.pub.domain.type.RoleType;
import com.geodsea.pub.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Frequently used utility methods for a service
 */
public class BaseService {

    @Autowired
    protected PersonRepository personRepository;


    public Person getPersonForPrincipal() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        return getPersonForParticipantName(username);
    }

    public String getUsernameForPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) auth.getPrincipal()).getUsername();
        } else {
            return auth.getPrincipal().toString();
        }
    }

    protected boolean userHasRole(RoleType role)
    {

        // get security context from thread local
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null)
            return false;

        Authentication authentication = context.getAuthentication();
        if (authentication == null)
            return false;

        for (GrantedAuthority auth : authentication.getAuthorities()) {
            if (role.toString().equals(auth.getAuthority()))
                return true;
        }

        return false;
    }

    public Person getPersonForParticipantName(String username)
    {
        return personRepository.getUserByParticipantName(username);
    }

}
