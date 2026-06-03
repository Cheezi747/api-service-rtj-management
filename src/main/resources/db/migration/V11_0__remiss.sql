-- V11_0: Remiss (samråd) table — outgoing remisser/samråd on an errand and their svar.
--
-- Shared, type-agnostic, keyed by errand_id (like `permit` and `decision`). En LBE-handläggare
-- skickar en remiss till en annan instans (14 § FBE — t.ex. miljökontor) eller begär polisens
-- yttrande för explosiv vara, och registrerar svaret när det kommer in. status följer livscykeln
-- SENT -> RESPONDED.

create table remiss (
    id varchar(255) not null,
    errand_id varchar(255) not null,
    instans varchar(64),
    recipient varchar(255),
    sent_at date,
    due_at date,
    response_text varchar(4096),
    status varchar(32),
    created datetime(6),
    modified datetime(6),
    primary key (id),
    index idx_remiss_errand_id (errand_id)
) engine=InnoDB;
