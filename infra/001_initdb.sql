CREATE TABLE run (
  id BIGSERIAL PRIMARY KEY,
  map INT NOT NULL,
  episode INT NOT NULL,
  skill_level INT NOT NULL,
  iwad VARCHAR(80) NOT NULL,
  engine_version VARCHAR(1024) NOT NULL,
  runner VARCHAR(1024) NULL,
  submitter VARCHAR(1024) NULL,
  run_category VARCHAR(1024) NULL,
  run_time INTERVAL NULL,
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
  history_time TIMESTAMP NOT NULL
);

CREATE INDEX ix_recording_history_recording_id ON recording_history (recording_id);
