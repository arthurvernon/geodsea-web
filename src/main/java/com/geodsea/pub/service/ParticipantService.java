package com.geodsea.pub.service;

import com.geodsea.pub.domain.Participant;
import com.geodsea.pub.domain.PersistentToken;
import com.geodsea.pub.domain.Person;
import com.geodsea.pub.domain.util.DateConstants;
import com.geodsea.pub.repository.ParticipantRepository;
import com.geodsea.pub.repository.PersistentTokenRepository;
import com.geodsea.pub.service.util.RandomUtil;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

/**
 * Created by Arthur Vernon on 6/09/2014.
 */
@Service
@Transactional
public class ParticipantService {

    private final Logger log = LoggerFactory.getLogger(ParticipantService.class);

    @Inject
    private ParticipantRepository participantRepository;

    @Inject
    private PersistentTokenRepository persistentTokenRepository;

    public static void addRegistrationToken(Participant participant) {
        // new user gets registration key that will expire in 8 hours
        participant.setRegistrationTokenExpires(new Date(System.currentTimeMillis() + 8 * DateConstants.HOURS));
        participant.setRegistrationToken(RandomUtil.generateActivationKey());
    }

    public Participant activateRegistration(String key) {
        log.debug("Activating user for activation key {}", key);
        Participant participant = participantRepository.getByActivationKey(key);

        // activate given user for the registration key.
        if (participant != null) {
            if (participant.getRegistrationTokenExpires().getTime() < System.currentTimeMillis()) {
                log.info("User account {} not activated as it has expired", participant);
                participantRepository.delete(participant);
                return null;
            }
            participant.setEnabled(true);
            participant.setRegistrationToken(null);
            participant.setRegistrationTokenExpires(null);
            participantRepository.save(participant);
            log.debug("Activated user: {}", participant);
        } else
            log.warn("Failed to identify person with the registration key: " + key);

        return participant;
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
        List<Participant> paticipants = participantRepository.findNotActivatedUsersByCreationDateBefore(now.minusDays(3));
        for (Participant participant : paticipants) {
            log.debug("Deleting not activated participant {}", participant.getParticipantName());
            participantRepository.delete(participant);
        }
    }

    /**
     * Check to see if a particular name is already in use.
     * @param participantName
     * @return
     */
    public boolean nameInUse(String participantName) {
        return participantRepository.getParticipantByParticipantName(participantName) != null;
    }
}
