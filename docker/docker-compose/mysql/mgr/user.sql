# 关闭bin log
set SQL_LOG_BIN=0;

# 创建用户 repl 用于组复制 @'%' 允许任何ip访问
CREATE USER repl@'%' IDENTIFIED BY 'Ux7Ah!2s';

# 权限，ON database.tables , *.* 表示允许任何 数据库.表
GRANT REPLICATION SLAVE ON *.* TO repl@'%';
GRANT CONNECTION_ADMIN ON *.* TO repl@'%';
GRANT BACKUP_ADMIN ON *.* TO repl@'%';
GRANT GROUP_REPLICATION_STREAM ON *.* TO repl@'%';
FLUSH PRIVILEGES;
SET SQL_LOG_BIN=1;
CHANGE MASTER TO MASTER_USER='repl', MASTER_PASSWORD='Ux7Ah!2s' FOR CHANNEL 'group_replication_recovery';