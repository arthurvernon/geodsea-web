package com.geodsea.pub.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geodsea.pub.domain.util.ValidationUtil;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * A user.
 */
@Entity
@Table(name = "T_PERSON", schema = "BOAT")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@PrimaryKeyJoinColumn(name="PERSON_ID", referencedColumnName = "ID")
public class Person extends Participant implements Serializable {

    @JsonIgnore
    @Size(min = 0, max = 100)
    private String password;

    @Size(min = 2, max = 50)
    @Column(name = "first_name")
    private String firstName;

    @Size(min = 2, max = 50)
    @Column(name = "last_name")
    private String lastName;

    @Size(min = 2, max = 5)
    @Column(name = "lang_key")
    private String langKey;

    @Size(min = 2, max = 100)
    @Column(name = "question", nullable = false)
    private String question;

    @Size(min = 2, max = 50)
    @Column(name = "answer", nullable = false)
    private String answer;


    @Column(name="BIRTH_DATE", nullable = true)
    @Temporal(TemporalType.DATE)
    @Past()
    @DateTimeFormat(style="S-")
    private Date birthDate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="formatted", column = @Column(name="ADDRESS_FORMATTED")),
            @AttributeOverride(name="point", column = @Column(name="ADDRESS_POINT"))
    })
    private Address address;

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



    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "person")
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<PersistentToken> persistentTokens;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public String getLangKey() {
        return langKey;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }


    public Set<PersistentToken> getPersistentTokens() {
        return persistentTokens;
    }

    public void setPersistentTokens(Set<PersistentToken> persistentTokens) {
        this.persistentTokens = persistentTokens;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String toString() {
        return "Person{" + super.toString() +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", langKey='" + langKey + '\'' +
                "} ";
    }
}
