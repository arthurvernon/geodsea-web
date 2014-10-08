package com.geodsea.pub.service;

import com.geodsea.epsg.MyEpsgFactory;
import com.geodsea.pub.service.GisService;
import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import org.apache.log4j.Logger;
import org.geojson.Crs;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Perform transformations between coordinate reference systems (CRS).
 * <p>
 * Based upon the default
 * </p>
 */
@Service
public class CrsTransformService {

    /**
     * The standard WGS 84 coordinate reference system based upon latitude and longitude values in degrees/minutes/seconds.
     */
    public static final int SRID_4326 = 4326;

    /**
     * WGS 84 / Pseudo-Mercator used by microsoft and google for map displays.
     * <p>
     *     Units are in metres positive values to the east and north of World and is valid between 85°S and 85°N.
     * </p>
     * <p>See <a href="http://georepository.com/crs_3857/WGS-84-Pseudo-Mercator.html">Definition</a></p>
     */
    public static final int SRID_3857 = 3857;

    private static final Logger log = Logger.getLogger(CrsTransformService.class);

    public static final String DEFAULT_SCHEMA = "epsg";

    /**
     * To override the {@link #DEFAULT_SCHEMA} then set a value for the {@value} environment variable.
     */
    public static final String SCHEMA_PROPERTY_NAME = "geodsea.epsg.schema";


    @Inject
    private DataSource dataSource;

    private String schemaName;

    @Inject
    private Environment env;

    private MathTransform transform;

    @PostConstruct
    public void configureEpsgFactory() {

        this.schemaName = env.getProperty(SCHEMA_PROPERTY_NAME, DEFAULT_SCHEMA);
        try {
            log.info("Loading EPSG Geodetic Parameter Dataset from the " + schemaName + " of " +
                    dataSource.getConnection().getMetaData().getURL());
        } catch (SQLException ex) {
            log.info("Loading EPSG Geodetic Parameter Dataset from the " + schemaName);
        }

        MyEpsgFactory.configure(dataSource, schemaName);
        CoordinateReferenceSystem MERCATOR_CRS = null;
        try {
            MERCATOR_CRS = CRS.decode("EPSG:4326");
            CoordinateReferenceSystem PSEUDO_MERCATOR_CRS = CRS.decode("EPSG:3857");

            boolean lenient = true; // allow for some error due to different datums
//        MathTransform transform = CRS.findMathTransform(MERCATOR_CRS, PSEUDO_MERCATOR_CRS, lenient);
            transform = CRS.findMathTransform(PSEUDO_MERCATOR_CRS, MERCATOR_CRS, lenient);
        } catch (FactoryException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Convert the geometry as specified to
     *
     * @param geom
     * @param toSRID the
     * @return
     * @throws FactoryException
     * @throws TransformException
     */
    public Geometry transform(Geometry geom, int toSRID) throws FactoryException, TransformException {
        CoordinateReferenceSystem fromCRS = CRS.decode("EPSG:" + geom.getSRID());
        CoordinateReferenceSystem toCRS = CRS.decode("EPSG:" + toSRID);
        boolean lenient = true; // allow for some error due to different datums?
        MathTransform transform = CRS.findMathTransform(fromCRS, toCRS, lenient);
        return JTS.transform(geom, transform);
    }

}
