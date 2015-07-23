package com.geodsea.pub.domain;

import com.geodsea.common.type.ActivityType;

import javax.persistence.*;

/**
 * Base class for defining an activity that a person may perform.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "T_TRIP", schema = "BOAT")
public class Activity extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(generator = "TRIP_GEN", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "TRIP_GEN", sequenceName = "BOAT.TRIP_ID_SEQ")
    @Column(name="ID")
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name="ACTIVITY_TYPE", updatable = false, nullable = false)
    private ActivityType activityType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }
}
