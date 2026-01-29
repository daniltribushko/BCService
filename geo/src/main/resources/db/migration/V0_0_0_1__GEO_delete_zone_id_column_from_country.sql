DO
$$
    BEGIN
        IF EXISTS(SELECT
                  FROM information_schema.columns
                  WHERE table_schema = 'public'
                    AND table_name = 'country'
                    AND column_name = 'zone_id')
        THEN
            ALTER TABLE country DROP COLUMN zone_id;
        END IF;
    END;
$$