CREATE TABLE oauth_client_details (

  client_id               VARCHAR(256) NOT NULL PRIMARY KEY,
  resource_ids            VARCHAR(256),
  client_secret           VARCHAR(256),
  scope                   VARCHAR(256),
  authorized_grant_types  VARCHAR(256),
  web_server_redirect_uri VARCHAR(256),
  authorities             VARCHAR(256),
  access_token_validity   INT,
  refresh_token_validity  INT,
  additional_information  VARCHAR(4096),
  autoapprove             VARCHAR(4096)
);

ALTER TABLE oauth_client_details OWNER TO geodsea;


CREATE TABLE oauth_client_token (
  token_id          VARCHAR(256),
  token             BYTEA,
  authentication_id VARCHAR(256),
  user_name         VARCHAR(256),
  client_id         VARCHAR(256)
);

ALTER TABLE oauth_client_token OWNER TO geodsea;

CREATE TABLE oauth_access_token (
  token_id          VARCHAR(256),
  token             BYTEA,
  authentication_id VARCHAR(256),
  user_name         VARCHAR(256),
  client_id         VARCHAR(256),
  authentication    BYTEA,
  refresh_token     VARCHAR(256)
);
ALTER TABLE oauth_access_token OWNER TO geodsea;

CREATE TABLE oauth_refresh_token (
  token_id       VARCHAR(256),
  token          BYTEA,
  authentication BYTEA
);

ALTER TABLE oauth_refresh_token OWNER TO geodsea;

CREATE TABLE oauth_code (
  code VARCHAR(256)
);

ALTER TABLE oauth_code OWNER TO geodsea;

CREATE TABLE oauth_approvals (
  userId         VARCHAR(256),
  clientId       VARCHAR(256),
  scope          VARCHAR(256),
  status         VARCHAR(256),
  expiresAt      TIMESTAMP,
  lastModifiedAt TIMESTAMP);

ALTER TABLE oauth_approvals OWNER TO geodsea;

