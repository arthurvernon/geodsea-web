package com.geodsea.pub.domain;

import com.vividsolutions.jts.geom.Point;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * An address and corresponding geographic point.
 */
@Embeddable
public class Address {

    /**
     * The address as formatted by Google places.
     */
    @Size(min = 0, max = 100)
    @Column(name = "FORMATTED", nullable = true)
    private String formatted;

    @Column(name = "POINT", nullable = true)
    @Type(type = "org.hibernate.spatial.GeometryType")
    private Point point;

    public Address(String formatted, Point point) {
        this.formatted = formatted;
        this.point = point;
    }

    public Address() {
    }

    public String getFormatted() {
        return formatted;
    }

    public void setFormatted(String formatted) {
        this.formatted = formatted;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return formatted;
    }
}
