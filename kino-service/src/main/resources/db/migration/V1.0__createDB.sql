create table show_entity
  (
    id BIGSERIAL PRIMARY KEY,
    cinema_id bigint not null,
    movie_id bigint not null,
    start_time timestamp not null
  );

create table show_entity_seats
  (
    show_entity_id bigint not null,
    seats varchar(255)
  );

create table theater_entity
  (
    id BIGSERIAL PRIMARY KEY,
    name varchar(255),
    seats_max integer not null
  );

create table theater_entity_seats
  (
    theater_entity_id bigint not null,
    seats varchar(255)
  );

alter table show_entity_seats add constraint FKtisb2mpuew0milxb9h80u0fhl foreign key (show_entity_id) references show_entity;
alter table theater_entity_seats add constraint FKm00qw99biocimyoaf7hghauuo foreign key (theater_entity_id) references theater_entity;

