package com.geodsea.pub.web.rest.dto;

import java.util.List;

/**
 * Created by Arthur Vernon on 10/09/2014.
 */
public class RescueOrganisationDTO extends OrganisationDTO {

    /**
     * Editable by a manager in the group
     */
    private String rescueCallsign;

    /**
     * Editable by a manager in the group
     */
    private String rescueZoneTitle;

    /**
     * Editable by a manager in the group
     */
    private String rescueZone;

    public RescueOrganisationDTO() {
    }

    public RescueOrganisationDTO(Long orgId, String groupLogin, String groupName, String langKey,  boolean enabled,
                                 String login, String email, UserDTO contactPerson, String telephone, String address,
                                 List<AddressPartDTO> addressParts, PointDTO point, String rescueCallsign,
                                 String rescueZoneTitle, String rescueZone) {

        super(orgId, groupLogin, groupName, langKey, enabled, login, email, contactPerson, telephone, address, addressParts, point);
        this.rescueCallsign = rescueCallsign;
        this.rescueZoneTitle = rescueZoneTitle;
        this.rescueZone = rescueZone;
    }

    public String getRescueCallsign() {
        return rescueCallsign;
    }

    public void setRescueCallsign(String rescueCallsign) {
        this.rescueCallsign = rescueCallsign;
    }

    public String getRescueZoneTitle() {
        return rescueZoneTitle;
    }

    public void setRescueZoneTitle(String rescueZoneTitle) {
        this.rescueZoneTitle = rescueZoneTitle;
    }

    public String getRescueZone() {
        return rescueZone;
    }

    public void setRescueZone(String rescueZone) {
        this.rescueZone = rescueZone;
    }
}
