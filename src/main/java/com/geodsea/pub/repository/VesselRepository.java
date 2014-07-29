package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Vessel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Boat entity.
 */
public interface VesselRepository extends JpaRepository<Vessel, Long> {

}
