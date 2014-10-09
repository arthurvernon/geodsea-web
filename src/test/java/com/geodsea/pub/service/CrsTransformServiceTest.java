package com.geodsea.pub.service;

import com.geodsea.epsg.CrsTransformService;
import com.geodsea.pub.Application;
import com.geodsea.pub.aop.logging.LoggingAspect;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("dev")
public class CrsTransformServiceTest {

    @Inject
    private CrsTransformService crsTransformService;

    @Test
    public void testTransform() throws Exception {

        // Create a dimension with within the EPSG:3857 CRS using Dimension(east, north)
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), CrsTransformService.CRS_CODE_3857);
        Coordinate c = new Coordinate(12883155.37, -3740617.96);
        Point p = new Point(new CoordinateArraySequence(new Coordinate[]{c}), factory);

        // Lat : 115.7313537655749 Long : -31.82696114717238
        // Lat : -3740617.96 Long : 1.288315537E7 in 164 ms
        long before = System.currentTimeMillis();
        Object value = crsTransformService.transform(p, CrsTransformService.CRS_CODE_4326);
        long after = System.currentTimeMillis();
        assertThat(value).isInstanceOf(Point.class);
        Point result = (Point) value;
        System.out.println("Lat : " + result.getCoordinates()[0].y + " Long : " + result.getCoordinates()[0].x +
                " in " + (after - before) + " ms");

        assertThat(result.getCoordinates().length).isEqualTo(1);

        Coordinate coordinate = result.getCoordinates()[0];

        //`north = coordinate.x` and `east = coordinate.y`

        // latitude (south negative)
        assertThat(coordinate.x).isEqualTo(-31.82696114717238);

        // longitude (east positive)
        assertThat(coordinate.y).isEqualTo(115.7313537655749);


        before = System.currentTimeMillis();
        result = (Point) crsTransformService.transform(p, CrsTransformService.CRS_CODE_4326);
        after = System.currentTimeMillis();

        System.out.println("Lat : " + result.getCoordinates()[0].y + " Long : " + result.getCoordinates()[0].x +
                " in " + (after - before) + " ms");

//        before = System.currentTimeMillis();
//        for (int i = 0; i < 10000; i++)
//            crsTransformService.transform(p, CrsTransformService.CRS_CODE_4326);
//        after = System.currentTimeMillis();
//        System.out.println("Performed 10,000 conversions in " + (after - before) + " ms");


    }
}