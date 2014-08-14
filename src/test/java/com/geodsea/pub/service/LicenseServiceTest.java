package com.geodsea.pub.service;

import com.geodsea.pub.Application;
import com.geodsea.pub.domain.Licensor;
import com.geodsea.pub.domain.PersistentToken;
import com.geodsea.pub.domain.Person;
import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

/**
 * Integration test to verify that client can talk to a web service
 *
 * @see com.geodsea.pub.service.LicenseService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@Transactional
public class LicenseServiceTest {

    @Inject
    private LicenseService licenseService;

    @Test
    public void testWSCall() {
        Licensor licensor = new Licensor();
        licensor.setLicenceWsURL("http://localhost:8080/ws");
        licensor.setLicenseWsUsername("username");
        licensor.setLicenseWsPassword("password");
        licenseService.customSendAndReceive(licensor, "123456");
    }

}
