package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Collective;
import com.geodsea.pub.domain.Member;
import com.geodsea.pub.domain.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the groups.
 * TODO queries need to have additional constraints based upon from and to dates.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member getMemberByCollectiveIdAndParticipantLogin(long collectiveId, String login);

    Member getMemberByCollectiveIdAndParticipantId(long collectiveId, long participantId);

    @Query("select distinct m from Member m where m.collective in " +
            "(select c from Collective c where exists " +
            "(select m2 from Member m2 where m2.participant.id = ?1 and m2.collective = c))"
    )
    List<Member> getMembersInSameGroupsAsUser(long personId);

    /**
     * Determine if the person specified is indeed an active manager within the group.
     * @param collectiveId
     * @param personId
     * @return
     */
    @Query("select m from Member m where m.collective.id = ?1 and m.participant.id = ?2 and m.active = true and m.manager = true")
    Member getActiveManager(long collectiveId, long personId);


    @Query("select m from Member m where m.participant.id = ?1 and m.active = true")
    List<Member> findWherePersonIsActiveMember(long id);
}
