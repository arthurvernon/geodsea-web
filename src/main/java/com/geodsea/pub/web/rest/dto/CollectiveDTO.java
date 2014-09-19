package com.geodsea.pub.web.rest.dto;


public abstract class CollectiveDTO extends ParticipantDTO {


    /**
     * Output only.
     * <p>If the contact person needs to be updated then this must be done with a specific service call.</p>
     */
    private ParticipantDTO contact;

    public CollectiveDTO() {
    }

    /**
     *
     * @param id ID of the group or the role that this participant performs
     * @param groupLogin
     * @param groupName
     * @param langKey
     * @param enabled
     * @param telephone
     * @param email
     * @param contact
     */
    public CollectiveDTO(Long id, String groupLogin, String groupName, String langKey, boolean enabled, String telephone,
                         String email, ParticipantDTO contact) {
        super(id, groupLogin, enabled, groupName, email, langKey, telephone);
        this.contact = contact;
    }


    public ParticipantDTO getContact() {
        return contact;
    }


    @Override
    public String toString() {
        return "GroupDTO{" +
                ", contactPerson=" + contact +
                "} " + super.toString();
    }
}
