package com.geodsea.pub.web.rest.dto;

import java.util.List;

public class OrganisationDTO extends GroupDTO {

    private String address;

    private List<AddressPartDTO> addressParts;

    private PointDTO point;

    public OrganisationDTO() {
    }

    public OrganisationDTO(Long orgId, String groupLogin, String groupName, String langKey, boolean enabled, String login, String email, ParticipantDTO contactPerson, String telephone,
                           String address, List<AddressPartDTO> addressParts, PointDTO point) {

        //Long groupId, String groupLogin, String groupName, String langKey, boolean enabled, String telephone,
        //String email, ParticipantDTO contact)

        super(orgId, groupLogin, groupName, langKey, enabled, telephone, email, contactPerson);
        this.address = address;
        this.addressParts = addressParts;
        this.point = point;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<AddressPartDTO> getAddressParts() {
        return addressParts;
    }

    public void setAddressParts(List<AddressPartDTO> addressParts) {
        this.addressParts = addressParts;
    }

    public PointDTO getPoint() {
        return point;
    }

    public void setPoint(PointDTO point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "OrganisationDTO{" +
                "address='" + address + '\'' +
                "} " + super.toString();
    }
}
