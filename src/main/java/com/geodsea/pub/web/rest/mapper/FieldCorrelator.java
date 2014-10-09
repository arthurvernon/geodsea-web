package com.geodsea.pub.web.rest.mapper;

import com.geodsea.pub.domain.TripSkipper;
import com.geodsea.pub.web.rest.dto.SkipperTripDTO;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Arthur Vernon on 9/10/2014.
 */
public class FieldCorrelator {

    /**
     * For a particular form name
     */
    private static final Map<Class<?>, Map<String,String>> variants = new HashMap<Class<?>, Map<String,String>>();

    private static void add(Class<?> clazz, String beanFieldName, String dtoFieldName)
    {
        Map<String,String> vals = variants.get(clazz);
        if (vals == null){
            vals = new HashMap<String, String>();
            variants.put(clazz, vals);
        }
        vals.put(beanFieldName, dtoFieldName);
    }

    static
    {
//        add(TripSkipper.class, "scheduledStartTime", "trip.start.scheduled");
//        add(TripSkipper.class, "actualStartTime", "trip.start.actual");
//        add(TripSkipper.class, "scheduledEndTime", "trip.end.scheduled");
//        add(TripSkipper.class, "actualEndTime", "trip.end.actual");
    }

    public static String translate(Class<?> beanClass, String beanFieldName) {
        Map<String,String> vals = variants.get(beanClass);
        if (vals == null)
            return beanFieldName;
        String value = vals.get(beanFieldName);
        if (value != null)
            return value;
        return beanFieldName;
    }
}
