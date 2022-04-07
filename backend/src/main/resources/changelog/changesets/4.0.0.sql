--liquibase formatted sql

--changeset ygusev:20200818-02
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM pg_tables WHERE schemaname = 'art_ping' AND tablename='director_office'
--comment: Связь директоров с офисами
CREATE TABLE art_ping.director_office (

    id              UUID PRIMARY KEY NOT NULL DEFAULT art_ping.uuid_generate_v4(),
    employee_id     UUID NOT NULL,
    office_id       UUID NOT NULL,

    CONSTRAINT unique_director_office_employee_id_office_id UNIQUE (employee_id, office_id),
    CONSTRAINT fk_director_office_employee_id FOREIGN KEY (employee_id) REFERENCES art_ping.employee(id),
    CONSTRAINT fk_director_office_office_id FOREIGN KEY (office_id) REFERENCES art_ping.office(id)
);
--rollback DROP TABLE IF EXISTS art_ping.director_office;

--changeset ygusev:20200818-04
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='employee' AND column_name='office';
--comment: Колонка для связи стотрудника с его офисом по ID
ALTER TABLE art_ping.employee ADD COLUMN office UUID,
ADD CONSTRAINT fk_employee_office_ref FOREIGN KEY (office) REFERENCES art_ping.office (id);
--rollback ALTER TABLE art_ping.employee DROP COLUMN IF EXISTS office;

--changeset ygusev:20200818-05
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='employee' AND column_name='email';
--comment: e-mail сотрудника
ALTER TABLE art_ping.employee ADD COLUMN email CHARACTER varying(255);
--rollback ALTER TABLE art_ping.employee DROP COLUMN IF EXISTS email;

--changeset ygusev:20200821-01
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:1 SELECT COUNT(1) FROM information_schema.columns WHERE table_schema ='art_ping' AND table_name='employee' AND column_name='login';
--comment: Уникальность логина
ALTER TABLE art_ping.employee ADD CONSTRAINT unique_employee_login UNIQUE (login);