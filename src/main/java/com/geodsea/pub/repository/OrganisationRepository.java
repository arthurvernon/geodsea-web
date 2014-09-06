package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Group;
import com.geodsea.pub.domain.Organisation;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for organisations.
 */
public interface OrganisationRepository extends JpaRepository<Organisation, Long> {


}
