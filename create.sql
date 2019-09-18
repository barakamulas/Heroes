
CREATE DATABASE heroes;

\c heroes

CREATE TABLE heroes(id SERIAL PRIMARY KEY, name varchar, age INTEGER, power varchar, squadId INTEGER);

CREATE TABLE squads(id SERIAL PRIMARY KEY, name varchar, size INTEGER, cause varchar);

CREATE DATABASE heroes_test WITH TEMPLATE heroes;