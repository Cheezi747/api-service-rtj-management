-- V6_0: Add EGENSOTNING type-scoped storage + automated-check results.
--
-- egensotning_details — 1:1 with errand. Holds the applicant personnummer and the
-- property (fastighetsbeteckning/address) the application concerns, which the three
-- automated checks need, plus the computed check results and the last routing
-- outcome so the handläggare UI and the audit trail can see why the process
-- auto-approved or escalated. Frontend populates personnummer/fastighetsbeteckning/
-- property_address; the verify step writes the computed columns.
--
-- The personnummer index supports the återansökan lookup (find prior egensotning
-- applications for the same applicant).

create table egensotning_details (
    id bigint not null auto_increment,
    errand_id varchar(255) not null,
    personnummer varchar(16),
    fastighetsbeteckning varchar(255),
    property_address varchar(255),
    bilaga_present boolean,
    registered_at_property boolean,
    reapplication_ok boolean,
    last_outcome varchar(32),
    manual_review_reason varchar(64),
    last_verified_at datetime(6),
    created datetime(6),
    modified datetime(6),
    primary key (id),
    unique key uq_egensotning_details_errand_id (errand_id),
    index idx_egensotning_details_personnummer (personnummer)
) engine=InnoDB;
