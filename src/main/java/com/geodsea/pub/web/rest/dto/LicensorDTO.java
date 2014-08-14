package com.geodsea.pub.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geodsea.pub.domain.License;
import com.geodsea.pub.domain.ParticipantGroup;
import com.vividsolutions.jts.geom.Polygon;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

public class LicensorDTO {

    private Long id;

    /**
     * this value needs to be set on the client to define the group that is a licensing agency.
     */
    private Long participantGroupId;

    /**
     * Immatable property defined by way of the participant.
     */
    private String groupName;

    /**
     * URL for license checks
     */
    private String webServiceURL;

    /**
     * The region in described in the language of the provider
     */
    private String region;

    public LicensorDTO() {
        super();
    }

    public LicensorDTO(Long id, Long participantGroupId, String groupName, String webServiceURL, String region) {
        this.id = id;
        this.participantGroupId = participantGroupId;
        this.groupName = groupName;
        this.webServiceURL = webServiceURL;
        this.region = region;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParticipantGroupId() {
        return participantGroupId;
    }

    public void setParticipantGroupId(Long participantGroupId) {
        this.participantGroupId = participantGroupId;
    }

    public String getWebServiceURL() {
        return webServiceURL;
    }

    public void setWebServiceURL(String webServiceURL) {
        this.webServiceURL = webServiceURL;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


}