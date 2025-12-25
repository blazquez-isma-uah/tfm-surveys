-- Add instrument_id column to survey_response table for YES_NO_MAYBE_WITH_INSTRUMENT surveys
ALTER TABLE survey_response
    ADD COLUMN instrument_id VARCHAR(100) NULL;

