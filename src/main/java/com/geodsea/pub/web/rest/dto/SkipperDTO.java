package com.geodsea.pub.web.rest.dto;

/**
 * DTO for the skipper.
 */
public class SkipperDTO {

    Long skipperId;

    /**
     * Output only.
     * <p>If the contact person needs to be updated then this must be done with a specific service call.</p>
     */
    private UserDTO person;

    public SkipperDTO(Long skipperId, UserDTO person) {
        this.skipperId = skipperId;
        this.person = person;
    }

    public SkipperDTO() {
    }

    public Long getSkipperId() {
        return skipperId;
    }

    public void setSkipperId(Long skipperId) {
        this.skipperId = skipperId;
    }

    public UserDTO getPerson() {
        return person;
    }

    public void setPerson(UserDTO person) {
        this.person = person;
    }
}
