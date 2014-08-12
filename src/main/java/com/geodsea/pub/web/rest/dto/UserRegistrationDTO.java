package com.geodsea.pub.web.rest.dto;

import java.util.List;

/**
 * Created by Arthur Vernon on 12/08/2014.
 */
public class UserRegistrationDTO extends UserDTO {

    private List<AddressPartDTO> addressParts;

    private PointDTO point;

    public UserRegistrationDTO() {
        super();
    }

    public UserRegistrationDTO(String login, String password, String firstName, String lastName, String email, String langKey,
                   String telephone, String streetAddress, List<AddressPartDTO> addressParts,
                   List<String> roles) {
        super(login, password, firstName, lastName, email, langKey, telephone, streetAddress, roles);
        this.addressParts = addressParts;
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

}
