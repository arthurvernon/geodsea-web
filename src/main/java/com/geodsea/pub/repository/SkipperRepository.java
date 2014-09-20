package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Person;
import com.geodsea.pub.domain.Rescue;
import com.geodsea.pub.domain.Skipper;
import com.vividsolutions.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the Skipper entity.
 */
public interface SkipperRepository extends JpaRepository<Skipper, Long> {

    List<Skipper> getSkipperByPerson(Person person);
}
