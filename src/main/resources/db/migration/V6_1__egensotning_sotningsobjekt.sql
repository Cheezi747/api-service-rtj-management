-- V6_1: Sotningsobjekt for EGENSOTNING errands.
--
-- N rows per errand — one per eldstad/anläggning the application (and the resulting
-- beslut) covers. Mirrors the per-objekt-tabell in the formal beslut (Fabrikat, Typ,
-- Tillverkningsår, Bränsleslag, Bränslemängd/år, Sotningsintervall) and the objektmodell
-- in the kontrollbok-registret. The verify step lists these in the generated beslutstext;
-- a complete application must have at least one.

create table egensotning_sotningsobjekt (
    id varchar(255) not null,
    errand_id varchar(255) not null,
    fabrikat varchar(255),
    typ varchar(64),
    tillverkningsar int,
    bransleslag varchar(64),
    branslemangd varchar(64),
    sotningsintervall_veckor int,
    created datetime(6),
    modified datetime(6),
    primary key (id),
    index idx_egensotning_sotningsobjekt_errand_id (errand_id)
) engine=InnoDB;
