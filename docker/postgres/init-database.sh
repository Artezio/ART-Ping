#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE USER art;
    CREATE DATABASE art_ping_db;
    GRANT ALL PRIVILEGES ON DATABASE art_ping_db TO art;
EOSQL