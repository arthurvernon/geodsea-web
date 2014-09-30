package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Person;
import com.geodsea.pub.domain.Rescue;
import com.geodsea.pub.domain.Skipper;
import com.geodsea.pub.domain.Vessel;
import com.vividsolutions.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the Skipper entity.
 */
public interface SkipperRepository extends JpaRepository<Skipper, Long> {

    List<Skipper> getSkipperByPerson(Person person);

    Skipper getSkipperByPersonAndVessel(Person person, Vessel vessel);

    @Query("select s from Skipper s where s.person.id = ?1 and s.suspended = false and " +
            "(s.grantedTo is null or s.grantedTo > CURRENT_TIMESTAMP) and (s.grantedFrom is null or s.grantedFrom < CURRENT_TIMESTAMP)")
    List<Skipper> getActiveSkippers(long personId);

    @Query("select s from Skipper s where s.vessel.id = ?1 and s.suspended = false and " +
            "(s.grantedTo is null or s.grantedTo > CURRENT_TIMESTAMP) and (s.grantedFrom is null or s.grantedFrom < CURRENT_TIMESTAMP)")
    List<Skipper> getActiveSkippersOfVessel(long vesselId);

    @Query("select s from Skipper s where s.vessel.id = ?1")
    List<Skipper> getAllSkippersOfVessel(long vesselId);

    Skipper getSkipperByPersonIdAndVesselId(Long personId, long vesselId);
}
