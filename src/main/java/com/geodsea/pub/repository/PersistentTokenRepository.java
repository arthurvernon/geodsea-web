package com.geodsea.pub.repository;

import com.geodsea.pub.domain.PersistentToken;
import com.geodsea.pub.domain.Person;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Spring Data JPA repository for the PersistentToken entity.
 */
public interface PersistentTokenRepository extends JpaRepository<PersistentToken, String> {

    List<PersistentToken> findByPerson(Person person);

    List<PersistentToken> findByTokenDateBefore(LocalDate localDate);

}
