    -- Script para insertar ciclos académicos
    -- Generado: 2025-06-07 12:17:59
    -- Usuario: icarius4iu

    -- Limpiar datos existentes (descomenta si es necesario)
    -- DELETE FROM cicles;
    -- ALTER TABLE cicles AUTO_INCREMENT = 1;

    -- Ciclos de Verano
    INSERT INTO cicles (year, type, period, start_date, end_date, name)
    VALUES
        ('2025', 'VERANO', NULL, '2025-01-06', '2025-03-17', 'VERANO_2025'),
        ('2026', 'VERANO', NULL, '2026-01-05', '2026-03-16', 'VERANO_2026');

    -- Ciclos Regulares
    INSERT INTO cicles (year, type, period, start_date, end_date, name)
    VALUES
    -- 2025
    ('2025', 'REGULAR', 'REGULAR_1', '2025-03-24', '2025-07-28', 'REGULAR_2025-I'),
    ('2025', 'REGULAR', 'REGULAR_2', '2025-08-11', '2025-12-15', 'REGULAR_2025-II'),
    -- 2026
    ('2026', 'REGULAR', 'REGULAR_1', '2026-03-23', '2026-07-27', 'REGULAR_2026-I'),
    ('2026', 'REGULAR', 'REGULAR_2', '2026-08-10', '2026-12-14', 'REGULAR_2026-II');