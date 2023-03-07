DROP TABLE IF EXISTS hits CASCADE;

CREATE TABLE IF NOT EXISTS hits (
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    app       VARCHAR(64)                                     NOT NULL,
    uri       VARCHAR(64)                                     NOT NULL,
    ip        VARCHAR(64)                                     NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE                        NOT NULL
);