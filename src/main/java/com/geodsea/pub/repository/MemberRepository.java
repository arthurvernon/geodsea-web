package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Group;
import com.geodsea.pub.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the groups.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member getMemberByGroupIdAndParticipantParticipantName(long groupId, String participantName);


}
