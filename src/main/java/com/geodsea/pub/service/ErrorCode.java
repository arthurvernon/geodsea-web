package com.geodsea.pub.service;

/**
 * Error codes that should be mapped on the client.
 */
public class ErrorCode {

    public static final String PASSWORD_MISMATCH = "PasswordMismatch";

    /**
     * The username already exists
     */
    public static final String USERNAME_ALREADY_EXISTS = "UsernameAlreadyExists";

    /**
     * The username specified is not that of a person (but of a group).
     */
    public static final String NOT_A_PERSON = "NotAPerson";

    public static final String MISSING_EMAIL = "MissingEmail";
    public static final String MISSING_GROUP_NAME = "MissingGroupName";
    public static final String NO_SUCH_USER = "NoSuchUser";
    public static final String NO_SUCH_GROUP = "NoSuchGroup";
    public static final String NOT_A_GROUP_MEMBER = "NotAGroupMember";
    public static final String INCORRECT_ANSWER = "IncorrectAnswer";
    public static final String PERMISSION_DENIED = "PermissionDenied";
    public static final String NO_SUCH_MEMBER = "NoSuchMember";
    public static final String OWNER_NOT_DEFINED = "VesselOwnerNotDefined";
    public static final String NO_SUCH_PARTICIPANT = "NoSuchParticipant";
    public static final String GROUP_CANNOT_OWN_VESSEL = "GroupCannotOwnVessel";
    public static final String NOT_A_MANAGER = "NotAManager";
    public static final String MEMBERSHIP_DISABLED = "MembershipDisabled";
    public static final String ONE_ORGANISATION_OWNS_VESSEL = "OneOrganisationOwnsVessel";
    public static final String OWNERSHIP_BY_PEOPLE_OR_ORGANISATION = "OwnershipByPeopleOrOrganisation";
    public static final String DUPLICATE_HIN = "DuplicateHIN";

    /**
     * When registering a vessel for private use (in the sense it is not owned by an organisation)
     * then the user performing the registration must be listed as an owner.
     * <p>
     *     The may be an unnecessary restriction and may be dropped in the future.
     * </p>
     */
    public static final String USER_MUST_OWN_VESSEL = "UserMustOwnVessel";

    /**
     * User account is disabled. Requested action cannot be performed.
     */
    public static final String USER_DISABLED = "UserDisabled";
    public static final String NO_SUCH_VESSEL = "NoSuchVessel";

    /**
     * The person is not a skipper of the specified vessel
     */
    public static final String NOT_SKIPPER_OF_VESSEL = "NotASkipperOFVessel";
    public static final String NO_SUCH_TRIP = "NoSuchTrip";

    public static final String VESSEL_NOT_SPECIFIED ="VesselNotSpecified";

    public static final String SKIPPER_NOT_SPECIFIED = "SkipperNotSpecified";
    public static final String NOT_A_SKIPPER = "NotASkipper";
    public static final String NOT_ACTIVE_SKIPPER = "NotActiveSkipper";
    public static final String PEOPLE_ON_BOARD_NOT_SPECIFIED = "PeopleOnBoardNotSpecified";
    public static final String NO_SUCH_SKIPPER = "NoSuchSkipper";
}
