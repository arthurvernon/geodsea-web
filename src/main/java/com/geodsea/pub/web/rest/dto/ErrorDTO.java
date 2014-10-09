package com.geodsea.pub.web.rest.dto;

/**
 * Created by Arthur Vernon on 9/10/2014.
 */
public class ErrorDTO {

    private String messageKey;

    private String field;

    public ErrorDTO(String field, String messageKey) {
        this.messageKey = messageKey;
        this.field = field;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String getField() {
        return field;
    }
}
