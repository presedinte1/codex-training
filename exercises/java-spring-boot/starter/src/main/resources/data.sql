INSERT INTO tasks (title, description, status, priority, due_date, created_at, updated_at)
VALUES
  ('Set up project structure', 'Create the core packages and dependencies for the task API', 'TODO', 'HIGH', DATE '2026-05-01', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Implement repository layer', 'Add Spring Data JPA repository methods for search and filtering', 'IN_PROGRESS', 'MEDIUM', DATE '2026-05-10', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Write controller tests', 'Add integration tests for the REST API endpoints', 'TODO', 'LOW', DATE '2026-05-15', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('Prepare workshop demo', 'Validate the starter app and seed data before delivery', 'DONE', 'MEDIUM', DATE '2026-05-20', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
