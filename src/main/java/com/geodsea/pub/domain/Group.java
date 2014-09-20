package com.geodsea.pub.domain;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

/**
 * A private group of individuals.
 */
@Entity
@Table(name = "T_GROUP", schema = "BOAT")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@PrimaryKeyJoinColumn(name="GROUP_ID", referencedColumnName = "COLLECTIVE_ID")
public class Group extends Collective {

	public Group(){

	}


}