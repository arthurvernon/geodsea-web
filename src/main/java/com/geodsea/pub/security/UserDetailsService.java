package com.geodsea.pub.security;

import com.geodsea.pub.domain.Authority;
import com.geodsea.pub.domain.Collective;
import com.geodsea.pub.domain.Member;
import com.geodsea.pub.domain.Person;
import com.geodsea.pub.repository.CollectiveRepository;
import com.geodsea.pub.repository.MemberRepository;
import com.geodsea.pub.repository.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

/**
 * Authenticate a user from the database.
 * <p>
 *     Roles are derived from the person plus those roles that a collective has where the person is an active member of
 *     the group.
 *     Clients will need to enforce finer grained control over actions where the person would need to be a manager
 *     within the group to perform certain actions.
 * </p>
 */
@Component("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final Logger log = LoggerFactory.getLogger(UserDetailsService.class);

    @Inject
    private PersonRepository personRepository;

    @Inject
    private MemberRepository memberRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        String lowercaseLogin = login.toLowerCase();

        Person personFromDatabase = personRepository.getByLogin(lowercaseLogin);
        if (personFromDatabase == null) {
            throw new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database");
        } else if (!personFromDatabase.isEnabled()) {
            throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
        }

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        // add authorities granted to the specific user
        for (Authority authority : personFromDatabase.getAuthorities()) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority.getName());
            grantedAuthorities.add(grantedAuthority);
        }

        // add those granted to this person by way of his membership in an organisation.
        List<Member> members = memberRepository.findWherePersonIsActiveMember(personFromDatabase.getId());
        for (Member member : members) {

//            // the authorities the member has in his/own right
//            for (Authority authority : member.getAuthorities()) {
//                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority.getName());
//                grantedAuthorities.add(grantedAuthority);
//            }

            // the authorities the member has by being in the group
            for (Authority authority : member.getCollective().getAuthorities()) {
                GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority.getName());
                grantedAuthorities.add(grantedAuthority);
            }
        }


        return new org.springframework.security.core.userdetails.User(lowercaseLogin, personFromDatabase.getPassword(),
                grantedAuthorities);
    }
}
