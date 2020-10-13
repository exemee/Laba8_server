-- Type: semester

-- DROP TYPE public.semester;

CREATE TYPE public.semester AS ENUM
   ('THIRD', 'FOURTH', 'FIFTH');

ALTER TYPE public.semester
    OWNER TO postgres;