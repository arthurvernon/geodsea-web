
----------------------------------------------------------
-- Views created for the benefit of open server
----------------------------------------------------------

CREATE VIEW BOAT.RESCUE_ZONE AS
  SELECT
    R.ID,
    G.GROUP_NAME,
    O.WEBSITE_URL,
    R.CALLSIGN,
    R.ZONE
  FROM
    BOAT.T_RESCUE R
    INNER JOIN BOAT.T_PARTICIPANT P ON R.ORGANISATION_FK = P.ID
    INNER JOIN BOAT.T_GROUP G ON R.ORGANISATION_FK = G.GROUP_ID
    INNER JOIN BOAT.T_ORGANISATION O ON O.ORGANISATION_ID = P.ID;

ALTER TABLE BOAT.RESCUE_ZONE OWNER TO geodsea;