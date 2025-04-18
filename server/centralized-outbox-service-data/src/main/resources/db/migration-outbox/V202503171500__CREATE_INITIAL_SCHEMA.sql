CREATE SCHEMA IF NOT EXISTS central_outbox;

-- outbox_event
CREATE TABLE IF NOT EXISTS central_outbox.outbox_event
(
    id               UUID                                  NOT NULL,
    event_type_id    UUID                                  NOT NULL,
    event_status     VARCHAR(20)                           NOT NULL,


    -- external fields
    source_user_id   VARCHAR(100)                DEFAULT NULL,
    source_event_id  VARCHAR(50)                           NOT NULL,
    source_payload   VARCHAR(2000)                         NOT NULL,


    -- processing fields
    process_group_id UUID                        DEFAULT NULL,
    processed_on     timestamp without time zone default null,
    processed_error  TEXT,

    -- auditing fields
    row_version      integer                     DEFAULT 0 NOT NULL,
    row_created_by   varchar(255)                          NOT NULL,
    row_created_on   timestamp without time zone default (now() at time zone 'UTC'),
    row_updated_by   varchar(255)                DEFAULT NULL,
    row_updated_on   timestamp without time zone default (now() at time zone 'UTC'),

    CONSTRAINT pk_outbox_event PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_outbox_event_type_id ON central_outbox.outbox_event (source_event_id) include (event_type_id,event_status,process_group_id);


-- outbox_event_type
CREATE TABLE IF NOT EXISTS central_outbox.outbox_event_type
(
    id                          UUID         NOT NULL,
    event_type                  VARCHAR(150) NOT NULL,
    active                      boolean                     DEFAULT true,
    description                 VARCHAR(150) NOT NULL,
    queue_name                  VARCHAR(150) NOT NULL,
    scheduled_cron              VARCHAR(20)  NOT NULL,
    scheduled_lock_at_most_for  VARCHAR(20)  NOT NULL       DEFAULT 'PT5M', -- Maximum lock duration
    scheduled_lock_at_least_for VARCHAR(20)  NOT NULL       DEFAULT 'PT1M', -- Minimum lock duration
    -- auditing fields
    row_version                 integer                     DEFAULT 0 NOT NULL,
    row_created_by              varchar(255) NOT NULL,
    row_created_on              timestamp without time zone default (now() at time zone 'UTC'),
    row_updated_by              varchar(255)                DEFAULT NULL,
    row_updated_on              timestamp without time zone default (now() at time zone 'UTC'),

    CONSTRAINT pk_outbox_event_type PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_event_type_id ON central_outbox.outbox_event_type (event_type) include (active);
CREATE UNIQUE INDEX IF NOT EXISTS idx_event_type_constraint ON central_outbox.outbox_event_type (event_type);


ALTER TABLE central_outbox.outbox_event
    ADD CONSTRAINT fk_outbox_event_type_outbox_event_type FOREIGN KEY (event_type_id)
        REFERENCES central_outbox.outbox_event_type (id);



/*
 Create domain event table
 */

CREATE TABLE IF NOT EXISTS central_outbox.outbox_domain_event
(
    id             UUID                NOT NULL,
    event_type     VARCHAR(255)        NOT NULL,
    aggregate_id   VARCHAR(255)        NOT NULL,
    aggregate_type VARCHAR(255)        NOT NULL,
    event_data     JSONB               NOT NULL,
    happened_on    timestamp DEFAULT now(),

    -- auditing fields
    row_version    integer      DEFAULT 0 NOT NULL,
    row_created_by varchar(255) NOT NULL,
    row_created_on timestamp without time zone default (now() at time zone 'UTC'),
    row_updated_by varchar(255) DEFAULT NULL,
    row_updated_on timestamp without time zone default (now() at time zone 'UTC'),

    CONSTRAINT pk_outbox_domain_event PRIMARY KEY (id)
);

/*
 Create shedlock table
 */
CREATE TABLE central_outbox.shedlock(name VARCHAR(64) NOT NULL, lock_until TIMESTAMP NOT NULL,
                                    locked_at TIMESTAMP NOT NULL, locked_by VARCHAR(255) NOT NULL, PRIMARY KEY (name));


