package com.geodsea.pub.domain;

import com.geodsea.pub.domain.util.DateConstants;
import com.geodsea.pub.service.util.TripUpdateChecks;
import com.vividsolutions.jts.geom.Polygon;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Information relating to the sea rescue role including the SRO's callsign and desired location reporting location
 * for vessels departing from their area of interest.
 */
@Entity
@Table(name = "T_RESCUE", schema = "BOAT")
public class Rescue extends OrganisationRole {

    public static final int FIVE_MINUTES = 5 * 60;

    /**
     * For info see <a href="http://en.wikipedia.org/wiki/Call_sign>Call sign on Wikipedia</a>
     */
    @NotNull
    @Size(min=4, max = 40)
    @Column(name="callsign")
    private String callsign;

    /**
     * The frequency in seconds at which the vessel should report its location.
     * <p>
     *     Zero implies no obligation.
     * </p>
     */
    @Column(name="REPORT_RATE", nullable = false)
    @Min(value=0)
    private int reportRate = FIVE_MINUTES;

    public Rescue()
    {
        super();
    }

    public Rescue(Organisation organisation, String callsign, String zoneTitle, Polygon zone, Integer reportRate) {
        super(organisation, new Zone(zoneTitle, zone));
        this.callsign = callsign;
        if (reportRate != null)
            this.reportRate = reportRate;
    }

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }

    public int getReportRate() {
        return reportRate;
    }

    public void setReportRate(int reportRate) {
        this.reportRate = reportRate;
    }
}
