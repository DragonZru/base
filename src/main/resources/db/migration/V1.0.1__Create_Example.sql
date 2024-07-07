CREATE TABLE IF NOT EXISTS `t_example`
(
    `id`          bigint UNSIGNED NOT NULL AUTO_INCREMENT,
    `username`    varchar(255)    NOT NULL,
    `password`    varchar(255)    NOT NULL,
    `version`     bigint          NOT NULL DEFAULT 0,
    `status`      tinyint         NOT NULL DEFAULT 1,
    `extras`      JSON            NULL,
    `value`       LONGTEXT        NULL,
    `create_time` TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` TIMESTAMP       NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE INDEX `U_username`(`username`)
);
-- ALTER TABLE t_example ADD INDEX n_extras( ( CAST( extras -> '$[*].serialNo' AS UNSIGNED ARRAY)) );

/*
 * 支持string,utf8mb4_bin 区分大小写,若想索引生效需要指定COLLATE utf8mb4_bin
 * The ->> operator is the same as JSON_UNQUOTE(JSON_EXTRACT(...))
 * CAST() returns a string with the collation utf8mb4_0900_ai_ci (the server default collation).
 * JSON_UNQUOTE() returns a string with the collation utf8mb4_bin (hard coded).
 * https://dev.mysql.com/doc/refman/8.0/en/create-index.html
 */
ALTER TABLE t_example ADD INDEX n_extras( ( CAST( extras ->> '$[*].serialNo' AS CHAR(30)) COLLATE utf8mb4_bin) );

/**
  * 创建全文索引,ngram支持中文
 */
ALTER TABLE t_example ADD FULLTEXT INDEX f_value (value) WITH PARSER ngram;