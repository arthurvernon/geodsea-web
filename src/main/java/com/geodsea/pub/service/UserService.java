package com.geodsea.pub.service;

import com.geodsea.pub.domain.*;
import com.geodsea.pub.repository.AuthorityRepository;
import com.geodsea.pub.repository.PersonRepository;
import com.geodsea.pub.security.AuthoritiesConstants;
import com.geodsea.pub.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Service class for managing users.
 */
@Service
@Transactional(rollbackFor = {ActionRefusedException.class})
public class UserService  {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private PersonRepository personRepository;


    @Inject
    private AuthorityRepository authorityRepository;


    public Person createUserInformation(String login, String password, String firstName, String lastName, String email,
                                        Address address, String langKey, String question, String answer) {
        Person newPerson = new Person();
        String encryptedPassword = passwordEncoder.encode(password);
        newPerson.setLogin(login);
        // new user gets initially a generated password
        newPerson.setPassword(encryptedPassword);
        newPerson.setFirstName(firstName);
        newPerson.setLastName(lastName);
        newPerson.setEmail(email);
        newPerson.setAddress(address);
        newPerson.setLangKey(langKey);
        newPerson.setQuestion(question);
        newPerson.setAnswer(answer);

        // new user is not active
        newPerson.setEnabled(false);

        ParticipantService.addRegistrationToken(newPerson);

        Authority authority = authorityRepository.findOne(AuthoritiesConstants.USER);
        newPerson.addAuthority(authority);

        personRepository.save(newPerson);

        log.debug("Created Information for User: {}", newPerson);
        return newPerson;
    }


    public void updateUserInformation(String firstName, String lastName, String email, String telephone, String question,
                                      String answer, Address address) {
        Person currentPerson = personRepository.getByLogin(SecurityUtils.getCurrentLogin());
        currentPerson.setFirstName(firstName);
        currentPerson.setLastName(lastName);
        currentPerson.setEmail(email);
        currentPerson.setTelephone(telephone);
        currentPerson.setQuestion(question);
        currentPerson.setAnswer(answer);

        // Only update address if there was a change. No change might imply no point details!
        if (address != null && !address.equals(currentPerson.getAddress()))
            currentPerson.setAddress(address);

        personRepository.save(currentPerson);
        log.debug("Changed Information for User: {}", currentPerson);
    }

    /**
     * Update the password, but only if the user supplies the correct existing password.
     * @param oldPassword current user's password
     * @param newPassword new password to store.
     * @throws ActionRefusedException if the oldPassword supplied is not the current user's password.
     */
    public void changePassword(String oldPassword, String newPassword) throws ActionRefusedException {
        Person currentPerson = personRepository.getByLogin(SecurityUtils.getCurrentLogin());

        String dbValue = currentPerson.getPassword();

        if (passwordEncoder.matches(oldPassword, dbValue))
        {
            String encryptedPassword = passwordEncoder.encode(newPassword);
            currentPerson.setPassword(encryptedPassword);
            personRepository.save(currentPerson);
            log.debug("Changed password for User: {}", currentPerson);
        }
        else
            throw new ActionRefusedException(ErrorCode.PASSWORD_MISMATCH, "Old password is incorrect");
    }

    /**
     * Reset the password to the new password when the right answer is provided for the question.
     * @param question the question that was asked.
     * @param answer the answer to the question posed
     * @param password the password to apply to this account
     * @throws ActionRefusedException if the answer is incorrect.
     */
    public void resetPassword(String user, String question, String answer, String password) throws ActionRefusedException {
        Person person = personRepository.getByLogin(user);
        if (person == null) {
            log.info("Reset Password: No such user: {}", user);
            throw new ActionRefusedException(ErrorCode.NO_SUCH_USER, "No such user: " + user);
        }
        if (person.getQuestion().equals(question) && person.getAnswer().equalsIgnoreCase(answer)) {
            String encryptedPassword = passwordEncoder.encode(password);
            person.setPassword(encryptedPassword);
            personRepository.save(person);
            log.debug("ResetPassword: Changed password for User: {}", person);
        }
        else
            throw new ActionRefusedException(ErrorCode.INCORRECT_ANSWER, "Incorrect answer");
    }

    @Transactional(readOnly = true)
    public Person getUserWithAuthorities() {
        Person currentPerson = personRepository.getByLogin(SecurityUtils.getCurrentLogin());
        currentPerson.getAuthorities().size(); // eagerly load the association
        return currentPerson;
    }

    public Person retrievePersonByLogin(String login) {
        return personRepository.getByLogin(login);
    }
}
