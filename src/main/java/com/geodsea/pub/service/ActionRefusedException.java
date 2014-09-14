package com.geodsea.pub.service;

/**
 * Deal with refusals by the service layer as a result of invalid requests from the client.
 */
public class ActionRefusedException extends Exception {



    /**
     * The code to allow a lookup of a message on the client.
     */
    private final String code;

    public ActionRefusedException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ActionRefusedException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
