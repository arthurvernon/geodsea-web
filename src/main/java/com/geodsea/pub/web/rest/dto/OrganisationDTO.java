package com.geodsea.pub.web.rest.dto;

import java.util.List;

public class OrganisationDTO extends CollectiveDTO {

    private String websiteURL;

    /**
     * on the way out
     */
    private String address;

    /**
     * Only set when incoming from the client
     */
    private List<AddressPartDTO> addressParts;

    /**
     * Only set when incoming from the client
     */
    private PointDTO point;

    public OrganisationDTO() {
    }

    /**
     *  @param id ID of the organisation or the role that this organisation performs
     * @param groupLogin
     * @param groupName
     * @param langKey
     * @param enabled
     * @param email
     * @param websiteURL
     * @param contactPerson
     * @param telephone
     * @param address
     */
    public OrganisationDTO(Long id, String groupLogin, String groupName, String langKey, boolean enabled,
                           String email, String websiteURL, ParticipantDTO contactPerson, String telephone,
                           String address) {

        super(id, groupLogin, groupName, langKey, enabled, telephone, email, contactPerson);
        this.address = address;
        this.websiteURL = websiteURL;
    }

    public String getAddress() {
        return address;
    }


    public List<AddressPartDTO> getAddressParts() {
        return addressParts;
    }


    public PointDTO getPoint() {
        return point;
    }

    public String getWebsiteURL() {
        return websiteURL;
    }

    @Override
    public String toString() {
        return "OrganisationDTO{" +
                "address='" + address + '\'' +
                "} " + super.toString();
    }
}
