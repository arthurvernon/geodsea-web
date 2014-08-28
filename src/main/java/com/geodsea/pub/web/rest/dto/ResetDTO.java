package com.geodsea.pub.web.rest.dto;


import org.apache.commons.lang.StringUtils;

public class ResetDTO extends AnswerDTO {

    private String password;

    public ResetDTO(){}

    public ResetDTO(String username, String question, String answer, String password){
        super(username, question, answer);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean incomplete() {
        return super.incomplete() || StringUtils.isBlank(password);
    }

}