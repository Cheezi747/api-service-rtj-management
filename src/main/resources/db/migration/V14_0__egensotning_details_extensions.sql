-- V14_0: Handläggar-redigerbar motivering + ägar-/fastighetsuppgifter på egensotning-detaljerna.
--
-- motivering (I2): hela beslutsmotiveringen kan redigeras av handläggaren under manuell granskning.
--   När den lämnas tom renderas den förifyllda standardtexten. Ett ärende slutar i antingen ett
--   godkännande- eller ett avslagsbeslut, så ett enda fält räcker för båda.
--
-- owns_property / ownership_motivation / applies_for_other_property (R4): underlag för manuell
--   granskning när sökanden inte är folkbokförd på fastigheten (t.ex. annan fastighet / sommarstuga)
--   — anger om sökanden äger fastigheten och, om inte, en motivering. Frontend-levererade fält.

alter table egensotning_details
    add column motivering varchar(4096),
    add column owns_property bit(1),
    add column ownership_motivation varchar(2048),
    add column applies_for_other_property bit(1);
