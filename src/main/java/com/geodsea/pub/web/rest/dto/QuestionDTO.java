package com.geodsea.pub.web.rest.dto;

import org.apache.commons.lang.StringUtils;

/**
 * When presented to the client the question will be given.
 * <p>
 *     A response must contain the question answer and userid
 * </p>
 */
public class QuestionDTO {

    private String question;

    public QuestionDTO(){}

    public QuestionDTO(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean incomplete() {
        return StringUtils.isBlank(question);
    }
}