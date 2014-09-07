package com.geodsea.pub.web.rest.mapper;

import com.geodsea.pub.domain.Person;
import com.geodsea.pub.web.rest.dto.UserDTO;

import java.util.List;

/**
 * Created by Arthur Vernon on 7/09/2014.
 */
public class Mapper {

    /**
     * @param person non-null person
     * @param roles optional set of roles. Required if supplying the user logged on to the client.
     * @return
     */
    public static UserDTO dto(Person person, List<String> roles) {
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
}
