-----------------------------------
-- Setup Dept of Transport with one member being Mr 'user'
-------------------------------------

INSERT INTO BOAT.T_PARTICIPANT_GROUP (id, published_name, website_url)
VALUES (5, 'Department of Transport', 'http://www.transport.wa.gov.au');

INSERT INTO BOAT.T_MEMBER (id, participant_group_id, participant_id, active, manager) VALUES (1, 5, 4, TRUE, TRUE);

UPDATE BOAT.T_PARTICIPANT_GROUP
SET member_id = 1 where id = 5;

-- WA department of transport
INSERT INTO BOAT.T_LICENSOR (participant_id, LICENSE_WS_URL, LICENSE_WS_USERNAME, LICENSE_WS_PASSWORD, ZONE_TITLE, ZONE)
VALUES
  (5, 'http://127.0.0.1:8080/geodsea/ws', 'username', 'password', 'Western Australia',
   ST_GeomFromText(
       'POLYGON((128.9669435963962 -12.31586203142603, 118.7810869522744 -17.5117120640925, 112.9250977181581 -21.958539388426, 111.9222312334651 -26.05384256490456, 113.1954255611551 -28.68809421211046, 115.0148502930076 -34.97612407490796, 118.3773563855272 -35.60096159511839, 124.5633263458142 -34.19939985184924, 128.9882990130726 -32.80754901877049, 128.9669435963962 -12.31586203142603
       ))',
       4326)
  );


-- NSW
INSERT INTO BOAT.T_PARTICIPANT_GROUP (id, published_name, website_url)
VALUES (6, 'Roads and Maritime', 'http://www.maritime.nsw.gov.au/');

INSERT INTO BOAT.T_MEMBER (id, participant_group_id, participant_id, active, manager) VALUES (2, 6, 4, TRUE, TRUE);

UPDATE BOAT.T_PARTICIPANT_GROUP
SET member_id = 2 where id = 6;


INSERT INTO BOAT.T_LICENSOR (participant_id, LICENSE_WS_URL, LICENSE_WS_USERNAME, LICENSE_WS_PASSWORD, ZONE_TITLE, ZONE)
VALUES
  (6, 'http://http://www.maritime.nsw.gov.au:8080/ws', 'username', 'password', 'NSW',
   ST_GeomFromText(
       'POLYGON((
141.0019221052301 -28.99544532054833, 140.9908864959677 -34.03774020328035, 150.5402962545999 -37.73266555220779, 152.0716408403959 -33.5220046004541, 153.1928339918643 -32.43154749457575, 154.3829512004806 -28.10690235077952, 141.0019221052301 -28.99544532054833
))',
       4326)
  );



