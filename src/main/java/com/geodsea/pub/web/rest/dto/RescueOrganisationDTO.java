package com.geodsea.pub.web.rest.dto;

import java.util.List;

/**
 * Rescue organisation details.
 */
public class RescueOrganisationDTO extends OrganisationDTO {

    /**
     * Editable by a manager in the group
     */
    private String rescueCallsign;


    /**
     * Editable by a manager in the group
     */
    private String zoneWKT;

    /**
     * Editable by a manager in the group
     */
    private String zoneTitle;

    public RescueOrganisationDTO() {
    }

    /**
     *
     * @param rescueId
     * @param groupLogin
     * @param groupName
     * @param langKey
     * @param enabled
     * @param email
     * @param websiteURL
     * @param contactPerson
     * @param telephone
     * @param address
     * @param addressParts
     * @param point
     * @param rescueCallsign
     * @param zoneWKT
     * @param zoneTitle
     */
    public RescueOrganisationDTO(Long rescueId, String groupLogin, String groupName, String langKey,  boolean enabled,
                                 String email, String websiteURL, ParticipantDTO contactPerson, String telephone, String address,
                                 List<AddressPartDTO> addressParts, PointDTO point, String rescueCallsign,
                                 String zoneTitle, String zoneWKT) {

        super(rescueId, groupLogin, groupName, langKey, enabled, email, websiteURL, contactPerson, telephone, address);
        this.rescueCallsign = rescueCallsign;
        this.zoneWKT = zoneWKT;
        this.zoneTitle = zoneTitle;
    }

    public String getRescueCallsign() {
        return rescueCallsign;
    }

    public String getZoneWKT() {
        return zoneWKT;
    }

    public String getZoneTitle() {
        return zoneTitle;
    }
}
