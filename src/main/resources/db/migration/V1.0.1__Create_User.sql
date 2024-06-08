DELIMITER //
CREATE PROCEDURE CreateTables(IN table_count INT)
BEGIN
    DECLARE counter INT DEFAULT 0;
    while counter < table_count do
        set @sql = concat('create table if not exists t_user_',counter,' (
        id int primary key auto_increment,
        name varchar(50)
        -- 其他字段...
    );');
        prepare stmt from @sql;
        execute stmt;
        deallocate prepare stmt;
        set counter = counter + 1;
    end while;
END//
DELIMITER ;
CALL CreateTables(2);
DROP PROCEDURE IF EXISTS CreateTables;