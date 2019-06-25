SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for t_department
-- ----------------------------
DROP TABLE IF EXISTS `t_department`;
CREATE TABLE `t_department`
(
    `id`          varchar(255) NOT NULL,
    `create_by`   varchar(255)   DEFAULT NULL,
    `create_time` datetime       DEFAULT NULL,
    `del_flag`    int(11)        DEFAULT NULL,
    `update_by`   varchar(255)   DEFAULT NULL,
    `update_time` datetime       DEFAULT NULL,
    `parent_id`   varchar(255)   DEFAULT NULL,
    `sort_order`  decimal(10, 2) DEFAULT NULL,
    `status`      int(11)        DEFAULT NULL,
    `title`       varchar(255)   DEFAULT NULL,
    `is_parent`   bit(1)         DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of t_department
-- ----------------------------
BEGIN;
INSERT INTO `t_department`
VALUES ('40322777781112832', '', '2018-08-10 20:40:40', 0, '', '2018-08-11 00:03:06', '0', 1.00, 0, '总部', b'1');
INSERT INTO `t_department`
VALUES ('40322811096469504', '', '2018-08-10 20:40:48', 0, '', '2018-08-11 00:27:05', '40322777781112832', 1.00, 0,
        '技术部', b'1');
INSERT INTO `t_department`
VALUES ('40322852833988608', '', '2018-08-10 20:40:58', 0, '', '2018-08-11 01:29:42', '40322811096469504', 1.00, 0,
        '研发中心', NULL);
INSERT INTO `t_department`
VALUES ('40327204755738624', '', '2018-08-10 20:58:15', 0, '', '2018-08-10 22:02:15', '40322811096469504', 2.00, 0,
        '大数据', NULL);
INSERT INTO `t_department`
VALUES ('40327253309001728', '', '2018-08-10 20:58:27', 0, '', '2018-08-11 17:26:38', '40322811096469504', 3.00, -1,
        '人工智障', NULL);
INSERT INTO `t_department`
VALUES ('40343262766043136', '', '2018-08-10 22:02:04', 0, 'admin', '2019-06-21 09:21:52', '0', 2.00, 0, '上海分部', b'1');
INSERT INTO `t_department`
VALUES ('40344005342400512', '', '2018-08-10 22:05:01', 0, '', '2018-08-11 17:48:44', '40343262766043136', 2.00, 0,
        'Vue', NULL);
INSERT INTO `t_department`
VALUES ('40389030113710080', '', '2018-08-11 01:03:56', 0, '', '2018-08-11 17:50:04', '40343262766043136', 1.00, 0,
        'JAVA', b'0');
INSERT INTO `t_department`
VALUES ('40652270295060480', '', '2018-08-11 18:29:57', 0, '', '2018-08-12 18:45:01', '0', 3.00, 0, '人事部', b'1');
INSERT INTO `t_department`
VALUES ('40652338142121984', NULL, '2018-08-11 18:30:13', 0, NULL, '2018-08-11 18:30:13', '40652270295060480', 1.00, 0,
        '游客', b'0');
INSERT INTO `t_department`
VALUES ('40681289119961088', '', '2018-08-11 20:25:16', 0, '', '2018-08-11 22:47:48', '40652270295060480', 2.00, 0,
        'VIP', b'0');
COMMIT;

-- ----------------------------
-- Table structure for t_department_header
-- ----------------------------
DROP TABLE IF EXISTS `t_department_header`;
CREATE TABLE `t_department_header`
(
    `id`            varchar(255) NOT NULL,
    `create_by`     varchar(255) DEFAULT NULL,
    `create_time`   datetime     DEFAULT NULL,
    `del_flag`      int(11)      DEFAULT NULL,
    `update_by`     varchar(255) DEFAULT NULL,
    `update_time`   datetime     DEFAULT NULL,
    `department_id` varchar(255) DEFAULT NULL,
    `type`          int(11)      DEFAULT NULL,
    `user_id`       varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Table structure for t_dict
-- ----------------------------
DROP TABLE IF EXISTS `t_dict`;
CREATE TABLE `t_dict`
(
    `id`          varchar(255) NOT NULL,
    `create_by`   varchar(255)   DEFAULT NULL,
    `create_time` datetime       DEFAULT NULL,
    `del_flag`    int(11)        DEFAULT NULL,
    `update_by`   varchar(255)   DEFAULT NULL,
    `update_time` datetime       DEFAULT NULL,
    `description` varchar(255)   DEFAULT NULL,
    `title`       varchar(255)   DEFAULT NULL,
    `type`        varchar(255)   DEFAULT NULL,
    `sort_order`  decimal(10, 2) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


-- ----------------------------
-- Table structure for t_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `t_dict_data`;
CREATE TABLE `t_dict_data`
(
    `id`          varchar(255) NOT NULL,
    `create_by`   varchar(255)   DEFAULT NULL,
    `create_time` datetime       DEFAULT NULL,
    `del_flag`    int(11)        DEFAULT NULL,
    `update_by`   varchar(255)   DEFAULT NULL,
    `update_time` datetime       DEFAULT NULL,
    `description` varchar(255)   DEFAULT NULL,
    `dict_id`     varchar(255)   DEFAULT NULL,
    `sort_order`  decimal(10, 2) DEFAULT NULL,
    `status`      int(11)        DEFAULT NULL,
    `title`       varchar(255)   DEFAULT NULL,
    `value`       varchar(255)   DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


-- ----------------------------
-- Table structure for t_file
-- ----------------------------
DROP TABLE IF EXISTS `t_file`;
CREATE TABLE `t_file`
(
    `id`          varchar(255) NOT NULL,
    `create_by`   varchar(255) DEFAULT NULL,
    `create_time` datetime     DEFAULT NULL,
    `del_flag`    int(11)      DEFAULT NULL,
    `update_by`   varchar(255) DEFAULT NULL,
    `update_time` datetime     DEFAULT NULL,
    `name`        varchar(255) DEFAULT NULL,
    `size`        bigint(20)   DEFAULT NULL,
    `type`        varchar(255) DEFAULT NULL,
    `url`         varchar(255) DEFAULT NULL,
    `f_key`       varchar(255) DEFAULT NULL,
    `location`    int(11)      DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;



-- ----------------------------
-- Table structure for t_log
-- ----------------------------
DROP TABLE IF EXISTS `t_log`;
CREATE TABLE `t_log`
(
    `id`            varchar(255) NOT NULL,
    `create_by`     varchar(255) DEFAULT NULL,
    `create_time`   datetime     DEFAULT NULL,
    `del_flag`      int(11)      DEFAULT NULL,
    `update_by`     varchar(255) DEFAULT NULL,
    `update_time`   datetime     DEFAULT NULL,
    `cost_time`     int(11)      DEFAULT NULL,
    `ip`            varchar(255) DEFAULT NULL,
    `ip_info`       varchar(255) DEFAULT NULL,
    `name`          varchar(255) DEFAULT NULL,
    `request_param` longtext,
    `request_type`  varchar(255) DEFAULT NULL,
    `request_url`   varchar(255) DEFAULT NULL,
    `username`      varchar(255) DEFAULT NULL,
    `log_type`      int(11)      DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


-- ----------------------------
-- Table structure for t_message
-- ----------------------------
DROP TABLE IF EXISTS `t_message`;
CREATE TABLE `t_message`
(
    `id`          varchar(255) NOT NULL,
    `create_by`   varchar(255) DEFAULT NULL,
    `create_time` datetime     DEFAULT NULL,
    `del_flag`    int(11)      DEFAULT NULL,
    `update_by`   varchar(255) DEFAULT NULL,
    `update_time` datetime     DEFAULT NULL,
    `create_send` bit(1)       DEFAULT NULL,
    `title`       varchar(255) DEFAULT NULL,
    `type`        varchar(255) DEFAULT NULL,
    `content`     longtext,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


-- ----------------------------
-- Table structure for t_message_send
-- ----------------------------
DROP TABLE IF EXISTS `t_message_send`;
CREATE TABLE `t_message_send`
(
    `id`          varchar(255) NOT NULL,
    `create_by`   varchar(255) DEFAULT NULL,
    `create_time` datetime     DEFAULT NULL,
    `del_flag`    int(11)      DEFAULT NULL,
    `update_by`   varchar(255) DEFAULT NULL,
    `update_time` datetime     DEFAULT NULL,
    `message_id`  varchar(255) DEFAULT NULL,
    `status`      int(11)      DEFAULT NULL,
    `user_id`     varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


-- ----------------------------
-- Table structure for t_permission
-- ----------------------------
DROP TABLE IF EXISTS `t_permission`;
CREATE TABLE `t_permission`
(
    `id`          varchar(255) NOT NULL,
    `create_by`   varchar(255)   DEFAULT NULL,
    `create_time` datetime       DEFAULT NULL,
    `del_flag`    int(11)        DEFAULT NULL,
    `update_by`   varchar(255)   DEFAULT NULL,
    `update_time` datetime       DEFAULT NULL,
    `description` varchar(255)   DEFAULT NULL,
    `name`        varchar(255)   DEFAULT NULL,
    `parent_id`   varchar(255)   DEFAULT NULL,
    `type`        int(11)        DEFAULT NULL,
    `sort_order`  decimal(10, 2) DEFAULT NULL,
    `component`   varchar(255)   DEFAULT NULL,
    `path`        varchar(255)   DEFAULT NULL,
    `title`       varchar(255)   DEFAULT NULL,
    `icon`        varchar(255)   DEFAULT NULL,
    `level`       int(11)        DEFAULT NULL,
    `button_type` varchar(255)   DEFAULT NULL,
    `status`      int(11)        DEFAULT NULL,
    `url`         varchar(255)   DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;




-- ----------------------------
-- Table structure for t_role
-- ----------------------------
DROP TABLE IF EXISTS `t_role`;
CREATE TABLE `t_role`
(
    `id`           varchar(255) NOT NULL,
    `create_by`    varchar(255) DEFAULT NULL,
    `create_time`  datetime     DEFAULT NULL,
    `update_by`    varchar(255) DEFAULT NULL,
    `update_time`  datetime     DEFAULT NULL,
    `name`         varchar(255) DEFAULT NULL,
    `del_flag`     int(11)      DEFAULT NULL,
    `default_role` bit(1)       DEFAULT NULL,
    `description`  varchar(255) DEFAULT NULL,
    `data_type`    int(11)      DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


-- ----------------------------
-- Table structure for t_role_department
-- ----------------------------
DROP TABLE IF EXISTS `t_role_department`;
CREATE TABLE `t_role_department`
(
    `id`            varchar(255) NOT NULL,
    `create_by`     varchar(255) DEFAULT NULL,
    `create_time`   datetime     DEFAULT NULL,
    `del_flag`      int(11)      DEFAULT NULL,
    `update_by`     varchar(255) DEFAULT NULL,
    `update_time`   datetime     DEFAULT NULL,
    `department_id` varchar(255) DEFAULT NULL,
    `role_id`       varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;



-- ----------------------------
-- Table structure for t_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `t_role_permission`;
CREATE TABLE `t_role_permission`
(
    `id`            varchar(255) NOT NULL,
    `create_by`     varchar(255) DEFAULT NULL,
    `create_time`   datetime     DEFAULT NULL,
    `del_flag`      int(11)      DEFAULT NULL,
    `update_by`     varchar(255) DEFAULT NULL,
    `update_time`   datetime     DEFAULT NULL,
    `permission_id` varchar(255) DEFAULT NULL,
    `role_id`       varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;



-- ----------------------------
-- Table structure for t_user
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`
(
    `id`            varchar(255) NOT NULL,
    `create_by`     varchar(255)  DEFAULT NULL,
    `create_time`   datetime      DEFAULT NULL,
    `update_by`     varchar(255)  DEFAULT NULL,
    `update_time`   datetime      DEFAULT NULL,
    `address`       varchar(255)  DEFAULT NULL,
    `avatar`        varchar(1000) DEFAULT NULL,
    `description`   varchar(255)  DEFAULT NULL,
    `email`         varchar(255)  DEFAULT NULL,
    `mobile`        varchar(255)  DEFAULT NULL,
    `nick_name`     varchar(255)  DEFAULT NULL,
    `password`      varchar(255)  DEFAULT NULL,
    `sex`           varchar(255)  DEFAULT NULL,
    `status`        int(11)       DEFAULT NULL,
    `type`          int(11)       DEFAULT NULL,
    `username`      varchar(255)  DEFAULT NULL,
    `del_flag`      int(11)       DEFAULT NULL,
    `department_id` varchar(255)  DEFAULT NULL,
    `street`        varchar(255)  DEFAULT NULL,
    `pass_strength` varchar(2)    DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


-- ----------------------------
-- Table structure for t_user_role
-- ----------------------------
DROP TABLE IF EXISTS `t_user_role`;
CREATE TABLE `t_user_role`
(
    `id`          varchar(255) NOT NULL,
    `create_by`   varchar(255) DEFAULT NULL,
    `create_time` datetime     DEFAULT NULL,
    `del_flag`    int(11)      DEFAULT NULL,
    `update_by`   varchar(255) DEFAULT NULL,
    `update_time` datetime     DEFAULT NULL,
    `role_id`     varchar(255) DEFAULT NULL,
    `user_id`     varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


SET FOREIGN_KEY_CHECKS = 1;
