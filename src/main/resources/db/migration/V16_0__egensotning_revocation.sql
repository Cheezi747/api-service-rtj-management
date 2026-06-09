-- V16_0: Återkallelse (R6) av ett godkänt egensotningsmedgivande.
--
-- Ett medgivande kan återkallas — automatiskt vid adressändring (sökanden inte längre folkbokförd
-- på fastigheten) eller manuellt (t.ex. vid underkänd brandskyddskontroll). Vid återkallelse sätts
-- ärendets status till REVOKED, valid_until nollställs (inga fler förnyelsepåminnelser) och dessa
-- system-ägda fält stämplas för handläggar-UI/revision.

alter table egensotning_details
    add column revoked_at        datetime(6),
    add column revocation_reason varchar(2048);
