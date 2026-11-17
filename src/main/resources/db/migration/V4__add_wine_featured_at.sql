ALTER TABLE wine
    ADD COLUMN featured_at TIMESTAMPTZ;

UPDATE wine
SET featured_at = COALESCE(update_at, created_at, now())
WHERE featured;
