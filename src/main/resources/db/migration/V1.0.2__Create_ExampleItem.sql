DELIMITER //
CREATE PROCEDURE CreateTables(IN table_count INT)
BEGIN
    DECLARE counter INT DEFAULT 0;
    while counter < table_count do
        set @sql = concat('create table if not exists t_example_item_',counter,' (
    `id`          bigint UNSIGNED NOT NULL,
    `example_id`  bigint          NOT NULL,
    `create_time` TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP       NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
    );');
        prepare stmt from @sql;
        execute stmt;
        deallocate prepare stmt;
        set counter = counter + 1;
    end while;
END//
DELIMITER ;
CALL CreateTables(10);
DROP PROCEDURE IF EXISTS CreateTables;