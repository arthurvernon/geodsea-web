package com.geodsea.pub.web.rest.dto;


import org.apache.commons.lang.StringUtils;

public class AnswerDTO extends QuestionDTO {

    private String username;
    private String answer;

    public AnswerDTO() {
    }

    public AnswerDTO(String username, String question, String answer) {
        super(question);
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean incomplete() {
        return super.incomplete() || StringUtils.isBlank(answer)
                || StringUtils.isBlank(username);
    }
}