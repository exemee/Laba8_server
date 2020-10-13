-- Type: country

-- DROP TYPE public.country;

CREATE TYPE public.country AS ENUM
   ('USA', 'CHINA', 'SOUTH_KOREA');

ALTER TYPE public.country
    OWNER TO postgres;