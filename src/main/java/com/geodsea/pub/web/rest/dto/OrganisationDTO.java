package com.geodsea.pub.web.rest.dto;

import java.util.List;

public class OrganisationDTO extends GroupDTO {

    private String telephone;

    private String address;

    private List<AddressPartDTO> addressParts;

    private PointDTO point;

    public OrganisationDTO() {
    }

    public OrganisationDTO(Long orgId, String login, String email, UserDTO contactPerson, String telephone,
                           String address, List<AddressPartDTO> addressParts, PointDTO point,
                           List<String> roles) {
        super(orgId, login, email, contactPerson);
        this.telephone = telephone;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Override
    public String toString() {
        return "OrganisationDTO{" +
                "address='" + address + '\'' +
                "} " + super.toString();
    }
}
