DELIMITER //
CREATE PROCEDURE CreateTables(IN table_count INT)
BEGIN
    DECLARE counter INT DEFAULT 0;
    simple_loop: LOOP
        IF counter >= table_count THEN
            LEAVE simple_loop;
        END IF;
        SET @sql = CONCAT('CREATE TABLE IF NOT EXISTS t_user_', counter, ' (
        id INT PRIMARY KEY AUTO_INCREMENT,
        name VARCHAR(50)
        -- 其他字段...
    );');
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
        SET counter = counter + 1;
    END LOOP simple_loop;
END//
DELIMITER ;
CALL CreateTables(2);
DROP PROCEDURE IF EXISTS CreateTables;