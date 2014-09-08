package com.geodsea.pub.service;

import com.geodsea.pub.Application;
import com.geodsea.pub.domain.Track;
import com.geodsea.pub.domain.TrackLocation;
import com.geodsea.pub.domain.Vessel;
import com.geodsea.pub.repository.TrackRepository;
import com.geodsea.pub.repository.VesselRepository;
import com.vividsolutions.jts.geom.Point;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.assertj.core.api.Assertions.*;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
@Transactional
public class GisServiceTest {

    @Inject
    private GisService gisService;

    @Test
    public void testLat() {
        double value = GisService.googleLat(" 31°49'17.72\"S");
        double expected = -(31.0 + 49.0 / 60 + 17.72 / 3600);
        assertThat(value).isEqualTo(expected);

    }

    @Test
    public void testLong() {
        double value = GisService.googleLong("115°44'20.36\"E");
        double expected = (115.0 + 44.0 / 60 + 20.36 / 3600);
        assertThat(value).isEqualTo(expected);


    }

}
