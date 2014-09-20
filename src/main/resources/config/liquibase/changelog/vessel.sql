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
-- A trip taken by a skipper on a vessel.
----------------------------------------------------------

CREATE SEQUENCE BOAT.TRIP_ID_SEQ INCREMENT 1 MINVALUE 1 START 1 CACHE 1;
ALTER TABLE BOAT.TRIP_ID_SEQ OWNER TO geodsea;

CREATE TABLE BOAT.T_TRIP (
  ID              BIGINT PRIMARY KEY DEFAULT nextval('BOAT.TRIP_ID_SEQ'),
  WAY_POINTS      geometry (MULTIPOINT, 4326),
  JOURNEY         geometry (LINESTRING, 4326),
  SUMMARY         VARCHAR(255) NULL,
  PEOPLE_ON_BOARD INT          NULL CHECK (PEOPLE_ON_BOARD > 0),
  SCHEDULED_START TIMESTAMP    NULL,
  ACTUAL_START    TIMESTAMP    NULL,
  FUEL_L          INT          NULL CHECK (FUEL_L > 0),
  HEADLINE        VARCHAR(100) NOT NULL,
  SCHEDULED_END   TIMESTAMP    NULL,
  ACTUAL_END      TIMESTAMP    NULL,
  RESCUE_ID       BIGINT       NULL REFERENCES BOAT.T_RESCUE ON DELETE SET NULL ON UPDATE RESTRICT,
  REPORT_RATE     INTEGER      NOT NULL DEFAULT 0,
  ADVISED_FIRST   TIMESTAMP,
  ADVISED_LAST    TIMESTAMP,
  PERSON_ID       BIGINT       NOT NULL REFERENCES BOAT.T_PERSON ON DELETE CASCADE ON UPDATE RESTRICT,
  VESSEL_ID       BIGINT       NOT NULL REFERENCES BOAT.T_VESSEL ON DELETE CASCADE ON UPDATE RESTRICT
);

ALTER TABLE BOAT.T_TRIP OWNER TO geodsea;
ALTER SEQUENCE BOAT.TRIP_ID_SEQ OWNED BY BOAT.T_TRIP.ID;

COMMENT ON COLUMN BOAT.T_TRIP.JOURNEY IS 'A historical record of the journey that is maintained when the user enables tracking.';
COMMENT ON COLUMN BOAT.T_TRIP.WAY_POINTS IS 'The start and end point of a trip with any number of points in-between.';
COMMENT ON COLUMN BOAT.T_TRIP.SUMMARY IS 'A useful precis of the trip, entered probably after the fact.';
COMMENT ON COLUMN BOAT.T_TRIP.PEOPLE_ON_BOARD IS 'The number of people including the skipper who are on the boat.';
COMMENT ON COLUMN BOAT.T_TRIP.SCHEDULED_START IS 'When this trip is planned to start.';
COMMENT ON COLUMN BOAT.T_TRIP.ACTUAL_START IS 'When this trip actually started (if the fact was recorded).';
COMMENT ON COLUMN BOAT.T_TRIP.FUEL_L IS 'The estimated number of liters of fuel on board at the start of the journey';
COMMENT ON COLUMN BOAT.T_TRIP.HEADLINE IS 'The reason for making the trip, e.g. weekend at Rottnest. Shared with rescue';
COMMENT ON COLUMN BOAT.T_TRIP.SCHEDULED_END IS 'When this trip is planned to end.';
COMMENT ON COLUMN BOAT.T_TRIP.ACTUAL_END IS 'The actual time, if ever, that the journey was logged as completed.';
COMMENT ON COLUMN BOAT.T_TRIP.RESCUE_ID IS 'The organisation (role) that the system has ascertained is responsible for monitoring this trip.';
COMMENT ON COLUMN BOAT.T_TRIP.ADVISED_FIRST IS 'The date when the plan was first advised to sea rescue.';
COMMENT ON COLUMN BOAT.T_TRIP.ADVISED_LAST IS 'The date when the plan was last advised to sea rescue.';
COMMENT ON COLUMN BOAT.T_TRIP.PERSON_ID IS 'Who is the skipper for this trip.';
COMMENT ON COLUMN BOAT.T_TRIP.VESSEL_ID IS 'Mandatory particular vessel that is intended to be used for this trip.';
COMMENT ON COLUMN BOAT.T_TRIP.REPORT_RATE IS 'Rate in seconds to report location. Zero implies no reporting requirement';


CREATE INDEX IDX_TRIP_WP_GIST ON BOAT.T_TRIP USING GIST (WAY_POINTS);
CREATE INDEX IDX_TRIP_JOURNEY_GIST ON BOAT.T_TRIP USING GIST (JOURNEY);

----------------------------------------------------------
-- Location/Time of the boat during a trip
----------------------------------------------------------


CREATE TABLE BOAT.T_LOCATION_TIME (
  TRIP_FK         BIGINT    NOT NULL,
  TIME_RECEIVED   TIMESTAMP NOT NULL,
  GPS_SIGNAL_TIME TIMESTAMP NOT NULL,
  LOCATION        geometry (POINT, 4326),
  PRIMARY KEY (TRIP_FK, GPS_SIGNAL_TIME)
);
ALTER TABLE boat.T_LOCATION_TIME OWNER TO geodsea;

ALTER TABLE BOAT.T_LOCATION_TIME ADD CONSTRAINT FK_LOCATION_TIME_TRIP FOREIGN KEY (TRIP_FK)
REFERENCES BOAT.T_TRIP (ID) ON DELETE CASCADE;


---------------------------------------------------------
-- Monitor
-- Participants who can observe the trip
----------------------------------------------------------

CREATE SEQUENCE BOAT.MONITOR_ID_SEQ INCREMENT 1 MINVALUE 1 START 1 CACHE 1;
ALTER TABLE BOAT.MONITOR_ID_SEQ OWNER TO geodsea;


CREATE TABLE BOAT.T_MONITOR (
  ID             BIGINT    NOT NULL PRIMARY KEY DEFAULT nextval('BOAT.MONITOR_ID_SEQ'),
  PARTICIPANT_FK BIGINT    NOT NULL,
  TRIP_FK        BIGINT    NOT NULL,
  START_TIME     TIMESTAMP NULL,
  FINISH_TIME    TIMESTAMP NULL
);
ALTER TABLE boat.T_MONITOR OWNER TO geodsea;
ALTER SEQUENCE BOAT.MONITOR_ID_SEQ OWNED BY BOAT.T_MONITOR.ID;

COMMENT ON TABLE BOAT.T_MONITOR IS 'Right of a participant to observe the trip';
COMMENT ON COLUMN BOAT.T_MONITOR.START_TIME IS 'Optional time from when the monitor may view the trip. If not specified then from the trip start';
COMMENT ON COLUMN BOAT.T_MONITOR.FINISH_TIME IS 'Optional time from when the monitor may view the trip. If not specified then it will be until the trip ends';

ALTER TABLE BOAT.T_MONITOR ADD CONSTRAINT FK_MONITOR_TRIP FOREIGN KEY (TRIP_FK)
REFERENCES BOAT.T_TRIP (ID) ON DELETE CASCADE;

ALTER TABLE BOAT.T_MONITOR ADD CONSTRAINT FK_MONITOR_PARTICIPANT FOREIGN KEY (PARTICIPANT_FK)
REFERENCES BOAT.T_PARTICIPANT (ID) ON DELETE CASCADE;

CREATE INDEX IDX_TRACK_PARTICIPANT ON Boat.T_MONITOR (PARTICIPANT_FK);
CREATE INDEX IDX_TRACK_PARTICIPANT_FINISH ON Boat.T_MONITOR (FINISH_TIME)
  WHERE FINISH_TIME IS NOT NULL;
CREATE INDEX IDX_TRACK_PARTICIPANT_START ON Boat.T_MONITOR (START_TIME)
  WHERE START_TIME IS NOT NULL;


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

