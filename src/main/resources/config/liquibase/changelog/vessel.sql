----------------------------------------------------------
-- the vessel, permits and licenses
-- Depends upon:
--     participant.sql
--     agencies.sql
----------------------------------------------------------


CREATE SEQUENCE BOAT.VESSEL_ID_SEQ INCREMENT 1 MINVALUE 1 START 100 CACHE 1;
ALTER TABLE BOAT.VESSEL_ID_SEQ OWNER TO geodsea;

CREATE TABLE BOAT.T_VESSEL (
  ID                    BIGINT      NOT NULL PRIMARY KEY DEFAULT nextval('BOAT.VESSEL_ID_SEQ'),
  HIN                   VARCHAR(14),
  VESSEL_NAME           VARCHAR(40),
  HULL_COLOUR           VARCHAR(30),
  SUPERSTRUCTURE_COLOUR VARCHAR(30),
  STORAGE_TYPE          VARCHAR(10),
  STORAGE_LOCATION      VARCHAR(50),
  VESSEL_TYPE           VARCHAR(20) NOT NULL,
  LENGTH_M              INT         NULL,
  TOTAL_HP              INT         NULL,
  FUEL_CAPACITY_L       INT         NULL
);

ALTER TABLE BOAT.T_VESSEL OWNER TO geodsea;
ALTER SEQUENCE BOAT.VESSEL_ID_SEQ OWNED BY BOAT.T_VESSEL.ID;

ALTER TABLE BOAT.T_VESSEL ADD CONSTRAINT UQ_VESSEL_HIN UNIQUE (HIN);
COMMENT ON COLUMN BOAT.T_VESSEL.HIN IS 'Unique Hull Identification number per ISO 10087:2006';
COMMENT ON COLUMN BOAT.T_VESSEL.LENGTH_M IS 'Length of the vessel in metres';
COMMENT ON COLUMN BOAT.T_VESSEL.TOTAL_HP IS 'Total horse power of a motorised boat.';
COMMENT ON COLUMN BOAT.T_VESSEL.FUEL_CAPACITY_L IS 'Maximum number of litres of fuel that the boat can carry.';
COMMENT ON COLUMN BOAT.T_VESSEL.STORAGE_LOCATION IS 'Meaningful description of where the vessel is stoerd.';


----------------------------------------------------------
-- The emergency equipment on the boat
----------------------------------------------------------
CREATE TABLE BOAT.T_VESSEL_EQUIPMENT (
  VESSEL_ID BIGINT NOT NULL REFERENCES BOAT.T_VESSEL ON DELETE CASCADE ON UPDATE RESTRICT,
  EQPT_TYPE VARCHAR(20),
  CONSTRAINT PK_VESSEL_EQUIPMENT PRIMARY KEY (VESSEL_ID, EQPT_TYPE)
);
ALTER TABLE BOAT.T_VESSEL_EQUIPMENT OWNER TO geodsea;

COMMENT ON TABLE BOAT.T_VESSEL_EQUIPMENT IS 'The emergency equipment held on a boat';
COMMENT ON COLUMN BOAT.T_VESSEL_EQUIPMENT.EQPT_TYPE IS 'String value of the EmergencyEquipment enum';
COMMENT ON COLUMN BOAT.T_VESSEL_EQUIPMENT.VESSEL_ID IS 'The vessel that has this item of equipment';

----------------------------------------------------------
-- The boat owner
----------------------------------------------------------

CREATE SEQUENCE BOAT.OWNER_ID_SEQ INCREMENT 1 MINVALUE 1 START 1 CACHE 1;
ALTER TABLE BOAT.OWNER_ID_SEQ OWNER TO geodsea;

CREATE TABLE BOAT.T_OWNER (
  ID             BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('BOAT.OWNER_ID_SEQ'),
  VESSEL_ID      BIGINT NOT NULL REFERENCES BOAT.T_VESSEL ON DELETE CASCADE ON UPDATE RESTRICT,
  PARTICIPANT_ID BIGINT NOT NULL REFERENCES BOAT.T_PARTICIPANT ON DELETE CASCADE ON UPDATE RESTRICT
);

ALTER TABLE BOAT.T_OWNER OWNER TO geodsea;
ALTER SEQUENCE BOAT.OWNER_ID_SEQ OWNED BY BOAT.T_OWNER.ID;

ALTER TABLE BOAT.T_OWNER ADD CONSTRAINT UC_VESSEL_PARTICIPANT UNIQUE (VESSEL_ID, PARTICIPANT_ID);

COMMENT ON TABLE BOAT.T_OWNER IS 'An owner of a vessel';
COMMENT ON COLUMN BOAT.T_OWNER.VESSEL_ID IS 'The vessel that is owned';
COMMENT ON COLUMN BOAT.T_OWNER.PARTICIPANT_ID IS 'The participant (person or organisation) that owns the boat';

----------------------------------------------------------
-- Permit to use a boat
----------------------------------------------------------

CREATE SEQUENCE boat.SKIPPER_ID_SEQ INCREMENT 1 MINVALUE 1 START 1 CACHE 1;
ALTER TABLE boat.SKIPPER_ID_SEQ OWNER TO geodsea;

CREATE TABLE BOAT.T_SKIPPER (
  ID           BIGINT  NOT NULL PRIMARY KEY DEFAULT nextval('BOAT.SKIPPER_ID_SEQ'),
  PERSON_ID    BIGINT  NOT NULL REFERENCES BOAT.T_PERSON ON DELETE CASCADE ON UPDATE RESTRICT,
  VESSEL_ID    BIGINT  NOT NULL REFERENCES BOAT.T_VESSEL ON DELETE CASCADE ON UPDATE RESTRICT,
  GRANTED_FROM TIMESTAMP,
  GRANTED_TO   TIMESTAMP,
  SUSPENDED    BOOLEAN NOT NULL DEFAULT FALSE
);

ALTER TABLE BOAT.T_SKIPPER OWNER TO geodsea;
ALTER SEQUENCE BOAT.SKIPPER_ID_SEQ OWNED BY BOAT.T_SKIPPER.ID;

ALTER TABLE BOAT.T_SKIPPER ADD CONSTRAINT UQ_SKIPPER UNIQUE (PERSON_ID, VESSEL_ID);

COMMENT ON TABLE BOAT.T_SKIPPER IS 'A permit to use the vessel which is granted by an owner to a skipper';
COMMENT ON COLUMN BOAT.T_SKIPPER.PERSON_ID IS 'The person who is granted the right to use the vessel';
COMMENT ON COLUMN BOAT.T_SKIPPER.GRANTED_FROM IS 'optional date from when the skipper may use the vessel';
COMMENT ON COLUMN BOAT.T_SKIPPER.GRANTED_TO IS 'optional date to which the skipper is granted this right';
COMMENT ON COLUMN BOAT.T_SKIPPER.SUSPENDED IS 'true indicates temporary disablement of access';



CREATE TABLE BOAT.T_TRIP_RESCUE (
  ID                      BIGINT      NOT NULL PRIMARY KEY REFERENCES BOAT.T_TRIP (ID) ON DELETE CASCADE,
  HIN                     VARCHAR(14) NOT NULL,
  VESSEL_NAME             VARCHAR(40),
  HULL_COLOUR             VARCHAR(30),
  SUPERSTRUCTURE_COLOUR   VARCHAR(30),
  STORAGE_TYPE            VARCHAR(10),
  STORAGE_LOCATION        VARCHAR(50),
  VESSEL_TYPE             VARCHAR(20) NOT NULL,
  LENGTH_M                INT         NULL,
  SKIPPER_NAME            VARCHAR(40) NOT NULL,
  CALL_SIGN               VARCHAR(40),
  CONTACT_PHONE           VARCHAR(20) NULL,
  EMERGENCY_CONTACT_NAME  VARCHAR(40) NULL,
  EMERGENCY_CONTACT_PHONE VARCHAR(40) NULL
);
ALTER TABLE BOAT.T_TRIP_RESCUE OWNER TO geodsea;

COMMENT ON COLUMN BOAT.T_TRIP_RESCUE.HIN IS 'Unique Hull Identification number per ISO 10087:2006';
COMMENT ON COLUMN BOAT.T_TRIP_RESCUE.LENGTH_M IS 'Length of the vessel in metres';


----------------------------------------------------------
-- Base class for Licenses
----------------------------------------------------------

CREATE SEQUENCE BOAT.LICENSE_ID_SEQ INCREMENT 1 MINVALUE 1 START 1 CACHE 1;
ALTER TABLE BOAT.LICENSE_ID_SEQ OWNER TO geodsea;

CREATE TABLE BOAT.T_LICENSE (
  ID             BIGINT      NOT NULL PRIMARY KEY DEFAULT nextval('BOAT.LICENSE_ID_SEQ'),
  LICENSE_NUMBER VARCHAR(30) NOT NULL,
  VALID_FROM     DATE        NOT NULL,
  VALID_TO       DATE        NOT NULL,
  VERIFIED_DT    TIMESTAMP   NULL,
  VERIFIED       BOOLEAN     NULL,
  LICENSOR_ID    BIGINT      NOT NULL REFERENCES BOAT.T_LICENSOR ON DELETE CASCADE ON UPDATE RESTRICT
);

ALTER TABLE BOAT.T_LICENSE OWNER TO geodsea;
ALTER SEQUENCE BOAT.LICENSE_ID_SEQ OWNED BY BOAT.T_LICENSE.ID;

COMMENT ON COLUMN BOAT.T_LICENSE.VERIFIED_DT IS 'The date when the relevant authority verified or repudiated the license';
COMMENT ON COLUMN BOAT.T_LICENSE.VERIFIED IS 'True if verified, false if repudiated, null if not verified';

----------------------------------------------------------
-- A skipper's license
----------------------------------------------------------

CREATE TABLE BOAT.T_LICENSE_SKIPPER (
  LICENSE_ID BIGINT NOT NULL PRIMARY KEY REFERENCES BOAT.T_LICENSE_SKIPPER ON DELETE CASCADE ON UPDATE RESTRICT,
  PERSON_ID  BIGINT NOT NULL REFERENCES BOAT.T_PERSON ON DELETE CASCADE ON UPDATE RESTRICT
);

ALTER TABLE BOAT.T_LICENSE_SKIPPER OWNER TO geodsea;
COMMENT ON TABLE BOAT.T_LICENSE_SKIPPER IS 'License granting a person a right to skipper a boat within the zone of the issuer';
COMMENT ON COLUMN BOAT.T_LICENSE_SKIPPER.PERSON_ID IS 'The person holding the skipper license';

----------------------------------------------------------
-- A vessel license
----------------------------------------------------------

CREATE TABLE BOAT.T_LICENSE_VESSEL (
  LICENSE_ID BIGINT NOT NULL PRIMARY KEY REFERENCES BOAT.T_LICENSE ON DELETE CASCADE ON UPDATE RESTRICT,
  VESSEL_ID  BIGINT NOT NULL REFERENCES BOAT.T_VESSEL ON DELETE CASCADE ON UPDATE RESTRICT,
  MAX_PEOPLE INT    NULL
);

ALTER TABLE BOAT.T_LICENSE_VESSEL OWNER TO geodsea;
COMMENT ON TABLE BOAT.T_LICENSE_VESSEL IS 'license required to own a boat within the zone of the issuer';
COMMENT ON COLUMN BOAT.T_LICENSE_VESSEL.MAX_PEOPLE IS 'The maximum number of persons allowed on this vessel';



