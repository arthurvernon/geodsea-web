package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Trip;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Skipper entity.
 */
public interface TripRepository extends JpaRepository<Trip, Long> {

}
