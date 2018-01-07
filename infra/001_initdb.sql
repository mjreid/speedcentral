CREATE TABLE pwad (
  id BIGSERIAL PRIMARY KEY,
  file_name VARCHAR(80) NOT NULL,
  idgames_url TEXT NOT NULL,
  friendly_name TEXT NULL
);

CREATE TABLE run (
  id BIGSERIAL PRIMARY KEY,
  map VARCHAR(10) NOT NULL,
  episode VARCHAR(10) NOT NULL,
  skill_level VARCHAR(10) NOT NULL,
  iwad VARCHAR(80) NOT NULL,
  primary_pwad_id BIGINT REFERENCES pwad (id) NOT NULL,
  engine_version VARCHAR(1024) NOT NULL,
  runner VARCHAR(1024) NULL,
  submitter VARCHAR(1024) NULL,
  run_category VARCHAR(1024) NULL,
  run_time BIGINT NULL,
  created_date TIMESTAMP NOT NULL,
  modified_date TIMESTAMP NOT NULL
);

CREATE TABLE recording (
  id BIGSERIAL PRIMARY KEY,
  run_id BIGINT REFERENCES run (id) NOT NULL,
  video_id VARCHAR(64) NULL,
  created_date TIMESTAMP NOT NULL
);

CREATE TABLE recording_history (
  id BIGSERIAL PRIMARY KEY,
  recording_id BIGINT REFERENCES recording (id) NOT NULL,
  state VARCHAR(64) NOT NULL,
  history_time TIMESTAMP NOT NULL,
  details TEXT NULL
);

CREATE TABLE run_secondary_pwads (
  id BIGSERIAL PRIMARY KEY,
  run_id BIGINT REFERENCES run (id) NOT NULL,
  pwad_id BIGINT REFERENCES pwad (id) NOT NULL
);

CREATE INDEX ix_pwad_file_name ON pwad (file_name);
CREATE INDEX ix_run_secondary_pwads_run_id ON run_secondary_pwads(run_id);
CREATE INDEX ix_recording_history_recording_id ON recording_history (recording_id);
