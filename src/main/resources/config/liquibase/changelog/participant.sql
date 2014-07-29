create sequence BOAT.PARTICIPANT_ID_SEQ increment 1 minvalue 100 start 100 cache 1;
alter table BOAT.PARTICIPANT_ID_SEQ owner to geodsea;

create table BOAT.T_PARTICIPANT (
  ID bigint not null primary key default nextval('BOAT.PARTICIPANT_ID_SEQ'),
  PARTICIPANT_NAME varchar(100) not null,
  REGISTRATION_TOKEN_EXPIRES timestamp null,
  REGISTRATION_TOKEN varchar(50) null,
  ENABLED boolean not null default false,
  CREATED_BY varchar(50) null default 'system',
  CREATED_DATE timestamp not null default now(),
  LAST_MODIFIED_BY varchar(50) null,
  LAST_MODIFIED_DATE timestamp null
);

alter table BOAT.T_PARTICIPANT OWNER to geodsea;
alter sequence BOAT.PARTICIPANT_ID_SEQ owned by BOAT.T_PARTICIPANT.ID;
alter table BOAT.T_PARTICIPANT add constraint UQ_PARTICIPANT_NAME unique(PARTICIPANT_NAME);

comment on COLUMN BOAT.T_PARTICIPANT.PARTICIPANT_NAME is 'unique identifier for a participant';
comment on COLUMN BOAT.T_PARTICIPANT.REGISTRATION_TOKEN is 'token that exists when account is initially created';
comment on COLUMN BOAT.T_PARTICIPANT.REGISTRATION_TOKEN_EXPIRES is 'time up till when the registration can be completed';
comment on COLUMN BOAT.T_PARTICIPANT.ENABLED is 'allows admin to lock the account. True if the participant may access the system, false otherwise';
comment on COLUMN BOAT.T_PARTICIPANT.CREATED_BY is 'The participant name of the user who created this participant';
comment on COLUMN BOAT.T_PARTICIPANT.CREATED_DATE is 'Date/time when the user created this participant';
comment on COLUMN BOAT.T_PARTICIPANT.LAST_MODIFIED_BY is 'The participant who last modified this record, null if it has not been modified';
comment on COLUMN BOAT.T_PARTICIPANT.LAST_MODIFIED_DATE is 'When (if ever) the participant''s details were last updated';

create index PARTICIPANT_NAME_IDX on BOAT.T_PARTICIPANT(PARTICIPANT_NAME);



create table BOAT.T_PERSON (
  PERSON_ID bigint not null primary key references BOAT.T_PARTICIPANT(ID) ON DELETE CASCADE,
  FIRST_NAME varchar(50) not null,
  LAST_NAME varchar(50) not null,
  EMAIL varchar(100) not null,
  LANG_KEY varchar(5) null,
  BIRTH_DATE DATE null,
  TELEPHONE varchar(20) null,
  PASSWORD varchar(150) not null,
  LANGUAGE_KEY varchar(5)
);

alter table BOAT.T_PERSON OWNER to geodsea;

create index PER_EMAIL_IDX on BOAT.T_PERSON(EMAIL);
