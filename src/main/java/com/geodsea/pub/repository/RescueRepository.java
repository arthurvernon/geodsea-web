package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Licensor;
import com.geodsea.pub.domain.Rescue;
import com.geodsea.pub.domain.Skipper;
import com.vividsolutions.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the Skipper entity.
 */
public interface RescueRepository extends JpaRepository<Rescue, Long> {

    @Query("select r from Rescue r where intersects(r.zone.zone, ?1) = TRUE")
    List<Rescue> getRescueOrganisationsForLocation(Point point);

}
