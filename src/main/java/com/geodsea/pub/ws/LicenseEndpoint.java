package com.geodsea.pub.ws;

import com.codahale.metrics.annotation.Timed;
import com.geodsea.ws.License;
import com.geodsea.ws.LicenseRequest;
import com.geodsea.ws.LicenseResponse;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;

/**
 * Sample licensing service
 */
@Endpoint
public class LicenseEndpoint {

    private static final String NAMESPACE_URI = "http://www.geodsea.com/License";

    private final Logger log = LoggerFactory.getLogger(LicenseEndpoint.class);

    public LicenseEndpoint()
    {
        super();
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "LicenseRequest")
    @Timed
    @ResponsePayload public LicenseResponse getLicense(@RequestPayload LicenseRequest request) {
        log.debug("REST request to obtain license details for license:{}", request.getLicenseNumber());
        LicenseResponse details = new LicenseResponse();
        GregorianCalendar c = new GregorianCalendar();
        XMLGregorianCalendar cal = new XMLGregorianCalendarImpl(c);
        License license = new License();
        license.setMaxPeople(2);
        license.setLicenseNumber(request.getLicenseNumber());
        license.setValidFrom(cal);
        license.setValidTo(cal);
        details.setCurrentLicense(license);
        return details;
    }

}
