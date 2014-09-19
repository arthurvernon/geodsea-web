package com.geodsea.pub.service;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;


/**
 * Services to manage people.
 */
@Service
public class GisService {

    private static final Logger log = Logger.getLogger(GisService.class);

    private static final char DEGREE_CHAR = '\u00b0';
    /**
     * 31°49'17.72"S
     */
    private static final String GOOGLE_LAT = "^\\s?[0-9]{1,2}" + DEGREE_CHAR + "[0-9]{1,2}'[0-9]{1,2}\\.[0-9]{1,2}\"[SN]$";
    private static final String GOOGLE_LONG = "^\\s?[0-9]{1,3}" + DEGREE_CHAR + "[0-9]{1,2}'[0-9]{1,2}\\.[0-9]{1,2}\"[EW]$";

    /**
     * Longitude values to the west are negative.
     */
    private static final char WEST = 'W';

    /**
     * Latitude values to the south are negative.
     */
    private static final char SOUTH = 'S';

    private static Pattern LAT_PATTERN = Pattern.compile(GOOGLE_LAT);
    private static Pattern LONG_PATTERN = Pattern.compile(GOOGLE_LONG);

    public static final int SRID = 4326;

    private static GeometryFactory factory = new GeometryFactory(new PrecisionModel(), GisService.SRID);


    public static MultiPoint createFromSeries(Geometry... items) {
        Coordinate[] coords = new Coordinate[items.length];
        for (int i = 0; i < items.length; i++)
            coords[i] = items[i].getCentroid().getCoordinate();

        return factory.createMultiPoint(coords);
    }


    public static final double googleLat(String value) {
        if (LAT_PATTERN.matcher(value).matches()) {
            String trimmed = value.trim();
            double unsigned = unsignedGoogleValue(trimmed);
            if (SOUTH == trimmed.charAt(trimmed.length() - 1))
                return -unsigned;
            else
                return unsigned;
        }
        log.info("Invalid latitude: " + value);
        return 0;
    }

    public static final double googleLong(String value) {
        if (LONG_PATTERN.matcher(value).matches()) {
            String trimmed = value.trim();
            double unsigned = unsignedGoogleValue(trimmed);
            if (WEST == trimmed.charAt(trimmed.length() - 1))
                return -unsigned;
            else
                return unsigned;
        }
        log.info("Invalid longtitude: " + value);
        return 0;
    }

    /**
     * Compute the absolute value from the degrees, minutes and seconds out of a string like <code>31°49'17.72"S</code>
     *
     * @param val
     * @return a positive number.
     */
    private static double unsignedGoogleValue(String val) {
        int idx1 = val.indexOf(DEGREE_CHAR);
        double degrees = Double.parseDouble(val.substring(0, idx1));
        int idx2 = val.indexOf('\'');
        double minutes = Double.parseDouble(val.substring(idx1 + 1, idx2));
        double seconds = Double.parseDouble(val.substring(idx2 + 1, val.length() - 2));
        return degrees + minutes / 60 + seconds / 3600;
    }

    /**
     * <p>
     * Point is in degress (minutes and seconds)
     * </p>
     *
     * @param lat
     * @param lon
     * @return
     */
    public static Point createPointFromLatLong(double lat, double lon) {
        // y => latitude (North South)
        // x => longitude (East/West)
        return factory.createPoint(new Coordinate(lon, lat));
    }

    /**
     * @param wkt the value to convert from well known Text format to a geometry object.
     * @return null if wkt is null or blank, a non-null Geometry otherwise
     * @throws ParseException if an invalid non-blank value is specified
     */
    public static Geometry createFromWKT(String wkt) throws ParseException {

        if (wkt == null | wkt.trim().length() == 0)
            return null;

        // class is not thread safe so don't reuse an instanceof WKTReader
        return new WKTReader(factory).read(wkt);
    }

    public static String toWKT(Geometry geometry) {
        return new WKTWriter(2).write(geometry);
    }
}
