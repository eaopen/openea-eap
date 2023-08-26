
-- token增加userKey, 方便集成第三方系统的用户 7/28

ALTER TABLE `system_oauth2_access_token`
    ADD COLUMN `user_key` varchar(100) NULL COMMENT '用户key' AFTER `user_id`;

ALTER TABLE `system_oauth2_refresh_token`
    ADD COLUMN `user_key` varchar(100) NULL COMMENT '用户key' AFTER `user_id`;


-- 数据字典扩展，增加树形配置，增加扩展类型json和sql

ALTER TABLE `system_dict_type`
ADD COLUMN `is_tree` tinyint NULL DEFAULT 0 COMMENT '树形' AFTER `status`,
ADD COLUMN `parent_id` bigint NULL DEFAULT 0 COMMENT '上级' AFTER `is_tree`,
ADD COLUMN `data_type` varchar(20) NULL DEFAULT 'data' COMMENT '数据类型(data/json/sql)' AFTER `parent_id`,
ADD COLUMN `data_json` varchar(500) NULL COMMENT 'JSON数据' AFTER `data_type`,
ADD COLUMN `data_sql` varchar(500) NULL COMMENT '查询SQL' AFTER `data_json`,
ADD COLUMN `data_ds` varchar(50) NULL COMMENT 'SQL数据源' AFTER `data_sql`;


ALTER TABLE `system_dict_data`
ADD COLUMN `parent_id` bigint NULL DEFAULT 0 COMMENT '上级' AFTER `id`;