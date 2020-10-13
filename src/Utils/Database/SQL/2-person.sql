-- Table: public.person

-- DROP TABLE public.person;

CREATE TABLE public.person
(
    person_id integer NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    person_name character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    height integer,
    eye_color color NOT NULL,
    hair_color color NOT NULL,
    nationality country NOT NULL,
    CONSTRAINT person_pkey PRIMARY KEY (person_id),
    CONSTRAINT person_person_name_check CHECK (person_name::text > ''::text),
    CONSTRAINT person_height_check CHECK (height > 0)
)

TABLESPACE pg_default;

ALTER TABLE public.person
    OWNER to postgres;