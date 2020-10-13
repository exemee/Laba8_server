-- Type: formOfEducation

-- DROP TYPE public.formOfEducation;

CREATE TYPE public.formOfEducation AS ENUM
    ('DISTANCE_EDUCATION', 'FULL_TIME_EDUCATION', 'EVENING_CLASSES');

ALTER TYPE public.formOfEducation
    OWNER TO postgres;