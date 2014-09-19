package com.geodsea.pub.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * An collection of members.
 */
@Entity
@Table(name = "T_COLLECTIVE", schema = "BOAT")
@PrimaryKeyJoinColumn(name = "COLLECTIVE_ID", referencedColumnName = "ID")
public class Collective extends Participant {

    /**
     * A name, not necessarily unique, that the group is known by, defined in the language of the organisation.
     * <p>
     *     For organisations, this should be the official name of the organisation
     * </p>
     */
    @Column(name = "COLLECTIVE_NAME", nullable = false, length = 100)
    @Size(min=2, max=100)
    @NotNull
    private String collectiveName;


    /**
     * The contact person within the group.
     * <p>
     * This is initially set to the person who registers the organisation.
     * </p>
     */
    @ManyToOne
    @JoinColumn(name = "CONTACT_PERSON_FK")
    private Person contactPerson;


    /**
     * The participants that make up the organisation.
     * <p>
     * Members may be people or they may be other organisations. The only constraint is that
     * an organisation cannot be a member of itself, either directly or indirectly.
     * </p>
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "collective")
    private List<Member> members;

    public Collective() {
        super();
    }

    /**
     * Create a new group where the person is to be the contact member and is granted manager status.
     * <p>
     * The member is not active until the registration process is completed. This might be upon
     * responding to the registration email for a private group. Otherwise it will be once the organisation
     * is approved.
     * </p>
     *
     * @param participantName
     * @param email
     * @param person
     */
    public Collective(String participantName, String email, Person person) {
        super(participantName, email);
        this.contactPerson = person;
    }

    public Person getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(Person contactPerson) {
        this.contactPerson = contactPerson;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public void addMember(Member member) {
        if (member == null)
            return;

        if (members == null)
            members = new ArrayList<Member>();
        members.add(member);
    }

    /**
     * Perform a superficial lookup of the participant in the list of members.
     * <p>
     * For a match to occur the participant must be a direct member of this organisation.
     * </p>
     *
     * @param participant a possibly null participant.
     * @return
     */
    public Member lookup(Participant participant) {
        return lookup(participant, 0);
    }

    public String getCollectiveName() {
        return collectiveName;
    }

    public void setCollectiveName(String collectiveName) {
        this.collectiveName = collectiveName;
    }

    /**
     * Perform a lookup for the participant down n levels.
     * <p>
     * <p/>
     * </p>
     *
     * @param participant
     * @param depth       a non-negative number where 0 is just this level
     * @return
     */
    public Member lookup(Participant participant, int depth) {
        if (participant == null || members == null)
            return null;
        for (Member m : members) {
            if (m.getParticipant().equals(participant))
                return m;
            else if (depth > 0 && m.getParticipant() instanceof Collective) {
                Member found = lookup(participant, depth--);
                if (found != null)
                    return found;
            }
        }
        return null;
    }
}