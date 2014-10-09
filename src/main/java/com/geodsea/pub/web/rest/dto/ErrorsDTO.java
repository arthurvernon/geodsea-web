package com.geodsea.pub.web.rest.dto;

import java.util.List;

/**
 * A wrapper around one or more error objects to avoid the problem on the client of discerning whether the return object
 * is an array or a singleton.
 */
public class ErrorsDTO {
    List<ErrorDTO> errors;

    public ErrorsDTO(List<ErrorDTO> errors) {
        this.errors = errors;
    }

    public List<ErrorDTO> getErrors() {
        return errors;
    }
}
