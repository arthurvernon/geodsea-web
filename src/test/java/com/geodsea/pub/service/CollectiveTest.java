package com.geodsea.pub.service;

import com.geodsea.pub.Application;
import com.geodsea.pub.domain.*;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
public class CollectiveTest {

    private static final Logger logger = Logger.getLogger(CollectiveTest.class);


    @Inject
    GroupService groupService;

    @Test
    public void testMemberQuery() throws ActionRefusedException {

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "user"));
        Collective collective = groupService.createFriends("DummyGroup", "en", "Dummy Group", "nobody@nowhere.com", "user", false);

        assertThat(groupService.getMembers(collective.getId()).size()).isEqualTo(1);

        groupService.deleteFriends(collective.getId());
    }

}
