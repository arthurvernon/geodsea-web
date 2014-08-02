
----------------------------------------------------------
-- Views created for the benefit of open server
----------------------------------------------------------

CREATE VIEW BOAT.RESCUE_ZONE AS
  SELECT
    R.ID,
    PG.PUBLISHED_NAME,
    PG.WEBSITE_URL,
    R.CALLSIGN,
    R.RESCUE_ZONE
  FROM
    BOAT.T_RESCUE R INNER JOIN BOAT.T_PARTICIPANT P ON R.PARTICIPANT_ID = P.ID
    INNER JOIN BOAT.T_PARTICIPANT_GROUP PG ON PG.ID = P.ID;

ALTER TABLE BOAT.RESCUE_ZONE OWNER TO geodsea;