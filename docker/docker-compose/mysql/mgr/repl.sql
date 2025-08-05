/*
each node
*/
set SQL_LOG_BIN=0;
CREATE USER repl@'%' IDENTIFIED BY '123456';
GRANT REPLICATION SLAVE ON *.* TO repl@'%';
GRANT CONNECTION_ADMIN ON *.* TO repl@'%';
GRANT BACKUP_ADMIN ON *.* TO repl@'%';
GRANT GROUP_REPLICATION_STREAM ON *.* TO repl@'%';
FLUSH PRIVILEGES;
SET SQL_LOG_BIN=1;
CHANGE MASTER TO MASTER_USER='repl', MASTER_PASSWORD='123456' FOR CHANNEL 'group_replication_recovery';

/*
first node to bootstrap , single primary will be master
*/
SET GLOBAL group_replication_bootstrap_group=ON;
START GROUP_REPLICATION;
SET GLOBAL group_replication_bootstrap_group=OFF;
SELECT * FROM performance_schema.replication_group_members;

/*

*/
-- 在后续节点执行
RESET MASTER;
SET GLOBAL group_replication_recovery_get_public_key=ON;
START GROUP_REPLICATION;