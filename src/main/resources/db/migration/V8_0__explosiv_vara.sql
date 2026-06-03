-- V8_0: Add EXPLOSIV_VARA type-scoped storage.
--
-- Two new tables (one per shape), mirroring the brandfarlig-vara layout (V5_0):
--   * explosiv_vara_details  — 1:1 with errand. Holds the type-specific scalar
--     fields that don't belong on core Errand (typAvHantering, isProxy,
--     fastighetsbeteckning, handling-location address).
--   * explosiv_goods_product — N per errand. Each row is one product line
--     under one of six MSBFS 2010:4 riskklasser (1.1–1.6). Required to be
--     queryable (e.g. "find every errand handling riskgrupp 1.1").
--
-- The `attachment.category` column was already added in V5_0 — do NOT re-add it.

create table explosiv_vara_details (
    id bigint not null auto_increment,
    errand_id varchar(255) not null,
    typ_av_hantering varchar(64),
    is_proxy boolean not null default false,
    fastighetsbeteckning varchar(255),
    handling_location_address varchar(255),
    handling_location_zip_code varchar(16),
    handling_location_city varchar(255),
    created datetime(6),
    modified datetime(6),
    primary key (id),
    unique key uq_explosiv_vara_details_errand_id (errand_id)
) engine=InnoDB;

create table explosiv_goods_product (
    id varchar(255) not null,
    errand_id varchar(255) not null,
    hazard_class varchar(8) not null,
    product_name varchar(255) not null,
    quantity decimal(18,3),
    quantity_unit varchar(32),
    storage_type varchar(64),
    storage_location varchar(64),
    created datetime(6),
    modified datetime(6),
    primary key (id),
    index idx_explosiv_goods_errand_id (errand_id),
    index idx_explosiv_goods_hazard_class (hazard_class)
) engine=InnoDB;
