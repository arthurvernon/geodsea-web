package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Group;
import com.geodsea.pub.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for friends (group).
 */
public interface GroupRepository extends JpaRepository<Group, Long> {

    Group findByLogin(String groupName);


}
