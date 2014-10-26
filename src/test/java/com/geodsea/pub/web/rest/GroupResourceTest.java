package com.geodsea.pub.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import javax.inject.Inject;

import com.geodsea.pub.service.GroupService;
import com.geodsea.common.dto.CollectiveDTO;
import com.geodsea.common.dto.GroupDTO;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.geodsea.pub.Application;


/**
 * Test class for the GroupResource REST controller.
 *
 * @see GroupResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
@ActiveProfiles("dev")
public class GroupResourceTest {
    
    private static final Long DEFAULT_ID = new Long(1L);

    private static final LocalDate DEFAULT_SAMPLE_DATE_ATTR = new LocalDate(0L);

    private static final LocalDate UPD_SAMPLE_DATE_ATTR = new LocalDate();

    private static final String DEFAULT_SAMPLE_TEXT_ATTR = "sampleTextAttribute";

    private static final String UPD_SAMPLE_TEXT_ATTR = "sampleTextAttributeUpt";

    @Inject
    private GroupService groupService;

    private MockMvc restFriendsMockMvc;
    
    private CollectiveDTO group;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        GroupResource groupResource = new GroupResource();
        ReflectionTestUtils.setField(groupResource, "groupService", groupService);

        this.restFriendsMockMvc = MockMvcBuilders.standaloneSetup(groupResource).build();

        group = new GroupDTO(DEFAULT_ID, null, null, null, false, null, null, null);
    }

    @Test
    public void testCRUDFriends() throws Exception {

    	// Create Group
//    	restFriendsMockMvc.perform(post("/app/rest/friends")
//    			.contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(group)))
//                .andExpect(status().isOk());

//    	// Read Group
//    	restFriendsMockMvc.perform(get("/app/rest/friends/{id}", DEFAULT_ID))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(DEFAULT_ID.intValue()))
//    			.andExpect(jsonPath("$.sampleDateAttribute").value(DEFAULT_SAMPLE_DATE_ATTR.toString()))
//    			.andExpect(jsonPath("$.sampleTextAttribute").value(DEFAULT_SAMPLE_TEXT_ATTR));
//
//    	// Update Group
//
//    	restFriendsMockMvc.perform(post("/app/rest/friends")
//    			.contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(group)))
//                .andExpect(status().isOk());
//
//    	// Read updated Group
//    	restGroupMockMvc.perform(get("/app/rest/friends/{id}", DEFAULT_ID))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(DEFAULT_ID.intValue()))
//    			.andExpect(jsonPath("$.sampleDateAttribute").value(UPD_SAMPLE_DATE_ATTR.toString()))
//    			.andExpect(jsonPath("$.sampleTextAttribute").value(UPD_SAMPLE_TEXT_ATTR));
//
//    	// Delete Group
//    	restGroupMockMvc.perform(delete("/app/rest/friends/{id}", DEFAULT_ID)
//                .accept(TestUtil.APPLICATION_JSON_UTF8))
//                .andExpect(status().isOk());
//
//    	// Read nonexisting Group
//    	restGroupMockMvc.perform(get("/app/rest/friends/{id}", DEFAULT_ID)
//                .accept(TestUtil.APPLICATION_JSON_UTF8))
//                .andExpect(status().isNotFound());
//
    }
}
