package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Member;
import com.geodsea.pub.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the groups.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member getMemberByCollectiveIdAndParticipantLogin(long collectiveId, String login);

    @Query("select distinct m from Member m where m.collective in "+
            "(select c from Collective c where exists " +
            "(select m2 from Member m2 where m2.participant.id = ?1 and m2.collective = c))"
    )
    List<Member> getMembersInSameGroupsAsUser(long personId);
}
