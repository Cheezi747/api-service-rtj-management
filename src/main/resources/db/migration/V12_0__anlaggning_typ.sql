-- V12_0: anlaggning_typ (befintlig/ny anläggning) on both LBE type detail tables.
--
-- EXISTING / NEW — whether the application concerns an existing or a new anläggning.

alter table brandfarlig_vara_details add column anlaggning_typ varchar(16);
alter table explosiv_vara_details add column anlaggning_typ varchar(16);
