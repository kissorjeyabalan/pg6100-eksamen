create table show_entity
  (
    id BIGSERIAL PRIMARY KEY,
    cinema_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    start_time TIMESTAMP NOT NULL
  );

create table show_entity_seats
  (
    show_entity_id BIGINT NOT NULL REFERENCES show_entity(id),
    seats TEXT
  );

create table theater_entity
  (
    id BIGSERIAL PRIMARY KEY,
    name TEXT,
    seats_max INTEGER NOT NULL
  );

create table theater_entity_seats
  (
    theater_entity_id BIGINT NOT NULL REFERENCES theater_entity(id),
    seats TEXT
  );