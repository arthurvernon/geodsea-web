package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Licensor;
        import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for the Licensor entity.
 */
public interface LicensorRepository extends JpaRepository<Licensor, Long> {

}
