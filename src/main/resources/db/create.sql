SET MODE PostgreSQL;

CREATE TABLE IF NOT EXISTS heroes (
  id int PRIMARY KEY auto_increment,
  name VARCHAR,
  age INTEGER,
  power VARCHAR,
  weakness VARCHAR,
  squadId INTEGER,
);

CREATE TABLE IF NOT EXISTS squads (
  id int PRIMARY KEY auto_increment,
  name VARCHAR,
  size INTEGER,
  cause VARCHAR,
);