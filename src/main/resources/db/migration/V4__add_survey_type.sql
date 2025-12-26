-- Add survey_type column to survey table
ALTER TABLE survey ADD COLUMN survey_type VARCHAR(20) NOT NULL DEFAULT 'OTHER';

-- Create index for survey_type to optimize queries
CREATE INDEX idx_survey_type ON survey (survey_type);

-- Create composite index for event_id and survey_type to optimize uniqueness checks
CREATE INDEX idx_survey_event_type ON survey (event_id, survey_type);

