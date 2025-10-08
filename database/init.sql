-- public.borme_constitucion_empresa definition

-- Drop table if exists
-- DROP TABLE IF EXISTS public.borme_constitucion_empresa;

CREATE TABLE IF NOT EXISTS public.borme_constitucion_empresa (
    fecha_constitucion date NOT NULL,
    numero_asiento varchar(100) NOT NULL,
    capital text NULL,
    domicilio text NULL,
    fecha_pdf date NULL,
    nombre_archivo_pdf varchar(255) NULL,
    nombre_empresa text NULL,
    objeto_social text NULL,
    CONSTRAINT borme_constitucion_empresa_pkey PRIMARY KEY (fecha_constitucion, numero_asiento)
);

-- Crear índices para mejor performance (si no existen)
CREATE INDEX IF NOT EXISTS idx_borme_fecha_constitucion ON public.borme_constitucion_empresa(fecha_constitucion);
CREATE INDEX IF NOT EXISTS idx_borme_nombre_empresa ON public.borme_constitucion_empresa(nombre_empresa);
CREATE INDEX IF NOT EXISTS idx_borme_fecha_pdf ON public.borme_constitucion_empresa(fecha_pdf);

-- Asegurar que el usuario tiene permisos
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO borme_user;
