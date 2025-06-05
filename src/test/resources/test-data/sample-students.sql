-- Insertar información de estudiantes
INSERT INTO student_information (id, first_name, last_name, mother_last_name, document_type, document_number, personal_email)
VALUES
    (1, 'Juan', 'Pérez', 'García', 'DNI', '72458963', 'juan@example.com'),
    (2, 'María', 'López', 'Sánchez', 'DNI', '71236548', 'maria@example.com'),
    (3, 'Pedro', 'González', 'Martínez', 'PASSPORT', 'AB123456', 'pedro@example.com');

-- Insertar estudiantes
INSERT INTO students (id, student_code, status, career_id, faculty, modality, information_id)
VALUES
    (1, 'U20190123', 'ACTIVO', 1, 'Ingeniería', 'PRESENCIAL', 1),
    (2, 'U20200456', 'ACTIVO', 2, 'Ciencias', 'VIRTUAL', 2),
    (3, 'U20180789', 'EGRESADO', 3, 'Medicina', 'PRESENCIAL', 3);