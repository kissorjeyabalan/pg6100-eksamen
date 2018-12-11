insert into users (enabled, password, username) values (true, '$2a$10$pUxIQMhhuHqNF4UFb4u/VOH8BAanKgCTr7VGHJ1E6.5wBgsE/Wf9i', 'admin')
insert into authentication_entity_roles (authentication_entity_username, roles) values ('admin', 'ROLE_USER')
insert into authentication_entity_roles (authentication_entity_username, roles) values ('admin', 'ROLE_ADMIN')