package com.geodsea.pub.service;

import com.geodsea.epsg.CrsTransformService;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import org.apache.log4j.Logger;
import org.geojson.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Services to perform translations between different geographic schemes
 */
@Service
public class GisService {

    @Inject
    private CrsTransformService crsTransformService;

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

    /**
     * The default WGS84_GEOMETRY_FACTORY to use for manipulating WGS84 data
     */
    private static GeometryFactory WGS84_GEOMETRY_FACTORY =
            new GeometryFactory(new PrecisionModel(), CrsTransformService.CRS_CODE_4326);


    /**
     * Convert a series of geometry items into a multipoint object by taking the centroid of each item.
     * <p>
     *     Coordinates are assumed to be in WGS84 CRS. (ie using a WGS84 Geometry factory to perform the collation),
     *     not that it may matter.
     * </p>
     * @param items zero or more items.
     * @return a non-null multipoint object.
     */
    public static MultiPoint createFromSeries(Geometry... items) {
        if (items == null || items.length == 0)
            return WGS84_GEOMETRY_FACTORY.createMultiPoint((Coordinate[]) null);

        Coordinate[] coords = new Coordinate[items != null ? items.length : 0];
        for (int i = 0; i < items.length; i++)
            coords[i] = items[i].getCentroid().getCoordinate();

        return WGS84_GEOMETRY_FACTORY.createMultiPoint(coords);
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
        return WGS84_GEOMETRY_FACTORY.createPoint(new Coordinate(lon, lat));
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
        return new WKTReader(WGS84_GEOMETRY_FACTORY).read(wkt);
    }

    public static String toWKT(Geometry geometry) {
        return new WKTWriter(2).write(geometry);
    }

    /**
     * Convert the feature to a line string using the coordinate reference system as specified for the
     * feature.
     *
     * @param feature
     * @return
     */
    public LineString toLineString(Feature feature) {
        if (feature == null)
            return null;

        Integer baseCRS = extractEpsgCodeFromCrs(feature.getCrs());

        GeoJsonObject object = feature.getGeometry();
        if (object == null) {
            log.error("No geometry component within feature");
            return null;
        }

        Integer geometryCrs = extractEpsgCodeFromCrs(object.getCrs());
        int crs = geometryCrs != null ? geometryCrs : (baseCRS != null ? baseCRS : 0);
        if (crs == 0)
        {
            log.warn("CRS not specified for feature. Defaulting to " + CrsTransformService.CRS_CODE_4326);
            crs = CrsTransformService.CRS_CODE_4326;
        }

        GeometryFactory factory = new GeometryFactory(new PrecisionModel(),crs);

        // This may be a way too broad approach to conversion.
        // It should apply to a LineString instance, but may be invalid to apply to
        // other types of MultiPoint objects. At present LineString is the only subclass
        // of multipoint so there should be no problems at present.
        if (object instanceof org.geojson.MultiPoint) {
            org.geojson.MultiPoint mp = (org.geojson.MultiPoint) object;
            List<LngLatAlt> list = mp.getCoordinates();

            Coordinate[] coords = new Coordinate[list.size()];
            for (int i = 0; i < coords.length; i++) {
                final LngLatAlt lngLatAlt = list.get(0);
                coords[i] = new Coordinate(lngLatAlt.getLongitude(), lngLatAlt.getLatitude(), lngLatAlt.getAltitude());
            }

            // build LineString object
            return factory.createLineString(coords);
        } else {
            log.error("Failed to convert feature into a linestring. " +
                    "Geometry within feature is not a MultiPoint object, but a " + object.getClass().getSimpleName());
            return null;
        }
    }

    public static void addEpsgCodeToMap(Map<String, Object> p, int epsgCode) {
        p.put("name", "EPSG:" + epsgCode);
    }

    /**
     * @param crs Crs object to extract from
     * @return the numeric value or {@link CrsTransformService#CRS_CODE_4326} if not defined.
     */
    public static Integer extractEpsgCodeFromCrs(Crs crs) throws IllegalStateException {
        if (crs == null)
            return null;
        return extractEpsgCodeFromMap(crs.getProperties());
    }

    /**
     * @param map map containing a "name" parameter with an "EPSG:xxxx" value.
     * @return the numeric value or {@link CrsTransformService#CRS_CODE_4326} if not defined.
     */
    public static Integer extractEpsgCodeFromMap(Map<String, Object> map) throws IllegalStateException {
        if (map == null) {
            log.debug("Map for EPSG extraction is null");
        } else {
            Object value = map.get("name");
            if (value == null) {
                log.debug("Value for \"name\" parameter not specified");
            } else {
                if (value instanceof String) {
                    String s = (String) value;
                    Pattern p = Pattern.compile("\\d+");
                    Matcher m = p.matcher(s);
                    if (m.find())
                        return Integer.parseInt(m.group());
                    else {
                        log.debug("Cannot extract number from " + s);
                    }
                } else {
                    log.debug("Value for \"name\" parameter is not a string, but a " +
                            value.getClass().getSimpleName());
                }
            }
        }
        return null;
    }
}
