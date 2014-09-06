package com.geodsea.pub.web.rest.dto;

public class LicensorDTO {

    private Long id;

    /**
     * this value needs to be set on the client to define the group that is a licensing agency.
     */
    private Long organisationId;

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
    private String zoneTitle;

    /**
     * The region in WKT form
     */
    private String zoneWKT;


    public LicensorDTO() {
        super();
    }

    public LicensorDTO(Long id, Long organisationId, String groupName, String webServiceURL, String zoneTitle, String zoneWKT) {
        this.id = id;
        this.organisationId = organisationId;
        this.groupName = groupName;
        this.webServiceURL = webServiceURL;
        this.zoneTitle = zoneTitle;
        this.zoneWKT = zoneWKT;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public String getWebServiceURL() {
        return webServiceURL;
    }

    public void setWebServiceURL(String webServiceURL) {
        this.webServiceURL = webServiceURL;
    }

    public String getZoneTitle() {
        return zoneTitle;
    }

    public void setZoneTitle(String zoneTitle) {
        this.zoneTitle = zoneTitle;
    }

    public String getZoneWKT() {
        return zoneWKT;
    }

    public void setZoneWKT(String zoneWKT) {
        this.zoneWKT = zoneWKT;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


}