-- V13_0: Persist the Eneo (LLM) document-validation result on the egensotning details row.
--
-- The verify step's auto-approve branch now runs an extra check: the two attachments
-- (brandskyddskontroll + utbildningsintyg) are sent to the Eneo LLM platform, which judges
-- whether they are the correct document types, valid, and match the applicant. The verdict gates
-- auto-approval (a non-valid verdict diverts the errand to manual review; the LLM never
-- auto-rejects). These columns are system-owned (written by the document-validation service) and
-- exist for the handläggare UI / audit trail — they mirror the existing last_outcome bookkeeping.

alter table egensotning_details
    add column documents_valid bit(1),
    add column document_validation_detail varchar(2048),
    add column document_validated_at datetime(6);
