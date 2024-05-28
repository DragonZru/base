CREATE TABLE IF NOT EXISTS `example`.`t_example`
(
    `id`          bigint UNSIGNED NOT NULL AUTO_INCREMENT,
    `username`    varchar(255)    NOT NULL,
    `password`    varchar(255)    NOT NULL,
    `strings`     varchar(1024)   NULL,
    `version`     bigint          NOT NULL DEFAULT 0,
    `status`      tinyint         NOT NULL DEFAULT 1,
    `create_time` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime        NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `U_username`(`username`)
);