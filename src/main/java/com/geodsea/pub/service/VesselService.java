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
     * There is no value in an administrator calling this method other than as an owner or a skipper.
     * Owners and skippers may retrieve vessel details at any time.
     * Rescue organisations can view vessels they are monitoring.
     * </p>
     *
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    public Collection<Vessel> retrieveOwnedAndSkipperedVessels() throws ActionRefusedException {
        Set<Vessel> all = new HashSet<Vessel>();
        all.addAll(retrieveOwnedVessels());
        all.addAll(retrieveSkipperedVessels());
        return all;
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

    /**
     * Get all vessels owned by the person or a collective that the person is an active member of.
     * @return
     * @throws ActionRefusedException
     */
    @PreAuthorize("isAuthenticated()")
    public Collection<Vessel> retrieveOwnedVessels() throws ActionRefusedException {
        Person user = personRepository.getByLogin(SecurityUtils.getCurrentLogin());
        List<Participant> userAndCollectives = new ArrayList<Participant>();
        userAndCollectives.add(user);
        userAndCollectives.addAll(collectiveRepository.findWherePersonIsActiveMember(user.getId()));
        Set<Vessel> all = new HashSet<Vessel>();
        all.addAll(getOwnedVessels(userAndCollectives));
        return all;
    }

    private Set<Vessel> getOwnedVessels(List<Participant> participants) {
        Set<Vessel> all = new HashSet<Vessel>();
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
            addOwnerRoleIfRequired(participant);
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

    private void addRoleIfRequired(Participant participant, String role) {
        if (!participant.hasAuthority(role)) {
            Authority authority = authorityRepository.findOne(role);
            participant.addAuthority(authority);
            SecurityUtils.addAuthority(role);
            participantRepository.save(participant);
        }
    }

    private void addOwnerRoleIfRequired(Participant participant) {
        addRoleIfRequired(participant, AuthoritiesConstants.OWNER);
    }

    private void addSkipperRole(Participant participant) {
        addRoleIfRequired(participant, AuthoritiesConstants.SKIPPER);
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

    @PreAuthorize("isAuthenticated()")
    public List<Vessel> retrieveSkipperedVessels() throws ActionRefusedException {
        Person person = personRepository.getByLogin(SecurityUtils.getCurrentLogin());
        if (!person.isEnabled())
            throw new ActionRefusedException(ErrorCode.USER_DISABLED, "User is disabled: " + person.getLogin());

        if (SecurityUtils.userHasRole(AuthoritiesConstants.SKIPPER)) {
            List<Skipper> skippers = skipperRepository.getSkipperByPerson(person);
            if (skippers.size() == 0)
                throw new ActionRefusedException(ErrorCode.NOT_A_SKIPPER, "User is not a skipper: " + person.getLogin());

            List<Vessel> vessels = new ArrayList<Vessel>();
            for (Skipper skipper : skippers)
                if (skipper.active())
                    vessels.add(skipper.getVessel());
            if (vessels.size() == 0)
                throw new ActionRefusedException(ErrorCode.NOT_ACTIVE_SKIPPER, "User is not an active skipper: " + person.getLogin());
            else
                return vessels;
        } else
            throw new ActionRefusedException(ErrorCode.NOT_A_SKIPPER, "User is not a skipper: " + person.getLogin());
    }

    /**
     * Get all the currently active skippers for the vessel.
     * <p>
     * User must be an administrator, or an active user who is either a skipper or an owner of the vessel.
     * As ownership may be by a collective, the person must be an active manager of that collective to be
     * recognized as an owner.
     * </p>
     *
     * @param vesselId
     * @return
     * @throws ActionRefusedException
     */
    @PreAuthorize("isAuthenticated()")
    public List<Skipper> retrieveSkippersForVessel(long vesselId) throws ActionRefusedException {
        Person person = personRepository.getByLogin(SecurityUtils.getCurrentLogin());
        if (!person.isEnabled())
            throw new ActionRefusedException(ErrorCode.USER_DISABLED, "User is disabled: " + person.getLogin());

        if (SecurityUtils.userHasRole(AuthoritiesConstants.ADMIN) ||
                personIsActiveSkipper(person.getId(), vesselId) ||
                personIsOwner(person.getId(), vesselId) ||
                personIsMemberOfOwnerCollective(person.getId(), vesselId, true, true))

            // Only active skippers
            return skipperRepository.getActiveSkippersOfVessel(vesselId);
        throw new ActionRefusedException(ErrorCode.PERMISSION_DENIED, "Person: " + person.getLogin() +
                " is not and owner, nor a skipper of vessel:" + vesselId);

    }

    private boolean personIsOwner(long personId, long vesselId) {
        return ownerRepository.getOwnerByParticipantIdAndVesselId(personId, vesselId) != null;
    }

    /**
     * Check if the person is an active skipper
     *
     * @param personId
     * @param vesselId
     * @return
     */
    private boolean personIsActiveSkipper(long personId, long vesselId) {
        Skipper skipper = skipperRepository.getSkipperByPersonIdAndVesselId(personId, vesselId);
        if (skipper == null)
            return false;
        return skipper.active();
    }

    /**
     * Does a simple check to see whether the person is a member of the organisation.
     * <p>
     * No check is performed as to whether the person is a manager or an active member.
     * </p>
     *
     * @param personId
     * @param vesselId
     * @return
     */
    private boolean personIsMemberOfOwnerCollective(long personId, long vesselId, boolean mustBeActive, boolean mustBeManager) {
        List<Owner> owners = ownerRepository.findByVesselId(vesselId);
        for (Owner o : owners)
            if (o.getParticipant() instanceof Collective) {
                for (Member m : ((Collective) o.getParticipant()).getMembers())
                    if (m.getParticipant().getId().equals(personId)) {
                        if (mustBeActive && !m.active())
                            continue;
                        if (mustBeManager & !m.isManager())
                            continue;
                        return true;
                    }
            }
        return false;
    }
}
