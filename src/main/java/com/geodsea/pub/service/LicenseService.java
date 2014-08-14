package com.geodsea.pub.service;

import com.geodsea.pub.domain.Licensor;
import com.geodsea.pub.domain.ParticipantGroup;
import com.geodsea.pub.repository.LicensorRepository;
import com.geodsea.pub.repository.ParticipantGroupRepository;
import com.geodsea.ws.LicenseRequest;
import com.geodsea.ws.LicenseResponse;
import com.geodsea.ws.ObjectFactory;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import javax.inject.Inject;

@Service
@Transactional
public class LicenseService {

    private final Logger log = LoggerFactory.getLogger(LicenseService.class);

    @Inject
    private LicensorRepository licensorRepository;

    @Inject
    private ParticipantGroupRepository participantGroupRepository;

    @Inject
    private WebServiceTemplate webServiceTemplate;

    @Inject
    private HttpComponentsMessageSender messageSender;

    public void addOrUpdateLicensor(Long existingLicensorId, long participantGroupId, String webServiceURL, String region) {
        Licensor licensor = null;

        // this convoluted piece of rubbish is required for testing because the client
        // in theory cannot provide an identity.
        // TODO include identity object by client.
        if (existingLicensorId != null)
            licensor = licensorRepository.findOne(existingLicensorId);
        if (licensor == null)
        {
            licensor = new Licensor();
            licensor.setId(existingLicensorId);
        }

        licensor.setLicenceWsURL(webServiceURL);
        licensor.setRegion(region);

        // TODO add in text area to define a region
//        licensor.setJurisdiction();
        ParticipantGroup participant = participantGroupRepository.findOne(participantGroupId);
        licensor.setParticipant(participant);

        licensor = licensorRepository.save(licensor);
    }

    // send to an explicit URI
    public LicenseResponse customSendAndReceive(Licensor licensor, String licenseNumber) {
        ObjectFactory of = new ObjectFactory();
        LicenseRequest request = of.createLicenseRequest();
        request.setLicenseNumber(licenseNumber);

        if (licensor.getLicenseWsUsername() != null)
            messageSender.setCredentials(new UsernamePasswordCredentials(licensor.getLicenseWsUsername(), licensor.getLicenseWsPassword()));
        else
            messageSender.setCredentials(null);

        LicenseResponse response = (LicenseResponse) webServiceTemplate.marshalSendAndReceive(licensor.getLicenceWsURL(), request);

        return response;
    }
}
