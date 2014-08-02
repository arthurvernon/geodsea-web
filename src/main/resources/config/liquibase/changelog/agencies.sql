----------------------------------------------------------
-- Some date to add after the fact to create a zone for Whitfords sea rescue
----------------------------------------------------------

--update boat.rescue set rescue_zone = 
--ST_GeomFromText('POLYGON((115.72723388672 -31.73126220703, 115.39215087891 -31.734008789061, 115.39489746094 -32.283325195311, 115.8041381836 -32.286071777342, 115.76293945313 -31.830139160155, 115.74645996094 -31.77520751953, 115.72723388672 -31.73126220703))',4326);


----------------------------------------------------------
-- Participants including people and organisations
-- depends upon participant.sql, geozone.sql
----------------------------------------------------------


----------------------------------------------------------
-- The licensing role
----------------------------------------------------------
CREATE SEQUENCE BOAT.LICENSOR_ID_SEQ INCREMENT 1 MINVALUE 1 START 1 CACHE 1;
ALTER TABLE BOAT.LICENSOR_ID_SEQ OWNER TO geodsea;

CREATE TABLE boat.T_LICENSOR (
  ID                   BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('BOAT.LICENSOR_ID_SEQ'),
  PARTICIPANT_ID       BIGINT NOT NULL REFERENCES BOAT.T_PARTICIPANT_GROUP (ID) ON DELETE CASCADE,
  WS_URL_LICENSE_CHECK VARCHAR(100),
  JURISDICTION         geometry (POLYGON, 4326)
);
ALTER TABLE boat.T_LICENSOR OWNER TO geodsea;
ALTER SEQUENCE BOAT.LICENSOR_ID_SEQ OWNED BY BOAT.T_LICENSOR.ID;

COMMENT ON TABLE BOAT.T_LICENSOR IS 'A role typically performed by a government organisation to confirm licensing';
COMMENT ON COLUMN BOAT.T_LICENSOR.WS_URL_LICENSE_CHECK IS 'The URL of the web service that may be called to confirm license details';
COMMENT ON COLUMN BOAT.T_LICENSOR.JURISDICTION IS 'The region over which this authority provides licenses.';


----------------------------------------------------------
-- The tracking role
----------------------------------------------------------

CREATE SEQUENCE BOAT.TRACK_ID_SEQ INCREMENT 1 MINVALUE 1 START 1 CACHE 1;
ALTER TABLE BOAT.TRACK_ID_SEQ OWNER TO geodsea;

CREATE TABLE BOAT.T_TRACK (
  ID             BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('BOAT.TRACK_ID_SEQ'),
  PARTICIPANT_ID BIGINT NOT NULL
);
ALTER TABLE BOAT.T_TRACK OWNER TO geodsea;
ALTER SEQUENCE BOAT.TRACK_ID_SEQ OWNED BY BOAT.T_TRACK.ID;
ALTER TABLE BOAT.T_TRACK ADD CONSTRAINT FK_TRACK_PARTICIPANT FOREIGN KEY (PARTICIPANT_ID) REFERENCES BOAT.T_PARTICIPANT_GROUP (ID) ON DELETE CASCADE;

COMMENT ON TABLE BOAT.T_TRACK IS 'An organisation that manages and wants to track boats in events that are conducted that organisation';

----------------------------------------------------------
-- The rescue role
----------------------------------------------------------

CREATE SEQUENCE BOAT.RESCUE_ID_SEQ INCREMENT 1 MINVALUE 1 START 1 CACHE 1;
ALTER TABLE BOAT.RESCUE_ID_SEQ OWNER TO geodsea;

CREATE TABLE boat.T_RESCUE (
  ID             BIGINT      NOT NULL PRIMARY KEY DEFAULT nextval('BOAT.RESCUE_ID_SEQ'),
  PARTICIPANT_ID BIGINT      NOT NULL,
  CALLSIGN       VARCHAR(40) NOT NULL,
  RESCUE_ZONE    geometry (POLYGON, 4326)
);
ALTER TABLE boat.T_RESCUE OWNER TO geodsea;
ALTER SEQUENCE BOAT.RESCUE_ID_SEQ OWNED BY BOAT.T_RESCUE.ID;

ALTER TABLE BOAT.T_RESCUE ADD CONSTRAINT FK_RESCUE_PARTICIPANT FOREIGN KEY (PARTICIPANT_ID)
    REFERENCES BOAT.T_PARTICIPANT_GROUP (ID) ON DELETE CASCADE;


COMMENT ON TABLE BOAT.T_RESCUE IS 'A sea rescue organisation details.';
COMMENT ON COLUMN BOAT.T_RESCUE.RESCUE_ZONE IS 'The rescue zone the organisation is responsible for.';
COMMENT ON COLUMN BOAT.T_RESCUE.CALLSIGN IS 'The call sign by which the rescue organisation goes by.';
