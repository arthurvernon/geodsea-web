----------------------------------------------------------
-- Participants including people and organisations
-- depends upon participant.sql, geozone.sql
----------------------------------------------------------


----------------------------------------------------------
-- sequencer for all organisation roles
----------------------------------------------------------

CREATE SEQUENCE BOAT.ROLE_ID_SEQ INCREMENT 1 MINVALUE 1 START 1 CACHE 1;
ALTER TABLE BOAT.ROLE_ID_SEQ OWNER TO geodsea;

----------------------------------------------------------
-- The licensing role
----------------------------------------------------------

CREATE TABLE boat.T_LICENSOR (
  ID                  BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('BOAT.ROLE_ID_SEQ'),
  ORGANISATION_FK     BIGINT NOT NULL,
  LICENSE_WS_URL      VARCHAR(100),
  LICENSE_WS_USERNAME VARCHAR(20),
  LICENSE_WS_PASSWORD VARCHAR(20),
  ZONE_TITLE          VARCHAR(50),
  ZONE                geometry (POLYGON, 4326)
);
ALTER TABLE boat.T_LICENSOR OWNER TO geodsea;

ALTER TABLE BOAT.T_LICENSOR ADD CONSTRAINT FK_LICENSOR_ORGANISATION FOREIGN KEY (ORGANISATION_FK)
REFERENCES BOAT.T_ORGANISATION (ORGANISATION_ID) ON DELETE CASCADE;

COMMENT ON TABLE BOAT.T_LICENSOR IS 'A role typically performed by a government organisation to confirm licensing';
COMMENT ON COLUMN BOAT.T_LICENSOR.LICENSE_WS_URL IS 'The URL of the web service that may be called to confirm license details';
COMMENT ON COLUMN BOAT.T_LICENSOR.LICENSE_WS_USERNAME IS 'The username if any to include in the header';
COMMENT ON COLUMN BOAT.T_LICENSOR.LICENSE_WS_PASSWORD IS 'The password if any to include in the header';
COMMENT ON COLUMN BOAT.T_LICENSOR.ZONE_TITLE IS 'A description of the zone in human readable form (not localised)';
COMMENT ON COLUMN BOAT.T_LICENSOR.ZONE IS 'The zone over which this authority provides licenses.';


----------------------------------------------------------
-- Boating clubs
----------------------------------------------------------


CREATE TABLE BOAT.T_CLUB (
  ID              BIGINT      NOT NULL PRIMARY KEY DEFAULT nextval('BOAT.ROLE_ID_SEQ'),
  ORGANISATION_FK BIGINT      NOT NULL,
  CALLSIGN        VARCHAR(40) NOT NULL,
  ZONE_TITLE      VARCHAR(50),
  REPORT_RATE     INTEGER     NOT NULL DEFAULT 0,
  ZONE            geometry (POLYGON, 4326)
);
ALTER TABLE BOAT.T_CLUB OWNER TO geodsea;
ALTER TABLE BOAT.T_CLUB ADD CONSTRAINT FK_CLUB_ORGANISATION FOREIGN KEY (ORGANISATION_FK)
REFERENCES BOAT.T_ORGANISATION (ORGANISATION_ID) ON DELETE CASCADE;

COMMENT ON TABLE BOAT.T_CLUB IS 'An organisation that hosts boating events.';
COMMENT ON COLUMN BOAT.T_CLUB.ZONE_TITLE IS 'A description of the zone in human readable form (not localised)';
COMMENT ON COLUMN BOAT.T_CLUB.ZONE IS 'The zone over which this club hosts events.';
COMMENT ON COLUMN BOAT.T_CLUB.CALLSIGN IS 'The club''s callsign.';
COMMENT ON COLUMN BOAT.T_CLUB.REPORT_RATE IS 'The default rate at which to request location updates. Otherwise event value is used.';

----------------------------------------------------------
-- The rescue role
----------------------------------------------------------

CREATE TABLE boat.T_RESCUE (
  ID              BIGINT      NOT NULL PRIMARY KEY DEFAULT nextval('BOAT.ROLE_ID_SEQ'),
  ORGANISATION_FK BIGINT      NOT NULL,
  CALLSIGN        VARCHAR(40) NOT NULL,
  ZONE_TITLE      VARCHAR(50),
  REPORT_RATE     INTEGER     NOT NULL DEFAULT 0,
  ZONE            geometry (POLYGON, 4326)
);
ALTER TABLE boat.T_RESCUE OWNER TO geodsea;

ALTER TABLE BOAT.T_RESCUE ADD CONSTRAINT FK_RESCUE_ORGANISATION FOREIGN KEY (ORGANISATION_FK)
REFERENCES BOAT.T_ORGANISATION (ORGANISATION_ID) ON DELETE CASCADE;


COMMENT ON TABLE BOAT.T_RESCUE IS 'A sea rescue organisation details.';
COMMENT ON COLUMN BOAT.T_RESCUE.ZONE IS 'The rescue zone the organisation is responsible for.';
COMMENT ON COLUMN BOAT.T_RESCUE.CALLSIGN IS 'The call sign by which the rescue organisation goes by.';
COMMENT ON COLUMN BOAT.T_RESCUE.REPORT_RATE IS 'The default rate at which to request location updates from vessels within the zone.';

----------------------------------------------------------
-- The legal jurisdiction role for sea rescue
----------------------------------------------------------

CREATE SEQUENCE BOAT.JURISDICTION_ID_SEQ INCREMENT 1 MINVALUE 1 START 1 CACHE 1;
ALTER TABLE BOAT.JURISDICTION_ID_SEQ OWNER TO geodsea;

CREATE TABLE boat.T_JURISDICTION (
  ID              BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('BOAT.JURISDICTION_ID_SEQ'),
  ORGANISATION_FK BIGINT NOT NULL,
  ZONE_TITLE      VARCHAR(50),
  ZONE            geometry (POLYGON, 4326)
);
ALTER TABLE boat.T_JURISDICTION OWNER TO geodsea;
ALTER SEQUENCE BOAT.JURISDICTION_ID_SEQ OWNED BY BOAT.T_JURISDICTION.ID;

ALTER TABLE BOAT.T_JURISDICTION ADD CONSTRAINT FK_JURISDICTION_ORGANISATION FOREIGN KEY (ORGANISATION_FK)
REFERENCES BOAT.T_ORGANISATION (ORGANISATION_ID) ON DELETE CASCADE;


COMMENT ON TABLE BOAT.T_JURISDICTION IS 'A sea rescue organisation details.';
COMMENT ON COLUMN BOAT.T_JURISDICTION.ZONE_TITLE IS 'Description of the zone in the local language';
COMMENT ON COLUMN BOAT.T_JURISDICTION.ZONE IS 'The rescue zone the organisation is responsible for.';
