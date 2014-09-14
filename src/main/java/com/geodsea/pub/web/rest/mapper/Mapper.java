package com.geodsea.pub.web.rest.mapper;

import com.geodsea.pub.domain.*;
import com.geodsea.pub.web.rest.dto.*;

import java.util.Date;
import java.util.List;

/**
 * One place where the mapping between DTO and bean is performed.
 */
public class Mapper {

    /**
     * Map everything except the user's password and aspects of the address that are not retained.
     *
     * @param person non-null person
     * @param roles  optional set of roles. Required if supplying the user logged on to the client.
     * @return
     */
    public static UserDTO user(Person person, List<String> roles) {
        return new UserDTO(person.getId(),
                person.getParticipantName(),
                person.isEnabled(),
                person.getFirstName(),
                person.getLastName(),
                person.getEmail(),
                person.getLangKey(),
                person.getTelephone(),
                person.getQuestion(),
                person.getAnswer(),
                person.getAddress() != null ? person.getAddress().getFormatted() : null,
                null, null,
                roles);
    }

    public static OrganisationDTO organisation(Organisation org) {
        return new OrganisationDTO(org.getId(),
                org.isEnabled(),
                org.getParticipantName(),
                org.getEmail(),
                user(org.getContactPerson(), null),
                org.getTelephone(),
                org.getAddress() != null ? org.getAddress().getFormatted() : null,
                null,
                null);
    }

    public static GroupDTO group(Group group) {
        return new GroupDTO(group.getId(), group.isEnabled(), group.getParticipantName(), group.getEmail(),
                group.getContactPerson().getParticipantName(),
                Mapper.user(group.getContactPerson(), null));
    }

    public static MemberDTO member(Member member) {
        return new MemberDTO(member.getId(), member.isManager(), member.isActive(), member.getMemberSince(), member.getMemberUntil(),
                participant(member.getParticipant()));
    }

    private static ParticipantDAO participant(Participant participant) {
        if (participant instanceof Person)
            return user((Person) participant, null);
        else if (participant instanceof Organisation)
            return organisation((Organisation) participant);
        else
            return group((Group) participant);
    }
}
