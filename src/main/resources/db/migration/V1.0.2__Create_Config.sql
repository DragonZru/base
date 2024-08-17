CREATE TABLE IF NOT EXISTS `t_config`
(
    `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `name`        VARCHAR(255)    NOT NULL,
    `value`       VARCHAR(255)    NOT NULL default '',
    `desc`        VARCHAR(255)    NULL,
    `create_time` TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP       NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `U_name` (`name`)
);