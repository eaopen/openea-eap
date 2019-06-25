-- ----------------------------
-- Table structure for t_github
-- ----------------------------
DROP TABLE IF EXISTS `t_github`;
CREATE TABLE `t_github`
(
    `id`              varchar(255) NOT NULL,
    `create_by`       varchar(255) DEFAULT NULL,
    `create_time`     datetime     DEFAULT NULL,
    `del_flag`        int(11)      DEFAULT NULL,
    `update_by`       varchar(255) DEFAULT NULL,
    `update_time`     datetime     DEFAULT NULL,
    `avatar`          varchar(255) DEFAULT NULL,
    `is_related`      bit(1)       DEFAULT NULL,
    `open_id`         varchar(255) DEFAULT NULL,
    `relate_username` varchar(255) DEFAULT NULL,
    `username`        varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


-- ----------------------------
-- Table structure for t_qq
-- ----------------------------
DROP TABLE IF EXISTS `t_qq`;
CREATE TABLE `t_qq`
(
    `id`              varchar(255) NOT NULL,
    `create_by`       varchar(255) DEFAULT NULL,
    `create_time`     datetime     DEFAULT NULL,
    `del_flag`        int(11)      DEFAULT NULL,
    `update_by`       varchar(255) DEFAULT NULL,
    `update_time`     datetime     DEFAULT NULL,
    `avatar`          varchar(255) DEFAULT NULL,
    `is_related`      bit(1)       DEFAULT NULL,
    `open_id`         varchar(255) DEFAULT NULL,
    `relate_username` varchar(255) DEFAULT NULL,
    `username`        varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;



-- ----------------------------
-- Table structure for t_weibo
-- ----------------------------
DROP TABLE IF EXISTS `t_weibo`;
CREATE TABLE `t_weibo`
(
    `id`              varchar(255) NOT NULL,
    `create_by`       varchar(255) DEFAULT NULL,
    `create_time`     datetime     DEFAULT NULL,
    `del_flag`        int(11)      DEFAULT NULL,
    `update_by`       varchar(255) DEFAULT NULL,
    `update_time`     datetime     DEFAULT NULL,
    `avatar`          varchar(255) DEFAULT NULL,
    `is_related`      bit(1)       DEFAULT NULL,
    `open_id`         varchar(255) DEFAULT NULL,
    `relate_username` varchar(255) DEFAULT NULL,
    `username`        varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
