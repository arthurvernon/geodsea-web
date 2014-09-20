package com.geodsea.pub.service;

import com.geodsea.pub.domain.*;
import com.geodsea.pub.repository.*;
import com.geodsea.pub.security.AuthoritiesConstants;
import com.geodsea.pub.security.SecurityUtils;
import org.apache.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.*;

/**
 * Manage the vessels, owners and skippers.
 */
@Service
@Transactional
public class VesselService {

    private static final Logger log = Logger.getLogger(VesselService.class);

    @Inject
    Validator validator;

    @Inject
    private VesselRepository vesselRepository;

    @Inject
    private OwnerRepository ownerRepository;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private MemberRepository memberRepository;

    @Inject
    private ParticipantRepository participantRepository;

    @Inject
    private MonitorRepository monitorRepository;

    @Inject
    private CollectiveRepository collectiveRepository;

    @Inject
    private SkipperRepository skipperRepository;
    /**
     * Retrieve the vessels the user is permitted to access.
     * <p>
     * Administrators can view all vessels, but are unlikely to call this method direct.
     * Owners and skippers may retrieve vessel details at any time.
     * Rescue organisations can view vessels they are monitoring.
     * </p>
     *
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    public Collection<Vessel> retrieveVesselsUserMaySee() throws ActionRefusedException {
        if (SecurityUtils.userHasRole(AuthoritiesConstants.ADMIN))
            return vesselRepository.findAll();

        String login = SecurityUtils.getCurrentLogin();
        Person person =  personRepository.getByLogin(login);
        if (!person.isEnabled())
            throw new ActionRefusedException(ErrorCode.PERMISSION_DENIED, "User is disabled");

        Set<Vessel> allVessels = new HashSet<Vessel>();

        // all vessels that the person is a skipper of
        allVessels.addAll(getSkipperedVessels(person));

        List<Participant> participants = compilePersonAndMemberships(person);


        // vessels that (a) the person owns an active collective the person is a member of
        allVessels.addAll(getOwnedVessels(participants));


        // vessels that the person or a collective the person belongs to is monitoring
        allVessels.addAll(getMonitoredVessels(participants));

        return allVessels;
    }

    private List<Participant> compilePersonAndMemberships(Person person) {
        List<Participant> participants = new ArrayList<Participant>();
        participants.add(person);
        List<Collective> collectives = collectiveRepository.findHavingMember(person.getId());
        for (Collective c : collectives)
            if (c.isEnabled())
                participants.add(c);
        return participants;
    }

    private List<Vessel> getOwnedVessels(List<Participant> participants) {
        List<Vessel> all = new ArrayList<Vessel>();
        List<Owner> ownerships = ownerRepository.findForParticipant(participants);
        for (Owner owner : ownerships)
            all.add(owner.getVessel());

        return all;
    }

    private List<Vessel> getSkipperedVessels(Person person) {
        List<Vessel> all = new ArrayList<Vessel>();
        List<Skipper> skippers = skipperRepository.getSkipperByPerson(person);
        for (Skipper skipper : skippers)
            all.add(skipper.getVessel());

        return all;
    }

    private List<Vessel> getMonitoredVessels(List<Participant> participants) {
        List<Vessel> all = new ArrayList<Vessel>();
        List<Monitor> monitors = monitorRepository.findForParticipant(participants);
        for (Monitor monitor : monitors)
            all.add(monitor.getTrip().getVessel());

        return all;
    }

    /**
     * An administrator or any person that is not currently disabled may register a vessel.
     * <p>If the owner is an organisation</p>
     * @param vessel
     * @param owner
     * @param skipperLogins
     * @return
     * @throws ActionRefusedException
     */
    @PreAuthorize("isAuthenticated()")
    public Vessel registerVessel(Vessel vessel, String owner, String[] skipperLogins) throws ActionRefusedException {
        if (SecurityUtils.userHasRole(AuthoritiesConstants.ADMIN))
            return doVesselRegistration(vessel, owner, skipperLogins);

        Person person = personRepository.getByLogin(SecurityUtils.getCurrentLogin());
        if (person.isEnabled())
            return doVesselRegistration(vessel, owner, skipperLogins);

        throw new ActionRefusedException(ErrorCode.PERMISSION_DENIED, "User is disabled: " + person.getLogin());
    }

    private Vessel doVesselRegistration(Vessel vessel, String owner, String[] skipperLogins) throws ActionRefusedException {
        if (owner == null)
            throw new ActionRefusedException(ErrorCode.OWNER_NOT_DEFINED, "No owner defined");

        validateVessel(vessel);

        vessel = vesselRepository.save(vessel);

        // establish the owner....
        Participant ownerParticipant = participantRepository.getParticipantByLogin(owner);
        if (ownerParticipant == null)
            throw new ActionRefusedException(ErrorCode.NO_SUCH_PARTICIPANT, "No such participant: " + owner);
        Owner theOwner = new Owner(vessel, ownerParticipant);
        ownerRepository.save(theOwner);

        // establish the skippers....
        if (ownerParticipant instanceof Collective) {
            if (ownerParticipant instanceof Group)
                throw new ActionRefusedException(ErrorCode.GROUP_CANNOT_OWN_VESSEL,
                        "Refused to register vessel to group: " + ownerParticipant.getLogin());
            registerSkippersInOrgansation(vessel, (Organisation) ownerParticipant, skipperLogins);
        }
        else
            registerPrivateSkippers(vessel, ownerParticipant, skipperLogins);

        return vessel;

    }

    private void validateVessel(Vessel vessel) {
        Set<ConstraintViolation<Vessel>> constraintViolations = validator.validate(vessel);
        if (constraintViolations.size() > 0) {
            if (log.isDebugEnabled())
                for (ConstraintViolation<Vessel> cv : constraintViolations)
                    log.debug(cv.getMessage());
            throw new ConstraintViolationException(constraintViolations);
        }
    }

    private void registerSkippersInOrgansation(Vessel vessel, Organisation organisation, String[] skipperLogins) throws ActionRefusedException {
        for (String login : skipperLogins)
        {
            Person person = personRepository.getByLogin(login);
            if (person == null)
                throw new ActionRefusedException(ErrorCode.NO_SUCH_PARTICIPANT, "No such participant: " + login);
            if (memberRepository.getMemberByCollectiveIdAndParticipantLogin(organisation.getId(), login) == null)
                throw new ActionRefusedException(ErrorCode.NO_SUCH_MEMBER,
                        "Participant: " + login + " is not a member of " + organisation.getLogin());

            Skipper skipper = new Skipper(vessel, person);
            skipperRepository.save(skipper);
        }

    }

    private void registerPrivateSkippers(Vessel vessel, Participant ownerParticipant, String[] skipperLogins) throws ActionRefusedException {
        for (String login : skipperLogins)
        {
            Person person = personRepository.getByLogin(login);
            if (person == null)
                throw new ActionRefusedException(ErrorCode.NO_SUCH_PARTICIPANT, "No such participant: " + login);
            Skipper skipper = new Skipper(vessel, person);
            skipperRepository.save(skipper);
        }

    }

    /**
     * A vessel may be updated by an administrator or the owner of the vessel.
     * @param vessel
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    public Vessel updateVessel(Vessel vessel) throws ActionRefusedException {
        checkVesselUpdatePermission(vessel);
        validateVessel(vessel);
        return vesselRepository.save(vessel);
    }

    /**
     * Check if the person is an owner in his own right or a manager of an organisation that owns the vessel.
     * @param vessel
     */
    private void checkVesselUpdatePermission(Vessel vessel) throws ActionRefusedException {
        if (SecurityUtils.userHasRole(AuthoritiesConstants.ADMIN))
            return;

        Person person = personRepository.getByLogin(SecurityUtils.getCurrentLogin());
        if (! person.isEnabled())
            throw new ActionRefusedException(ErrorCode.PERMISSION_DENIED, "User is disabled: " + person.getLogin());

        Participant participantOwner = null;
        String errorCode = ErrorCode.PERMISSION_DENIED;
        String message = "No authority for person " + person.getLogin() + " to maintain vessel: " + vessel.getId();
        for (Owner owner : ownerRepository.findByVesselId(vessel.getId()))
        {
            participantOwner = owner.getParticipant();
            if (participantOwner.equals(person))
                return;
            else if (participantOwner instanceof Collective)
            {
                Member member = memberRepository.getMemberByCollectiveIdAndParticipantLogin(participantOwner.getId(),
                        person.getLogin());
                if (member != null) {
                    if (member.isActive())
                        if (member.isManager()) {
                            return;
                        } else {
                            errorCode = ErrorCode.NOT_A_MANAGER;
                            message = "Person: " + person.getLogin() + " is not a manager of " + participantOwner.getLogin();

                        }
                    else {
                        errorCode = ErrorCode.MEMBERSHIP_DISABLED;
                        message = "Disabled membership of " + participantOwner.getLogin() + " for person " + person.getLogin();
                    }

                }
            }
        }
    }

    @PreAuthorize("isAuthenticated()")
    public Vessel retrieveVessel(long vesselId) {

        // TODO security checks on vessel access
        return vesselRepository.findOne(vesselId);
    }

    @PreAuthorize("isAuthenticated()")
    public void deleteVessel(long vesselId) throws ActionRefusedException {

        Vessel vessel = vesselRepository.findOne(vesselId);
        checkVesselUpdatePermission(vessel);
        vesselRepository.delete(vessel);

    }
}
