package com.geodsea.pub.web.rest.dto;

import com.geodsea.pub.domain.EmergencyEquipment;
import com.geodsea.pub.domain.type.StorageType;
import com.geodsea.pub.domain.type.VesselType;

import java.util.List;

/**
 * Additional data used when registering a vessel, that includes the logins of the owners and the skippers.
 */
public class VesselAddDTO extends VesselDTO {

    /**
     * Array of participant logins for owners
     */
    private long[] owners;

    /**
     * Login IDs for the people who will be a skipper of the vessel
     */
    private long[] skippers;

    public VesselAddDTO() {
    }

    public VesselAddDTO(Long id, String hullIdentificationNumber, String vesselName, VesselType vesselType, String hullColor, String superstructureColor, int length, Integer totalHP, int fuelCapacity, StorageType storageType, List<EmergencyEquipment> emergencyEquipment) {
        super(id, hullIdentificationNumber, vesselName, vesselType, hullColor, superstructureColor, length, totalHP, fuelCapacity, storageType, emergencyEquipment);
    }

    public long[] getOwners() {
        return owners;
    }

    public long[] getSkippers() {
        return skippers;
    }
}
