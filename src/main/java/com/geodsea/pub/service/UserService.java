package com.geodsea.pub.service;

import com.geodsea.pub.domain.Authority;
import com.geodsea.pub.domain.PersistentToken;
import com.geodsea.pub.domain.Person;
import com.geodsea.pub.domain.util.DateConstants;
import com.geodsea.pub.repository.AuthorityRepository;
import com.geodsea.pub.repository.PersistentTokenRepository;
import com.geodsea.pub.repository.PersonRepository;
import com.geodsea.pub.security.SecurityUtils;
import com.geodsea.pub.service.util.RandomUtil;
import org.jadira.usertype.dateandtime.joda.PersistentDateTime;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    @Inject
    private AuthorityRepository authorityRepository;

    public Person activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        Person person = personRepository.getUserByActivationKey(key);

        // activate given user for the registration key.
        if (person != null) {
            if (person.getRegistrationTokenExpires().getTime() < System.currentTimeMillis())
            {
                log.info("User account {} not activated as it has expired", person);
                personRepository.delete(person);
                return null;
            }
            person.setEnabled(true);
            person.setRegistrationToken(null);
            person.setRegistrationTokenExpires(null);
            personRepository.save(person);
            log.debug("Activated user: {}", person);
        }
        else
            log.warn("Failed to identify person with the registration key: " + key);

        return person;
    }

    public Person createUserInformation(String login, String password, String firstName, String lastName, String email,
                                        String streetAddress, String langKey) {
        Person newPerson = new Person();
        String encryptedPassword = passwordEncoder.encode(password);
        newPerson.setParticipantName(login);
        // new user gets initially a generated password
        newPerson.setPassword(encryptedPassword);
        newPerson.setFirstName(firstName);
        newPerson.setLastName(lastName);
        newPerson.setEmail(email);
        newPerson.setStreetAddress(streetAddress);
        newPerson.setLangKey(langKey);
        // new user is not active
        newPerson.setEnabled(false);

        // new user gets registration key that will expire in 8 hours
        newPerson.setRegistrationTokenExpires(new Date(System.currentTimeMillis() + 8 * DateConstants.HOURS));
        newPerson.setRegistrationToken(RandomUtil.generateActivationKey());

        Authority authority = authorityRepository.findOne("ROLE_USER");
        Set<Authority> authorities = new HashSet<Authority>();
        authorities.add(authority);
        newPerson.setAuthorities(authorities);

        personRepository.save(newPerson);

        log.debug("Created Information for User: {}", newPerson);
        return newPerson;
    }

    public void updateUserInformation(String firstName, String lastName, String email, String address) {
        Person currentPerson = personRepository.getUserByParticipantName(SecurityUtils.getCurrentLogin());
        currentPerson.setFirstName(firstName);
        currentPerson.setLastName(lastName);
        currentPerson.setEmail(email);
        personRepository.save(currentPerson);
        log.debug("Changed Information for User: {}", currentPerson);
    }

    public void changePassword(String password) {
        Person currentPerson = personRepository.getUserByParticipantName(SecurityUtils.getCurrentLogin());
        String encryptedPassword = passwordEncoder.encode(password);
        currentPerson.setPassword(encryptedPassword);
        personRepository.save(currentPerson);
        log.debug("Changed password for User: {}", currentPerson);
    }

    @Transactional(readOnly = true)
    public Person getUserWithAuthorities() {
        Person currentPerson = personRepository.getUserByParticipantName(SecurityUtils.getCurrentLogin());
        currentPerson.getAuthorities().size(); // eagerly load the association
        return currentPerson;
    }

    /**
     * Persistent Token are used for providing automatic authentication, they should be automatically deleted after
     * 30 days.
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at midnight.
     * </p>
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void removeOldPersistentTokens() {
        LocalDate now = new LocalDate();
        List<PersistentToken> tokens = persistentTokenRepository.findByTokenDateBefore(now.minusMonths(1));
        for (PersistentToken token : tokens) {
            log.debug("Deleting token {}", token.getSeries());
            Person person = token.getPerson();
            person.getPersistentTokens().remove(token);
            persistentTokenRepository.delete(token);
        }
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p/>
     * <p>
     * This is scheduled to get fired everyday, at 01:00 (am).
     * </p>
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        DateTime now = new DateTime();
        List<Person> persons = personRepository.findNotActivatedUsersByCreationDateBefore(now.minusDays(3));
        for (Person person : persons) {
            log.debug("Deleting not activated user {}", person.getParticipantName());
            personRepository.delete(person);
        }
    }
}
