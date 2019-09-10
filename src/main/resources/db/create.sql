SET MODE PostgreSQL;

CREATE TABLE IF NOT EXISTS heroes (
  id int PRIMARY KEY auto_increment,
  name VARCHAR,
  age INTEGER,
  special_power VARCHAR,
  weakness VARCHAR,
  squad_id INTEGER,
);

CREATE TABLE IF NOT EXISTS squads (
  id int PRIMARY KEY auto_increment,
  name VARCHAR,
  max_size INTEGER,
  cause VARCHAR,
);