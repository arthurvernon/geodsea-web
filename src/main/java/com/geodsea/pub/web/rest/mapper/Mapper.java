package com.geodsea.pub.web.rest.mapper;

import com.geodsea.common.dto.*;
import com.geodsea.common.type.FeatureType;
import com.geodsea.pub.domain.*;
import com.geodsea.pub.domain.Person;
import com.geodsea.pub.domain.Vessel;
import com.geodsea.common.type.VesselType;
import com.geodsea.pub.service.GisService;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import org.geojson.Crs;
import org.geojson.Feature;
import org.geojson.LngLatAlt;
import org.geojson.Point;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

/**
 * One place where the mapping between DTO and bean is performed.
 */
public class Mapper {

    /**
     * Convert a {@link ConstraintViolationException} into an ErrorsDTO object to ship to the client.
     * @param ex a non-null constraint violation exception containing at least one
     * @return
     */
    public static ErrorsDTO errors(ConstraintViolationException ex) {
        List<ErrorDTO> list = new ArrayList<ErrorDTO>(ex.getConstraintViolations().size());
        for (ConstraintViolation<?> cv : ex.getConstraintViolations()) {
            list.add(new ErrorDTO(FieldCorrelator.translate(cv.getRootBeanClass(), cv.getPropertyPath().toString()),
                    cv.getMessageTemplate()));
        }

        return new ErrorsDTO(list);
    }


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
                vessel.getTotalHP(), vessel.getFuelCapacity(), vessel.getStorageType(), vessel.getStorageLocation(),
                vessel.getEmergencyEquipment());
    }

    public static Vessel vessel(VesselDTO vessel) {
        return new Vessel(vessel.getId(), vessel.getHullIdentificationNumber(), vessel.getVesselName(),
                vessel.getVesselType(), vessel.getHullColor(), vessel.getSuperstructureColor(), vessel.getLength(),
                vessel.getTotalHP(), vessel.getFuelCapacity(), vessel.getStorageType(), vessel.getStorageLocation(), vessel.getEmergencyEquipment());
    }

    public static SkipperTripDTO tripSkipper(TripSkipper tripSkipper, Feature feature) {
        return new SkipperTripDTO(
                tripSkipper.getId(),
                skipper(tripSkipper.getSkipper()),
                vessel(tripSkipper.getVessel()),
                rescueOrganisation(tripSkipper.getRescue()),
                tripSkipper.getHeadline(),
                tripSkipper.getSummary(),
                tripSkipper.getPeopleOnBoard(),
                tripSkipper.getFuelOnBoard(),
                tripSkipper.getScheduledStartTime(),
                tripSkipper.getActualStartTime(),
                tripSkipper.getScheduledEndTime(),
                tripSkipper.getActualEndTime(),
                feature);
    }

    /**
     * Creates a vessel location where the point is exactly as specified in the database.
     * @param locationTime
     * @param vessel
     * @return
     */
    public static VesselLocationDTO vesselLocation(LocationTime locationTime, Vessel vessel)
    {
        // TODO figure out which CRS to display in.
        return new VesselLocationDTO(
                vessel(vessel),
                new Point(locationTime.getLocation().getX(),locationTime.getLocation().getY()),
                locationTime.getAccuracy(),
                locationTime.getGpsSignalTime(),
                locationTime.getBearing(),
                locationTime.getSpeedMetresSec());
    }

    /**
     *
     * @param trip
     * @return
     */
    public static SkipperTripDTO tripSkipper(TripSkipper trip) {
        return tripSkipper(trip, feature(trip.getWayPoints(), FeatureType.WAY_POINTS, null));
    }


    /**
     *
     * @param lineString
     * @param featureType
     * @param properties
     * @return
     */
    public static Feature feature(LineString lineString, FeatureType featureType , Map<String, Object> properties) {
        if (lineString == null)
            return null;

        int crs = crs = lineString.getSRID();
        Coordinate[] coords = lineString.getCoordinates();
        LngLatAlt[] values = new LngLatAlt[coords.length];
        for (int i = 0; i < coords.length; i++)
            values[i] = new LngLatAlt(coords[i].x, coords[i].y, coords[i].z);

        org.geojson.LineString ls = new org.geojson.LineString(values);
        Feature feature = new Feature();
        feature.setId(featureType.getIdString());
        feature.setGeometry(ls);
        feature.setCrs(crs(crs));
        if (properties != null)
            feature.setProperties(properties);
        return feature;
    }


    /**
     * Create a CRS object containing the EPSG code as a property.
     * @param crsCode
     * @return
     */
    public static Crs crs(int crsCode)
    {
//        String name = "urn:ogc:def:crs:EPSG::4326";
//        String name = "EPSG:4326";
//        String name = "EPSG:3857";
        Crs crs = new Crs();
        Map<String, Object> p = new HashMap<String, Object>();
        GisService.addEpsgCodeToMap(p, crsCode);
        crs.setProperties(p);
        return crs;
    }

    public static SkipperDTO skipper(Skipper skipper)
    {
        return new SkipperDTO(skipper.getId(), participant(skipper.getPerson()), vessel(skipper.getVessel()),
                skipper.getGrantedFrom(), skipper.getGrantedTo(), skipper.isSuspended());
    }

    public static LicensorDTO licensor(Licensor licensor) {
        Organisation org = licensor.getOrganisation();
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

        Organisation org = rescue.getOrganisation();

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

    /**
     * Create a vessel from the details within a license query.
     * <p>
     *     Details excludes emergency equipment and the storage location/type.
     * </p>
     * @param vessel data returned from web service call.
     * @return a non-null vessel DTO.
     */
    public static VesselDTO vessel(com.geodsea.ws.Vessel vessel) {

        return new VesselDTO(null, vessel.getHullIdentificationNumber(), vessel.getVesselName(),
                VesselType.valueOf(vessel.getVesselType()), vessel.getHullColor(), vessel.getSuperstructureColor(),
                vessel.getLength(), vessel.getTotalHP(), vessel.getFuelCapacity(), null, null, null);
    }

}
