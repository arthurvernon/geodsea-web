package com.geodsea.pub.service;

import com.geodsea.pub.Application;
import com.geodsea.pub.domain.*;
import com.geodsea.pub.domain.type.VesselType;
import com.geodsea.pub.domain.util.DateConstants;
import com.geodsea.pub.repository.*;
import com.vividsolutions.jts.geom.Point;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;

import java.util.ArrayList;
import java.util.Date;
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
//@Transactional()
public class SkipperServiceTripTest {

    private static final Logger logger = Logger.getLogger(SkipperServiceTripTest.class);

    @Inject
    private SkipperService skipperService;

    @Inject
    private VesselRepository vesselRepository;

    @Inject
    private PersonRepository personRepository;

    private Vessel vessel;

    private Person person;

    @Before
    public void setup() {
        vessel = new Vessel("Monty", VesselType.CABIN);
        vessel = vesselRepository.save(vessel);
        person = personRepository.getByLogin("user");
    }

    @After
    public void cleanup() {
        if (vessel.getId() != null)
            vesselRepository.delete(vessel);
    }

    @Test
    public void testTripLifecycle() throws ActionRefusedException {

//        int locationReportsBefore = locationTimeRepository.findAll().size();

        // need to delete this once done!


        long now = System.currentTimeMillis();

        long currentTime = now + 1 * DateConstants.DAYS;
        long finish = now + 1 * DateConstants.DAYS + 1 * DateConstants.HOURS;

        // when a plan is made, the trip is defined.
        // vesselId, skipperId, headline, startTime, endTime, summary, MultiPoint wayPoints, int fuel, int people
        Trip trip = skipperService.createTripPlan(vessel.getId(), person.getId(), "Test", new Date(currentTime),
                new Date(finish), "Summary", null, 100, 3);

        // the person then makes a start
        trip = skipperService.getTrip(trip.getId());

        // actual start 30 seconds later than planned start
        currentTime = currentTime +  + 30000;
        skipperService.commenceTrip(trip.getId(), new Date(currentTime));


        // test data for starting location - Hillary's boat ramp 10 minutes ago
        double latitude = GisService.googleLat(" 31°49'17.72\"S");
        double longitude = GisService.googleLong("115°44'20.36\"E");
        Point point = GisService.createPointFromLatLong(latitude, longitude);

        // first report one minute later, once GPS has settled.
        currentTime = currentTime + 1 * DateConstants.MINUTES;
        Date locationReportTime = new Date(currentTime);
        LocationTime location = new LocationTime(point, locationReportTime, locationReportTime);

        skipperService.reportLocation(trip.getId(), location);

        trip = skipperService.getTrip(trip.getId());
        assertThat(trip.getRescue()).isNotNull();

        // some time later a location report is received.
        // and the location reports appended.
        List<LocationTime> updates = exitHillarys(currentTime);
        int updateSize = updates.size();
        skipperService.reportLocations(trip.getId(), updates);

        Trip currentTrip = skipperService.getTrip(trip.getId());

        List<LocationTime> locationTimes = skipperService.getLocationTimes("user", trip.getId());

        // check
        assertThat(locationTimes.size()).isEqualTo(1 + updateSize);


        skipperService.deleteTrip(trip.getId());

        // person is not deleted in the process
        assertThat(personRepository.getByLogin("user")).isNotNull();
    }

    private List<LocationTime> exitHillarys(long start) {
        List<LocationTime> list = new ArrayList<LocationTime>();

        long afterLastLocationReport = start + 7 * DateConstants.MINUTES;
        list.add(create("31°49'24.25\"S", "115°44'19.58\"E", start += DateConstants.MINUTES, afterLastLocationReport));
        list.add(create("31°49'27.13\"S", "115°44'0.40\"E", start += DateConstants.MINUTES, afterLastLocationReport));
        list.add(create(" 31°49'12.11\"S", "115°43'54.81\"E", start += DateConstants.MINUTES, afterLastLocationReport));
        list.add(create(" 31°48'41.91\"S", "115°43'35.84\"E", start += DateConstants.MINUTES, afterLastLocationReport));
        list.add(create(" 31°48'27.17\"S", "115°43'28.91\"E", start += DateConstants.MINUTES, afterLastLocationReport));
        list.add(create(" 31°48'1.64\"S", "115°43'26.96\"E", start += DateConstants.MINUTES, afterLastLocationReport));

        return list;
    }

    private LocationTime create(String lat, String lon, long at, long received) {
        double latitude = GisService.googleLat(lat);
        double longitude = GisService.googleLong(lon);
        Point point = GisService.createPointFromLatLong(latitude, longitude);
        LocationTime location = new LocationTime(point, new Date(at), new Date(received));
        return location;
    }
}
