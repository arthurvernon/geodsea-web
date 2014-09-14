package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Skipper;
        import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Skipper entity.
 */
public interface SkipperRepository extends JpaRepository<Skipper, Long> {


    Skipper findByPersonParticipantName(String participantName);

}