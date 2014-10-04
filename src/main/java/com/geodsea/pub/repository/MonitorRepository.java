package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Monitor;
import com.geodsea.pub.domain.Participant;
import com.geodsea.pub.domain.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the Skipper entity.
 */
public interface MonitorRepository extends JpaRepository<Monitor, Long> {

    Monitor findByParticipantLoginAndTripId(String login, long tripId);

    @Query("select m from Monitor m where m.participant in ?1")
    List<Monitor> findForParticipant(List<Participant> participants);

    @Query("select m from Monitor m where m.trip = ?1 " +
            "and exists (select r.id from Rescue r where r.organisation.id = m.participant.id)")
    Monitor findRescueMonitoringTrip(Trip trip);
}
