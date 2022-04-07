--liquibase formatted sql

--changeset ikuzmin:20200630-01
CREATE TABLE IF NOT EXISTS art_ping.system_settings
  (
     setting_key   CHARACTER varying(100) PRIMARY KEY NOT NULL,
     setting_value CHARACTER varying(50) NOT NULL
  );
--rollback DROP TABLE IF EXISTS art_ping.system_settings;

--changeset ikuzmin:20200630-02
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM art_ping.system_settings WHERE setting_key='TEST_SUCCESSFUL_MINUTES'
INSERT INTO art_ping.system_settings VALUES ('TEST_SUCCESSFUL_MINUTES', 10);
--rollback DELETE FROM art_ping.system_settings WHERE setting_key='TEST_SUCCESSFUL_MINUTES';

--changeset ikuzmin:20200630-03
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM art_ping.system_settings WHERE setting_key='TEST_NO_RESPONSE_MINUTES'
INSERT INTO art_ping.system_settings VALUES ('TEST_NO_RESPONSE_MINUTES', 60);
--rollback DELETE FROM art_ping.system_settings WHERE setting_key='TEST_NO_RESPONSE_MINUTES';

--changeset ikuzmin:20200630-04
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM art_ping.system_settings WHERE setting_key='RECOMMENDED_DAILY_CHECKS'
INSERT INTO art_ping.system_settings VALUES ('RECOMMENDED_DAILY_CHECKS', 2);
--rollback DELETE FROM art_ping.system_settings WHERE setting_key='RECOMMENDED_DAILY_CHECKS';

--changeset mgichevsky:09-08-2021-add-token-validity
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM art_ping.system_settings WHERE setting_key='JWT_TOKEN_VALIDITY'
INSERT INTO art_ping.system_settings VALUES ('JWT_TOKEN_VALIDITY', 86400);
--rollback DELETE FROM art_ping.system_settings WHERE setting_key='JWT_TOKEN_VALIDITY';

--changeset oryntsevich:10-19-2021-add-default-page-size
--preconditions onFail:MARK_RAN
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM art_ping.system_settings WHERE setting_key='DEFAULT_PAGE_SIZE'
INSERT INTO art_ping.system_settings VALUES ('DEFAULT_PAGE_SIZE', 20);
--rollback DELETE FROM art_ping.system_settings WHERE setting_key='DEFAULT_PAGE_SIZE';

--changeset ikuzmin:20200819-01
-- CREATE OR REPLACE VIEW art_ping.pm_info_view AS
-- SELECT pe.id,
--        pe.project_id,
--        pm.employee_id AS pm_id,
--        e.office_id
-- FROM   art_ping.project_employee pe
--        JOIN art_ping.project p
--          ON p.id = pe.project_id
--             AND p.is_active IS TRUE
--        JOIN art_ping.employee e
--          ON e.id = pe.employee_id
--        JOIN art_ping.project_employee pm
--          ON pe.project_id = pm.project_id
--             AND pm.is_manager IS TRUE;
--rollback DROP VIEW IF EXISTS art_ping.pm_info_view;