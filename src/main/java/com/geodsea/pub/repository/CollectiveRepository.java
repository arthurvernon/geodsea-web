package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Collective;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for friends (group).
 */
public interface CollectiveRepository extends JpaRepository<Collective, Long> {
    Collective findByParticipantName(String groupName);

}
