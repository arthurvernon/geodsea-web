package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Organisation;
import com.geodsea.pub.domain.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for organisations.
 */
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {

    @Query("select o from Organisation o where exists " +
            "(select m.id from Member m where o.id = m.collective.id and m.participant.id = ?1 and m.manager = true and m.active = true)")
    List<Organisation> findActiveManagers(long personId);
}
