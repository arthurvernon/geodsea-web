package com.geodsea.pub.web.rest.dto;

import java.util.List;

public class LicensorDTO extends OrganisationDTO {

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


    private Long licensorId;

    public LicensorDTO() {
        super();
    }

    public LicensorDTO(Long organisationId, Long licensorId, String groupLogin, String groupName, String langKey,  boolean enabled,
                       String email, String websiteURL, ParticipantDTO contactPerson, String telephone, String address,
                       List<AddressPartDTO> addressParts, PointDTO point, String zoneTitle, String zoneWKT, String webServiceURL) {
        super(organisationId, groupLogin, groupName, langKey, enabled, email, websiteURL, contactPerson, telephone, address);
        this.webServiceURL = webServiceURL;
        this.zoneTitle = zoneTitle;
        this.zoneWKT = zoneWKT;
        this.licensorId = licensorId;
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

}