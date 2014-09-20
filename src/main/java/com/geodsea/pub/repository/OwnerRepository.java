package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Owner;
import com.geodsea.pub.domain.Participant;
import com.geodsea.pub.domain.Person;
import com.geodsea.pub.domain.Vessel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the Boat entity.
 */
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    List<Owner> findByVesselId(long vesselId);

    List<Owner> findByParticipantId(long participantId);

    @Query("select o from Owner o where o.participant in ?1")
    List<Owner> findForParticipant(List<Participant> participantId);
}
