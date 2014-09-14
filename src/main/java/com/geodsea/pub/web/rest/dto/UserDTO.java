package com.geodsea.pub.web.rest.dto;

import java.util.List;

public class UserDTO extends ParticipantDAO {


    /**
     * Only defined when user first creates an account.
     */
    private String password;
    
    private String firstName;
    
    private String lastName;

    private String telephone;
    
    private String langKey;

    private String question;

    private String answer;

    private String address;

    private List<AddressPartDTO> addressParts;

    private PointDTO point;

    private List<String> roles;

    public UserDTO() {
    }

    public UserDTO(long participantId, String login, boolean enabled, String firstName, String lastName, String email, String langKey,
                   String telephone, String question, String answer, String address, List<AddressPartDTO> addressParts, PointDTO point,
                   List<String> roles) {
        super(participantId, login, enabled, email);
        this.firstName = firstName;
        this.lastName = lastName;
        this.langKey = langKey;
        this.telephone = telephone;
        this.question = question;
        this.answer = answer;
        this.address = address;
        this.addressParts = addressParts;
        this.point = point;
        this.roles = roles;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLangKey() {
        return langKey;
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserDTO{");
        sb.append("login='").append(getLogin()).append('\'');
        if(password != null) {
            sb.append(", password='").append(password.length()).append('\'');
        }
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", telephone='").append(telephone).append('\'');
        sb.append(", address='").append(address).append('\'');
        sb.append(", question='").append(question).append('\'');
        sb.append(", answer='").append(answer).append('\'');
        sb.append(", langKey='").append(langKey).append('\'');
        sb.append(", roles=").append(roles);
        sb.append("} ");
        sb.append(super.toString());

        return sb.toString();
    }
}
