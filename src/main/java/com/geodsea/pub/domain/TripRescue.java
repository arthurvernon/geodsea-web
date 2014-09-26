package com.geodsea.pub.domain;

import com.geodsea.pub.domain.type.StorageType;
import com.geodsea.pub.domain.type.VesselType;
import com.geodsea.pub.domain.util.ValidationUtil;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * A trip created by the sea rescue organisation when a person calls in and no existing vessel and skipper (person) can be found
 */
@Entity
@Table(name = "T_TRIP_RESCUE", schema = "BOAT")
@PrimaryKeyJoinColumn(name="ID", referencedColumnName = "ID")
public class TripRescue extends Trip {

    @Column(name="HIN", nullable = true)
    @Size(min=12, max = 14)
    private String hullIdentificationNumber;

    /**
     * The name given to the vessel.
     * <p>
     *     This is optional as many craft, particularly those that are not powered, will not have a name.
     * </p>
     */
    @Column(name="VESSEL_NAME", nullable = false)
    @Size(min=5, max = 40)
    private String vesselName;

    /**
     * The length of the craft in metres.
     */
    @Column(name="LENGTH_M", nullable = false)
    private int length;

    /**
     * A classification of the vessel that would provide the best clue from a distance to identify the vessel.
     */
    @Column(name="VESSEL_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private VesselType vesselType;

    /**
     * The colour (or colours) of the hull.
     * <p>
     *     A reasonable size field if allowed for at this stage so that a person could enter multiple colours.
     * </p>
     */
    @Column(name="HULL_COLOUR", nullable = true)
    @Size(min=4, max = 30)
    private String hullColor;

    /**
     * The colour (or colours) of the superstructure.
     * <p>
     *     A reasonable size field if allowed for at this stage so that a person could enter multiple colours.
     * </p>
     */
    @Column(name="SUPERSTRUCTURE_COLOUR", nullable = true)
    @Size(min=4, max = 30)
    private String superstructureColor;


    /**
     * Where the boat is usually stored.
     * <p>
     *     The purpose of recording this is to provide a clue where the boat may be in the event that the skipper
     *     does not report back and is not contactable.
     * </p>
     */
    @Column(name="STORAGE_TYPE", nullable = true)
    @Enumerated(EnumType.STRING)
    private StorageType storageType;

    /**
     * A description of the location where the boat is stored, e.g. Pen 23.
     * <p>
     *     The purpose of recording this is to provide a clue where the boat may be in the event that the skipper
     *     does not report back and is not contactable.
     * </p>
     */
    @Column(name="STORAGE_LOCATION", nullable = true, length = 50)
    @Size(min=2, max = 50)
    private String storageLocation;


    /**
     * Name of the skipper.
     */
    @Column(name="SKIPPER_NAME", nullable = false, length = 40)
    @Size(min=2, max = 40)
    private String skipperName;

    /**
     * Call sign for the vessel
     */
    @Column(name="CALL_SIGN", nullable = true, length = 40)
    @Size(min=2, max = 40)
    private String callSign;

    /**
     * The telephone number upon which the person can be contacted.
     * <p>
     *     (optional) Two or 3 digits in round brackets for an area code
     *     optional - (dash) or blank space
     *     3-4 digits optionally followed by a dash or a space followed by another 4 digits
     * </p>
     * >
     */
    @Column(name="CONTACT_PHONE", nullable = true, length = 20)
    @Pattern(regexp= ValidationUtil.TELEPHONE_REGEX,
            message="{invalid.phonenumber}")
    private String contactPhone;

    @Column(name="EMERGENCY_CONTACT_NAME", nullable = true, length = 40)
    private String emergencyContactName;

    /**
     * The telephone number upon which the emergency contact can be reached on.
     * <p>
     *     (optional) Two or 3 digits in round brackets for an area code
     *     optional - (dash) or blank space
     *     3-4 digits optionally followed by a dash or a space followed by another 4 digits
     * </p>
     * >
     */
    @Column(name="EMERGENCY_CONTACT_PHONE", nullable = true, length = 20)
    @Pattern(regexp= ValidationUtil.TELEPHONE_REGEX,
            message="{invalid.phonenumber}")
    private String emergencyContactPhone;


    public String getHullIdentificationNumber() {
        return hullIdentificationNumber;
    }

    public void setHullIdentificationNumber(String hullIdentificationNumber) {
        this.hullIdentificationNumber = hullIdentificationNumber;
    }

    public String getVesselName() {
        return vesselName;
    }

    public void setVesselName(String vesselName) {
        this.vesselName = vesselName;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public VesselType getVesselType() {
        return vesselType;
    }

    public void setVesselType(VesselType vesselType) {
        this.vesselType = vesselType;
    }

    public String getHullColor() {
        return hullColor;
    }

    public void setHullColor(String hullColor) {
        this.hullColor = hullColor;
    }

    public String getSuperstructureColor() {
        return superstructureColor;
    }

    public void setSuperstructureColor(String superstructureColor) {
        this.superstructureColor = superstructureColor;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getSkipperName() {
        return skipperName;
    }

    public void setSkipperName(String skipperName) {
        this.skipperName = skipperName;
    }

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }
}
