package com.geodsea.pub.web.rest.dto;


public class QAndADTO {

    private String question;
    private String answer;

    public QAndADTO(){}

    public QAndADTO(String question) {
        this.question = question;
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


}