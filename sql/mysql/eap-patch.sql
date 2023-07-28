
-- token增加userKey, 方便集成第三方系统的用户

ALTER TABLE `system_oauth2_access_token`
    ADD COLUMN `user_key` varchar(100) NULL COMMENT '用户key' AFTER `user_id`;

ALTER TABLE `system_oauth2_refresh_token`
    ADD COLUMN `user_key` varchar(100) NULL COMMENT '用户key' AFTER `user_id`;