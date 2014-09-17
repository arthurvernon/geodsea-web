-----------------------------------
-- Setup Dept of Transport with one member being Mr 'user'
-------------------------------------

-- WA department of transport
INSERT INTO BOAT.T_GROUP (GROUP_ID, GROUP_NAME, CONTACT_PERSON_FK) VALUES (5, 'Department of Transport', 4);
INSERT INTO BOAT.T_ORGANISATION (ORGANISATION_ID, WEBSITE_URL) VALUES (5, 'http://www.transport.wa.gov.au');
INSERT INTO BOAT.T_MEMBER (ID, GROUP_FK, PARTICIPANT_FK, active, manager) VALUES (1, 5, 4, TRUE, TRUE);
INSERT INTO BOAT.T_LICENSOR (ORGANISATION_FK, LICENSE_WS_URL, LICENSE_WS_USERNAME, LICENSE_WS_PASSWORD, ZONE_TITLE, ZONE)
VALUES
  (5, 'http://127.0.0.1:8080/geodsea/ws', 'username', 'password', 'Western Australia',
   ST_GeomFromText(
       'MULTIPOLYGON(((128.9669435963962 -12.31586203142603, 118.7810869522744 -17.5117120640925, 112.9250977181581 -21.958539388426, 111.9222312334651 -26.05384256490456, 113.1954255611551 -28.68809421211046, 115.0148502930076 -34.97612407490796, 118.3773563855272 -35.60096159511839, 124.5633263458142 -34.19939985184924, 128.9882990130726 -32.80754901877049, 128.9669435963962 -12.31586203142603
       )))',
       4326)
  );


-- NSW
INSERT INTO BOAT.T_GROUP (GROUP_ID, GROUP_NAME, CONTACT_PERSON_FK) VALUES (6, 'Roads and Maritime', 4);
INSERT INTO BOAT.T_ORGANISATION (ORGANISATION_ID, WEBSITE_URL) VALUES (6, 'http://www.maritime.nsw.gov.au/');

INSERT INTO BOAT.T_MEMBER (ID, GROUP_FK, PARTICIPANT_FK, active, manager) VALUES (2, 6, 4, TRUE, TRUE);

INSERT INTO BOAT.T_LICENSOR (ORGANISATION_FK, LICENSE_WS_URL, LICENSE_WS_USERNAME, LICENSE_WS_PASSWORD, ZONE_TITLE, ZONE)
VALUES
  (6, 'http://http://www.maritime.nsw.gov.au:8080/ws', 'username', 'password', 'NSW',
   ST_GeomFromText(
       'MULTIPOLYGON(((
141.0019221052301 -28.99544532054833, 140.9908864959677 -34.03774020328035, 150.5402962545999 -37.73266555220779, 152.0716408403959 -33.5220046004541, 153.1928339918643 -32.43154749457575, 154.3829512004806 -28.10690235077952, 141.0019221052301 -28.99544532054833
)))',
       4326)
  );

-- Whitfords Sea Rescue
INSERT INTO BOAT.T_GROUP (GROUP_ID, GROUP_NAME, CONTACT_PERSON_FK) VALUES (7, 'Whitfords Sea Rescue', 4);
INSERT INTO BOAT.T_ORGANISATION (ORGANISATION_ID, WEBSITE_URL) VALUES (7, 'http://www.whitfordssearescue.org.au/');

INSERT INTO BOAT.T_MEMBER (ID, GROUP_FK, PARTICIPANT_FK, active, manager) VALUES (3, 7, 4, TRUE, TRUE);

INSERT INTO BOAT.T_RESCUE (ORGANISATION_FK, CALLSIGN, REPORT_RATE, ZONE_TITLE, ZONE)
VALUES
  (7, 'VJ6LQ', 300, 'Mindarie To City Beach and out Past Rottnest',
   ST_GeomFromText(
       'MULTIPOLYGON(((
115.72723388672 -31.73126220703, 115.39215087891 -31.734008789061, 115.39489746094 -32.283325195311, 115.8041381836 -32.286071777342, 115.76293945313 -31.830139160155, 115.74645996094 -31.77520751953, 115.72723388672 -31.73126220703
)))',
       4326)
  );

