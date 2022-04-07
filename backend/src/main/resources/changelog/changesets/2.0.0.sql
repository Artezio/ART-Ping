--liquibase formatted sql

--changeset vbystrova:20200508-04
CREATE INDEX IF NOT EXISTS office_name_idx ON art_ping.office (name);
--rollback DROP INDEX IF EXISTS office_name_idx;

--changeset abovshevich:20200514-01
ALTER TABLE art_ping.project ADD UNIQUE (name);
CREATE INDEX IF NOT EXISTS project_name_idx ON art_ping.project (name);
--rollback DROP INDEX IF EXISTS project_name_idx;

--changeset ikuleshov:20210922-001
CREATE INDEX IF NOT EXISTS user_login_idx ON art_ping.user (login);
--rollback DROP INDEX IF EXISTS user_login_idx;

--changeset ikuleshov:20210922-002
CREATE INDEX IF NOT EXISTS employee_login_idx ON art_ping.employee (login);
--rollback DROP INDEX IF EXISTS employee_login_idx;

--changeset mgichevsky:20211221-001
CREATE INDEX IF NOT EXISTS employee_asc_project_field ON art_ping.employee (asc_project_name);
--rollback DROP INDEX IF EXISTS employee_asc_project_field;

--changeset mgichevsky:20211221-002
CREATE INDEX IF NOT EXISTS employee_desc_project_field ON art_ping.employee (desc_project_name);
--rollback DROP INDEX IF EXISTS employee_desc_project_field;

--changeset mgichevsky:20211223-001
CREATE INDEX IF NOT EXISTS employee_asc_role_field ON art_ping.employee (asc_role_name);
--rollback DROP INDEX IF EXISTS employee_asc_role_field;

--changeset mgichevsky:20211223-002
CREATE INDEX IF NOT EXISTS employee_desc_role_field ON art_ping.employee (desc_role_name);
--rollback DROP INDEX IF EXISTS employee_desc_role_field;