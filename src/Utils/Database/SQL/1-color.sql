-- Type: color

-- DROP TYPE public.color;

CREATE TYPE public.color AS ENUM
    ('GREEN', 'RED', 'BLACK', 'BLUE', 'BROWN');

ALTER TYPE public.color
    OWNER TO postgres;