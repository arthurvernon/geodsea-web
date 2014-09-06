package com.geodsea.pub.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * An organisation made up of members who fulfill may have roles and
 */
@Entity
@Table(name="T_GROUP", schema = "BOAT")
@PrimaryKeyJoinColumn(name="GROUP_ID", referencedColumnName = "ID")
public class Group extends Participant {


    /**
     * The contact person within the organsation.
     * <p>
     * This is initally set to the person who registers the organisation.
     * </p>
     */
    @ManyToOne
    @JoinColumn(name = "CONTACT_MEMBER_ID")
    private Member contactMember;


    /**
     * The participants that make up the organisation.
     * <p>
     * Members may be people or they may be other organisations. The only constraint is that
     * an organisation cannot be a member of itself, either directly or indirectly.
     * </p>
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "group")
    private List<Member> members;


    public Member getContactMember() {
        return contactMember;
    }

    public void setContactMember(Member contactMember) {
        this.contactMember = contactMember;
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

    /**
     * Perform a lookup for the participant down n levels.
     * <p>
     *
     * </p>
     *
     * @param participant
     * @param depth a non-negative number where 0 is just this level
     * @return
     */
    public Member lookup(Participant participant, int depth) {
        if (participant == null || members == null)
            return null;
        for (Member m : members) {
            if (m.getParticipant().equals(participant))
                return m;
            else if (depth > 0 && m.getParticipant() instanceof Group) {
                Member found = lookup(participant, depth--);
                if (found != null)
                    return found;
            }
        }
        return null;
    }
}