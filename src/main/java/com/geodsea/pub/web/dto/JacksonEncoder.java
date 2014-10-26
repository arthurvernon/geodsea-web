package com.geodsea.pub.web.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.config.managed.Encoder;

import java.io.IOException;

/**
 * Integrate Jackson encoding into Atmosphere.
 */
public class JacksonEncoder implements Encoder<JacksonEncoder.Encodable, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String encode(Encodable m) {
        try {
            return mapper.writeValueAsString(m);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Marker interface for Jackson.
     */
    public static interface Encodable {
    }
}