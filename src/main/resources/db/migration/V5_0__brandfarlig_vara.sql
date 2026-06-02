-- V5_0: Add BRANDFARLIG_VARA type-scoped storage.
--
-- Two new tables (one per shape):
--   * brandfarlig_vara_details — 1:1 with errand. Holds the type-specific scalar
--     fields that don't belong on core Errand (verksamhetstyp, isProxy,
--     fastighetsbeteckning, handling-location address).
--   * hazardous_goods_product  — N per errand. Each row is one product line
--     under one of four categories (GAS, LIQUID, AEROSOL, REACTIVE). Required
--     to be queryable (e.g. "find every errand handling > 1000 L gas").
--
-- Plus a new nullable `category` column on `attachment` so we can distinguish
-- DELEGATION (fullmakt) from COMPETENCE (föreståndar-bevis) from generic OTHER.
-- Backfill is unnecessary; existing rows just stay NULL.

create table brandfarlig_vara_details (
    id bigint not null auto_increment,
    errand_id varchar(255) not null,
    verksamhetstyp varchar(64),
    is_proxy boolean not null default false,
    fastighetsbeteckning varchar(255),
    handling_location_address varchar(255),
    handling_location_zip_code varchar(16),
    handling_location_city varchar(255),
    created datetime(6),
    modified datetime(6),
    primary key (id),
    unique key uq_brandfarlig_vara_details_errand_id (errand_id)
) engine=InnoDB;

create table hazardous_goods_product (
    id varchar(255) not null,
    errand_id varchar(255) not null,
    category varchar(32) not null,
    product_name varchar(255) not null,
    quantity decimal(18,3),
    quantity_unit varchar(32),
    storage_type varchar(64),
    storage_location varchar(64),
    flash_point decimal(8,2),
    created datetime(6),
    modified datetime(6),
    primary key (id),
    index idx_hazardous_goods_errand_id (errand_id),
    index idx_hazardous_goods_category (category)
) engine=InnoDB;

alter table attachment
    add column category varchar(32) null;
