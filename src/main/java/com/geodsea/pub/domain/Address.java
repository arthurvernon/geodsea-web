package com.geodsea.pub.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Address address = (Address) o;

        if (formatted != null ? !formatted.equals(address.formatted) : address.formatted != null) return false;

        return true;
    }

}
