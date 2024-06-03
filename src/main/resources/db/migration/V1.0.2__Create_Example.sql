CREATE TABLE IF NOT EXISTS `t_example`
(
    `id`          bigint UNSIGNED NOT NULL AUTO_INCREMENT,
    `username`    varchar(255)    NOT NULL,
    `password`    varchar(255)    NOT NULL,
    `version`     bigint          NOT NULL DEFAULT 0,
    `status`      tinyint         NOT NULL DEFAULT 1,
    `extras`      JSON            NULL,
    `create_time` datetime        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` datetime        NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `U_username`(`username`)
);
ALTER TABLE t_example ADD INDEX n_extras( ( CAST( extras -> '$[*].serialNo' AS UNSIGNED ARRAY)) );