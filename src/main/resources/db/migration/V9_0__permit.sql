-- V9_0: Permit (tillstånd) table — the issued LBE permit with validity period + conditions.
--
-- Shared, type-agnostic, keyed by errand_id (like `decision`). Captures what the flat `decision`
-- table cannot: valid_from/valid_until (giltighetstid), conditions (villkor) and a lifecycle
-- status (ACTIVE → REVOKED for återkallande enligt 20 § LBE). permit_type discriminates
-- BRANDFARLIG_VARA (5 år) from EXPLOSIV_VARA (högst 3 år, 19 b § LBE).

create table permit (
    id varchar(255) not null,
    errand_id varchar(255) not null,
    permit_type varchar(64),
    valid_from date,
    valid_until date,
    conditions varchar(4096),
    status varchar(32),
    created datetime(6),
    modified datetime(6),
    primary key (id),
    index idx_permit_errand_id (errand_id)
) engine=InnoDB;
