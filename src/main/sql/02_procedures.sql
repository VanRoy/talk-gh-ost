-- Populate dataset

DELIMITER $$
DROP PROCEDURE IF EXISTS insert_data$$
CREATE PROCEDURE insert_data(IN table_name VARCHAR(100), IN rows_count INT)
BEGIN
DECLARE x INT;
DECLARE y INT;
DECLARE usr_uuid VARCHAR(255);

SET x = 0;

LOOP_X: REPEAT

    START TRANSACTION;

    SET y = 1;
    SET @usr_uuid = uuid();

    LOOP_Y: REPEAT

     SET @cmd = CONCAT('INSERT INTO ', table_name, '(date, description, amount, debit, user_uuid, timestamp_last_update)
     VALUES (CURRENT_TIMESTAMP - INTERVAL FLOOR(RAND() * 365) DAY, CONCAT("Transaction Test ", ?), ROUND(RAND()*1024, 2), true, ?, now())');

     PREPARE stmt FROM @cmd;
     EXECUTE stmt USING @usr_uuid, @usr_uuid;

     SET  y = y + 1;

    UNTIL y > 1000
    END REPEAT LOOP_Y;

    COMMIT;

 SET x = x + 1000;

UNTIL x >= rows_count
END REPEAT LOOP_X;

END$$
DELIMITER ;
