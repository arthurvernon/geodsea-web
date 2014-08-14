package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Participant;
import com.geodsea.pub.domain.ParticipantGroup;
import com.geodsea.pub.domain.Person;
import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface ParticipantGroupRepository extends JpaRepository<ParticipantGroup, Long> {


}
