CREATE TABLE survey
(
    id            VARCHAR(255) NOT NULL,
    event_id      VARCHAR(255) NOT NULL,
    title         VARCHAR(200) NOT NULL,
    `description` VARCHAR(4000) NULL,
    status        VARCHAR(20)  NOT NULL,
    response_type VARCHAR(30)  NOT NULL,
    opens_at      datetime NULL,
    closes_at     datetime NULL,
    created_by    VARCHAR(255)    NOT NULL,
    created_at    datetime     NOT NULL,
    updated_at    datetime     NOT NULL,
    CONSTRAINT pk_survey PRIMARY KEY (id)
);

CREATE TABLE survey_response
(
    id                  VARCHAR(255) NOT NULL,
    survey_id           VARCHAR(255) NOT NULL,
    user_iam_id         VARCHAR(255) NOT NULL,
    answer_yes_no_maybe VARCHAR(10)  NOT NULL,
    comment             VARCHAR(1000) NULL,
    answered_at         datetime     NOT NULL,
    CONSTRAINT pk_survey_response PRIMARY KEY (id)
);

ALTER TABLE survey_response
    ADD CONSTRAINT uk_response_survey_user UNIQUE (survey_id, user_iam_id);

CREATE INDEX idx_response_survey ON survey_response (survey_id);

CREATE INDEX idx_response_user ON survey_response (user_iam_id);

CREATE INDEX idx_survey_event ON survey (event_id);

CREATE INDEX idx_survey_status ON survey (status);

CREATE INDEX idx_survey_window ON survey (opens_at, closes_at);