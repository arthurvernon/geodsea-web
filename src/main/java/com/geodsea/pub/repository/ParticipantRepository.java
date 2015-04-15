package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Participant;
import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the groups.
 */
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Participant getParticipantByLogin(String login);

    @Query("select u from Participant u where u.enabled = false and u.registrationTokenExpires is not null and u.createdDate > ?1")
    List<Participant> findNotActivatedUsersByCreationDateBefore(DateTime before);

    @Query("select u from Participant u where u.registrationToken = ?1")
    Participant getByActivationKey(String registrationToken);

    @Query("select p from Participant p where p.id in ?1")
    List<Participant> getParticipantsForIDs(long[] participantIds);

    Participant getParticipantByEmail(String email);
}
