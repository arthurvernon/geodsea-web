package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Boat;
        import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Boat entity.
 */
public interface BoatRepository extends JpaRepository<Boat, Long> {

}
