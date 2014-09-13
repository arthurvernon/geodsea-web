package com.geodsea.pub.web.rest.mapper;

import com.geodsea.pub.domain.Organisation;
import com.geodsea.pub.domain.Person;
import com.geodsea.pub.web.rest.dto.OrganisationDTO;
import com.geodsea.pub.web.rest.dto.UserDTO;

import java.util.List;

/**
 * One place where the mapping between DTO and bean is performed.
 */
public class Mapper {

    /**
     * @param person non-null person
     * @param roles  optional set of roles. Required if supplying the user logged on to the client.
     * @return
     */
    public static UserDTO user(Person person, List<String> roles) {
        return new UserDTO(
                person.getParticipantName(),
                null,
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

}
