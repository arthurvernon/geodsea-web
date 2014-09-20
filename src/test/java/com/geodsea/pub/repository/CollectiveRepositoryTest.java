package com.geodsea.pub.repository;

import com.geodsea.pub.Application;
import com.geodsea.pub.domain.Collective;
import com.geodsea.pub.domain.PersistentToken;
import com.geodsea.pub.domain.Person;
import com.geodsea.pub.service.ParticipantService;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the UserResource REST controller.
 *
 * @see com.geodsea.pub.service.UserService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@Transactional
public class CollectiveRepositoryTest {

    @Inject
    private CollectiveRepository collectiveRepository;

    @Inject
    private PersonRepository personRepository;

    @Test
    public void testRemoveOldPersistentTokens() {

    	Person person = personRepository.getByLogin("user");
        List<Collective> collectives = collectiveRepository.findHavingMember(person.getId());
        assertThat(collectives).hasSize(3); // 2 x licensing and 1 x SRO
    }

}
