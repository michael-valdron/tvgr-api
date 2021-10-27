-- Create users

-- !Ups

CREATE TABLE users
(
  username VARCHAR(64) NOT NULL CONSTRAINT users_pk PRIMARY KEY,
  password VARCHAR(128) NOT NULL
);

CREATE UNIQUE INDEX users_username_uindex
  ON users(username);

-- !Downs

DROP TABLE users;