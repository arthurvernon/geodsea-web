package com.geodsea.pub.domain;


import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * A public group that is allowed to perform select activities once approved.
 */
@Entity
@Table(name = "T_ORGANISATION", schema = "BOAT")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@PrimaryKeyJoinColumn(name="ORGANISATION_ID", referencedColumnName = "GROUP_ID")
public class Organisation extends Group {

    @Column(name = "PUBLISHED_NAME", nullable = false, length = 100)
    private String publishedName;

    @Column(name = "WEBSITE_URL", nullable = true, length = 100)
    private String websiteURL;

    public Organisation(){
        super();
	}

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="formatted", column = @Column(name="ADDRESS_FORMATTED")),
            @AttributeOverride(name="point", column = @Column(name="ADDRESS_POINT"))
    })
    private Address address;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getWebsiteURL() {
        return websiteURL;
    }

    public void setWebsiteURL(String websiteURL) {
        this.websiteURL = websiteURL;
    }

    public String getPublishedName() {
        return publishedName;
    }

    public void setPublishedName(String publishedName) {
        this.publishedName = publishedName;
    }


}