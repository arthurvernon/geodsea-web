CREATE SEQUENCE BOAT.VESSEL_ID_SEQ INCREMENT 1 MINVALUE 1 START 1 CACHE 1;
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
  FUEL_CAPACITY_L       INT         NULL,
-- the crap to get rid of eventually...
  sample_text_attribute VARCHAR(50),
  sample_date_attribute DATE
);

ALTER TABLE BOAT.T_VESSEL OWNER TO geodsea;
ALTER SEQUENCE BOAT.VESSEL_ID_SEQ OWNED BY BOAT.T_VESSEL.ID;

COMMENT ON COLUMN BOAT.T_VESSEL.HIN IS 'Hull Identification number per ISO 10087:2006';
COMMENT ON COLUMN BOAT.T_VESSEL.LENGTH_M IS 'Length of the vessel in metres';
COMMENT ON COLUMN BOAT.T_VESSEL.TOTAL_HP IS 'Total horse power of a motorised boat.';
COMMENT ON COLUMN BOAT.T_VESSEL.FUEL_CAPACITY_L IS 'Maximum number of litres of fuel that the boat can carry.';