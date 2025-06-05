-- /test-data/cicles.sql
-- Insertar ciclos académicos para pruebas
INSERT INTO cicles (id, year, type, period, start_date, end_date, name) VALUES
                                                                            (1, '2025', 'REGULAR', 'REGULAR_1', '2025-03-15', '2025-07-15', 'REGULAR_2025-I'),
                                                                            (2, '2025', 'REGULAR', 'REGULAR_2', '2025-08-15', '2025-12-15', 'REGULAR_2025-II'),
                                                                            (3, '2025', 'VERANO', NULL, '2025-01-05', '2025-02-28', 'VERANO_2025');