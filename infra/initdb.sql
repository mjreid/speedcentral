CREATE TABLE run (
  id BIGSERIAL PRIMARY KEY,
  submitter VARCHAR(1024) NOT NULL
);

CREATE TABLE recording (
  id BIGSERIAL PRIMARY KEY,
  run_id BIGINT REFERENCES run (id) NOT NULL
);

CREATE TABLE recording_history (
  id BIGSERIAL PRIMARY KEY,
  recording_id BIGINT REFERENCES recording (id) NOT NULL
);

CREATE TABLE recording_history_state (
  id BIGSERIAL PRIMARY KEY,
  state VARCHAR(64) NOT NULL,
  description VARCHAR(512) NOT NULL
);

INSERT INTO recording_history_state (state, description) VALUES
  ('submitted', 'Run is submitted, but has not yet started processing.'),
  ('encoding', 'Run is being encoded.'),
  ('uploading', 'Run is being uploaded.'),
  ('complete', 'Run has finished uploading.'),
  ('failed', 'Run failed, check recording history for details.');
