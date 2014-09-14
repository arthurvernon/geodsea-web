package com.geodsea.pub.service;

import com.geodsea.pub.Application;
import com.geodsea.pub.domain.*;
import com.geodsea.pub.domain.type.VesselType;
import com.geodsea.pub.domain.util.DateConstants;
import com.geodsea.pub.repository.PersonRepository;
import com.geodsea.pub.repository.SkipperRepository;
import com.geodsea.pub.repository.VesselRepository;
import com.vividsolutions.jts.geom.Point;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

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
public class GroupTest {

    private static final Logger logger = Logger.getLogger(GroupTest.class);


    @Inject
    GroupService groupService;

    @Test
    public void testMemberQuery() throws ActionRefusedException {

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("user", "user"));
        Group group = groupService.createGroup("DummyGroup", "nobody@nowhere.com", "user", "");

        assertThat(groupService.getMembers(group.getId()).size()).isEqualTo(1);

        groupService.deleteGroup(group.getId());
    }

}
