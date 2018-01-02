-- Insert IWADS into the pwad table; counterintuitive, but makes sense in the context of a consistent
-- way to look up what runs are for what levels

INSERT INTO pwad (file_name, idgames_url, friendly_name) VALUES
  ('doom2', 'iwad', 'Doom 2'),
  ('doom', 'iwad', 'Doom'),
  ('heretic', 'iwad', 'Heretic'),
  ('plutonia', 'iwad', 'The Plutonia Experiment'),
  ('tnt', 'iwad', 'TNT: Evilution'); 
