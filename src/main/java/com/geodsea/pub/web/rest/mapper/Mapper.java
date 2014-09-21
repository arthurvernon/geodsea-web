package com.geodsea.pub.web.rest.mapper;

import com.geodsea.pub.domain.*;
import com.geodsea.pub.service.GisService;
import com.geodsea.pub.web.rest.dto.*;

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
        if (person == null)
            return null;

        return new UserDTO(person.getId(),
                person.getLogin(),
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
        if (org == null)
            return null;
        //Long orgId, String groupLogin, String groupName, String langKey, boolean enabled, String login, String email, ParticipantDTO contactPerson, String telephone,
        //String address, List<AddressPartDTO> addressParts, PointDTO point

        return new OrganisationDTO(org.getId(), org.getLogin(), org.getCollectiveName(), org.getLangKey(),
                org.isEnabled(),
                org.getEmail(),
                org.getWebsiteURL(),
                participant(org.getContactPerson()),
                org.getTelephone(),
                org.getAddress() != null ? org.getAddress().getFormatted() : null
        );
    }

    public static VesselDTO vessel(Vessel vessel) {
        return new VesselDTO(vessel.getId(), vessel.getHullIdentificationNumber(), vessel.getVesselName(),
                vessel.getVesselType(), vessel.getHullColor(), vessel.getSuperstructureColor(), vessel.getLength(),
                vessel.getTotalHP(), vessel.getFuelCapacity(), vessel.getStorageType(), vessel.getEmergencyEquipment());
    }

    public static Vessel vessel(VesselDTO vessel) {
        return new Vessel(vessel.getId(), vessel.getHullIdentificationNumber(), vessel.getVesselName(),
                vessel.getVesselType(), vessel.getHullColor(), vessel.getSuperstructureColor(), vessel.getLength(),
                vessel.getTotalHP(), vessel.getFuelCapacity(), vessel.getStorageType(), vessel.getEmergencyEquipment());
    }

    public static LicensorDTO licensor(Licensor licensor) {
        Organisation org = licensor.getOrgansation();
        String zoneWKT = GisService.toWKT(licensor.getZone().getZone());

        return new LicensorDTO(licensor.getId(),
                org.getId(),
                org.getLogin(),
                org.getCollectiveName(),
                org.getLangKey(),
                org.isEnabled(),
                org.getEmail(),
                org.getWebsiteURL(),
                participant(org.getContactPerson()),
                org.getTelephone(),
                org.getAddress() != null ? org.getAddress().getFormatted() : null,
                null, null,  // no address details.
                licensor.getZone().getZoneTitle(),
                zoneWKT,
                licensor.getLicenceWsURL());
    }

    public static RescueOrganisationDTO rescueOrganisation(Rescue rescue) {
        if (rescue == null)
            return null;

        Organisation org = rescue.getOrgansation();

        String zoneWKT = GisService.toWKT(rescue.getZone().getZone());

        return new RescueOrganisationDTO(rescue.getId(), org.getLogin(), org.getCollectiveName(), org.getLangKey(),
                org.isEnabled(),
                org.getEmail(),
                org.getWebsiteURL(),
                participant(org.getContactPerson()),
                org.getTelephone(),
                org.getAddress() != null ? org.getAddress().getFormatted() : null,
                null, null,  // no address details.
                rescue.getCallsign(),
                rescue.getZone().getZoneTitle(),
                zoneWKT);
    }

    public static GroupDTO friends(Group group) {

        if (group == null)
            return null;
        return new GroupDTO(group.getId(), group.getLogin(), group.getCollectiveName(), group.getLangKey(),
                group.isEnabled(), null, group.getEmail(), participant(group.getContactPerson()));
    }

    public static MemberDTO member(Member member) {
        if (member == null)
            return null;

        Collective collective = member.getCollective();
        CollectiveDTO groupDTO = (collective instanceof Group) ? friends((Group) collective) : organisation((Organisation) collective);
        return new MemberDTO(member.getId(), participant(member.getParticipant()), groupDTO,
                member.isManager(), member.isActive(), member.getMemberSince(), member.getMemberUntil());
    }

    public static ParticipantDTO participant(Participant participant) {

        if (participant == null)
            return null;
        String name = null;
        if (participant instanceof Person) {
            Person p = (Person) participant;
            name = p.getFirstName() + " " + p.getLastName();
        } else {
            name = ((Collective) participant).getCollectiveName();
        }

        return new ParticipantDTO(participant.getId(), participant.getLogin(), participant.isEnabled(), name,
                participant.getEmail(), participant.getLangKey(), ((Person) participant).getTelephone());
    }

}
