package com.geodsea.pub.web.rest.dto;

import java.util.Date;

public class MemberDTO {

    private Long id;

    private boolean manager;

    private Date memberSince;

    private Date memberUntil;

    private UserDTO contact;

    private String login;

    private String email;


    public MemberDTO() {
    }

    public MemberDTO(String login, String email) {
        this.login = login;
        this.email = email;
    }


    public String getLogin() {
        return login;
    }


    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "GroupDTO{" +
                "login='" + login + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
