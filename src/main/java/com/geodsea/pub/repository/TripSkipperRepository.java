package com.geodsea.pub.repository;

import com.geodsea.pub.domain.TripSkipper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the TripSkipper entity.
 */
public interface TripSkipperRepository extends JpaRepository<TripSkipper, Long> {

    List<TripSkipper> getBySkipperPersonLogin(String login);
}
