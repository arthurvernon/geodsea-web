package com.geodsea.pub.web.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
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
import com.geodsea.pub.domain.Boat;
import com.geodsea.pub.repository.BoatRepository;


/**
 * Test class for the BoatResource REST controller.
 *
 * @see BoatResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
@ActiveProfiles("dev")
public class BoatResourceTest {
    
    private static final Long DEFAULT_ID = new Long(1L);

    private static final LocalDate DEFAULT_SAMPLE_DATE_ATTR = new LocalDate(0L);

    private static final LocalDate UPD_SAMPLE_DATE_ATTR = new LocalDate();

    private static final String DEFAULT_SAMPLE_TEXT_ATTR = "sampleTextAttribute";

    private static final String UPD_SAMPLE_TEXT_ATTR = "sampleTextAttributeUpt";

    @Inject
    private BoatRepository boatRepository;

    private MockMvc restBoatMockMvc;
    
    private Boat boat;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BoatResource boatResource = new BoatResource();
        ReflectionTestUtils.setField(boatResource, "boatRepository", boatRepository);

        this.restBoatMockMvc = MockMvcBuilders.standaloneSetup(boatResource).build();

        boat = new Boat();
        boat.setId(DEFAULT_ID);
    	boat.setSampleDateAttribute(DEFAULT_SAMPLE_DATE_ATTR);
    	boat.setSampleTextAttribute(DEFAULT_SAMPLE_TEXT_ATTR);
    }

    // TODO text is broken - cannot rely on preset ID.
    @Test
    public void testCRUDBoat() throws Exception {

    	// Create Boat
    	restBoatMockMvc.perform(post("/app/rest/boats")
    			.contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(boat)))
                .andExpect(status().isOk());

    	// Read Boat
//    	restBoatMockMvc.perform(get("/app/rest/boats/{id}", DEFAULT_ID))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(DEFAULT_ID.intValue()))
//    			.andExpect(jsonPath("$.sampleDateAttribute").value(DEFAULT_SAMPLE_DATE_ATTR.toString()))
//    			.andExpect(jsonPath("$.sampleTextAttribute").value(DEFAULT_SAMPLE_TEXT_ATTR));
//
//    	// Update Boat
//    	boat.setSampleDateAttribute(UPD_SAMPLE_DATE_ATTR);
//    	boat.setSampleTextAttribute(UPD_SAMPLE_TEXT_ATTR);
//
//    	restBoatMockMvc.perform(post("/app/rest/boats")
//    			.contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(boat)))
//                .andExpect(status().isOk());
//
//    	// Read updated Boat
//    	restBoatMockMvc.perform(get("/app/rest/boats/{id}", DEFAULT_ID))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(DEFAULT_ID.intValue()))
//    			.andExpect(jsonPath("$.sampleDateAttribute").value(UPD_SAMPLE_DATE_ATTR.toString()))
//    			.andExpect(jsonPath("$.sampleTextAttribute").value(UPD_SAMPLE_TEXT_ATTR));
//
//    	// Delete Boat
//    	restBoatMockMvc.perform(delete("/app/rest/boats/{id}", DEFAULT_ID)
//                .accept(TestUtil.APPLICATION_JSON_UTF8))
//                .andExpect(status().isOk());
//
//    	// Read nonexisting Boat
//    	restBoatMockMvc.perform(get("/app/rest/boats/{id}", DEFAULT_ID)
//                .accept(TestUtil.APPLICATION_JSON_UTF8))
//                .andExpect(status().isNotFound());
//
    }
}
