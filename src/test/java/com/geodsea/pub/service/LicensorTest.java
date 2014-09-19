package com.geodsea.pub.service;

import com.geodsea.pub.Application;
import com.geodsea.pub.domain.Licensor;
import com.geodsea.pub.repository.LicensorRepository;
import com.vividsolutions.jts.geom.Point;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

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
public class LicensorTest {

    private static final Logger logger = Logger.getLogger(LicensorTest.class);


    @Inject
    LicensorRepository licensorRepository;

    ;

    @Test
    public void testLicensorLocationQuery() throws ActionRefusedException {

        Point point = GisService.createPointFromLatLong(-32, 115);
        List<Licensor> licensor = licensorRepository.getLicensorForLocation(point);

        assertThat(licensor.size()).isEqualTo(1);

        // Perth in the nothern hemishpere where there is no licensor
        point = GisService.createPointFromLatLong(32, 115);
        licensor = licensorRepository.getLicensorForLocation(point);
        assertThat(licensor.size()).isEqualTo(0);

    }

}
