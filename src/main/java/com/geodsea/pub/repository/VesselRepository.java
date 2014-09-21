package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Rescue;
import com.geodsea.pub.domain.Vessel;
import com.vividsolutions.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the Boat entity.
 */
public interface VesselRepository extends JpaRepository<Vessel, Long> {

    Vessel findByHullIdentificationNumber(String hin);
}
