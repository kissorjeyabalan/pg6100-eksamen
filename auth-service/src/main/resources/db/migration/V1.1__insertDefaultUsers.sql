INSERT INTO users (username, password, enabled) VALUES ('admin', '$2a$10$tu4xrkOehq5/lu5czqKP1uIH1LDiCd59RSKyJ6ZWlv9JeGQODo1g2', true);
INSERT INTO authentication_entity_roles (authentication_entity_username, roles) VALUES ('admin', 'ROLE_ADMIN');
INSERT INTO authentication_entity_roles (authentication_entity_username, roles) VALUES ('admin', 'ROLE_USER');