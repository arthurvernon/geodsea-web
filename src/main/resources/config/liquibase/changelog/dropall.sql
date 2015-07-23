----------------------------------------------------------
-- cleanup database
----------------------------------------------------------

drop table if exists BOAT.T_GEO_ZONE cascade;
drop sequence if exists BOAT.GEO_ZONE_GID_SEQ cascade;
drop sequence if exists BOAT.VESSEL_PATH_GID_SEQ cascade;
drop table if exists BOAT.T_VESSEL cascade;
drop table if exists BOAT.T_VESSEL_EQUIPMENT cascade;
drop table if exists BOAT.T_OWNER cascade;
drop table if exists BOAT.T_SKIPPER cascade;
drop table if exists BOAT.T_LICENSE cascade;
drop table if exists BOAT.T_LICENSE_SKIPPER cascade;
drop table if exists BOAT.T_LICENSE_VESSEL cascade;
drop table if exists BOAT.T_TRIP cascade;
drop table if exists BOAT.T_ACTIVITY cascade;
drop table if exists BOAT.T_JURISDICTION cascade;

drop sequence if exists BOAT.ROLE_ID_SEQ;
drop sequence if exists BOAT.T_VESSEL_ID_SEQ;
drop sequence if exists BOAT.T_LICENSE_ID_SEQ;
drop sequence if exists BOAT.T_VESSEL_OWNER_ID_SEQ;
drop sequence if exists BOAT.T_SKIPPER_ID_SEQ;
drop sequence if exists BOAT.T_RIGHT_ID_SEQ;
drop sequence if exists BOAT.T_BOAT_ID_SEQ;

-- delete soon
drop sequence if exists BOAT.T_TRIP_ID_SEQ;
drop sequence if exists BOAT.T_ACTIVITY_ID_SEQ;
drop sequence if exists BOAT.T_JURISDICTION_ID_SEQ cascade;


