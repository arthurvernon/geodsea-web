package com.geodsea.pub.service;

import com.vividsolutions.jts.geom.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Validator;


/**
 * Services to manage people.
 */
@Service
public class GisService {

    public static final int SRID = 4326;

    private GeometryFactory factory = new GeometryFactory(new PrecisionModel(), GisService.SRID);

    private static final Logger log = Logger.getLogger(GisService.class);

    public MultiPoint createFromSeries(Geometry... items)
    {
        Coordinate[] coords = new Coordinate[items.length];
        for (int i=0; i < items.length; i++)
            coords[i] = items[i].getCentroid().getCoordinate();

        return factory.createMultiPoint(coords);
    }


    public Point createPointFromLatLong(double lat, double lon)
    {
        return factory.createPoint(new Coordinate(lat, lon));
    }

}
