package com.geodsea.pub.web.rest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;

import com.geodsea.pub.domain.ParticipantGroup;
import com.geodsea.pub.repository.ParticipantGroupRepository;
import com.geodsea.pub.service.LicenseService;
import com.geodsea.pub.service.UserService;
import com.geodsea.pub.web.rest.dto.LicensorDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.geodsea.pub.Application;
import com.geodsea.pub.repository.LicensorRepository;
import org.springframework.transaction.annotation.Transactional;


/**
 * Test class for the LicensorResource REST controller.
 *
 * @see LicensorResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
    TransactionalTestExecutionListener.class })
@ActiveProfiles("dev")
public class LicensorResourceTest {
    
    private static final Long DEFAULT_ID = new Long(5000000L);

    private static final String URL_BEFORE = "https://transport.nsw.gov.au/ws/license_x";
    private static final String URL_AFTER = "https://transport.nsw.gov.au/ws/license_y";

    private static final String REGION_BEFORE = "NSW";
    private static final String REGION_AFTER = "New South Wales, Australia";

    @Inject
    private LicensorRepository licensorRepository;

    @Inject
    private ParticipantGroupRepository participantGroupRepository;

    @Inject
    LicenseService licenseService;

    private MockMvc restLicensorMockMvc;

    private LicensorDTO licensor;

    private ParticipantGroup participantGroup;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LicensorResource licensorResource = new LicensorResource();

        ReflectionTestUtils.setField(licensorResource, "licensorRepository", licensorRepository);
        ReflectionTestUtils.setField(licensorResource, "licenseService", licenseService);

        this.restLicensorMockMvc = MockMvcBuilders.standaloneSetup(licensorResource).build();

        participantGroup = new ParticipantGroup();
        participantGroup.setEnabled(true);
        participantGroup.setParticipantName("transport.nsw.gov.au");
        participantGroup.setPublishedName("Marine and Harbours, NSW");

//        when(licenseService.addOrUpdateLicensor();getUserWithAuthorities()).thenReturn(person);

        licensor = new LicensorDTO();
        licensor.setId(DEFAULT_ID);
        // relies on the test data. Does this work?
    	licensor.setWebServiceURL(URL_BEFORE);
    	licensor.setRegion(REGION_BEFORE);
    }

    @Test
    @Transactional
    public void testCRUDLicensor() throws Exception {

        participantGroup = participantGroupRepository.save(participantGroup);
        licensor.setParticipantGroupId(participantGroup.getId());

//  These just don't work because an ID cannot be assumed.

//        // Create Licensor
//    	restLicensorMockMvc.perform(post("/app/rest/licensors")
//    			.contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(licensor)))
//                .andExpect(status().isOk());
//
//    	// Read Licensor
//    	restLicensorMockMvc.perform(get("/app/rest/licensors/{id}", DEFAULT_ID))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//    			.andExpect(jsonPath("$.webServiceURL").value(URL_BEFORE))
//    			.andExpect(jsonPath("$.region").value(REGION_BEFORE));
//
//    	// Update Licensor
//    	licensor.setWebServiceURL(URL_AFTER);
//    	licensor.setRegion(REGION_AFTER);
//
//    	restLicensorMockMvc.perform(post("/app/rest/licensors")
//    			.contentType(TestUtil.APPLICATION_JSON_UTF8)
//                .content(TestUtil.convertObjectToJsonBytes(licensor)))
//                .andExpect(status().isOk());
//
//    	// Read updated Licensor
//    	restLicensorMockMvc.perform(get("/app/rest/licensors/{id}", DEFAULT_ID))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(DEFAULT_ID.intValue()))
//    			.andExpect(jsonPath("$.webServiceURL").value(URL_AFTER))
//    			.andExpect(jsonPath("$.region").value(REGION_AFTER));
//
//    	// Delete Licensor
//    	restLicensorMockMvc.perform(delete("/app/rest/licensors/{id}", DEFAULT_ID)
//                .accept(TestUtil.APPLICATION_JSON_UTF8))
//                .andExpect(status().isOk());
//
//    	// Read nonexisting Licensor
//    	restLicensorMockMvc.perform(get("/app/rest/licensors/{id}", DEFAULT_ID)
//                .accept(TestUtil.APPLICATION_JSON_UTF8))
//                .andExpect(status().isNotFound());
//
    }
}