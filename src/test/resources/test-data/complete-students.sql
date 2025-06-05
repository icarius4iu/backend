-- Insertar información completa de estudiantes
INSERT INTO student_information (id, first_name, last_name, mother_last_name, document_type, document_number,
                                 birth_date, civil_status, address, district, province, department,
                                 emergency_contact_name, emergency_contact_relationship, emergency_contact_phone, emergency_contact_address,
                                 landline_phone, mobile_phone, personal_email)
VALUES
    (1, 'Juan', 'Pérez', 'García', 'DNI', '12345678', '1995-05-10', 'SOLTERO',
     'Av. Los Pinos 123', 'San Borja', 'Lima', 'Lima',
     'María Pérez', 'Madre', '987654321', 'Av. Los Pinos 123, Lima',
     '01-3456789', '987654321', 'juan.perez@example.com'),

    (2, 'María', 'López', 'Sánchez', 'DNI', '87654321', '1994-07-15', 'CASADO',
     'Jr. Las Flores 456', 'Miraflores', 'Lima', 'Lima',
     'Carlos López', 'Hermano', '912345678', 'Jr. Las Palmas 789, Lima',
     '01-2345678', '912345678', 'maria.lopez@example.com'),

    (3, 'Carlos', 'García', 'Martínez', 'PASSPORT', 'AB123456', '1992-12-20', 'SOLTERO',
     'Calle Los Olivos 789', 'San Isidro', 'Lima', 'Lima',
     'Pedro García', 'Padre', '956781234', 'Calle Los Olivos 789, Lima',
     '01-4567890', '956781234', 'carlos.garcia@example.com');

-- Insertar estudiantes completos
INSERT INTO students (id, student_code, status, career_id, faculty, modality, campus,
                      enrollment_date, last_registration_date, last_enrollment_date, information_id)
VALUES
    (1, 'U20190123', 'ACTIVO', 1, 'Ingeniería', 'PRESENCIAL', 'Campus Principal',
     '2019-03-01', '2025-02-10', '2025-03-01', 1),

    (2, 'U20200456', 'ACTIVO', 2, 'Medicina', 'PRESENCIAL', 'Campus Este',
     '2020-03-01', '2025-02-15', '2025-03-05', 2),

    (3, 'U20180789', 'EGRESADO', 3, 'Derecho', 'SEMIPRESENCIAL', 'Campus Centro',
     '2018-03-01', '2023-02-20', '2023-03-10', 3);

-- Insertar perfiles de estudiantes
INSERT INTO student_profiles (id, student_id, photo_url, student_code, full_name, status,
                              faculty, modality, document_type, document_number, mobile_phone, personal_email, address, last_updated)
VALUES
    (1, 1, 'https://example.com/photos/juan.jpg', 'U20190123', 'Juan Pérez García', 'Activo',
     'Ingeniería', 'Presencial', 'DNI', '12345678', '987654321', 'juan.perez@example.com',
     'Av. Los Pinos 123, San Borja, Lima, Lima', '2025-06-01 10:30:00'),

    (2, 2, 'https://example.com/photos/maria.jpg', 'U20200456', 'María López Sánchez', 'Activo',
     'Medicina', 'Presencial', 'DNI', '87654321', '912345678', 'maria.lopez@example.com',
     'Jr. Las Flores 456, Miraflores, Lima, Lima', '2025-06-02 11:45:00'),

    (3, 3, 'https://example.com/photos/carlos.jpg', 'U20180789', 'Carlos García Martínez', 'Egresado',
     'Derecho', 'Semipresencial', 'PASSPORT', 'AB123456', '956781234', 'carlos.garcia@example.com',
     'Calle Los Olivos 789, San Isidro, Lima, Lima', '2025-06-03 09:15:00');