package com.geodsea.pub.repository;

import com.geodsea.pub.domain.Person;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Spring Data JPA repository for the User entity.
 */
public interface PersonRepository extends JpaRepository<Person, Long> {


//    @Query("select p from Person p where p.login = ?1")
    Person getByLogin(String login);
}
