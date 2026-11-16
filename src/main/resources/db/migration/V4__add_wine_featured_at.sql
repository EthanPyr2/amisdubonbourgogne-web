-- Date de mise en vedette : la landing affiche les vins mis en vedette le plus récemment.
ALTER TABLE wine
    ADD COLUMN featured_at TIMESTAMPTZ;

-- Vins déjà en vedette : on prend leur dernière modification comme date de référence
UPDATE wine
SET featured_at = COALESCE(update_at, created_at, now())
WHERE featured;
