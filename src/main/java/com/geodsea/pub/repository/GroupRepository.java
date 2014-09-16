package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the groups.
 */
public interface GroupRepository extends JpaRepository<Group, Long> {


    Group findByParticipantName(String groupName);
}
