-- Local data for testing

INSERT INTO run (
  map,
  skill_level,
  iwad,
  primary_pwad_id,
  episode,
  engine_version,
  runner,
  submitter,
  run_category,
  run_time,
  created_date,
  modified_date
) VALUES (
  2,
  3,
  'doom2',
  1,
  1,
  'prboom-plus',
  'prower-runner',
  'prower-submitter',
  'UV-Max',
  'PT10M23S',
  now(),
  now()
);
