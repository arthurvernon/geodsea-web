package com.geodsea.pub.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geodsea.pub.domain.type.StorageType;
import com.geodsea.pub.domain.type.VesselType;
import com.geodsea.pub.domain.util.QueryName;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;


import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * A Boat.
 */
@Entity
@Table(name = "T_VESSEL", schema = "BOAT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Vessel implements Serializable {

    @Id
    @GeneratedValue(generator = "VESSEL_SEQ_GEN", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "VESSEL_SEQ_GEN", sequenceName = "BOAT.VESSEL_ID_SEQ")
    private Long id;

    /**
     * A standard form string consisting of country code, manufacturer ID, serial number, agent code,
     * and year of issue.
     * See <a href="http://www.iso.org/iso/home/store/catalogue_ics/catalogue_detail_ics.htm?csnumber=37497">
     * http://www.iso.org/iso/home/store/catalogue_ics/catalogue_detail_ics.htm?csnumber=37497</a>
     * <p>The Hull Identification Number (HIN) is a 12 or 14 character serial number that uniquely identifies a boat.</p>
     * <p>The HIN is analogous to a VIN on a car. All boats manufactured or imported on or after November 1, 1972, must
     * bear a HIN, and this HIN must be identified during the boat registration process.</p>
     * <p>Here is a <a href="http://www.transport.wa.gov.au/imarine/boatcode-and-hin.asp">link to BoatCode and HIN</a>
     * details for WA.
     * <p/>
     * <p>This International Standard establishes a coding system to achieve identification of any small craft in terms of</p>
     * <ul>
     * <li>identification code of the country of the manufacturer;</li>
     * <li>identification code of the manufacturer;</li>
     * <li>serial number;</li>
     * <li>month and year of manufacture;</li>
     * <li>model year.</li>
     * </ul>
     * <p>
     * It applies to small craft of all types and materials, of hull length up to 24 m. It does not apply to beach or bathing toys
     * </p>
     * <p>Value is the raw number excluding special characters like "-" and spaces.</p>
     * TODO place a unique constraint on this value.
     */
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
     * The colour (or colours) of the hull.
     * <p>
     *     A reasonable size field if allowed for at this stage so that a person could enter multiple colours.
     *     TODO Need to ascertain if the requirement is for only a dominant colour to be registered.
     * </p>
     */
    @Column(name="HULL_COLOUR", nullable = true)
    @Size(min=4, max = 30)
    private String hullColor;

    /**
     * The colour (or colours) of the superstructure.
     * <p>
     *     A reasonable size field if allowed for at this stage so that a person could enter multiple colours.
     *     TODO Need to ascertain if the requirement is for only a dominant colour to be registered.
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
     * The length of the craft in metres.
     */
    @Column(name="LENGTH_M", nullable = false)
    private int length;

    /**
     * Total horse power of a motorised boat.
     */
    @Column(name="TOTAL_HP", nullable = true)
    private Integer totalHP;

    /**
     * Maximum number of litres of fuel that the boat can carry.
     */
    @Column(name="FUEL_CAPACITY_L", nullable = true)
    private int fuelCapacity;

    /**
     * A classification of the vessel that would provide the best clue from a distance to identify the vessel.
     */
    @Column(name="VESSEL_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private VesselType vesselType;

    /**
     * A list of the emergency equipment on the boat.
     * <p>
     *     This list ignores aspects of emergency equipment which may be important to record.
     * </p>
     */
    @ElementCollection(targetClass = EmergencyEquipment.class, fetch = FetchType.EAGER)
    @JoinTable(name="T_VESSEL_EQUIPMENT", joinColumns = @JoinColumn(name="VESSEL_ID") ,schema = "BOAT")
    @Column(name="EQPT_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<EmergencyEquipment> emergencyEquipment;

    public Vessel()
    {
        super();
    }

    /**
     * Essential fields to create a vessel.
     * @param name
     * @param type
     */
    public Vessel(String name, VesselType type)
    {
        super();
        this.vesselName = name;
        this.vesselType = type;
    }

    /**
     * Create a vessel with all but it's ID
     * @param hullIdentificationNumber
     * @param vesselName
     * @param vesselType
     * @param hullColor
     * @param superstructureColor
     * @param length
     * @param totalHP
     * @param fuelCapacity
     * @param storageType
     * @param emergencyEquipment
     */
    public Vessel(Long id, String hullIdentificationNumber, String vesselName, VesselType vesselType, String hullColor,
                  String superstructureColor, int length, Integer totalHP, int fuelCapacity, StorageType storageType,
                  List<EmergencyEquipment> emergencyEquipment) {
        this.id = id;
        this.hullIdentificationNumber = hullIdentificationNumber;
        this.vesselName = vesselName;
        this.vesselType = vesselType;
        this.hullColor = hullColor;
        this.superstructureColor = superstructureColor;
        this.length = length;
        this.totalHP = totalHP;
        this.fuelCapacity = fuelCapacity;
        this.storageType = storageType;
        this.emergencyEquipment = emergencyEquipment;
    };

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Vessel vessel = (Vessel) o;

        if (id != vessel.id) {
            return false;
        }

        return true;
    }


    @Override
    public String toString() {
        return "Vessel{" +
                "id=" + id +
                ", hullIdentificationNumber='" + hullIdentificationNumber + '\'' +
                ", vesselName='" + vesselName + '\'' +
                '}';
    }

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

    /**
     *
     * @return length in metres
     */
    public int getLength() {
        return length;
    }

    /**
     *
     * @param length length in metres
     */
    public void setLength(int length) {
        this.length = length;
    }

    public Integer getTotalHP() {
        return totalHP;
    }

    public void setTotalHP(Integer totalHP) {
        this.totalHP = totalHP;
    }

    public int getFuelCapacity() {
        return fuelCapacity;
    }

    public void setFuelCapacity(int fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }

    public VesselType getVesselType() {
        return vesselType;
    }

    public void setVesselType(VesselType vesselType) {
        this.vesselType = vesselType;
    }

    public List<EmergencyEquipment> getEmergencyEquipment() {
        return emergencyEquipment;
    }

    public void setEmergencyEquipment(List<EmergencyEquipment> emergencyEquipment) {
        this.emergencyEquipment = emergencyEquipment;
    }

}
