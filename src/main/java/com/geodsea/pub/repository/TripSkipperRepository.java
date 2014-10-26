package com.geodsea.pub.repository;

import com.geodsea.pub.domain.TripSkipper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the TripSkipper entity.
 */
public interface TripSkipperRepository extends JpaRepository<TripSkipper, Long> {

    List<TripSkipper> getBySkipperPersonLogin(String login);

    @Query("select t from TripSkipper t where t.skipper.person.login = ?1 "
            + "and t.actualStartTime is not null and t.actualEndTime is null")
    TripSkipper findActiveTripForSkipper(String login);
}
