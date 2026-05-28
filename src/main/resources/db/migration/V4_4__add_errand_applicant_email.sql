-- V4_4: stash the applicant's email on the errand envelope.
-- Workers reference it as a process variable when sending decision /
-- supplementation emails — previously it was hardcoded in the BPMN.

ALTER TABLE errand
    ADD COLUMN applicant_email VARCHAR(255) NULL;
