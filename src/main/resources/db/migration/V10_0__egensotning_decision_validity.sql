-- V10_0: Time-limit egensotning decisions to six years + expiry-reminder bookkeeping.
--
-- An approved egensotning decision is no longer "tillsvidare" but valid for six years:
-- valid_from = beslutsdatum, valid_until = +6 år snapped up to the next fixed quarterly
-- date (1 mar/jun/sep/dec) so reminders batch. A daily scheduler emails the applicant
-- ahead of valid_until and stamps reminder_sent_at so the reminder is sent at most once.
--
-- All three columns are system-owned (written by the decision listener / reminder
-- scheduler), never from the API payload. The valid_until index supports the scheduler's
-- "expiring within N days and not yet reminded" query.

alter table egensotning_details
    add column valid_from date,
    add column valid_until date,
    add column reminder_sent_at datetime(6);

create index idx_egensotning_details_valid_until on egensotning_details (valid_until);
