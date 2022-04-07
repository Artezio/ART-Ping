--liquibase formatted sql
--changeset aabramovich:11-01-2021-create-schema
--preconditions onFail:MARK_RAN
CREATE SCHEMA IF NOT EXISTS art_ping;
--rollback DROP SCHEMA IF EXISTS art_ping;

--changeset aabramovich:11-01-2021-create-user
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM pg_tables WHERE schemaname = 'art_ping' AND tablename='user'
CREATE TABLE art_ping.user
(
    id       UUID PRIMARY KEY NOT NULL,
    login    CHARACTER varying(256) UNIQUE,
    password CHARACTER varying(256),
    active   BOOLEAN default false
);
--rollback DROP TABLE IF EXISTS art_ping.user;

--changeset aabramovich:11-01-2021-create-employee
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM pg_tables WHERE schemaname = 'art_ping' AND tablename='employee'
CREATE TABLE art_ping.employee
(
    id           UUID PRIMARY KEY       NOT NULL,
    ref_staff_id CHARACTER varying(256),
    first_name   CHARACTER varying(100),
    last_name    CHARACTER varying(100) NOT NULL,
    office_id    CHARACTER varying(256),
    user_id      UUID                   NOT NULL,
    is_deleted   BOOLEAN   DEFAULT FALSE,
    leave_date   DATE      DEFAULT NULL,
    updated      TIMESTAMP DEFAULT NULL,
    login        CHARACTER varying(256) NOT NULL,
    CONSTRAINT fk_employee_user_id FOREIGN KEY (user_id) REFERENCES art_ping.user (id)
);
--rollback DROP TABLE IF EXISTS art_ping.employee;

--changeset aabramovich:03-20-2021-create-office
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM pg_tables WHERE schemaname = 'art_ping' AND tablename='office'
CREATE TABLE art_ping.office
(
    id       UUID PRIMARY KEY NOT NULL,
    name     CHARACTER varying(40),
    timezone CHARACTER varying(40)
);
--rollback DROP TABLE IF EXISTS art_ping.office;

--changeset mgichevsky:23-07-2021-create-calendar
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM pg_tables WHERE schemaname = 'art_ping' AND tablename='calendar'
CREATE TABLE art_ping.calendar
(
    id            UUID PRIMARY KEY NOT NULL,
    name          CHARACTER VARYING(100),
    active        BOOLEAN,
    creation_time TIMESTAMP DEFAULT NULL
);
--rollback DROP TABLE IF EXISTS art_ping.calendar;

--changeset aabramovich:03-11-2021-create-project
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM pg_tables WHERE schemaname = 'art_ping' AND tablename='project'
CREATE TABLE art_ping.project
(
    id        UUID PRIMARY KEY       NOT NULL,
    name      CHARACTER VARYING(100) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE   NOT NULL
);
--rollback DROP TABLE IF EXISTS art_ping.project;

--changeset aabramovich:03-11-2021-create-project-employee
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM pg_tables WHERE schemaname = 'art_ping' AND tablename='project_employee'
CREATE TABLE art_ping.project_employee
(
    id          UUID PRIMARY KEY      NOT NULL,
    project_id  UUID                  NOT NULL,
    employee_id UUID                  NOT NULL,
    is_manager  BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT fk_project_employee_employee_id FOREIGN KEY (employee_id) REFERENCES art_ping.employee (id),
    CONSTRAINT fk_project_employee_project_id FOREIGN KEY (project_id) REFERENCES art_ping.project (id)
);
--rollback DROP TABLE IF EXISTS art_ping.project_employee;

--changeset aabramovich:29-03-2021-link-employee-to-user
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='user' AND column_name='employee_id';
ALTER TABLE art_ping.user
    ADD COLUMN employee_id UUID,
    ADD CONSTRAINT fk_user_employee_id FOREIGN KEY (employee_id) REFERENCES art_ping.employee (id);
--rollback ALTER TABLE art_ping.office DROP COLUMN IF EXISTS employee_id;;

--changeset ygusev:20200810-01
--preconditions onFail:MARK_RAN
--comment: Добавление расширения "uuid-ossp"
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

--changeset aabramovich:20200812-01
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM pg_tables WHERE schemaname = 'art_ping' AND tablename='role'
--comment: Справочник ролей
CREATE TABLE art_ping.role
(
    id   UUID PRIMARY KEY              NOT NULL,
    code CHARACTER varying(255) UNIQUE NOT NULL,
    name CHARACTER varying(255)        NOT NULL
);
--rollback DROP TABLE IF EXISTS art_ping.role;

--changeset ygusev:20200812-02
--comment: Наполнение справочника ролей
INSERT INTO art_ping.role
VALUES ('992e5c70-7b14-44ae-b2fc-d0c884659841', 'admin', 'Администратор');
INSERT INTO art_ping.role
VALUES ('e7051ffa-0edb-4ac7-84ed-12c1d3b2b5f8', 'director', 'Руководитель офиса');
INSERT INTO art_ping.role
VALUES ('5b204bc5-6430-4939-aa23-17b1482c6d98', 'pm', 'Руководитель проекта');
INSERT INTO art_ping.role
VALUES ('eea13552-ad24-4165-bce4-f1b22776739c', 'user', 'Сотрудник');
INSERT INTO art_ping.role
VALUES ('9f4932a6-c650-4cd2-9338-2eefa47aeb35', 'hr', 'Менеджер по подбору персонала');
--rollback DELETE FROM art_ping.role;

--changeset mgichevsky:20102021
--comment: Приведение ролей в UPPER CASE
UPDATE art_ping.role
SET code = 'ADMIN'
WHERE id = '992e5c70-7b14-44ae-b2fc-d0c884659841';
UPDATE art_ping.role
SET code = 'DIRECTOR'
WHERE id = 'e7051ffa-0edb-4ac7-84ed-12c1d3b2b5f8';
UPDATE art_ping.role
SET code = 'PM'
WHERE id = '5b204bc5-6430-4939-aa23-17b1482c6d98';
UPDATE art_ping.role
SET code = 'USER'
WHERE id = 'eea13552-ad24-4165-bce4-f1b22776739c';
UPDATE art_ping.role
SET code = 'HR'
WHERE id = '9f4932a6-c650-4cd2-9338-2eefa47aeb35';
--rollback DELETE FROM art_ping.role;

--changeset mgichevsky:24122021
--comment: Изменение русского наименования HR
UPDATE art_ping.role
SET name = 'Менеджер по персоналу'
WHERE id = '9f4932a6-c650-4cd2-9338-2eefa47aeb35';
--rollback DELETE FROM art_ping.role;

--changeset ygusev:20200818-01
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM pg_tables WHERE schemaname = 'art_ping' AND tablename='employee_role'
--comment: Связь пользователей с ролями
CREATE TABLE art_ping.employee_role
(
    id          UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    employee_id UUID             NOT NULL,
    role_id     UUID             NOT NULL,

    CONSTRAINT unique_employee_id_role_id UNIQUE (employee_id, role_id),
    CONSTRAINT fk_employee_role_employee_id FOREIGN KEY (employee_id) REFERENCES art_ping.employee (id),
    CONSTRAINT fk_employee_role_role_id FOREIGN KEY (role_id) REFERENCES art_ping.role (id)
);
--rollback DROP TABLE IF EXISTS art_ping.employee_role;

--changeset aabramovich:add-calendar_id-column-to-office
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='office' AND column_name='calendar_id';
ALTER TABLE art_ping.office
    ADD COLUMN calendar_id UUID,
    ADD CONSTRAINT fk_office_calendar_id FOREIGN KEY (calendar_id) REFERENCES art_ping.calendar (id);

--rollback ALTER TABLE art_ping.office DROP COLUMN IF EXISTS startDate;

--rollback ALTER TABLE art_ping.calendar DROP COLUMN IF EXISTS startDate;

--rollback ALTER TABLE art_ping.office DROP COLUMN IF EXISTS endDate;

--rollback ALTER TABLE art_ping.employee DROP COLUMN IF EXISTS active;
--changeset aabramovich:add-active-column-to-employee
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='employee' AND column_name='active';
ALTER TABLE art_ping.employee
    ADD COLUMN active boolean;
--rollback ALTER TABLE art_ping.employee DROP COLUMN IF EXISTS active;

--changeset aabramovich:add-email-column-to-employee
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='employee' AND column_name='email';
ALTER TABLE art_ping.employee
    ADD COLUMN email varchar(255);
--rollback ALTER TABLE art_ping.employee DROP COLUMN IF EXISTS email;

--changeset aabramovich:add-employee_id-column-to-office
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='office' AND column_name='employee_id';
ALTER TABLE art_ping.office
    ADD COLUMN employee_id UUID,
    ADD CONSTRAINT fk_office_employee_id FOREIGN KEY (employee_id) REFERENCES art_ping.employee (id);

--rollback ALTER TABLE art_ping.office DROP COLUMN IF EXISTS employee_id;

--changeset aabramovich:remove-ref-id-column
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='employee' AND column_name='ref_staff_id';
ALTER TABLE art_ping.employee
    DROP COLUMN IF EXISTS ref_staff_id;

--rollback ALTER TABLE art_ping.employee ADD COLUMN ref_staff_id varchar ref_staff_id;

--changeset aabramovich:07-14-2021-add-root-user-admin
--comment: add-root-user-admin
--password is encrypted with bcrypt 'password'
INSERT INTO art_ping.user
VALUES ('5fb4f0e1-e585-4d8d-886b-1f707d03df83', 'admin',
        '$2a$12$h8Q4lTxvmm.hFY1O7k.BnuKPNhMVmhjgmZs2oqnnjlTu65ScY/hkq', true);
--rollback DELETE FROM art_ping.user;

--changeset aabramovich:add-calendar_id-column-to-employee
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='employee' AND column_name='calendar_id';
ALTER TABLE art_ping.employee
    ADD COLUMN calendar_id UUID,
    ADD CONSTRAINT fk_employee_calendar_id FOREIGN KEY (calendar_id) REFERENCES art_ping.calendar (id);

--rollback ALTER TABLE art_ping.employee DROP COLUMN IF EXISTS calendar_id;

--changeset aabramovich:05-02-2021-create-test
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM pg_tables WHERE schemaname = 'art_ping' AND tablename='test'
CREATE TABLE art_ping.test
(
    id           UUID PRIMARY KEY NOT NULL,
    initiator_id UUID      DEFAULT NULL,
    start_date   TIMESTAMP DEFAULT NULL,

    CONSTRAINT fk_test_employee_id FOREIGN KEY (initiator_id) REFERENCES art_ping.employee (id)

);
--rollback DROP TABLE IF EXISTS art_ping.employee;

--changeset mgichevsky:30-07-2021-create-event
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM pg_tables WHERE schemaname = 'art_ping' AND tablename='event'
CREATE TABLE art_ping.event
(
    id          UUID PRIMARY KEY       NOT NULL,
    description CHARACTER VARYING(100),
    date        DATE                   NOT NULL,
    type        CHARACTER VARYING(100) NOT NULL
);
--rollback DROP TABLE IF EXISTS art_ping.event;

--changeset mgichevsky:link-event-to-calendar
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='event' AND column_name='calendar_id';
ALTER TABLE art_ping.event
    ADD COLUMN calendar_id UUID,
    ADD CONSTRAINT fk_calendar_event_id FOREIGN KEY (calendar_id) REFERENCES art_ping.calendar (id);
--rollback ALTER TABLE art_ping.event DROP COLUMN IF EXISTS calendar_id;

--changeset mgichevsky:30-07-2021-add_fields_to_calendar
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='calendar' AND column_name='start_week_day';
ALTER TABLE art_ping.calendar
    ADD COLUMN start_week_day  SMALLINT,
    ADD COLUMN weekend_days    CHARACTER VARYING(20),
    ADD COLUMN work_Hours_From TIME WITHOUT TIME ZONE,
    ADD COLUMN work_Hours_TO   TIME WITHOUT TIME ZONE;
--rollback ALTER TABLE art_ping.event DROP COLUMN IF EXISTS start_week_day;

--changeset mgichevsky:03-08-2021-drop_fields_from_employee
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='employee' AND column_name='active';
ALTER TABLE art_ping.employee
    DROP COLUMN IF EXISTS active,
    DROP COLUMN IF EXISTS is_deleted;
--rollback ALTER TABLE art_ping.employee ADD COLUMN active boolean active;

--changeset mgichevsky:03-08-2021-create_employee_subscription
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM pg_tables WHERE schemaname = 'art_ping' AND tablename='employee_subscription'
CREATE TABLE art_ping.employee_subscription
(
    id               UUID PRIMARY KEY NOT NULL,
    employee_id      UUID             NOT NULL,
    subscribe_id     CHARACTER varying(256),
    type             CHARACTER varying(20),
    user_agent       CHARACTER varying(255),
    is_valid         BOOLEAN   DEFAULT TRUE,
    creation_time    TIMESTAMP DEFAULT NULL,
    last_update_time TIMESTAMP DEFAULT NULL,
    CONSTRAINT fk_employee_subscription_employee_id FOREIGN KEY (employee_id) REFERENCES art_ping.employee (id)
);
--rollback DROP TABLE IF EXISTS art_ping.employee_subscription;

--changeset mgichevsky:04-08-2021-create_employee_test
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM pg_tables WHERE schemaname = 'art_ping' AND tablename='employee_test'
CREATE TABLE art_ping.employee_test
(
    id               UUID PRIMARY KEY      NOT NULL,
    employee_id      UUID                  NOT NULL,
    test_id          UUID                  NOT NULL,
    status           CHARACTER varying(20) NOT NULL,
    start_time       TIMESTAMP DEFAULT current_timestamp,
    response_time    TIMESTAMP DEFAULT NULL,
    device_type      CHARACTER varying(20),
    user_agent       CHARACTER varying(255),
    notification     CHARACTER varying(20) NOT NULL,
    last_update_time TIMESTAMP DEFAULT NULL,
    CONSTRAINT fk_employee_id FOREIGN KEY (employee_id) REFERENCES art_ping.employee (id),
    CONSTRAINT fk_test_id FOREIGN KEY (test_id) REFERENCES art_ping.test (id)
);
--rollback DROP TABLE IF EXISTS art_ping.employee_test;

--changeset mgichevsky:11-09-2021-delete-testId-column
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='employee_test' AND column_name='test_id';
ALTER TABLE art_ping.employee_test
    DROP CONSTRAINT fk_test_id,
    DROP COLUMN IF EXISTS test_id;

--changeset mgichevsky:16-09-2021-edit-officeid-type
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='employee' AND column_name='office_id';
ALTER TABLE art_ping.employee
    ALTER COLUMN office_id TYPE UUID USING office_id::uuid;

--changeset mgichevsky:add-custom-calendar-column-to-calendar
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='employee' AND column_name='custom_calendar';
ALTER TABLE art_ping.calendar
    ADD COLUMN custom_calendar BOOLEAN DEFAULT FALSE;

--changeset mgichevsky:21-10-2021-create_employee_office
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM pg_tables WHERE schemaname = 'art_ping' and tablename='employee_office'
CREATE TABLE art_ping.employee_office
(
    id          UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    employee_id UUID             NOT NULL,
    office_id   UUID             NOT NULL,

    CONSTRAINT unique_employee_id_office_id UNIQUE (employee_id, office_id),
    CONSTRAINT fk_employee_office_employee_id FOREIGN KEY (employee_id) REFERENCES art_ping.employee (id),
    CONSTRAINT fk_employee_office_office_id FOREIGN KEY (office_id) REFERENCES art_ping.office (id)
);
--rollback DROP TABLE IF EXISTS art_ping.employee_office

--changeset ikuleshov:11-11-2021-start_time
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='employee_test' AND column_name='start_time';
ALTER TABLE art_ping.employee_test
    ALTER COLUMN start_time TYPE timestamptz;

--changeset ikuleshov:11-11-2021-response_time
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='employee_test' AND column_name='response_time';
ALTER TABLE art_ping.employee_test
    ALTER COLUMN response_time TYPE timestamptz;

--changeset oryntsevich:12-11-2021-add-constraint-unique-name-calendar
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='calendar' AND column_name='name'
ALTER TABLE art_ping.calendar
    ADD CONSTRAINT calendar_name_unq UNIQUE (name);
--rollback ALTER TABLE art_ping.calendar DROP CONSTRAINT calendar_name_unq;

--changeset oryntsevich:12-11-2021-add-constraint-unique-name-office
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='office' AND column_name='name'
ALTER TABLE art_ping.office
    ADD CONSTRAINT office_name_unq UNIQUE (name);
--rollback ALTER TABLE art_ping.office DROP CONSTRAINT office_name_unq;

--changeset oryntsevich:12-11-2021-add-constraint-unique-name-project
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='project' AND column_name='name'
ALTER TABLE art_ping.project
    ADD CONSTRAINT project_name_unq UNIQUE (name);
--rollback ALTER TABLE art_ping.project DROP CONSTRAINT project_name_unq;

--changeset ikuleshov:24-11-2021-password-recovery
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM pg_tables WHERE schemaname = 'art_ping' AND tablename='pwd_recovery'
CREATE TABLE art_ping.pwd_recovery
(
    id          UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    created     DATE             NOT NULL DEFAULT current_date,
    active      BOOLEAN          NOT NULL DEFAULT TRUE,
    employee_id UUID             NOT NULL,
    CONSTRAINT fk_pwd_recovery_employee_id FOREIGN KEY (employee_id) REFERENCES art_ping.employee (id)
);
--rollback DROP TABLE IF EXISTS art_ping.pwd_recovery;

--changeset mgichevsky:add-managed_office_id-column-to-employee
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='employee' AND column_name='managed_office_id';
ALTER TABLE art_ping.employee
    ADD COLUMN managed_office_id UUID,
    ADD CONSTRAINT fk_employee_managed_office FOREIGN KEY (managed_office_id) REFERENCES art_ping.office (id);
--rollback ALTER TABLE art_ping.employee DROP COLUMN IF EXISTS managed_office_id;

--changeset mgichevsky:drop_employee_id_from_office
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='office' AND column_name='employee_id';
ALTER TABLE art_ping.office
    DROP CONSTRAINT fk_office_employee_id,
    DROP COLUMN IF EXISTS employee_id;
--rollback ALTER TABLE art_ping.office ADD COLUMN employee_id UUID;

--changeset mgichevsky:07-12-2021-create_employee_director
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM pg_tables WHERE schemaname = 'art_ping' and tablename='employee_director'
CREATE TABLE art_ping.employee_director
(
    id          UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    employee_id UUID             NOT NULL,
    office_id   UUID             NOT NULL,

    CONSTRAINT unique_employee_id_managed_office_id UNIQUE (employee_id, office_id),
    CONSTRAINT fk_employee_managed_office_employee_id FOREIGN KEY (employee_id) REFERENCES art_ping.employee (id),
    CONSTRAINT fk_employee_managed_office_office_id FOREIGN KEY (office_id) REFERENCES art_ping.office (id)
);
--rollback DROP TABLE IF EXISTS art_ping.employee_director

--changeset mgichevsky:drop_managed_office_id_from_office
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='employee' AND column_name='managed_office_id';
ALTER TABLE art_ping.employee
    DROP CONSTRAINT fk_employee_managed_office,
    DROP COLUMN IF EXISTS managed_office_id;
--rollback ALTER TABLE art_ping.employee ADD COLUMN managed_office_id UUID;

--changeset mgichevsky:21-12-2021-add_sorting_columns_to_employee
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='employee' AND column_name='asc_project_name';
ALTER TABLE art_ping.employee
    ADD COLUMN asc_project_name  CHARACTER VARYING(100),
    ADD COLUMN desc_project_name CHARACTER VARYING(100);
--rollback ALTER TABLE art_ping.employee DROP COLUMN IF EXISTS asc_project_name, DROP COLUMN IF EXISTS desc_project_name;

--changeset mgichevsky:23-12-2021-add_sorting_role_columns_to_employee
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='employee' AND column_name='asc_role_name';
ALTER TABLE art_ping.employee
    ADD COLUMN asc_role_name  CHARACTER VARYING(255),
    ADD COLUMN desc_role_name CHARACTER VARYING(255);
--rollback ALTER TABLE art_ping.employee DROP COLUMN IF EXISTS asc_role_name, DROP COLUMN IF EXISTS desc_role_name;

--changeset mgichevsky:31-01-2022-add-notification-table
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM pg_tables WHERE schemaname = 'art_ping' AND tablename='notification'
CREATE TABLE art_ping.notification
(
    id          UUID PRIMARY KEY NOT NULL DEFAULT uuid_generate_v4(),
    text        CHARACTER varying(500),
    is_deleted  BOOLEAN                   DEFAULT FALSE,
    viewed      BOOLEAN                   DEFAULT FALSE,
    employee_id UUID             NOT NULL,
    type        CHARACTER varying(20),
    CONSTRAINT fk_notification_employee_id FOREIGN KEY (employee_id) REFERENCES art_ping.employee (id)
);
--rollback DROP TABLE IF EXISTS art_ping.notification;

--changeset mgichevsky:01-02-2022-add_creation_time_column_to_notification
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='notification' AND column_name='creation_time';
ALTER TABLE art_ping.notification
    ADD COLUMN creation_time TIMESTAMP DEFAULT current_timestamp NOT NULL;
--rollback ALTER TABLE art_ping.notification DROP COLUMN IF EXISTS creation_time;

--changeset mgichevsky:17-03-2022-add-point-table
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM pg_tables WHERE schemaname = 'art_ping' AND tablename='point'
CREATE TABLE art_ping.point
(
    id         UUID PRIMARY KEY      NOT NULL DEFAULT uuid_generate_v4(),
    latitude   CHARACTER VARYING(25) NOT NULL,
    longitude  CHARACTER VARYING(25) NOT NULL,
    accuracy   CHARACTER VARYING(50),
    is_valid   BOOLEAN                        DEFAULT FALSE,
    is_deleted BOOLEAN                        DEFAULT FALSE
);
--rollback DROP TABLE IF EXISTS art_ping.point;

--changeset mgichevsky:17-03-2022-add-point_id-column-to-employee-test
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='employee_test' AND column_name='point_id';
ALTER TABLE art_ping.employee_test
    ADD COLUMN point_id UUID,
    ADD CONSTRAINT fk_employee_test_point FOREIGN KEY (point_id) REFERENCES art_ping.point (id);
--rollback ALTER TABLE art_ping.employee_test DROP COLUMN IF EXISTS point_id;
