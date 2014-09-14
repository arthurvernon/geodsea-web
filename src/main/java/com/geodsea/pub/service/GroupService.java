package com.geodsea.pub.service;

import com.geodsea.pub.domain.Group;
import com.geodsea.pub.domain.Member;
import com.geodsea.pub.domain.Participant;
import com.geodsea.pub.domain.Person;
import com.geodsea.pub.repository.GroupRepository;
import com.geodsea.pub.repository.MemberRepository;
import com.geodsea.pub.repository.ParticipantRepository;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    MemberRepository memberRepository;


    public void updateGroup(long groupId, String email, String username) throws ActionRefusedException {

        Group group = groupRepository.findOne(groupId);
        if (group == null) {
            log.info("No such group: {}", groupId);
            throw new ActionRefusedException(ErrorCode.NO_SUCH_GROUP, "no such group: " + groupId);
        }

        boolean updated = updateContactPerson(group, username);
        updated = updateEmail(group, email) || updated;

        if (updated)
            groupRepository.save(group);

    }

    private boolean updateEmail(Group group, String email) {
        if (StringUtils.isNotBlank(email))
            if (!email.equals(group.getEmail())) {
                group.setEmail(email);
                return true;
            }
        return false;
    }

    private boolean updateContactPerson(Group group, String username) throws ActionRefusedException {
        if (StringUtils.isNotBlank(username))

            // is there a change to the username?
            if (!group.getContactPerson().getParticipantName().equals(username)) {
                Person person = lookupPerson(username);
                Member member = memberRepository.getMemberByGroupIdAndParticipantParticipantName(group.getId(), username);
                if (member == null) {
                    log.info("User: " + username + " is not a member of group: " + group.getId());
                    throw new ActionRefusedException(ErrorCode.NOT_A_GROUP_MEMBER, "User: " + username + " is not a member of group: " + group.getId());
                }
                group.setContactPerson(person);
                return true;
            }
        return false;
    }

    /**
     * Create a new group with participant name specified.
     * <p>
     * The contact person will be the user specified or the current user if no username is specified.
     * </p>
     *
     * @param groupName (hopefully) unique name for the group.
     * @param email     email address to which the registration confirmation can be sent.
     * @param username  optional, if specified the person who is to be setup as the contact person, otherwise the current user.
     * @param baseUrl   the base URL for the website, if not specified no email will be sent
     * @throws ActionRefusedException if the group name is already in use
     */
    @PreAuthorize("isAuthenticated()")
    public Group createGroup(String groupName, String email, String username, String baseUrl) throws ActionRefusedException {

        // if this is a new group then there should no be a participant with the same name
        Participant participant = participantRepository.getParticipantByParticipantName(groupName);
        if (participant != null) {
            log.info("An participant exists with the name {}", groupName);
            throw new ActionRefusedException(ErrorCode.USERNAME_ALREADY_EXISTS, "Participant name: " + groupName + " already exists");
        }

        // OK so we can setup the group.
        Group group = new Group();
        group.setParticipantName(groupName);
        ParticipantService.addRegistrationToken(group);

        // if no username is offered then make the current user the contact person
        if (StringUtils.isBlank(username))
            username = SecurityUtils.getCurrentLogin();

        Person person = lookupPerson(username);

        group.setEmail(email);
        group.setEnabled(false);
        group.setContactPerson(person);

        Member member = Member.createActiveManager(person, group);
        group.addMember(member);

        groupRepository.save(group);
        memberRepository.save(member);

        if (StringUtils.isNotBlank(baseUrl)) {
            final Locale locale = Locale.forLanguageTag(person.getLangKey());
            String content = createHtmlContentFromTemplate(group, locale, baseUrl);
            mailService.sendActivationEmail(group.getEmail(), content, locale);
        }
        return group;
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

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group lookupGroup(Long groupId) {
        return groupRepository.findOne(groupId);
    }

    public void deleteGroup(Long groupId) {
        groupRepository.delete(groupId);
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

        Group group = groupRepository.findOne(groupId);
        if (group == null)
            throw new ActionRefusedException(ErrorCode.NO_SUCH_GROUP, "No such group: " + groupId);

        if (SecurityUtils.userHasRole(AuthoritiesConstants.ADMIN))
            return group.getMembers();

        Member member = memberRepository.getMemberByGroupIdAndParticipantParticipantName(groupId, person.getParticipantName());
        if (member == null)
            throw new ActionRefusedException(ErrorCode.PERMISSION_DENIED, "User: " + person.getParticipantName() +
                    " is not a member of Group: " + groupId);
        else if (member.isManager() && member.isActive())
            return group.getMembers();

        throw new ActionRefusedException(ErrorCode.PERMISSION_DENIED, "User: " + person.getParticipantName() +
                " is not an active manager of Group: " + groupId);

    }
}
