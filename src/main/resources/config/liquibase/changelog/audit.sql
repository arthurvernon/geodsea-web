CREATE SEQUENCE BOAT.AUDIT_EVENT_ID_SEQ INCREMENT 1 MINVALUE 1 START 1 CACHE 1;
ALTER TABLE BOAT.AUDIT_EVENT_ID_SEQ OWNER TO geodsea;

CREATE TABLE BOAT.T_AUDIT_EVENT (
  EVENT_ID   BIGINT NOT NULL PRIMARY KEY DEFAULT nextval('BOAT.AUDIT_EVENT_ID_SEQ'),
  principal  VARCHAR(50),
  event_date TIMESTAMP,
  event_type VARCHAR(50)
);

ALTER TABLE BOAT.T_AUDIT_EVENT OWNER TO geodsea;
ALTER SEQUENCE BOAT.AUDIT_EVENT_ID_SEQ OWNED BY BOAT.T_AUDIT_EVENT.EVENT_ID;

COMMENT ON COLUMN BOAT.T_AUDIT_EVENT.principal IS 'The name of the principal logged on';
COMMENT ON COLUMN BOAT.T_AUDIT_EVENT.event_date IS 'Date on which the event occurred';
COMMENT ON COLUMN BOAT.T_AUDIT_EVENT.event_type IS 'Description of the event that occurred.';


CREATE TABLE BOAT.T_AUDIT_EVENT_DATA (
  EVENT_ID   BIGINT      NOT NULL,
  name       VARCHAR(50) NOT NULL,
  value      VARCHAR(255),
  event_date DATE,
  event_type VARCHAR(50)
);

ALTER TABLE BOAT.T_AUDIT_EVENT_DATA OWNER TO geodsea;


ALTER TABLE Boat.T_AUDIT_EVENT_DATA ADD PRIMARY KEY (event_id, name);
CREATE INDEX idx_audit_event ON Boat.T_AUDIT_EVENT (principal, event_date);

CREATE INDEX idx_audit_event_data ON Boat.T_AUDIT_EVENT_DATA (event_id);

ALTER TABLE BOAT.T_AUDIT_EVENT_DATA ADD CONSTRAINT FK_event_audit_event_data FOREIGN KEY (event_id) REFERENCES BOAT.T_AUDIT_EVENT (EVENT_ID);

