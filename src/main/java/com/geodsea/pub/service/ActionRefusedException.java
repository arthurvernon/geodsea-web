package com.geodsea.pub.service;

/**
 * Deal with refusals by the service layer as a result of invalid requests from the client.
 */
public class ActionRefusedException extends Exception {

    public ActionRefusedException() {
    }

    public ActionRefusedException(String message) {
        super(message);
    }

    public ActionRefusedException(String message, Throwable cause) {
        super(message, cause);
    }
}
