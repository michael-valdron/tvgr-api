-- Create games

-- !Ups

CREATE TABLE games
(
  id INT NOT NULL CONSTRAINT games_pk PRIMARY KEY,
  title VARCHAR(128) NOT NULL,
  genre VARCHAR(64) NOT NULL,
  description TEXT NOT NULL,
  release_date VARCHAR(10) NOT NULL
);

CREATE UNIQUE INDEX games_id_uindex
  ON games(id);

CREATE UNIQUE INDEX games_title_uindex
  ON games(title);

-- !Downs

DROP TABLE games;