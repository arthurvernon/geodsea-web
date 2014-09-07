package com.geodsea.pub.service;

import com.geodsea.pub.domain.*;
import com.geodsea.pub.repository.LicensorRepository;
import com.geodsea.pub.repository.OrganisationRepository;
import com.geodsea.ws.LicenseRequest;
import com.geodsea.ws.LicenseResponse;
import com.geodsea.ws.ObjectFactory;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.WebServiceException;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class LicenseService {

    private final Logger log = LoggerFactory.getLogger(LicenseService.class);

    @Inject
    private LicensorRepository licensorRepository;

    @Inject
    private OrganisationRepository organisationRepository;

    @Inject
    private WebServiceTemplate webServiceTemplate;

    @Inject
    private HttpComponentsMessageSender messageSender;


    /**
     *
     * @param existingLicensorId
     * @param organisationId
     * @param webServiceURL
     * @param zone
     * @throws ActionRefusedException if the zone is invalid.
     */
    public void addOrUpdateLicensor(Long existingLicensorId, long organisationId, String webServiceURL, Zone zone)  {
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
        licensor.setZone(zone);

        Organisation organisation = organisationRepository.findOne(organisationId);
        licensor.setOrgansation(organisation);

        licensor = licensorRepository.save(licensor);
    }


    /**
     * Obtain from the liscensor the details pertaining to the specified registration/license number.
     * @param licensor
     * @param licenseNumber
     * @return
     */
    public LicenseResponse lookupLicense(Licensor licensor, String licenseNumber) {

        log.info("Looking up license: {} from licensor {} via WS: {}", licenseNumber,
                licensor.getOrgansation().getPublishedName(), licensor.getLicenceWsURL());

        ObjectFactory of = new ObjectFactory();
        LicenseRequest request = of.createLicenseRequest();
        request.setLicenseNumber(licenseNumber);

        if (licensor.getLicenseWsUsername() != null)
            messageSender.setCredentials(new UsernamePasswordCredentials(
                    licensor.getLicenseWsUsername(),
                    licensor.getLicenseWsPassword()));
        else
            messageSender.setCredentials(null);

        try {
            LicenseResponse response = (LicenseResponse) webServiceTemplate.marshalSendAndReceive(licensor.getLicenceWsURL(), request);
            return response;
        }
        catch (WebServiceException ex)
        {
            log.warn("Request to " + licensor.getLicenceWsURL() + " for license: "
                    + licenseNumber + " failed. Cause:\n " + ex.getMessage());
            return null;
        }
    }

    /**
     * Provide a list of Licensors that issue licenses for the location identified by the address.
     * <p>
     *     There may not be a licensor defined for this location, in which case, no licensors will be returned.
     * </p>
     * <p>
     *     Typically there would be one licensor defined where a system is in use.
     * </p>
     * <p>
     *     Depending upon how boundaries are defined by authorities, there may be an overlap where this address
     *     is within the area defined for two or more licensors. This can occur on state and international boundaries
     *     e.g. along a river where for the sake of simplicity an authority marks a straight line that includes territory
     *     within an adjacent jurisdiction.
     * </p>
     * @param address the point to resolve a licensing authority for.
     * @return a non-null list of zero or more authorities.
     */
    public List<Licensor> getLocalLicensor(Address address)
    {
        if (address == null)
            return new ArrayList<Licensor>();
        return licensorRepository.getLicensorForLocation(address.getPoint());
    }
}
