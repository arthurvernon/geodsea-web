package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Collective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for friends (group).
 */
public interface CollectiveRepository extends JpaRepository<Collective, Long> {

    /**
     * Find a collective by login
     * @param login
     * @return
     */
    Collective findByLogin(String login);

    /**
     * Find those collectives where this participant is a member.
     * @param participantId
     * @return
     */
    @Query("select c from Collective c where exists (select m from Member m where m.participant.id = ?1 and m.collective = c)")
    List<Collective> findHavingMember(long participantId);
}
