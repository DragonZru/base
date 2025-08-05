CREATE DATABASE IF NOT EXISTS leaf;
use leaf;
CREATE TABLE IF NOT EXISTS `leaf_alloc` (
                                            `id` bigint     UNSIGNED NOT NULL AUTO_INCREMENT,
                                            `biz_tag`       varchar(128)  NOT NULL DEFAULT '', -- your biz unique name
                                            `idx`           bigint(20) NOT NULL DEFAULT '1',
                                            `step`          int(11) NOT NULL,
                                            `description`   varchar(256)  DEFAULT NULL,
                                            `update_time`   timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                            PRIMARY KEY (`id`),
                                            UNIQUE INDEX `U_biz_tag`(`biz_tag`)
) ENGINE=InnoDB;

insert into leaf_alloc(biz_tag, idx, step, description) values('default', 1, 2000, 'Test leaf Segment Mode Get Id');
