        -- Script para insertar carreras universitarias
        -- Generado: 2025-06-07 12:17:59
        -- Usuario: icarius4iu

        -- Limpiar datos existentes (descomenta si es necesario)
        -- DELETE FROM careers;
        -- ALTER TABLE careers AUTO_INCREMENT = 1;

        -- Carreras de Ingeniería
        INSERT INTO careers (code, name, description, faculty, duration_semesters)
        VALUES
            ('INFSIST', 'Ingeniería de Sistemas', 'Carrera enfocada en el desarrollo de sistemas informáticos, infraestructura tecnológica y gestión de proyectos de TI.', 'Facultad de Ingeniería', 10),
            ('INFSOFT', 'Ingeniería de Software', 'Especialización en el desarrollo y arquitectura de aplicaciones, gestión de calidad y metodologías ágiles.', 'Facultad de Ingeniería', 10),
            ('INGCIVIL', 'Ingeniería Civil', 'Formación en diseño, construcción y mantenimiento de infraestructuras como edificios, carreteras, puentes y sistemas de agua.', 'Facultad de Ingeniería', 10),
            ('INGELEC', 'Ingeniería Eléctrica', 'Estudios sobre sistemas eléctricos, redes de distribución, automatización y energías renovables.', 'Facultad de Ingeniería', 10),
            ('INGIND', 'Ingeniería Industrial', 'Optimización de procesos productivos, logística, control de calidad y gestión de operaciones.', 'Facultad de Ingeniería', 10);

        -- Carreras de Ciencias Empresariales
        INSERT INTO careers (code, name, description, faculty, duration_semesters)
        VALUES
            ('ADMINEG', 'Administración de Negocios', 'Formación en gestión empresarial, marketing, finanzas, recursos humanos y emprendimiento.', 'Facultad de Ciencias Empresariales', 10),
            ('CONTFIN', 'Contabilidad y Finanzas', 'Especialización en análisis financiero, auditoría, tributación y normativa contable.', 'Facultad de Ciencias Empresariales', 10),
            ('MARKET', 'Marketing y Publicidad', 'Estudios sobre comportamiento del consumidor, estrategias de marketing digital, branding y comunicación.', 'Facultad de Ciencias Empresariales', 10),
            ('NEGOSIN', 'Negocios Internacionales', 'Formación en comercio exterior, logística internacional, aduanas y relaciones internacionales.', 'Facultad de Ciencias Empresariales', 10);

        -- Carreras de Humanidades
        INSERT INTO careers (code, name, description, faculty, duration_semesters)
        VALUES
            ('PSICOL', 'Psicología', 'Estudio del comportamiento humano, procesos mentales, desarrollo personal y terapias psicológicas.', 'Facultad de Humanidades', 10),
            ('DERECHO', 'Derecho', 'Formación en ciencias jurídicas, derecho civil, penal, laboral y constitucional.', 'Facultad de Humanidades', 12),
            ('COMUNIC', 'Ciencias de la Comunicación', 'Especialización en periodismo, comunicación digital, producción audiovisual y relaciones públicas.', 'Facultad de Humanidades', 10);

        -- Carreras de Ciencias de la Salud
        INSERT INTO careers (code, name, description, faculty, duration_semesters)
        VALUES
            ('ENFERM', 'Enfermería', 'Formación en cuidados de la salud, atención al paciente, procedimientos clínicos y salud pública.', 'Facultad de Ciencias de la Salud', 10),
            ('NUTRIC', 'Nutrición y Dietética', 'Estudios sobre alimentación saludable, evaluación nutricional, dietoterapia y nutrición deportiva.', 'Facultad de Ciencias de la Salud', 10);

        -- Carreras de Ciencias Básicas
        INSERT INTO careers (code, name, description, faculty, duration_semesters)
        VALUES
            ('BIOAMB', 'Biología Ambiental', 'Estudio de ecosistemas, conservación, impacto ambiental y gestión de recursos naturales.', 'Facultad de Ciencias', 10),
            ('QUIMICA', 'Química', 'Formación en química orgánica e inorgánica, análisis químico, bioquímica y procesos industriales.', 'Facultad de Ciencias', 10);