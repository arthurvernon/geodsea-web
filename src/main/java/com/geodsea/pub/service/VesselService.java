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
@Transactional(rollbackFor = {ActionRefusedException.class})
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
    private AuthorityRepository authorityRepository;

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
    @PreAuthorize("hasRole('" + AuthoritiesConstants.ADMIN + "')")
    public Collection<Vessel> retrieveVesselsUserMaySee() throws ActionRefusedException {
        return vesselRepository.findAll();
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


    /**
     * An administrator or any person that is not currently disabled may register a vessel.
     * <p>If the owner is an organisation</p>
     *
     * @param vessel
     * @param owners
     * @param skipperLogins
     * @return
     * @throws ActionRefusedException
     */
    @PreAuthorize("isAuthenticated()")
    public Vessel registerVessel(Vessel vessel, long[] owners, long[] skipperLogins) throws ActionRefusedException {
        if (SecurityUtils.userHasRole(AuthoritiesConstants.ADMIN))
            return doVesselRegistration(vessel, owners, skipperLogins, null);

        Person person = personRepository.getByLogin(SecurityUtils.getCurrentLogin());
        if (person.isEnabled())
            return doVesselRegistration(vessel, owners, skipperLogins, person);

        throw new ActionRefusedException(ErrorCode.PERMISSION_DENIED, "User is disabled: " + person.getLogin());
    }

    /**
     * @param vessel
     * @param owners        IDs for owners
     * @param skipperLogins IDs for skippers
     * @param user          a non-administrator that must be a manager in the owning organisation or a specified owner
     * @return
     * @throws ActionRefusedException
     */
    private Vessel doVesselRegistration(Vessel vessel, long[] owners, long[] skipperLogins, Person user) throws ActionRefusedException {
        if (owners == null || owners.length == 0)
            throw new ActionRefusedException(ErrorCode.OWNER_NOT_DEFINED, "No owner defined");

        validateVessel(vessel);

        if (vesselRepository.findByHullIdentificationNumber(vessel.getHullIdentificationNumber()) != null)
            throw new ActionRefusedException(ErrorCode.DUPLICATE_HIN, "HIN already defined " + vessel.getHullIdentificationNumber());

        vessel = vesselRepository.save(vessel);

        // establish the owners....
        List<Participant> ownerParticipants = participantRepository.getParticipantsForIDs(owners);
        if (owners.length != ownerParticipants.size())
            throw new ActionRefusedException(ErrorCode.NO_SUCH_PARTICIPANT, "Expected " + owners.length +
                    " participants. Got only " + ownerParticipants.size());

        // ensure we find this user in the list of friends who are skippers.
        boolean lookingForUser = (user != null);
        int organisations = 0;
        int individuals = 0;
        Organisation organisation = null;
        for (Participant participant : ownerParticipants) {
            if (participant instanceof Group)
                throw new ActionRefusedException(ErrorCode.GROUP_CANNOT_OWN_VESSEL,
                        "Refused to register vessel to group: " + participant.getLogin());
            else if (participant instanceof Organisation) {

                // one we have organisation ownership we don't need to both with finding the user.
                lookingForUser = false;
                organisations++;
                organisation = (Organisation) participant;
                if (user != null && memberRepository.getActiveManager(participant.getId(), user.getId()) == null)
                    throw new ActionRefusedException(ErrorCode.NOT_A_MANAGER,
                            "Person: " + user.getLogin() + " is not an active manager of collective: " + participant.getLogin());
            } else {

                if (lookingForUser && participant.getId() == user.getId())
                    lookingForUser = false;
                individuals++;
            }
            addOwnerRole(participant);
            Owner theOwner = new Owner(vessel, participant);
            ownerRepository.save(theOwner);
        }

        if (organisations > 1)
            throw new ActionRefusedException(ErrorCode.ONE_ORGANISATION_OWNS_VESSEL,
                    "Refused to register vessel to multiple groups");
        if (individuals > 0 && organisations > 0)
            throw new ActionRefusedException(ErrorCode.OWNERSHIP_BY_PEOPLE_OR_ORGANISATION,
                    "Failed attempt to own a vessel by " + individuals + " people and " + organisations + " organisations");

        if (lookingForUser)
            throw new ActionRefusedException(ErrorCode.USER_MUST_OWN_VESSEL,
                    "User: " + user.getLogin() + " did not specify himself in private vessel ownership list");

        // establish the skippers....
        if (organisations > 0)
            registerSkippersInOrgansation(vessel, organisation, skipperLogins);
        else
            registerPrivateSkippers(vessel, skipperLogins);

        if(true)
            throw new ActionRefusedException(ErrorCode.USER_MUST_OWN_VESSEL,
                "User: " + user.getLogin() + " did not specify himself in private vessel ownership list");

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

    /**
     * Register any number of people who are members of an organisation as skippers of a vessel that is registered
     * to that organisation.
     *
     * @param vessel
     * @param organisation
     * @param skipperLogins
     * @throws ActionRefusedException if a person specified does not exist or is not a member of the organisation.
     */
    private void registerSkippersInOrgansation(Vessel vessel, Organisation organisation, long[] skipperLogins) throws ActionRefusedException {
        if (skipperLogins != null) {
            for (long login : skipperLogins) {
                Person person = personRepository.findOne(login);
                if (person == null)
                    throw new ActionRefusedException(ErrorCode.NO_SUCH_PARTICIPANT, "No such participant: " + login);
                if (memberRepository.getMemberByCollectiveIdAndParticipantId(organisation.getId(), login) == null)
                    throw new ActionRefusedException(ErrorCode.NO_SUCH_MEMBER,
                            "Participant: " + login + " is not a member of " + organisation.getLogin());

                Skipper skipper = new Skipper(vessel, person);
                addSkipperRole(person);
                skipperRepository.save(skipper);
            }
        }
    }

    private void addRole(Participant participant, String role)
    {
        if (!participant.hasAuthority(role)) {
            Authority authority = authorityRepository.findOne(role);
            participant.addAuthority(authority);
            participantRepository.save(participant);
        }
    }

    private void addOwnerRole(Participant participant)
    {
        addRole(participant, AuthoritiesConstants.OWNER);
    }

    private void addSkipperRole(Participant participant)
    {
        addRole(participant, AuthoritiesConstants.SKIPPER);
    }

    /**
     * Register any number of friends as skippers of the vessel
     *
     * @param vessel
     * @param skipperLogins
     * @throws ActionRefusedException
     */
    private void registerPrivateSkippers(Vessel vessel, long[] skipperLogins) throws ActionRefusedException {
        if (skipperLogins != null)
            for (long login : skipperLogins) {
                Person person = personRepository.findOne(login);
                if (person == null)
                    throw new ActionRefusedException(ErrorCode.NO_SUCH_PARTICIPANT, "No such participant: " + login);
                addSkipperRole(person);
                Skipper skipper = new Skipper(vessel, person);
                skipperRepository.save(skipper);
            }
    }

    /**
     * A vessel may be updated by an administrator or the owner of the vessel.
     *
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
     *
     * @param vessel
     */
    private void checkVesselUpdatePermission(Vessel vessel) throws ActionRefusedException {
        if (SecurityUtils.userHasRole(AuthoritiesConstants.ADMIN))
            return;

        Person person = personRepository.getByLogin(SecurityUtils.getCurrentLogin());
        if (!person.isEnabled())
            throw new ActionRefusedException(ErrorCode.PERMISSION_DENIED, "User is disabled: " + person.getLogin());

        Participant participantOwner = null;
        String errorCode = ErrorCode.PERMISSION_DENIED;
        String message = "No authority for person " + person.getLogin() + " to maintain vessel: " + vessel.getId();
        for (Owner owner : ownerRepository.findByVesselId(vessel.getId())) {
            participantOwner = owner.getParticipant();
            if (participantOwner.equals(person))
                return;
            else if (participantOwner instanceof Collective) {
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
