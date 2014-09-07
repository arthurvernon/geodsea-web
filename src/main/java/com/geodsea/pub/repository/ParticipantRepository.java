package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Group;
import com.geodsea.pub.domain.Participant;
import com.geodsea.pub.domain.Person;
import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the groups.
 */
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Participant getParticipantByParticipantName(String participantName);

    @Query("select u from Participant u where u.enabled = false and u.registrationTokenExpires is not null and u.createdDate > ?1")
    List<Participant> findNotActivatedUsersByCreationDateBefore(DateTime before);

    @Query("select u from Participant u where u.registrationToken = ?1")
    Participant getByActivationKey(String registrationToken);

}
