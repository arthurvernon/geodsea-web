package com.geodsea.pub.domain;

import com.vividsolutions.jts.geom.Polygon;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * An organisation that undertakes events such as yacht races or fishing competitions and needs to be able
 * to monitor the location of boats for a given period of time.
 * <p>
 *     For the club to be able to monitor the location of a boat, it must be able to obtain the permission
 *     of the skipper.
 * </p>
 */
@Entity
@Table(name = "T_CLUB", schema = "BOAT")
public class Club extends OrganisationRole {

    /**
     * The default setting for a club with respect to the frequency of updates during events.
     */
    public static final int ONE_MINUTE = 60;

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
    private int reportRate = ONE_MINUTE;

    public Club()
    {
        super();
    }

    public Club(Organisation organisation, String callsign, String zoneTitle, Polygon zone) {
        super(organisation, new Zone(zoneTitle, zone));
    }

    public int getReportRate() {
        return reportRate;
    }

    public void setReportRate(int reportRate) {
        this.reportRate = reportRate;
    }

    public String getCallsign() {
        return callsign;
    }

    public void setCallsign(String callsign) {
        this.callsign = callsign;
    }
}
