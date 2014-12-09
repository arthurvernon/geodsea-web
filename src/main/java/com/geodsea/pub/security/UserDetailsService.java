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
 * TODO figure out why there is an endless loop as follows:
 [DEBUG] com.geodsea.pub.security.CustomPersistentRememberMeServices - Cancelling cookie
 [DEBUG] com.geodsea.pub.security.UserDetailsService - Authenticating user
 Hibernate: select person0_.ID as ID1_16_, person0_1_.created_by as created_2_16_, person0_1_.created_date as created_3_16_, person0_1_.last_modified_by as last_mod4_16_, person0_1_.last_modified_date as last_mod5_16_, person0_1_.email as email6_16_, person0_1_.ENABLED as ENABLED7_16_, person0_1_.lang_key as lang_key8_16_, person0_1_.LOGIN as LOGIN9_16_, person0_1_.REGISTRATION_TOKEN as REGISTR10_16_, person0_1_.REGISTRATION_TOKEN_EXPIRES as REGISTR11_16_, person0_.ADDRESS_FORMATTED as ADDRESS_1_19_, person0_.ADDRESS_POINT as ADDRESS_2_19_, person0_.answer as answer3_19_, person0_.BIRTH_DATE as BIRTH_DA4_19_, person0_.first_name as first_na5_19_, person0_.last_name as last_nam6_19_, person0_.password as password7_19_, person0_.question as question8_19_, person0_.TELEPHONE as TELEPHON9_19_ from BOAT.T_PERSON person0_ inner join BOAT.T_PARTICIPANT person0_1_ on person0_.ID=person0_1_.ID where person0_1_.LOGIN=?
 Hibernate: select authoritie0_.participant_id as particip1_16_0_, authoritie0_.name as name2_17_0_, authority1_.name as name1_2_1_ from BOAT.T_PARTICIPANT_AUTHORITY authoritie0_ inner join BOAT.T_AUTHORITY authority1_ on authoritie0_.name=authority1_.name where authoritie0_.participant_id=?
 Hibernate: select member0_.id as id1_12_, member0_.created_by as created_2_12_, member0_.created_date as created_3_12_, member0_.last_modified_by as last_mod4_12_, member0_.last_modified_date as last_mod5_12_, member0_.ACTIVE as ACTIVE6_12_, member0_.COLLECTIVE_FK as COLLECT10_12_, member0_.MANAGER as MANAGER7_12_, member0_.MEMBER_SINCE as MEMBER_S8_12_, member0_.MEMBER_UNTIL as MEMBER_U9_12_, member0_.PARTICIPANT_FK as PARTICI11_12_ from BOAT.T_MEMBER member0_ where member0_.PARTICIPANT_FK=? and member0_.ACTIVE=true
 Hibernate: select collective0_.COLLECTIVE_ID as ID1_16_0_, collective0_1_.created_by as created_2_16_0_, collective0_1_.created_date as created_3_16_0_, collective0_1_.last_modified_by as last_mod4_16_0_, collective0_1_.last_modified_date as last_mod5_16_0_, collective0_1_.email as email6_16_0_, collective0_1_.ENABLED as ENABLED7_16_0_, collective0_1_.lang_key as lang_key8_16_0_, collective0_1_.LOGIN as LOGIN9_16_0_, collective0_1_.REGISTRATION_TOKEN as REGISTR10_16_0_, collective0_1_.REGISTRATION_TOKEN_EXPIRES as REGISTR11_16_0_, collective0_.COLLECTIVE_NAME as COLLECTI1_4_0_, collective0_.CONTACT_PERSON_FK as CONTACT_3_4_0_, collective0_2_.ADDRESS_FORMATTED as ADDRESS_1_14_0_, collective0_2_.ADDRESS_POINT as ADDRESS_2_14_0_, collective0_2_.TELEPHONE as TELEPHON3_14_0_, collective0_2_.WEBSITE_URL as WEBSITE_4_14_0_, case when collective0_2_.ORGANISATION_ID is not null then 2 when collective0_3_.GROUP_ID is not null then 4 when collective0_.COLLECTIVE_ID is not null then 1 end as clazz_0_, person1_.ID as ID1_16_1_, person1_1_.created_by as created_2_16_1_, person1_1_.created_date as created_3_16_1_, person1_1_.last_modified_by as last_mod4_16_1_, person1_1_.last_modified_date as last_mod5_16_1_, person1_1_.email as email6_16_1_, person1_1_.ENABLED as ENABLED7_16_1_, person1_1_.lang_key as lang_key8_16_1_, person1_1_.LOGIN as LOGIN9_16_1_, person1_1_.REGISTRATION_TOKEN as REGISTR10_16_1_, person1_1_.REGISTRATION_TOKEN_EXPIRES as REGISTR11_16_1_, person1_.ADDRESS_FORMATTED as ADDRESS_1_19_1_, person1_.ADDRESS_POINT as ADDRESS_2_19_1_, person1_.answer as answer3_19_1_, person1_.BIRTH_DATE as BIRTH_DA4_19_1_, person1_.first_name as first_na5_19_1_, person1_.last_name as last_nam6_19_1_, person1_.password as password7_19_1_, person1_.question as question8_19_1_, person1_.TELEPHONE as TELEPHON9_19_1_ from BOAT.T_COLLECTIVE collective0_ inner join BOAT.T_PARTICIPANT collective0_1_ on collective0_.COLLECTIVE_ID=collective0_1_.ID left outer join BOAT.T_ORGANISATION collective0_2_ on collective0_.COLLECTIVE_ID=collective0_2_.ORGANISATION_ID left outer join BOAT.T_GROUP collective0_3_ on collective0_.COLLECTIVE_ID=collective0_3_.GROUP_ID left outer join BOAT.T_PERSON person1_ on collective0_.CONTACT_PERSON_FK=person1_.ID left outer join BOAT.T_PARTICIPANT person1_1_ on person1_.ID=person1_1_.ID where collective0_.COLLECTIVE_ID=?
 Hibernate: select collective0_.COLLECTIVE_ID as ID1_16_0_, collective0_1_.created_by as created_2_16_0_, collective0_1_.created_date as created_3_16_0_, collective0_1_.last_modified_by as last_mod4_16_0_, collective0_1_.last_modified_date as last_mod5_16_0_, collective0_1_.email as email6_16_0_, collective0_1_.ENABLED as ENABLED7_16_0_, collective0_1_.lang_key as lang_key8_16_0_, collective0_1_.LOGIN as LOGIN9_16_0_, collective0_1_.REGISTRATION_TOKEN as REGISTR10_16_0_, collective0_1_.REGISTRATION_TOKEN_EXPIRES as REGISTR11_16_0_, collective0_.COLLECTIVE_NAME as COLLECTI1_4_0_, collective0_.CONTACT_PERSON_FK as CONTACT_3_4_0_, collective0_2_.ADDRESS_FORMATTED as ADDRESS_1_14_0_, collective0_2_.ADDRESS_POINT as ADDRESS_2_14_0_, collective0_2_.TELEPHONE as TELEPHON3_14_0_, collective0_2_.WEBSITE_URL as WEBSITE_4_14_0_, case when collective0_2_.ORGANISATION_ID is not null then 2 when collective0_3_.GROUP_ID is not null then 4 when collective0_.COLLECTIVE_ID is not null then 1 end as clazz_0_, person1_.ID as ID1_16_1_, person1_1_.created_by as created_2_16_1_, person1_1_.created_date as created_3_16_1_, person1_1_.last_modified_by as last_mod4_16_1_, person1_1_.last_modified_date as last_mod5_16_1_, person1_1_.email as email6_16_1_, person1_1_.ENABLED as ENABLED7_16_1_, person1_1_.lang_key as lang_key8_16_1_, person1_1_.LOGIN as LOGIN9_16_1_, person1_1_.REGISTRATION_TOKEN as REGISTR10_16_1_, person1_1_.REGISTRATION_TOKEN_EXPIRES as REGISTR11_16_1_, person1_.ADDRESS_FORMATTED as ADDRESS_1_19_1_, person1_.ADDRESS_POINT as ADDRESS_2_19_1_, person1_.answer as answer3_19_1_, person1_.BIRTH_DATE as BIRTH_DA4_19_1_, person1_.first_name as first_na5_19_1_, person1_.last_name as last_nam6_19_1_, person1_.password as password7_19_1_, person1_.question as question8_19_1_, person1_.TELEPHONE as TELEPHON9_19_1_ from BOAT.T_COLLECTIVE collective0_ inner join BOAT.T_PARTICIPANT collective0_1_ on collective0_.COLLECTIVE_ID=collective0_1_.ID left outer join BOAT.T_ORGANISATION collective0_2_ on collective0_.COLLECTIVE_ID=collective0_2_.ORGANISATION_ID left outer join BOAT.T_GROUP collective0_3_ on collective0_.COLLECTIVE_ID=collective0_3_.GROUP_ID left outer join BOAT.T_PERSON person1_ on collective0_.CONTACT_PERSON_FK=person1_.ID left outer join BOAT.T_PARTICIPANT person1_1_ on person1_.ID=person1_1_.ID where collective0_.COLLECTIVE_ID=?
 Hibernate: select collective0_.COLLECTIVE_ID as ID1_16_0_, collective0_1_.created_by as created_2_16_0_, collective0_1_.created_date as created_3_16_0_, collective0_1_.last_modified_by as last_mod4_16_0_, collective0_1_.last_modified_date as last_mod5_16_0_, collective0_1_.email as email6_16_0_, collective0_1_.ENABLED as ENABLED7_16_0_, collective0_1_.lang_key as lang_key8_16_0_, collective0_1_.LOGIN as LOGIN9_16_0_, collective0_1_.REGISTRATION_TOKEN as REGISTR10_16_0_, collective0_1_.REGISTRATION_TOKEN_EXPIRES as REGISTR11_16_0_, collective0_.COLLECTIVE_NAME as COLLECTI1_4_0_, collective0_.CONTACT_PERSON_FK as CONTACT_3_4_0_, collective0_2_.ADDRESS_FORMATTED as ADDRESS_1_14_0_, collective0_2_.ADDRESS_POINT as ADDRESS_2_14_0_, collective0_2_.TELEPHONE as TELEPHON3_14_0_, collective0_2_.WEBSITE_URL as WEBSITE_4_14_0_, case when collective0_2_.ORGANISATION_ID is not null then 2 when collective0_3_.GROUP_ID is not null then 4 when collective0_.COLLECTIVE_ID is not null then 1 end as clazz_0_, person1_.ID as ID1_16_1_, person1_1_.created_by as created_2_16_1_, person1_1_.created_date as created_3_16_1_, person1_1_.last_modified_by as last_mod4_16_1_, person1_1_.last_modified_date as last_mod5_16_1_, person1_1_.email as email6_16_1_, person1_1_.ENABLED as ENABLED7_16_1_, person1_1_.lang_key as lang_key8_16_1_, person1_1_.LOGIN as LOGIN9_16_1_, person1_1_.REGISTRATION_TOKEN as REGISTR10_16_1_, person1_1_.REGISTRATION_TOKEN_EXPIRES as REGISTR11_16_1_, person1_.ADDRESS_FORMATTED as ADDRESS_1_19_1_, person1_.ADDRESS_POINT as ADDRESS_2_19_1_, person1_.answer as answer3_19_1_, person1_.BIRTH_DATE as BIRTH_DA4_19_1_, person1_.first_name as first_na5_19_1_, person1_.last_name as last_nam6_19_1_, person1_.password as password7_19_1_, person1_.question as question8_19_1_, person1_.TELEPHONE as TELEPHON9_19_1_ from BOAT.T_COLLECTIVE collective0_ inner join BOAT.T_PARTICIPANT collective0_1_ on collective0_.COLLECTIVE_ID=collective0_1_.ID left outer join BOAT.T_ORGANISATION collective0_2_ on collective0_.COLLECTIVE_ID=collective0_2_.ORGANISATION_ID left outer join BOAT.T_GROUP collective0_3_ on collective0_.COLLECTIVE_ID=collective0_3_.GROUP_ID left outer join BOAT.T_PERSON person1_ on collective0_.CONTACT_PERSON_FK=person1_.ID left outer join BOAT.T_PARTICIPANT person1_1_ on person1_.ID=person1_1_.ID where collective0_.COLLECTIVE_ID=?
 Hibernate: select authoritie0_.participant_id as particip1_16_0_, authoritie0_.name as name2_17_0_, authority1_.name as name1_2_1_ from BOAT.T_PARTICIPANT_AUTHORITY authoritie0_ inner join BOAT.T_AUTHORITY authority1_ on authoritie0_.name=authority1_.name where authoritie0_.participant_id=?
 Hibernate: select authoritie0_.participant_id as particip1_16_0_, authoritie0_.name as name2_17_0_, authority1_.name as name1_2_1_ from BOAT.T_PARTICIPANT_AUTHORITY authoritie0_ inner join BOAT.T_AUTHORITY authority1_ on authoritie0_.name=authority1_.name where authoritie0_.participant_id=?
 Hibernate: select authoritie0_.participant_id as particip1_16_0_, authoritie0_.name as name2_17_0_, authority1_.name as name1_2_1_ from BOAT.T_PARTICIPANT_AUTHORITY authoritie0_ inner join BOAT.T_AUTHORITY authority1_ on authoritie0_.name=authority1_.name where authoritie0_.participant_id=?
 [DEBUG] com.geodsea.pub.aop.logging.LoggingAspect - Enter: org.springframework.boot.actuate.audit.AuditEventRepository.add() with argument[s] = [AuditEvent [timestamp=Wed Dec 10 05:10:29 WST 2014, principal=user, type=AUTHENTICATION_FAILURE, data={message=Bad credentials, type=org.springframework.security.authentication.BadCredentialsException}]]
 Hibernate: insert into BOAT.T_AUDIT_EVENT (event_date, event_type, principal, event_id) values (?, ?, ?, ?)
 Hibernate: insert into BOAT.T_AUDIT_EVENT_DATA (event_id, name, value) values (?, ?, ?)
 Hibernate: insert into BOAT.T_AUDIT_EVENT_DATA (event_id, name, value) values (?, ?, ?)
 [DEBUG] com.geodsea.pub.aop.logging.LoggingAspect - Exit: org.springframework.boot.actuate.audit.AuditEventRepository.add() with result = null
 [DEBUG] com.geodsea.pub.security.CustomPersistentRememberMeServices - Interactive login attempt was unsuccessful.
 [DEBUG] com.geodsea.pub.security.CustomPersistentRememberMeServices - Cancelling cookie
 *
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
