package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Licensor;
import com.geodsea.pub.domain.Person;
import com.vividsolutions.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the Licensor entity.
 */
public interface LicensorRepository extends JpaRepository<Licensor, Long> {

    @Query("select l from Licensor l where intersects(l.zone.zone, ?1) = TRUE")
    List<Licensor> getLicensorForLocation(Point point);


}
