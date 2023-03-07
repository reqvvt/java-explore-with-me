DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS categories CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS compilations CASCADE;
DROP TABLE IF EXISTS events_compilations CASCADE;
DROP TABLE IF EXISTS requests CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    name  VARCHAR(256)                                    NOT NULL,
    email VARCHAR(256)                                    NOT NULL,
    CONSTRAINT uniq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS categories (
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    name VARCHAR(256)                                    NOT NULL,
    CONSTRAINT uniq_category_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS events (
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    annotation         TEXT,
    category_id        BIGINT REFERENCES categories (id),
    confirmed_requests INTEGER                                         NOT NULL,
    created_on         TIMESTAMP WITHOUT TIME ZONE                     NOT NULL,
    description        TEXT                                            NOT NULL,
    event_date         TIMESTAMP WITHOUT TIME ZONE                     NOT NULL,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    initiator_id       BIGINT REFERENCES users (id) ON DELETE CASCADE,
    location_lat       FLOAT                                           NOT NULL,
    location_lon       FLOAT                                           NOT NULL,
    paid               BOOLEAN                                         NOT NULL,
    participant_limit  INTEGER                                         NOT NULL,
    request_moderation BOOLEAN                                         NOT NULL,
    state              VARCHAR(64)                                     NOT NULL,
    title              VARCHAR(256)                                    NOT NULL,
    views              BIGINT
);

CREATE TABLE IF NOT EXISTS compilations (
    id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    pinned BOOLEAN                                         NOT NULL,
    title  VARCHAR(128)                                    NOT NULL
);

CREATE TABLE IF NOT EXISTS events_compilations (
    event_id       BIGINT REFERENCES events (id),
    compilation_id BIGINT REFERENCES compilations (id),
    CONSTRAINT pk_events_compilations PRIMARY KEY (event_id, compilation_id)
);

CREATE TABLE IF NOT EXISTS requests (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE                     NOT NULL,
    event_id     BIGINT REFERENCES events (id),
    requester_id BIGINT REFERENCES users (id),
    status       VARCHAR(32)                                     NOT NULL
);