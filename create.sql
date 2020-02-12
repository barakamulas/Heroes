
--CREATE DATABASE heroes;
--
--\c heroes
--
--CREATE TABLE heroes(id SERIAL PRIMARY KEY, name varchar, age INTEGER, power varchar, weakness varchar, squadId INTEGER);
--
--CREATE TABLE squads(id SERIAL PRIMARY KEY, name varchar, size INTEGER, cause varchar);
--
--CREATE DATABASE heroes_test WITH TEMPLATE heroes;

CREATE DATABASE evaluate;

\c evaluate

CREATE TABLE users(id SERIAL PRIMARY KEY, name varchar, email varchar, job_grade_id INTEGER);

CREATE TABLE competencies(id SERIAL PRIMARY KEY, name varchar);

CREATE TABLE report_type(id SERIAL PRIMARY KEY, name varchar);

CREATE TABLE strands(id SERIAL PRIMARY KEY, name varchar, competency_id INTEGER);

CREATE TABLE reports(id SERIAL PRIMARY KEY, user_id varchar, strand_id INTEGER, score INTEGER, evaluation_type varchar);

INSERT INTO users(name, email, job_grade_id) VALUES ('Frankline Mulama', 'bb@gmail.com', 1);

INSERT INTO report_type(name) VALUES('self');

INSERT INTO report_type(name) VALUES('manager');

INSERT INTO report_type(name) VALUES('final');

INSERT INTO competencies(name) VALUES ('Organisation');

INSERT INTO strands(name, competency_id) VALUES('planning',1);

INSERT INTO reports(user_id, strand_id, score, evaluation_type) VALUES (1,1,3,1);

INSERT INTO reports(user_id, strand_id, score, evaluation_type) VALUES (1,1,2,2);

INSERT INTO reports(user_id, strand_id, score, evaluation_type) VALUES (1,1,1,3);






--CREATE DATABASE heroes_test WITH TEMPLATE heroes;