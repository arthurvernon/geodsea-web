package com.geodsea.pub.domain;


import com.geodsea.pub.domain.util.ValidationUtil;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.URL;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * A public group that is allowed to perform select activities once approved.
 */
@Entity
@Table(name = "T_ORGANISATION", schema = "BOAT")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@PrimaryKeyJoinColumn(name="ORGANISATION_ID", referencedColumnName = "COLLECTIVE_ID")
public class Organisation extends Collective {


    /**
     *
     */
    @Column(name = "WEBSITE_URL", nullable = true, length = 100)
    @URL
    @Size(min=2, max=100)
    private String websiteURL;

    /**
     * The telephone number upon which the person can be contacted in emergencies.
     * <p>
     *     (optional) Two or 3 digits in round brackets for an area code
     *     optional - (dash) or blank space
     *     3-4 digits optionally followed by a dash or a space followed by another 4 digits
     * </p>
     * >
     */
    @Column(name="TELEPHONE", nullable = true)
    @Pattern(regexp= ValidationUtil.TELEPHONE_REGEX,
            message="{invalid.phonenumber}")
    private String telephone;

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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}