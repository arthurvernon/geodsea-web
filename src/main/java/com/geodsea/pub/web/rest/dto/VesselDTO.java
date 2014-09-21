package com.geodsea.pub.web.rest.dto;

import com.geodsea.pub.domain.EmergencyEquipment;
import com.geodsea.pub.domain.type.StorageType;
import com.geodsea.pub.domain.type.VesselType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * A Boat.
 */
public class VesselDTO {

    private Long id;

    /**
     * Something like AUSTA36119H899
     */
    private String hullIdentificationNumber;

    private String vesselName;

    private VesselType vesselType;

    private String hullColor;

    private String superstructureColor;

    private int length;

    private Integer totalHP;

    private int fuelCapacity;

    private StorageType storageType;

    private List<EmergencyEquipment> emergencyEquipment;

    public VesselDTO()
    {
        super();
    }

    public VesselDTO(Long id, String hullIdentificationNumber, String vesselName, VesselType vesselType, String hullColor,
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
    }

    public Long getId() {
        return id;
    }

    public String getHullIdentificationNumber() {
        return hullIdentificationNumber;
    }

    public String getVesselName() {
        return vesselName;
    }

    public String getHullColor() {
        return hullColor;
    }

    public String getSuperstructureColor() {
        return superstructureColor;
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public int getLength() {
        return length;
    }

    public Integer getTotalHP() {
        return totalHP;
    }

    public int getFuelCapacity() {
        return fuelCapacity;
    }

    public VesselType getVesselType() {
        return vesselType;
    }

    public List<EmergencyEquipment> getEmergencyEquipment() {
        return emergencyEquipment;
    }

}
