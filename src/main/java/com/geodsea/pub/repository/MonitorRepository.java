package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Monitor;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Skipper entity.
 */
public interface MonitorRepository extends JpaRepository<Monitor, Long> {

    Monitor findByParticipantParticipantName(String participantName);

}
