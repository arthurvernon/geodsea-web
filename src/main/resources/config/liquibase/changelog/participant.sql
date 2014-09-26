-----------------------------------------
-- Participant
------------------------------------------


CREATE SEQUENCE BOAT.PARTICIPANT_ID_SEQ INCREMENT 1 MINVALUE 100 START 100 CACHE 1;


ALTER TABLE BOAT.PARTICIPANT_ID_SEQ OWNER TO geodsea;

CREATE TABLE BOAT.T_PARTICIPANT (
  ID                         BIGINT       NOT NULL PRIMARY KEY DEFAULT nextval('BOAT.PARTICIPANT_ID_SEQ'),
  LOGIN           VARCHAR(100) NOT NULL,
  REGISTRATION_TOKEN_EXPIRES TIMESTAMP    NULL,
  REGISTRATION_TOKEN         VARCHAR(50)  NULL,
  ENABLED                    BOOLEAN      NOT NULL DEFAULT FALSE,
  CREATED_BY                 VARCHAR(50)  NULL DEFAULT 'system',
  CREATED_DATE               TIMESTAMP    NOT NULL DEFAULT now(),
  LAST_MODIFIED_BY           VARCHAR(50)  NULL,
  LAST_MODIFIED_DATE         TIMESTAMP    NULL,
  LANG_KEY                   VARCHAR(5)   NOT NULL,
  EMAIL                      VARCHAR(100) NOT NULL
);

ALTER TABLE BOAT.T_PARTICIPANT OWNER TO geodsea;
ALTER SEQUENCE BOAT.PARTICIPANT_ID_SEQ OWNED BY BOAT.T_PARTICIPANT.ID;
ALTER TABLE BOAT.T_PARTICIPANT ADD CONSTRAINT UQ_LOGIN UNIQUE (LOGIN);
CREATE INDEX PARTICIPANT_LOGIN_IDX ON BOAT.T_PARTICIPANT (LOGIN);
CREATE INDEX PER_EMAIL_IDX ON BOAT.T_PARTICIPANT (EMAIL);

COMMENT ON COLUMN BOAT.T_PARTICIPANT.LOGIN IS 'unique identifier for a participant';
COMMENT ON COLUMN BOAT.T_PARTICIPANT.REGISTRATION_TOKEN IS 'token that exists when account is initially created';
COMMENT ON COLUMN BOAT.T_PARTICIPANT.REGISTRATION_TOKEN_EXPIRES IS 'time up till when the registration can be completed';
COMMENT ON COLUMN BOAT.T_PARTICIPANT.ENABLED IS 'allows admin to lock the account. True if the participant may access the system, false otherwise';
COMMENT ON COLUMN BOAT.T_PARTICIPANT.CREATED_BY IS 'The participant name of the user who created this participant';
COMMENT ON COLUMN BOAT.T_PARTICIPANT.CREATED_DATE IS 'Date/time when the user created this participant';
COMMENT ON COLUMN BOAT.T_PARTICIPANT.LAST_MODIFIED_BY IS 'The participant who last modified this record, null if it has not been modified';
COMMENT ON COLUMN BOAT.T_PARTICIPANT.LAST_MODIFIED_DATE IS 'When (if ever) the participant''s details were last updated';
COMMENT ON COLUMN BOAT.T_PARTICIPANT.EMAIL IS 'Address that a registration token is sent to in order to complete initial registration';
COMMENT ON COLUMN BOAT.T_PARTICIPANT.LANG_KEY IS 'Two (lower) case ISO language code for this participant';


CREATE TABLE BOAT.T_PERSON (
  ID         BIGINT       NOT NULL PRIMARY KEY REFERENCES BOAT.T_PARTICIPANT (ID) ON DELETE CASCADE,
  FIRST_NAME        VARCHAR(50)  NOT NULL,
  LAST_NAME         VARCHAR(50)  NOT NULL,
  BIRTH_DATE        DATE         NULL,
  TELEPHONE         VARCHAR(20)  NULL,
  PASSWORD          VARCHAR(150) NOT NULL,
  QUESTION          VARCHAR(100) NOT NULL,
  ANSWER            VARCHAR(50)  NOT NULL,
  ADDRESS_FORMATTED VARCHAR(100) NULL,
  ADDRESS_POINT     geometry (POINT, 4326)

);

ALTER TABLE BOAT.T_PERSON OWNER TO geodsea;

CREATE INDEX IDX_PER_ADDRESS_GIST ON BOAT.T_PERSON USING GIST (ADDRESS_POINT);

COMMENT ON COLUMN BOAT.T_PERSON.QUESTION IS 'The question to ask in the event of a person forgetting their password';
COMMENT ON COLUMN BOAT.T_PERSON.ANSWER IS 'The answer to the question created on registration';
COMMENT ON COLUMN BOAT.T_PERSON.ADDRESS_FORMATTED IS 'The google-defined address string';
COMMENT ON COLUMN BOAT.T_PERSON.ADDRESS_POINT IS 'the location of the address as specified by google';


----------------------------------------------------------
-- Group creation
----------------------------------------------------------

CREATE TABLE BOAT.T_COLLECTIVE (
  COLLECTIVE_ID          BIGINT       NOT NULL PRIMARY KEY REFERENCES BOAT.T_PARTICIPANT (ID) ON DELETE CASCADE,
  COLLECTIVE_NAME        VARCHAR(100) NOT NULL,
  CONTACT_PERSON_FK BIGINT       NULL
);

ALTER TABLE BOAT.T_COLLECTIVE OWNER TO geodsea;

ALTER TABLE BOAT.T_COLLECTIVE ADD CONSTRAINT FK_COLLECTIVE_CONTACT_PERSON
FOREIGN KEY (CONTACT_PERSON_FK) REFERENCES BOAT.T_PERSON (ID) ON DELETE CASCADE;

CREATE INDEX COLLECTIVE_NAME_IDX ON BOAT.T_COLLECTIVE (COLLECTIVE_NAME);


COMMENT ON COLUMN BOAT.T_COLLECTIVE.CONTACT_PERSON_FK IS 'The member who is the contact person (the one who registered the organisation). Should be not null but Hibernate doesn''t handle bidirectional FKs';


----------------------------------------------------------
-- members of a collective
----------------------------------------------------------


CREATE SEQUENCE BOAT.MEMBER_ID_SEQ INCREMENT 1 MINVALUE 1 START 100 CACHE 1;
ALTER TABLE BOAT.MEMBER_ID_SEQ OWNER TO geodsea;

CREATE TABLE BOAT.T_MEMBER (
  ID             BIGINT  NOT NULL PRIMARY KEY DEFAULT nextval('BOAT.MEMBER_ID_SEQ'),
  COLLECTIVE_FK       BIGINT  NOT NULL REFERENCES BOAT.T_COLLECTIVE ON UPDATE RESTRICT ON DELETE CASCADE,
  PARTICIPANT_FK BIGINT  NOT NULL REFERENCES BOAT.T_PARTICIPANT ON UPDATE RESTRICT ON DELETE CASCADE,
  MEMBER_SINCE   DATE    NULL,
  MEMBER_UNTIL   DATE    NULL,
  ACTIVE         BOOLEAN NOT NULL DEFAULT FALSE,
  MANAGER        BOOLEAN NOT NULL DEFAULT FALSE,
  CREATED_BY                 VARCHAR(50)  NULL DEFAULT 'system',
  CREATED_DATE               TIMESTAMP    NOT NULL DEFAULT now(),
  LAST_MODIFIED_BY           VARCHAR(50)  NULL,
  LAST_MODIFIED_DATE         TIMESTAMP    NULL
);

ALTER TABLE BOAT.T_MEMBER OWNER TO geodsea;
ALTER SEQUENCE BOAT.MEMBER_ID_SEQ OWNED BY BOAT.T_MEMBER.ID;

ALTER TABLE BOAT.T_MEMBER ADD CONSTRAINT UC_MEMBER_ONCE_ONLY UNIQUE (COLLECTIVE_FK, PARTICIPANT_FK);

CREATE INDEX ORG_MEMBER_PARTICIPANT_IDX ON BOAT.T_MEMBER (PARTICIPANT_FK);
CREATE INDEX ORG_MEMBER_MANAGER_IDX ON BOAT.T_MEMBER (MANAGER);

COMMENT ON TABLE BOAT.T_MEMBER IS 'The participants within an collective which may be anyone except the group itself';

COMMENT ON COLUMN BOAT.T_MEMBER.PARTICIPANT_FK IS 'The immutable person or collective that belongs to this organisation';
COMMENT ON COLUMN BOAT.T_MEMBER.COLLECTIVE_FK IS 'The immutable collective that has this member';


----------------------------------------------------------
-- A specialisation of a collective for an organisation
----------------------------------------------------------

CREATE TABLE BOAT.T_ORGANISATION (
  ORGANISATION_ID   BIGINT       NOT NULL PRIMARY KEY REFERENCES BOAT.T_PARTICIPANT (ID) ON DELETE CASCADE,
  WEBSITE_URL       VARCHAR(100),
  TELEPHONE         VARCHAR(20)  NULL,
  ADDRESS_FORMATTED VARCHAR(100) NULL,
  ADDRESS_POINT     geometry (POINT, 4326)
);

ALTER TABLE BOAT.T_ORGANISATION OWNER TO geodsea;

CREATE INDEX IDX_ORG_ADDRESS_GIST ON BOAT.T_ORGANISATION USING GIST (ADDRESS_POINT);


COMMENT ON COLUMN BOAT.T_ORGANISATION.ADDRESS_FORMATTED IS 'The google-defined address string';
COMMENT ON COLUMN BOAT.T_ORGANISATION.ADDRESS_POINT IS 'the location of the address as specified by google';


-----------------------------------------------------------------------------
-- A specialisation of a collective for a group setup by private individuals
-----------------------------------------------------------------------------

CREATE TABLE BOAT.T_GROUP (
  GROUP_ID   BIGINT       NOT NULL PRIMARY KEY REFERENCES BOAT.T_COLLECTIVE (COLLECTIVE_ID) ON DELETE CASCADE
);

ALTER TABLE BOAT.T_GROUP OWNER TO geodsea;

COMMENT ON TABLE BOAT.T_GROUP IS 'Identifier for a private group';


--------------------------------
-- Authorities
-------------------------------
CREATE TABLE BOAT.T_AUTHORITY (
  NAME VARCHAR(255) NOT NULL PRIMARY KEY
);

ALTER TABLE BOAT.T_AUTHORITY OWNER TO geodsea;


CREATE TABLE BOAT.T_PARTICIPANT_AUTHORITY (
  participant_id BIGINT       NOT NULL REFERENCES BOAT.T_PARTICIPANT ON DELETE CASCADE ON UPDATE RESTRICT,
  NAME           VARCHAR(255) NOT NULL REFERENCES BOAT.T_AUTHORITY ON DELETE CASCADE ON UPDATE RESTRICT,
  PRIMARY KEY (participant_id, name)
);
ALTER TABLE BOAT.T_PARTICIPANT_AUTHORITY OWNER TO geodsea;


---------------------------------------------------------
-- Persistent Tokens that record concurrent user access
--------------------------------------------------------

CREATE TABLE BOAT.T_PERSISTENT_TOKEN (
  SERIES      VARCHAR(255) PRIMARY KEY,
  PERSON_FK   BIGINT REFERENCES BOAT.T_PERSON ON DELETE CASCADE ON UPDATE RESTRICT,
  TOKEN_VALUE VARCHAR(255),
  TOKEN_DATE  DATE,
  IP_ADDRESS  VARCHAR(39),
  USER_AGENT  VARCHAR(255)
);
ALTER TABLE BOAT.T_PERSISTENT_TOKEN OWNER TO geodsea;

