package com.geodsea.pub.service;

import com.geodsea.pub.domain.*;
import com.geodsea.pub.repository.*;
import com.geodsea.pub.security.AuthoritiesConstants;
import com.geodsea.pub.security.SecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.*;

/**
 * Created by Arthur Vernon on 13/09/2014.
 */
@Service
@Transactional
public class GroupService extends BaseService {

    private final Logger log = LoggerFactory.getLogger(GroupService.class);

    @Inject
    private ParticipantRepository participantRepository;

    @Inject
    protected MailService mailService;

    @Inject
    private SpringTemplateEngine templateEngine;

    @Inject
    private GroupRepository groupRepository;

    @Inject
    private OrganisationRepository organisationRepository;

    @Inject
    MemberRepository memberRepository;

    @Inject
    CollectiveRepository collectiveRepository;

    @Inject
    Validator validator;

    public Group updateFriends(long groupId, String email, String contactPersonLogin, String groupName, String langKey, boolean enabled) throws ActionRefusedException {

        Group group = groupRepository.findOne(groupId);
        if (group == null) {
            log.info("No such group: {}", groupId);
            throw new ActionRefusedException(ErrorCode.NO_SUCH_GROUP, "no such group: " + groupId);
        }

        applyChanges(group, contactPersonLogin, email, groupName, langKey, enabled);


        Set<ConstraintViolation<Group>> constraintViolations = validator.validate(group);
        if (constraintViolations.size() > 0) {
            if (log.isDebugEnabled())
                for (ConstraintViolation<Group> cv : constraintViolations)
                    log.debug(cv.getMessage());
            throw new ConstraintViolationException(constraintViolations);
        }

        group = groupRepository.save(group);

        return group;
    }

    public Organisation updateOrganisation(Long orgId, String email, String contactPersonLogin, String groupName,
                                           String langKey, boolean enabled, String websiteURL) throws ActionRefusedException {
        Organisation organisation = organisationRepository.findOne(orgId);
        if (organisation == null) {
            log.info("No such organisation: {}", orgId);
            throw new ActionRefusedException(ErrorCode.NO_SUCH_GROUP, "no such organisation: " + orgId);
        }
        applyChanges(organisation, contactPersonLogin, email, groupName, langKey, enabled);

        Set<ConstraintViolation<Organisation>> constraintViolations = validator.validate(organisation);
        if (constraintViolations.size() > 0) {
            if (log.isDebugEnabled())
                for (ConstraintViolation<Organisation> cv : constraintViolations)
                    log.debug(cv.getMessage());
            throw new ConstraintViolationException(constraintViolations);
        }

        organisation = organisationRepository.save(organisation);

        return organisation;

    }

    private void applyChanges(Collective collective, String contactPersonLogin, String email, String groupName, String langKey, boolean enabled) throws ActionRefusedException {
        updateContactPerson(collective, contactPersonLogin);
        collective.setEmail(email);
        collective.setCollectiveName(groupName);
        collective.setLangKey(langKey);

        // only allow the administrator to enable a disabled group.
        // The only way a user can do it is through the initial registration process.
        if (collective.isEnabled() != enabled) {
            if (enabled) {
                if (SecurityUtils.userHasRole(AuthoritiesConstants.ADMIN))
                    collective.setEnabled(true);
                else
                    throw new ActionRefusedException(ErrorCode.PERMISSION_DENIED, "Only an administrator may enable group: " + collective.getId());
            } else {
                collective.setEnabled(false);
            }
        }
    }

    private boolean updateContactPerson(Collective collective, String username) throws ActionRefusedException {
        if (StringUtils.isNotBlank(username))

            // is there a change to the username?
            if (!collective.getContactPerson().getParticipantName().equals(username)) {
                Person person = lookupPerson(username);
                Member member = memberRepository.getMemberByCollectiveIdAndParticipantParticipantName(collective.getId(), username);
                if (member == null) {
                    log.info("User: " + username + " is not a member of group: " + collective.getId());
                    throw new ActionRefusedException(ErrorCode.NOT_A_GROUP_MEMBER, "User: " + username + " is not a member of group: " + collective.getId());
                }
                collective.setContactPerson(person);
                return true;
            }
        return false;
    }

    /**
     *
     * @param participantName (hopefully) unique name for the group.
     * @param groupName name in the user's language that the group goes - this may not be unique.
     * @param langKey ISO (lowercase) langauge in which the group name is defined and which.
     * @param email     email address to which the registration confirmation can be sent.
     * @param username  optional, if specified the person who is to be setup as the contact person, otherwise the current user.
     * @param enabled honoured only if the person making the request is an administrator.
     * @param websiteURL
     * @param address
     * @param telephone
     * @return
     * @throws ActionRefusedException if the group name is already in use
     */
    @PreAuthorize("isAuthenticated()")
    public Organisation createOrganisation(String participantName, String langKey, String groupName, String email,
                                    String username, boolean enabled, String websiteURL,
                                    Address address, String telephone) throws ActionRefusedException {
        // if this is a new group then there should no be a participant with the same name
        Participant participant = participantRepository.getParticipantByParticipantName(groupName);
        if (participant != null) {
            log.info("An participant exists with the name {}", groupName);
            throw new ActionRefusedException(ErrorCode.USERNAME_ALREADY_EXISTS, "Participant name: " + groupName + " already exists");
        }

        // OK so we can setup the group.
        Organisation organisation = new Organisation();
        Member member = setupNewGroup(organisation, participantName, groupName, langKey, username, email, enabled);
        organisation.setWebsiteURL(websiteURL);
        organisation.setAddress(address);
        organisation.setTelephone(telephone);

        Set<ConstraintViolation<Organisation>> constraintViolations = validator.validate(organisation);
        if (constraintViolations.size() > 0) {
            if (log.isDebugEnabled())
                for (ConstraintViolation<Organisation> cv : constraintViolations)
                    log.debug(cv.getMessage());
            throw new ConstraintViolationException(constraintViolations);
        }

        organisationRepository.save(organisation);
        memberRepository.save(member);

        return organisation;
    }

    /**
     * Create a new group with participant name specified.
     * <p>
     * The contact person will be the user specified or the current user if no username is specified.
     * </p>
     *
     * @param participantName (hopefully) unique name for the group.
     * @param groupName name in the user's language that the group goes - this may not be unique.
     * @param langKey ISO (lowercase) langauge in which the group name is defined and which.
     * @param email     email address to which the registration confirmation can be sent.
     * @param username  optional, if specified the person who is to be setup as the contact person, otherwise the current user.
     * @param enabled honoured only if the person making the request is an administrator.
     * @throws ActionRefusedException if the group name is already in use
     */
    @PreAuthorize("isAuthenticated()")
    public Collective createFriends(String participantName, String langKey, String groupName, String email, String username, boolean enabled) throws ActionRefusedException {

        // if this is a new group then there should no be a participant with the same name
        Participant participant = participantRepository.getParticipantByParticipantName(groupName);
        if (participant != null) {
            log.info("An participant exists with the name {}", groupName);
            throw new ActionRefusedException(ErrorCode.USERNAME_ALREADY_EXISTS, "Participant name: " + groupName + " already exists");
        }

        // OK so we can setup the group.
        Group group = new Group();
        Member member = setupNewGroup(group, participantName, groupName, langKey, username, email, enabled);

        groupRepository.save(group);
        memberRepository.save(member);

        return group;
    }

    private Member setupNewGroup(Collective collective, String participantName, String groupName, String langKey,
                                 String username, String email, boolean enabled) throws ActionRefusedException {
        collective.setParticipantName(participantName);
        collective.setCollectiveName(groupName);
        collective.setLangKey(langKey);

        // if no username is offered then make the current user the contact person
        if (StringUtils.isBlank(username))
            username = SecurityUtils.getCurrentLogin();

        Person person = lookupPerson(username);

        collective.setEmail(email);

        if (enabled && SecurityUtils.userHasRole(AuthoritiesConstants.ADMIN))
            collective.setEnabled(true);
        else {
            ParticipantService.addRegistrationToken(collective);
            collective.setEnabled(false);
        }

        collective.setContactPerson(person);

        Member member = Member.createActiveManager(person, collective);
        collective.addMember(member);

        return member;
    }

    /**
     *
     * @param collective the group that is to be emailed.
     * @param baseUrl   the base URL for the website, if not specified no email will be sent
     */
    public void sendRegistrationEmail(Collective collective, String baseUrl)
    {
        if (StringUtils.isNotBlank(baseUrl)) {
            final Locale locale = Locale.forLanguageTag(collective.getLangKey());
            String content = createHtmlContentFromTemplate(collective, locale, baseUrl);
            mailService.sendActivationEmail(collective.getEmail(), content, locale);
        }

    }

    /**
     * Lookup the person.
     *
     * @param username
     * @return
     * @throws ActionRefusedException if the person does not exist or the name specified is not a person
     */
    private Person lookupPerson(String username) throws ActionRefusedException {
        Participant participant = participantRepository.getParticipantByParticipantName(username);
        if (participant == null) {
            log.info("No participant exists with the name {}", username);
            throw new ActionRefusedException(ErrorCode.NO_SUCH_USER, "No such user: " + username);
        } else if (!(participant instanceof Person)) {
            log.info("participant name {} is not a person", username);
            throw new ActionRefusedException(ErrorCode.NOT_A_PERSON, "Not a Person: " + username);
        }
        return (Person) participant;
    }


    /**
     * Create an email based upon the activationEmail.html template.
     *
     * @param participant
     * @param locale
     * @param baseUrl
     * @return the content for the email.
     */
    protected String createHtmlContentFromTemplate(final Participant participant, final Locale locale, String baseUrl) {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("participant", participant);
        variables.put("baseUrl", baseUrl);
        Context context = new Context(locale, variables);
        return templateEngine.process(MailService.EMAIL_ACTIVATION_PREFIX + MailService.TEMPLATE_SUFFIX, context);
    }

    public List<Group> getAllFriends() {
        return groupRepository.findAll();
    }

    public List<Organisation> getAllOrganisations() {
        return organisationRepository.findAll();
    }

    public Group lookupFriends(Long groupId) {
        return groupRepository.findOne(groupId);
    }
    public Organisation lookupOrganisation(Long groupId) {
        return organisationRepository.findOne(groupId);
    }

    public void deleteFriends(Long groupId) {
        groupRepository.delete(groupId);
    }

    public void deleteOrganisation(Long groupId) {
        organisationRepository.delete(groupId);
    }

    /**
     * Get all members of the group.
     * <p>
     * Available to administrators and to active managers of the group only!
     * </p>
     *
     * @param groupId
     * @return
     */
    @PreAuthorize("isAuthenticated()")
    public List<Member> getMembers(long groupId) throws ActionRefusedException {

        Person person = this.getPersonForPrincipal();
        if (person == null)
            throw new ActionRefusedException(ErrorCode.PERMISSION_DENIED, "User must be logged on to access group: " + groupId);

        Collective collective = groupRepository.findOne(groupId);
        if (collective == null)
            throw new ActionRefusedException(ErrorCode.NO_SUCH_GROUP, "No such group: " + groupId);

        if (SecurityUtils.userHasRole(AuthoritiesConstants.ADMIN))
            return collective.getMembers();

        Member member = memberRepository.getMemberByCollectiveIdAndParticipantParticipantName(groupId, person.getParticipantName());
        if (member == null)
            throw new ActionRefusedException(ErrorCode.PERMISSION_DENIED, "User: " + person.getParticipantName() +
                    " is not a member of Group: " + groupId);
        else if (member.isManager() && member.isActive())
            return collective.getMembers();

        throw new ActionRefusedException(ErrorCode.PERMISSION_DENIED, "User: " + person.getParticipantName() +
                " is not an active manager of Group: " + groupId);

    }

    /**
     * Active managers and administrators may add a new member to the group.
     *
     * @param groupName
     * @param participantName
     * @param active
     * @param manager
     * @param memberSince
     * @param memberUntil
     * @return
     * @throws ActionRefusedException
     */
    public Member addMember(String groupName, String participantName, boolean active, boolean manager, Date memberSince,
                            Date memberUntil) throws ActionRefusedException {

        Person person = this.getPersonForPrincipal();
        if (person == null)
            throw new ActionRefusedException(ErrorCode.PERMISSION_DENIED, "User must be logged on to add group members.");

        Collective collective = collectiveRepository.findByParticipantName(groupName);
        if (collective == null)
            throw new ActionRefusedException(ErrorCode.NO_SUCH_GROUP, "No such group: " + groupName);

        Participant participant = participantRepository.getParticipantByParticipantName(participantName);

        checkAdministratorOrActiveManager(collective);

        // is the user an admin user or an active manager of the group?
        Member member = new Member(participant, collective, active, manager, memberSince, memberUntil);
        member = memberRepository.save(member);

        // not sure if this is necessary, but just in case.
        collective.addMember(member);
        collectiveRepository.save(collective);

        return member;

    }


    @PreAuthorize("hasRole('" + AuthoritiesConstants.ADMIN + "')")
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    /**
     * Update the particulars of the specified member.
     *
     * @param memberId
     * @param active
     * @param manager
     * @param memberSince
     * @param memberUntil
     */
    public void updateMember(Long memberId, boolean active, boolean manager, Date memberSince, Date memberUntil) throws ActionRefusedException {



        Member member = memberRepository.findOne(memberId);
        if (member == null)
            throw new ActionRefusedException(ErrorCode.NO_SUCH_MEMBER, "No such member: " + memberId);

        checkAdministratorOrActiveManager(member.getCollective());

        member.setActive(active);
        member.setManager(manager);
        member.setMemberSince(memberSince);
        member.setMemberUntil(memberUntil);

        memberRepository.save(member);
    }

    /**
     * Get the details for the specified member.
     * <p>
     * User must be the member himself, an active manager or an administrator to get the member's details.
     * </p>
     *
     * @param memberId
     * @return a non-null member
     * @throws ActionRefusedException if there is no such member or the user does not have the permission
     *                                to access this member.
     */
    public Member getMember(Long memberId) throws ActionRefusedException {
        Member member = memberRepository.findOne(memberId);
        if (member == null)
            throw new ActionRefusedException(ErrorCode.NO_SUCH_MEMBER, "No such member: " + memberId);

        Person person = this.getPersonForPrincipal();

        //
        if (!member.getParticipant().getParticipantName().equals(person.getAnswer())) {
            checkAdministratorOrActiveManager(member.getCollective());
        }

        return member;
    }


    /**
     *
     * @param collective the group to check the permissions of the current user
     * @throws ActionRefusedException
     */
    private void checkAdministratorOrActiveManager(Collective collective) throws ActionRefusedException {

        if (!SecurityUtils.isAuthenticated())
            throw new ActionRefusedException(ErrorCode.PERMISSION_DENIED, "User is not authenticated");

        // is the user an admin user or an active manager of the group?
        if (SecurityUtils.userHasRole(AuthoritiesConstants.ADMIN))
            return;

        Person person = getPersonForPrincipal();
        Member loggedOnMember = memberRepository.getMemberByCollectiveIdAndParticipantParticipantName(collective.getId(), person.getParticipantName());

        if (loggedOnMember == null)
            throw new ActionRefusedException(ErrorCode.PERMISSION_DENIED, "User: " + SecurityUtils.getCurrentLogin() +
                    " is not member of Group: " + collective.getParticipantName());

        if (loggedOnMember.isManager() && loggedOnMember.isActive())
            return;

        throw new ActionRefusedException(ErrorCode.PERMISSION_DENIED, "User: " + SecurityUtils.getCurrentLogin() +
                " is not an active manager of Group: " + collective.getParticipantName());

    }

    public void deleteMember(Long memberId) throws ActionRefusedException {

        Member memberToDelete = memberRepository.findOne(memberId);
        if (memberToDelete == null)
            throw new ActionRefusedException(ErrorCode.NO_SUCH_MEMBER, "No such member: " + memberId);

        Collective collective = memberToDelete.getCollective();

        checkAdministratorOrActiveManager(collective);


        memberRepository.delete(memberId);
    }

}
