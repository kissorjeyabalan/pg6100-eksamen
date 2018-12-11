CREATE TABLE users (
  username TEXT NOT NULL PRIMARY KEY,
  password TEXT NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE authentication_entity_roles (
  authentication_entity_username TEXT NOT NULL references users(username),
  roles TEXT NOT NULL
);