-- Table: public.studyGroup

-- DROP TABLE public.studyGroup;

CREATE TABLE public.studyGroup
(
    studyGroup_id integer NOT NULL GENERATED BY DEFAULT AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 2147483647 CACHE 1 ),
    studyGroup_name character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    coordinatex integer NOT NULL,
    coordinatey real NOT NULL,
    creationdate timestamp without time zone NOT NULL,
    studentsCount integer NOT NULL,
    formOfEducation formOfEducation,
    semester semester NOT NULL,
    person_id bigint NOT NULL,
    user_id integer NOT NULL,
    CONSTRAINT studyGroup_pkey PRIMARY KEY (studyGroup_id),
    CONSTRAINT person_id_pkey FOREIGN KEY (person_id)
        REFERENCES public.person (person_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE,
    CONSTRAINT user_id_fkey FOREIGN KEY (user_id)
        REFERENCES public."user" (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT studyGroup_studyGroup_name_check CHECK (studyGroup_name::text > ''::text),
    CONSTRAINT studyGroup_coordinatex_check CHECK (coordinatex < 531),
    CONSTRAINT studyGroup_coordinatey_check CHECK (coordinatey > -653::double precision),
    CONSTRAINT studyGroup_studentsCount_check CHECK (studentsCount > 0)
)

TABLESPACE pg_default;

ALTER TABLE public.studyGroup
    OWNER to postgres;
-- Index: fki_persod_id_pkey

-- DROP INDEX public.fki_persod_id_pkey;

CREATE INDEX fki_persod_id_pkey
    ON public.studyGroup USING btree
    (person_id ASC NULLS LAST)
    TABLESPACE pg_default;
-- Index: fki_user_id_fkey

-- DROP INDEX public.fki_user_id_fkey;

CREATE INDEX fki_user_id_fkey
    ON public.studyGroup USING btree
    (user_id ASC NULLS LAST)
    TABLESPACE pg_default;