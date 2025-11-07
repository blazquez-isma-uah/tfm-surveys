-- Add version column for optimistic locking to survey table
ALTER TABLE survey
    ADD COLUMN version INT NOT NULL DEFAULT 0;

-- Add version column for optimistic locking to survey_response table
ALTER TABLE survey_response
    ADD COLUMN version INT NOT NULL DEFAULT 0;
