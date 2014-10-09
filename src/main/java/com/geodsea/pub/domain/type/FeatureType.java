package com.geodsea.pub.domain.type;

/**
 * Feature types that may be shared with the user interface.
 */
public enum FeatureType {

    WAY_POINTS("way points");

    private final String idString;

    private FeatureType(String idString)
    {
        this.idString = idString;
    }

    public String getIdString() {
        return idString;
    }
}
