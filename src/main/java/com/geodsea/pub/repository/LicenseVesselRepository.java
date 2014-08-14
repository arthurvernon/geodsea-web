package com.geodsea.pub.repository;

import com.geodsea.pub.domain.LicenseVessel;
import com.geodsea.pub.domain.Vessel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Boat entity.
 */
public interface LicenseVesselRepository extends JpaRepository<LicenseVessel, Long> {

}
