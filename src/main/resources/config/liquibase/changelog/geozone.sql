----------------------------------------------------------
-- No dependencies on other tables
----------------------------------------------------------


----------------------------------------------------------
-- Store for zones
----------------------------------------------------------

CREATE SEQUENCE BOAT.GEO_ZONE_GID_SEQ INCREMENT 1 MINVALUE 1 START 448 CACHE 1;
ALTER TABLE BOAT.GEO_ZONE_GID_SEQ OWNER TO geodsea;


CREATE TABLE BOAT.T_GEO_ZONE
(
  gid             BIGINT NOT NULL DEFAULT nextval('BOAT.GEO_ZONE_GID_SEQ' :: REGCLASS),
  ZONE_TYPE       CHARACTER VARYING(30),
  ORGANISATION_ID BIGINT,
  geometry        geometry (POLYGON, 4326),
  CONSTRAINT PK_GEO_ZONE PRIMARY KEY (gid)
)
WITH (
OIDS = FALSE
);
ALTER TABLE BOAT.T_GEO_ZONE OWNER TO geodsea;
ALTER SEQUENCE BOAT.GEO_ZONE_GID_SEQ OWNED BY BOAT.T_GEO_ZONE.GID;

CREATE INDEX IDX_GEO_ZONE_GIST ON BOAT.T_GEO_ZONE USING GIST (GEOMETRY);

COMMENT ON COLUMN BOAT.T_GEO_ZONE.ORGANISATION_ID IS 'organisation that defines/owns the zone definition';
COMMENT ON COLUMN BOAT.T_GEO_ZONE.ZONE_TYPE IS 'Identification of the type of zone being declared, e.g. AreaOfInterest';

INSERT INTO BOAT.T_GEO_ZONE (geometry, ZONE_TYPE)
VALUES (ST_GeomFromText(
            'POLYGON((115.72723388672 -31.73126220703, 115.39215087891 -31.734008789061, 115.39489746094 -32.283325195311, 115.8041381836 -32.286071777342, 115.76293945313 -31.830139160155, 115.74645996094 -31.77520751953, 115.72723388672 -31.73126220703))',
            4326),
        'RescueZone');



----------------------------------------------------------
-- Store for boat paths
----------------------------------------------------------

CREATE SEQUENCE BOAT.VESSEL_PATH_GID_SEQ INCREMENT 1 MINVALUE 1 START 1 CACHE 1;
ALTER TABLE BOAT.VESSEL_PATH_GID_SEQ OWNER TO geodsea;


CREATE TABLE BOAT.T_VESSEL_PATH
(
  gid      BIGINT NOT NULL DEFAULT nextval('BOAT.VESSEL_PATH_GID_SEQ' :: REGCLASS),
--  TRIP_ID BIGINT not null,
  geometry geometry (LINESTRING, 4326),
  CONSTRAINT PK_PATH PRIMARY KEY (gid)
)
WITH (
OIDS = FALSE
);
ALTER TABLE BOAT.T_VESSEL_PATH OWNER TO geodsea;
ALTER SEQUENCE BOAT.VESSEL_PATH_GID_SEQ OWNED BY BOAT.T_VESSEL_PATH.GID;

CREATE INDEX IDX_VESSEL_PATH_GIST ON BOAT.T_VESSEL_PATH USING GIST (GEOMETRY);
COMMENT ON TABLE BOAT.T_VESSEL_PATH IS 'A path taken by the boat without record of time';
--comment on column BOAT.VESSEL_PATH.TRIP_ID is 'the trip that this path relates to';

