package com.geodsea.pub.service;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
        // y => latitude (North South)
        // x => longitude (East/West)
        return factory.createPoint(new Coordinate(lon, lat));
    }

    /**
     *
     * @param wkt the value to convert from well known Text format to a geometry object.
     * @return null if wkt is null or blank, a non-null Geometry otherwise
     * @throws ParseException if an invalid non-blank value is specified
     */
    public Geometry createFromWKT(String wkt) throws ParseException {

        if (wkt == null | wkt.trim().length() == 0)
            return null;

        // class is not thread safe so don't reuse an instanceof WKTReader
        return new WKTReader(factory).read(wkt);
    }

    public String toWKT(Geometry geometry) {
        return new WKTWriter(2).write(geometry);
    }
}
