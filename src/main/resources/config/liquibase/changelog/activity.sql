----------------------------------------------------------
-- the vessel, permits and licenses
-- Depends upon:
--     participant.sql
--     agencies.sql
----------------------------------------------------------




CREATE SEQUENCE BOAT.ACTIVITY_ID_SEQ INCREMENT 1 MINVALUE 1 START 1 CACHE 1;
ALTER TABLE BOAT.ACTIVITY_ID_SEQ OWNER TO geodsea;

CREATE TABLE BOAT.T_ACTIVITY (
    ID                 BIGINT PRIMARY KEY   DEFAULT nextval('BOAT.ACTIVITY_ID_SEQ'),
    CREATED_BY         VARCHAR(50) NULL     DEFAULT 'system',
    CREATED_DATE       TIMESTAMP   NOT NULL DEFAULT now(),
    LAST_MODIFIED_BY   VARCHAR(50) NULL,
    LAST_MODIFIED_DATE TIMESTAMP   NULL,
    ACTIVITY_TYPE      VARCHAR(10) NOT NULL
);
ALTER TABLE BOAT.T_ACTIVITY OWNER TO geodsea;
ALTER SEQUENCE BOAT.ACTIVITY_ID_SEQ OWNED BY BOAT.T_ACTIVITY.ID;


----------------------------------------------------------
-- An event that the person is participating in
----------------------------------------------------------
CREATE TABLE BOAT.T_EVENT (
    ID                 BIGINT PRIMARY KEY REFERENCES BOAT.T_ACTIVITY (ID) ON DELETE CASCADE,
    TITLE              VARCHAR(100) NOT NULL,
    WAY_POINTS         geometry (LINESTRING, 4326),
    SCHEDULED_START    TIMESTAMP    NOT NULL,
    ACTUAL_START       TIMESTAMP   NULL,
    SCHEDULED_END      TIMESTAMP    NOT NULL,
    ACTUAL_END         TIMESTAMP    NULL,
    PARTICIPANT_ID     BIGINT       NULL REFERENCES BOAT.T_PARTICIPANT ON DELETE SET NULL ON UPDATE RESTRICT
);

ALTER TABLE BOAT.T_EVENT OWNER TO geodsea;

COMMENT ON COLUMN BOAT.T_EVENT.TITLE IS 'The name of the event';
COMMENT ON COLUMN BOAT.T_EVENT.WAY_POINTS IS 'The start and end point of a trip with any number of points in-between.';
COMMENT ON COLUMN BOAT.T_EVENT.SCHEDULED_START IS 'When the event is supposed to be starting.';
COMMENT ON COLUMN BOAT.T_EVENT.SCHEDULED_END IS 'When the event is supposed to be finishing.';
COMMENT ON COLUMN BOAT.T_EVENT.ACTUAL_START IS 'When this event actually started (if the fact was recorded).';
COMMENT ON COLUMN BOAT.T_EVENT.ACTUAL_END IS 'When this event actually ended.';
COMMENT ON COLUMN BOAT.T_EVENT.PARTICIPANT_ID IS 'The participant arranging the event and who can therefore start it.';

CREATE INDEX IDX_TRIP_WP_GIST ON BOAT.T_TRIP USING GIST (WAY_POINTS);


----------------------------------------------------------
-- A trip taken by a skipper on a vessel.
----------------------------------------------------------
CREATE TABLE BOAT.T_TRIP (
  ID                 BIGINT PRIMARY KEY REFERENCES BOAT.T_ACTIVITY (ID) ON DELETE CASCADE,
  WAY_POINTS         geometry (LINESTRING, 4326),
  PEOPLE_ON_BOARD    INT          NULL CHECK (PEOPLE_ON_BOARD > 0),
  ACTUAL_START       TIMESTAMP    NULL,
  FUEL_L             INT          NULL CHECK (FUEL_L > 0),
  HEADLINE           VARCHAR(100) NOT NULL,
  SCHEDULED_END      TIMESTAMP    NULL,
  ACTUAL_END         TIMESTAMP    NULL,
  RESCUE_ID          BIGINT       NULL REFERENCES BOAT.T_RESCUE ON DELETE SET NULL ON UPDATE RESTRICT
);

ALTER TABLE BOAT.T_TRIP OWNER TO geodsea;

COMMENT ON COLUMN BOAT.T_TRIP.WAY_POINTS IS 'The start and end point of a trip with any number of points in-between.';
COMMENT ON COLUMN BOAT.T_TRIP.PEOPLE_ON_BOARD IS 'The number of people including the skipper who are on the boat.';
COMMENT ON COLUMN BOAT.T_TRIP.ACTUAL_START IS 'When this trip actually started (if the fact was recorded).';
COMMENT ON COLUMN BOAT.T_TRIP.FUEL_L IS 'The estimated number of liters of fuel on board at the start of the journey';
COMMENT ON COLUMN BOAT.T_TRIP.HEADLINE IS 'The reason for making the trip, e.g. weekend at Rottnest. Shared with rescue';
COMMENT ON COLUMN BOAT.T_TRIP.SCHEDULED_END IS 'When this trip is planned to end.';
COMMENT ON COLUMN BOAT.T_TRIP.ACTUAL_END IS 'The actual time, if ever, that the journey was logged as completed.';
COMMENT ON COLUMN BOAT.T_TRIP.RESCUE_ID IS 'The organisation (role) that the system has ascertained is responsible for monitoring this trip.';

CREATE INDEX IDX_TRIP_WP_GIST ON BOAT.T_TRIP USING GIST (WAY_POINTS);


CREATE TABLE BOAT.T_TRIP_SKIPPER (
  ID              BIGINT       NOT NULL PRIMARY KEY REFERENCES BOAT.T_TRIP (ID) ON DELETE CASCADE,
  SCHEDULED_START TIMESTAMP    NULL,
  SUMMARY         VARCHAR(255) NULL,
  REPORT_RATE     INTEGER      NOT NULL DEFAULT 0,
  SKIPPER_ID      BIGINT       NOT NULL REFERENCES BOAT.T_SKIPPER ON DELETE CASCADE ON UPDATE RESTRICT,
  VESSEL_ID       BIGINT       NOT NULL REFERENCES BOAT.T_VESSEL ON DELETE CASCADE ON UPDATE RESTRICT
);
ALTER TABLE BOAT.T_TRIP_SKIPPER OWNER TO geodsea;

COMMENT ON COLUMN BOAT.T_TRIP_SKIPPER.SUMMARY IS 'A useful precis of the trip, entered probably after the fact.';
COMMENT ON COLUMN BOAT.T_TRIP_SKIPPER.VESSEL_ID IS 'Mandatory particular vessel that is intended to be used for this trip.';
COMMENT ON COLUMN BOAT.T_TRIP_SKIPPER.SKIPPER_ID IS 'Who is the skipper for this trip.';
COMMENT ON COLUMN BOAT.T_TRIP_SKIPPER.REPORT_RATE IS 'Rate in seconds to report location. Zero implies no reporting requirement';



----------------------------------------------------------
-- Location/Time of the boat during a trip
----------------------------------------------------------


CREATE TABLE BOAT.T_LOCATION_TIME (
  TRIP_FK         BIGINT    NOT NULL,
  TIME_RECEIVED   TIMESTAMP NOT NULL,
  GPS_SIGNAL_TIME TIMESTAMP NOT NULL,
  LOCATION        geometry (POINT, 4326),
  ACCURACY        INTEGER   NULL,
  BEARING         FLOAT4    NULL,
  SPEED           FLOAT4    NULL,
  PRIMARY KEY (TRIP_FK, GPS_SIGNAL_TIME)
);
ALTER TABLE boat.T_LOCATION_TIME OWNER TO geodsea;

ALTER TABLE BOAT.T_LOCATION_TIME ADD CONSTRAINT FK_LOCATION_TIME_TRIP FOREIGN KEY (TRIP_FK)
REFERENCES BOAT.T_TRIP (ID) ON DELETE CASCADE;

COMMENT ON TABLE BOAT.T_LOCATION_TIME IS 'Location reports submitted by a vessel';
COMMENT ON COLUMN BOAT.T_LOCATION_TIME.SPEED IS 'The accuracy in metres (86% confidence level) of the reported location';
COMMENT ON COLUMN BOAT.T_LOCATION_TIME.BEARING IS 'Direction, in degrees in the range 0.0 < bearing <= 360.0';
COMMENT ON COLUMN BOAT.T_LOCATION_TIME.SPEED IS 'Speed in metres per second';

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

