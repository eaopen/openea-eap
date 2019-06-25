SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;



-- ----------------------------
-- Table structure for t_leave
-- ----------------------------
DROP TABLE IF EXISTS `t_leave`;
CREATE TABLE `t_leave`
(
    `id`          varchar(255) NOT NULL,
    `create_by`   varchar(255) DEFAULT NULL,
    `create_time` datetime     DEFAULT NULL,
    `del_flag`    int(11)      DEFAULT NULL,
    `update_by`   varchar(255) DEFAULT NULL,
    `update_time` datetime     DEFAULT NULL,
    `description` varchar(255) DEFAULT NULL,
    `end_date`    datetime     DEFAULT NULL,
    `start_date`  datetime     DEFAULT NULL,
    `title`       varchar(255) DEFAULT NULL,
    `duration`    int(11)      DEFAULT NULL,
    `type`        varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;



-- ----------------------------
-- Table structure for t_act_business
-- ----------------------------
DROP TABLE IF EXISTS `t_act_business`;
CREATE TABLE `t_act_business`
(
    `id`           varchar(255) NOT NULL,
    `create_by`    varchar(255) DEFAULT NULL,
    `create_time`  datetime     DEFAULT NULL,
    `del_flag`     int(11)      DEFAULT NULL,
    `update_by`    varchar(255) DEFAULT NULL,
    `update_time`  datetime     DEFAULT NULL,
    `proc_def_id`  varchar(255) DEFAULT NULL,
    `proc_inst_id` varchar(255) DEFAULT NULL,
    `result`       int(11)      DEFAULT NULL,
    `status`       int(11)      DEFAULT NULL,
    `table_id`     varchar(255) DEFAULT NULL,
    `title`        varchar(255) DEFAULT NULL,
    `user_id`      varchar(255) DEFAULT NULL,
    `apply_time`   datetime     DEFAULT NULL,
    `is_history`   bit(1)       DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Table structure for t_act_category
-- ----------------------------
DROP TABLE IF EXISTS `t_act_category`;
CREATE TABLE `t_act_category`
(
    `id`          varchar(255) NOT NULL,
    `create_by`   varchar(255)   DEFAULT NULL,
    `create_time` datetime       DEFAULT NULL,
    `del_flag`    int(11)        DEFAULT NULL,
    `update_by`   varchar(255)   DEFAULT NULL,
    `update_time` datetime       DEFAULT NULL,
    `is_parent`   bit(1)         DEFAULT NULL,
    `parent_id`   varchar(255)   DEFAULT NULL,
    `sort_order`  decimal(10, 2) DEFAULT NULL,
    `status`      int(11)        DEFAULT NULL,
    `title`       varchar(255)   DEFAULT NULL,
    `type`        int(11)        DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Records of t_act_category
-- ----------------------------
BEGIN;
INSERT INTO `t_act_category`
VALUES ('80540745236221952', 'admin', '2018-11-29 20:12:31', 0, 'admin', '2018-11-29 22:28:09', b'1', '0', 1.00, 0,
        '人事相关', 0);
INSERT INTO `t_act_category`
VALUES ('80544250063753216', 'admin', '2018-11-29 20:26:27', 0, 'admin', '2018-11-29 22:27:45', b'0',
        '80540745236221952', 0.00, 0, '请假申请', 1);
INSERT INTO `t_act_category`
VALUES ('80574743098429440', 'admin', '2018-11-29 22:27:37', 0, 'admin', '2018-11-29 22:27:37', b'0',
        '80540745236221952', 0.00, 0, '报销申请', 1);
INSERT INTO `t_act_category`
VALUES ('80574860496998400', 'admin', '2018-11-29 22:28:05', 0, 'admin', '2018-11-29 22:29:03', b'1', '0', 0.00, 0,
        '工作审核', 0);
INSERT INTO `t_act_category`
VALUES ('80575104039260160', 'admin', '2018-11-29 22:29:03', 0, 'admin', '2018-11-29 22:29:03', b'0',
        '80574860496998400', 0.00, 0, '合同审批', 1);
INSERT INTO `t_act_category`
VALUES ('80575163153780736', 'admin', '2018-11-29 22:29:17', 0, 'admin', '2018-11-29 22:29:17', b'0',
        '80574860496998400', 0.00, 0, '订单审批', 1);
COMMIT;

-- ----------------------------
-- Table structure for t_act_model
-- ----------------------------
DROP TABLE IF EXISTS `t_act_model`;
CREATE TABLE `t_act_model`
(
    `id`          varchar(255) NOT NULL,
    `create_by`   varchar(255) DEFAULT NULL,
    `create_time` datetime     DEFAULT NULL,
    `del_flag`    int(11)      DEFAULT NULL,
    `update_by`   varchar(255) DEFAULT NULL,
    `update_time` datetime     DEFAULT NULL,
    `description` varchar(255) DEFAULT NULL,
    `model_key`   varchar(255) DEFAULT NULL,
    `name`        varchar(255) DEFAULT NULL,
    `version`     int(11)      DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Table structure for t_act_node
-- ----------------------------
DROP TABLE IF EXISTS `t_act_node`;
CREATE TABLE `t_act_node`
(
    `id`          varchar(255) NOT NULL,
    `create_by`   varchar(255) DEFAULT NULL,
    `create_time` datetime     DEFAULT NULL,
    `del_flag`    int(11)      DEFAULT NULL,
    `update_by`   varchar(255) DEFAULT NULL,
    `update_time` datetime     DEFAULT NULL,
    `node_id`     varchar(255) DEFAULT NULL,
    `type`        varchar(255) DEFAULT NULL,
    `relate_id`   varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

-- ----------------------------
-- Table structure for t_act_process
-- ----------------------------
DROP TABLE IF EXISTS `t_act_process`;
CREATE TABLE `t_act_process`
(
    `id`             varchar(255) NOT NULL,
    `create_by`      varchar(255) DEFAULT NULL,
    `create_time`    datetime     DEFAULT NULL,
    `del_flag`       int(11)      DEFAULT NULL,
    `update_by`      varchar(255) DEFAULT NULL,
    `update_time`    datetime     DEFAULT NULL,
    `category_id`    varchar(255) DEFAULT NULL,
    `deployment_id`  varchar(255) DEFAULT NULL,
    `description`    varchar(255) DEFAULT NULL,
    `diagram_name`   varchar(255) DEFAULT NULL,
    `latest`         bit(1)       DEFAULT NULL,
    `name`           varchar(255) DEFAULT NULL,
    `process_key`    varchar(255) DEFAULT NULL,
    `status`         int(11)      DEFAULT NULL,
    `version`        int(11)      DEFAULT NULL,
    `xml_name`       varchar(255) DEFAULT NULL,
    `business_table` varchar(255) DEFAULT NULL,
    `route_name`     varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


-- ----------------------------
-- Table structure for ACT_EVT_LOG
-- ----------------------------
DROP TABLE IF EXISTS `ACT_EVT_LOG`;
CREATE TABLE `ACT_EVT_LOG`
(
    `LOG_NR_`       bigint(20)   NOT NULL AUTO_INCREMENT,
    `TYPE_`         varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `PROC_DEF_ID_`  varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `PROC_INST_ID_` varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `EXECUTION_ID_` varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `TASK_ID_`      varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `TIME_STAMP_`   timestamp(3) NOT NULL         DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    `USER_ID_`      varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `DATA_`         longblob,
    `LOCK_OWNER_`   varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `LOCK_TIME_`    timestamp(3) NULL             DEFAULT NULL,
    `IS_PROCESSED_` tinyint(4)                    DEFAULT '0',
    PRIMARY KEY (`LOG_NR_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_GE_BYTEARRAY
-- ----------------------------
DROP TABLE IF EXISTS `ACT_GE_BYTEARRAY`;
CREATE TABLE `ACT_GE_BYTEARRAY`
(
    `ID_`            varchar(64) COLLATE utf8_bin NOT NULL,
    `REV_`           int(11)                       DEFAULT NULL,
    `NAME_`          varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `DEPLOYMENT_ID_` varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `BYTES_`         longblob,
    `GENERATED_`     tinyint(4)                    DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY `ACT_FK_BYTEARR_DEPL` (`DEPLOYMENT_ID_`),
    CONSTRAINT `ACT_FK_BYTEARR_DEPL` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `ACT_RE_DEPLOYMENT` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_GE_PROPERTY
-- ----------------------------
DROP TABLE IF EXISTS `ACT_GE_PROPERTY`;
CREATE TABLE `ACT_GE_PROPERTY`
(
    `NAME_`  varchar(64) COLLATE utf8_bin NOT NULL,
    `VALUE_` varchar(300) COLLATE utf8_bin DEFAULT NULL,
    `REV_`   int(11)                       DEFAULT NULL,
    PRIMARY KEY (`NAME_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Records of ACT_GE_PROPERTY
-- ----------------------------
BEGIN;
INSERT INTO `ACT_GE_PROPERTY`
VALUES ('next.dbid', '1', 1);
INSERT INTO `ACT_GE_PROPERTY`
VALUES ('schema.history', 'create(5.22.0.0)', 1);
INSERT INTO `ACT_GE_PROPERTY`
VALUES ('schema.version', '5.22.0.0', 1);
COMMIT;

-- ----------------------------
-- Table structure for ACT_HI_ACTINST
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_ACTINST`;
CREATE TABLE `ACT_HI_ACTINST`
(
    `ID_`                varchar(64) COLLATE utf8_bin  NOT NULL,
    `PROC_DEF_ID_`       varchar(64) COLLATE utf8_bin  NOT NULL,
    `PROC_INST_ID_`      varchar(64) COLLATE utf8_bin  NOT NULL,
    `EXECUTION_ID_`      varchar(64) COLLATE utf8_bin  NOT NULL,
    `ACT_ID_`            varchar(255) COLLATE utf8_bin NOT NULL,
    `TASK_ID_`           varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `CALL_PROC_INST_ID_` varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `ACT_NAME_`          varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `ACT_TYPE_`          varchar(255) COLLATE utf8_bin NOT NULL,
    `ASSIGNEE_`          varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `START_TIME_`        datetime(3)                   NOT NULL,
    `END_TIME_`          datetime(3)                   DEFAULT NULL,
    `DURATION_`          bigint(20)                    DEFAULT NULL,
    `TENANT_ID_`         varchar(255) COLLATE utf8_bin DEFAULT '',
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_HI_ACT_INST_START` (`START_TIME_`),
    KEY `ACT_IDX_HI_ACT_INST_END` (`END_TIME_`),
    KEY `ACT_IDX_HI_ACT_INST_PROCINST` (`PROC_INST_ID_`, `ACT_ID_`),
    KEY `ACT_IDX_HI_ACT_INST_EXEC` (`EXECUTION_ID_`, `ACT_ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_ATTACHMENT
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_ATTACHMENT`;
CREATE TABLE `ACT_HI_ATTACHMENT`
(
    `ID_`           varchar(64) COLLATE utf8_bin NOT NULL,
    `REV_`          int(11)                        DEFAULT NULL,
    `USER_ID_`      varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `NAME_`         varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `DESCRIPTION_`  varchar(4000) COLLATE utf8_bin DEFAULT NULL,
    `TYPE_`         varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `TASK_ID_`      varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `PROC_INST_ID_` varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `URL_`          varchar(4000) COLLATE utf8_bin DEFAULT NULL,
    `CONTENT_ID_`   varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `TIME_`         datetime(3)                    DEFAULT NULL,
    PRIMARY KEY (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_COMMENT
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_COMMENT`;
CREATE TABLE `ACT_HI_COMMENT`
(
    `ID_`           varchar(64) COLLATE utf8_bin NOT NULL,
    `TYPE_`         varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `TIME_`         datetime(3)                  NOT NULL,
    `USER_ID_`      varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `TASK_ID_`      varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `PROC_INST_ID_` varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `ACTION_`       varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `MESSAGE_`      varchar(4000) COLLATE utf8_bin DEFAULT NULL,
    `FULL_MSG_`     longblob,
    PRIMARY KEY (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_DETAIL
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_DETAIL`;
CREATE TABLE `ACT_HI_DETAIL`
(
    `ID_`           varchar(64) COLLATE utf8_bin  NOT NULL,
    `TYPE_`         varchar(255) COLLATE utf8_bin NOT NULL,
    `PROC_INST_ID_` varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `EXECUTION_ID_` varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `TASK_ID_`      varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `ACT_INST_ID_`  varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `NAME_`         varchar(255) COLLATE utf8_bin NOT NULL,
    `VAR_TYPE_`     varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `REV_`          int(11)                        DEFAULT NULL,
    `TIME_`         datetime(3)                   NOT NULL,
    `BYTEARRAY_ID_` varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `DOUBLE_`       double                         DEFAULT NULL,
    `LONG_`         bigint(20)                     DEFAULT NULL,
    `TEXT_`         varchar(4000) COLLATE utf8_bin DEFAULT NULL,
    `TEXT2_`        varchar(4000) COLLATE utf8_bin DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_HI_DETAIL_PROC_INST` (`PROC_INST_ID_`),
    KEY `ACT_IDX_HI_DETAIL_ACT_INST` (`ACT_INST_ID_`),
    KEY `ACT_IDX_HI_DETAIL_TIME` (`TIME_`),
    KEY `ACT_IDX_HI_DETAIL_NAME` (`NAME_`),
    KEY `ACT_IDX_HI_DETAIL_TASK_ID` (`TASK_ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_IDENTITYLINK
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_IDENTITYLINK`;
CREATE TABLE `ACT_HI_IDENTITYLINK`
(
    `ID_`           varchar(64) COLLATE utf8_bin NOT NULL,
    `GROUP_ID_`     varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `TYPE_`         varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `USER_ID_`      varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `TASK_ID_`      varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `PROC_INST_ID_` varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_HI_IDENT_LNK_USER` (`USER_ID_`),
    KEY `ACT_IDX_HI_IDENT_LNK_TASK` (`TASK_ID_`),
    KEY `ACT_IDX_HI_IDENT_LNK_PROCINST` (`PROC_INST_ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_PROCINST
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_PROCINST`;
CREATE TABLE `ACT_HI_PROCINST`
(
    `ID_`                        varchar(64) COLLATE utf8_bin NOT NULL,
    `PROC_INST_ID_`              varchar(64) COLLATE utf8_bin NOT NULL,
    `BUSINESS_KEY_`              varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `PROC_DEF_ID_`               varchar(64) COLLATE utf8_bin NOT NULL,
    `START_TIME_`                datetime(3)                  NOT NULL,
    `END_TIME_`                  datetime(3)                    DEFAULT NULL,
    `DURATION_`                  bigint(20)                     DEFAULT NULL,
    `START_USER_ID_`             varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `START_ACT_ID_`              varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `END_ACT_ID_`                varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `SUPER_PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `DELETE_REASON_`             varchar(4000) COLLATE utf8_bin DEFAULT NULL,
    `TENANT_ID_`                 varchar(255) COLLATE utf8_bin  DEFAULT '',
    `NAME_`                      varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    UNIQUE KEY `PROC_INST_ID_` (`PROC_INST_ID_`),
    KEY `ACT_IDX_HI_PRO_INST_END` (`END_TIME_`),
    KEY `ACT_IDX_HI_PRO_I_BUSKEY` (`BUSINESS_KEY_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_TASKINST
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_TASKINST`;
CREATE TABLE `ACT_HI_TASKINST`
(
    `ID_`             varchar(64) COLLATE utf8_bin NOT NULL,
    `PROC_DEF_ID_`    varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `TASK_DEF_KEY_`   varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `PROC_INST_ID_`   varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `EXECUTION_ID_`   varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `NAME_`           varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `PARENT_TASK_ID_` varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `DESCRIPTION_`    varchar(4000) COLLATE utf8_bin DEFAULT NULL,
    `OWNER_`          varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `ASSIGNEE_`       varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `START_TIME_`     datetime(3)                  NOT NULL,
    `CLAIM_TIME_`     datetime(3)                    DEFAULT NULL,
    `END_TIME_`       datetime(3)                    DEFAULT NULL,
    `DURATION_`       bigint(20)                     DEFAULT NULL,
    `DELETE_REASON_`  varchar(4000) COLLATE utf8_bin DEFAULT NULL,
    `PRIORITY_`       int(11)                        DEFAULT NULL,
    `DUE_DATE_`       datetime(3)                    DEFAULT NULL,
    `FORM_KEY_`       varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `CATEGORY_`       varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `TENANT_ID_`      varchar(255) COLLATE utf8_bin  DEFAULT '',
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_HI_TASK_INST_PROCINST` (`PROC_INST_ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_HI_VARINST
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_VARINST`;
CREATE TABLE `ACT_HI_VARINST`
(
    `ID_`                varchar(64) COLLATE utf8_bin  NOT NULL,
    `PROC_INST_ID_`      varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `EXECUTION_ID_`      varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `TASK_ID_`           varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `NAME_`              varchar(255) COLLATE utf8_bin NOT NULL,
    `VAR_TYPE_`          varchar(100) COLLATE utf8_bin  DEFAULT NULL,
    `REV_`               int(11)                        DEFAULT NULL,
    `BYTEARRAY_ID_`      varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `DOUBLE_`            double                         DEFAULT NULL,
    `LONG_`              bigint(20)                     DEFAULT NULL,
    `TEXT_`              varchar(4000) COLLATE utf8_bin DEFAULT NULL,
    `TEXT2_`             varchar(4000) COLLATE utf8_bin DEFAULT NULL,
    `CREATE_TIME_`       datetime(3)                    DEFAULT NULL,
    `LAST_UPDATED_TIME_` datetime(3)                    DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_HI_PROCVAR_PROC_INST` (`PROC_INST_ID_`),
    KEY `ACT_IDX_HI_PROCVAR_NAME_TYPE` (`NAME_`, `VAR_TYPE_`),
    KEY `ACT_IDX_HI_PROCVAR_TASK_ID` (`TASK_ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_ID_GROUP
-- ----------------------------
DROP TABLE IF EXISTS `ACT_ID_GROUP`;
CREATE TABLE `ACT_ID_GROUP`
(
    `ID_`   varchar(64) COLLATE utf8_bin NOT NULL,
    `REV_`  int(11)                       DEFAULT NULL,
    `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
    PRIMARY KEY (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_ID_INFO
-- ----------------------------
DROP TABLE IF EXISTS `ACT_ID_INFO`;
CREATE TABLE `ACT_ID_INFO`
(
    `ID_`        varchar(64) COLLATE utf8_bin NOT NULL,
    `REV_`       int(11)                       DEFAULT NULL,
    `USER_ID_`   varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `TYPE_`      varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `KEY_`       varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `VALUE_`     varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `PASSWORD_`  longblob,
    `PARENT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
    PRIMARY KEY (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_ID_MEMBERSHIP
-- ----------------------------
DROP TABLE IF EXISTS `ACT_ID_MEMBERSHIP`;
CREATE TABLE `ACT_ID_MEMBERSHIP`
(
    `USER_ID_`  varchar(64) COLLATE utf8_bin NOT NULL,
    `GROUP_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
    PRIMARY KEY (`USER_ID_`, `GROUP_ID_`),
    KEY `ACT_FK_MEMB_GROUP` (`GROUP_ID_`),
    CONSTRAINT `ACT_FK_MEMB_GROUP` FOREIGN KEY (`GROUP_ID_`) REFERENCES `ACT_ID_GROUP` (`ID_`),
    CONSTRAINT `ACT_FK_MEMB_USER` FOREIGN KEY (`USER_ID_`) REFERENCES `ACT_ID_USER` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_ID_USER
-- ----------------------------
DROP TABLE IF EXISTS `ACT_ID_USER`;
CREATE TABLE `ACT_ID_USER`
(
    `ID_`         varchar(64) COLLATE utf8_bin NOT NULL,
    `REV_`        int(11)                       DEFAULT NULL,
    `FIRST_`      varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `LAST_`       varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `EMAIL_`      varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `PWD_`        varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `PICTURE_ID_` varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    PRIMARY KEY (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_PROCDEF_INFO
-- ----------------------------
DROP TABLE IF EXISTS `ACT_PROCDEF_INFO`;
CREATE TABLE `ACT_PROCDEF_INFO`
(
    `ID_`           varchar(64) COLLATE utf8_bin NOT NULL,
    `PROC_DEF_ID_`  varchar(64) COLLATE utf8_bin NOT NULL,
    `REV_`          int(11)                      DEFAULT NULL,
    `INFO_JSON_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    UNIQUE KEY `ACT_UNIQ_INFO_PROCDEF` (`PROC_DEF_ID_`),
    KEY `ACT_IDX_INFO_PROCDEF` (`PROC_DEF_ID_`),
    KEY `ACT_FK_INFO_JSON_BA` (`INFO_JSON_ID_`),
    CONSTRAINT `ACT_FK_INFO_JSON_BA` FOREIGN KEY (`INFO_JSON_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
    CONSTRAINT `ACT_FK_INFO_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_RE_DEPLOYMENT
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RE_DEPLOYMENT`;
CREATE TABLE `ACT_RE_DEPLOYMENT`
(
    `ID_`          varchar(64) COLLATE utf8_bin NOT NULL,
    `NAME_`        varchar(255) COLLATE utf8_bin     DEFAULT NULL,
    `CATEGORY_`    varchar(255) COLLATE utf8_bin     DEFAULT NULL,
    `TENANT_ID_`   varchar(255) COLLATE utf8_bin     DEFAULT '',
    `DEPLOY_TIME_` timestamp(3)                 NULL DEFAULT NULL,
    PRIMARY KEY (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_RE_MODEL
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RE_MODEL`;
CREATE TABLE `ACT_RE_MODEL`
(
    `ID_`                           varchar(64) COLLATE utf8_bin NOT NULL,
    `REV_`                          int(11)                           DEFAULT NULL,
    `NAME_`                         varchar(255) COLLATE utf8_bin     DEFAULT NULL,
    `KEY_`                          varchar(255) COLLATE utf8_bin     DEFAULT NULL,
    `CATEGORY_`                     varchar(255) COLLATE utf8_bin     DEFAULT NULL,
    `CREATE_TIME_`                  timestamp(3)                 NULL DEFAULT NULL,
    `LAST_UPDATE_TIME_`             timestamp(3)                 NULL DEFAULT NULL,
    `VERSION_`                      int(11)                           DEFAULT NULL,
    `META_INFO_`                    varchar(4000) COLLATE utf8_bin    DEFAULT NULL,
    `DEPLOYMENT_ID_`                varchar(64) COLLATE utf8_bin      DEFAULT NULL,
    `EDITOR_SOURCE_VALUE_ID_`       varchar(64) COLLATE utf8_bin      DEFAULT NULL,
    `EDITOR_SOURCE_EXTRA_VALUE_ID_` varchar(64) COLLATE utf8_bin      DEFAULT NULL,
    `TENANT_ID_`                    varchar(255) COLLATE utf8_bin     DEFAULT '',
    PRIMARY KEY (`ID_`),
    KEY `ACT_FK_MODEL_SOURCE` (`EDITOR_SOURCE_VALUE_ID_`),
    KEY `ACT_FK_MODEL_SOURCE_EXTRA` (`EDITOR_SOURCE_EXTRA_VALUE_ID_`),
    KEY `ACT_FK_MODEL_DEPLOYMENT` (`DEPLOYMENT_ID_`),
    CONSTRAINT `ACT_FK_MODEL_DEPLOYMENT` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `ACT_RE_DEPLOYMENT` (`ID_`),
    CONSTRAINT `ACT_FK_MODEL_SOURCE` FOREIGN KEY (`EDITOR_SOURCE_VALUE_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
    CONSTRAINT `ACT_FK_MODEL_SOURCE_EXTRA` FOREIGN KEY (`EDITOR_SOURCE_EXTRA_VALUE_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_RE_PROCDEF
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RE_PROCDEF`;
CREATE TABLE `ACT_RE_PROCDEF`
(
    `ID_`                     varchar(64) COLLATE utf8_bin  NOT NULL,
    `REV_`                    int(11)                        DEFAULT NULL,
    `CATEGORY_`               varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `NAME_`                   varchar(255) COLLATE utf8_bin  DEFAULT NULL,
    `KEY_`                    varchar(255) COLLATE utf8_bin NOT NULL,
    `VERSION_`                int(11)                       NOT NULL,
    `DEPLOYMENT_ID_`          varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `RESOURCE_NAME_`          varchar(4000) COLLATE utf8_bin DEFAULT NULL,
    `DGRM_RESOURCE_NAME_`     varchar(4000) COLLATE utf8_bin DEFAULT NULL,
    `DESCRIPTION_`            varchar(4000) COLLATE utf8_bin DEFAULT NULL,
    `HAS_START_FORM_KEY_`     tinyint(4)                     DEFAULT NULL,
    `HAS_GRAPHICAL_NOTATION_` tinyint(4)                     DEFAULT NULL,
    `SUSPENSION_STATE_`       int(11)                        DEFAULT NULL,
    `TENANT_ID_`              varchar(255) COLLATE utf8_bin  DEFAULT '',
    PRIMARY KEY (`ID_`),
    UNIQUE KEY `ACT_UNIQ_PROCDEF` (`KEY_`, `VERSION_`, `TENANT_ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_EVENT_SUBSCR
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_EVENT_SUBSCR`;
CREATE TABLE `ACT_RU_EVENT_SUBSCR`
(
    `ID_`            varchar(64) COLLATE utf8_bin  NOT NULL,
    `REV_`           int(11)                                DEFAULT NULL,
    `EVENT_TYPE_`    varchar(255) COLLATE utf8_bin NOT NULL,
    `EVENT_NAME_`    varchar(255) COLLATE utf8_bin          DEFAULT NULL,
    `EXECUTION_ID_`  varchar(64) COLLATE utf8_bin           DEFAULT NULL,
    `PROC_INST_ID_`  varchar(64) COLLATE utf8_bin           DEFAULT NULL,
    `ACTIVITY_ID_`   varchar(64) COLLATE utf8_bin           DEFAULT NULL,
    `CONFIGURATION_` varchar(255) COLLATE utf8_bin          DEFAULT NULL,
    `CREATED_`       timestamp(3)                  NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `PROC_DEF_ID_`   varchar(64) COLLATE utf8_bin           DEFAULT NULL,
    `TENANT_ID_`     varchar(255) COLLATE utf8_bin          DEFAULT '',
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_EVENT_SUBSCR_CONFIG_` (`CONFIGURATION_`),
    KEY `ACT_FK_EVENT_EXEC` (`EXECUTION_ID_`),
    CONSTRAINT `ACT_FK_EVENT_EXEC` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_EXECUTION
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_EXECUTION`;
CREATE TABLE `ACT_RU_EXECUTION`
(
    `ID_`               varchar(64) COLLATE utf8_bin NOT NULL,
    `REV_`              int(11)                           DEFAULT NULL,
    `PROC_INST_ID_`     varchar(64) COLLATE utf8_bin      DEFAULT NULL,
    `BUSINESS_KEY_`     varchar(255) COLLATE utf8_bin     DEFAULT NULL,
    `PARENT_ID_`        varchar(64) COLLATE utf8_bin      DEFAULT NULL,
    `PROC_DEF_ID_`      varchar(64) COLLATE utf8_bin      DEFAULT NULL,
    `SUPER_EXEC_`       varchar(64) COLLATE utf8_bin      DEFAULT NULL,
    `ACT_ID_`           varchar(255) COLLATE utf8_bin     DEFAULT NULL,
    `IS_ACTIVE_`        tinyint(4)                        DEFAULT NULL,
    `IS_CONCURRENT_`    tinyint(4)                        DEFAULT NULL,
    `IS_SCOPE_`         tinyint(4)                        DEFAULT NULL,
    `IS_EVENT_SCOPE_`   tinyint(4)                        DEFAULT NULL,
    `SUSPENSION_STATE_` int(11)                           DEFAULT NULL,
    `CACHED_ENT_STATE_` int(11)                           DEFAULT NULL,
    `TENANT_ID_`        varchar(255) COLLATE utf8_bin     DEFAULT '',
    `NAME_`             varchar(255) COLLATE utf8_bin     DEFAULT NULL,
    `LOCK_TIME_`        timestamp(3)                 NULL DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_EXEC_BUSKEY` (`BUSINESS_KEY_`),
    KEY `ACT_FK_EXE_PROCINST` (`PROC_INST_ID_`),
    KEY `ACT_FK_EXE_PARENT` (`PARENT_ID_`),
    KEY `ACT_FK_EXE_SUPER` (`SUPER_EXEC_`),
    KEY `ACT_FK_EXE_PROCDEF` (`PROC_DEF_ID_`),
    CONSTRAINT `ACT_FK_EXE_PARENT` FOREIGN KEY (`PARENT_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
    CONSTRAINT `ACT_FK_EXE_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`),
    CONSTRAINT `ACT_FK_EXE_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `ACT_FK_EXE_SUPER` FOREIGN KEY (`SUPER_EXEC_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_IDENTITYLINK
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_IDENTITYLINK`;
CREATE TABLE `ACT_RU_IDENTITYLINK`
(
    `ID_`           varchar(64) COLLATE utf8_bin NOT NULL,
    `REV_`          int(11)                       DEFAULT NULL,
    `GROUP_ID_`     varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `TYPE_`         varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `USER_ID_`      varchar(255) COLLATE utf8_bin DEFAULT NULL,
    `TASK_ID_`      varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `PROC_INST_ID_` varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    `PROC_DEF_ID_`  varchar(64) COLLATE utf8_bin  DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_IDENT_LNK_USER` (`USER_ID_`),
    KEY `ACT_IDX_IDENT_LNK_GROUP` (`GROUP_ID_`),
    KEY `ACT_IDX_ATHRZ_PROCEDEF` (`PROC_DEF_ID_`),
    KEY `ACT_FK_TSKASS_TASK` (`TASK_ID_`),
    KEY `ACT_FK_IDL_PROCINST` (`PROC_INST_ID_`),
    CONSTRAINT `ACT_FK_ATHRZ_PROCEDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`),
    CONSTRAINT `ACT_FK_IDL_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
    CONSTRAINT `ACT_FK_TSKASS_TASK` FOREIGN KEY (`TASK_ID_`) REFERENCES `ACT_RU_TASK` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_JOB
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_JOB`;
CREATE TABLE `ACT_RU_JOB`
(
    `ID_`                  varchar(64) COLLATE utf8_bin  NOT NULL,
    `REV_`                 int(11)                            DEFAULT NULL,
    `TYPE_`                varchar(255) COLLATE utf8_bin NOT NULL,
    `LOCK_EXP_TIME_`       timestamp(3)                  NULL DEFAULT NULL,
    `LOCK_OWNER_`          varchar(255) COLLATE utf8_bin      DEFAULT NULL,
    `EXCLUSIVE_`           tinyint(1)                         DEFAULT NULL,
    `EXECUTION_ID_`        varchar(64) COLLATE utf8_bin       DEFAULT NULL,
    `PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8_bin       DEFAULT NULL,
    `PROC_DEF_ID_`         varchar(64) COLLATE utf8_bin       DEFAULT NULL,
    `RETRIES_`             int(11)                            DEFAULT NULL,
    `EXCEPTION_STACK_ID_`  varchar(64) COLLATE utf8_bin       DEFAULT NULL,
    `EXCEPTION_MSG_`       varchar(4000) COLLATE utf8_bin     DEFAULT NULL,
    `DUEDATE_`             timestamp(3)                  NULL DEFAULT NULL,
    `REPEAT_`              varchar(255) COLLATE utf8_bin      DEFAULT NULL,
    `HANDLER_TYPE_`        varchar(255) COLLATE utf8_bin      DEFAULT NULL,
    `HANDLER_CFG_`         varchar(4000) COLLATE utf8_bin     DEFAULT NULL,
    `TENANT_ID_`           varchar(255) COLLATE utf8_bin      DEFAULT '',
    PRIMARY KEY (`ID_`),
    KEY `ACT_FK_JOB_EXCEPTION` (`EXCEPTION_STACK_ID_`),
    CONSTRAINT `ACT_FK_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_TASK
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_TASK`;
CREATE TABLE `ACT_RU_TASK`
(
    `ID_`               varchar(64) COLLATE utf8_bin NOT NULL,
    `REV_`              int(11)                           DEFAULT NULL,
    `EXECUTION_ID_`     varchar(64) COLLATE utf8_bin      DEFAULT NULL,
    `PROC_INST_ID_`     varchar(64) COLLATE utf8_bin      DEFAULT NULL,
    `PROC_DEF_ID_`      varchar(64) COLLATE utf8_bin      DEFAULT NULL,
    `NAME_`             varchar(255) COLLATE utf8_bin     DEFAULT NULL,
    `PARENT_TASK_ID_`   varchar(64) COLLATE utf8_bin      DEFAULT NULL,
    `DESCRIPTION_`      varchar(4000) COLLATE utf8_bin    DEFAULT NULL,
    `TASK_DEF_KEY_`     varchar(255) COLLATE utf8_bin     DEFAULT NULL,
    `OWNER_`            varchar(255) COLLATE utf8_bin     DEFAULT NULL,
    `ASSIGNEE_`         varchar(255) COLLATE utf8_bin     DEFAULT NULL,
    `DELEGATION_`       varchar(64) COLLATE utf8_bin      DEFAULT NULL,
    `PRIORITY_`         int(11)                           DEFAULT NULL,
    `CREATE_TIME_`      timestamp(3)                 NULL DEFAULT NULL,
    `DUE_DATE_`         datetime(3)                       DEFAULT NULL,
    `CATEGORY_`         varchar(255) COLLATE utf8_bin     DEFAULT NULL,
    `SUSPENSION_STATE_` int(11)                           DEFAULT NULL,
    `TENANT_ID_`        varchar(255) COLLATE utf8_bin     DEFAULT '',
    `FORM_KEY_`         varchar(255) COLLATE utf8_bin     DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_TASK_CREATE` (`CREATE_TIME_`),
    KEY `ACT_FK_TASK_EXE` (`EXECUTION_ID_`),
    KEY `ACT_FK_TASK_PROCINST` (`PROC_INST_ID_`),
    KEY `ACT_FK_TASK_PROCDEF` (`PROC_DEF_ID_`),
    CONSTRAINT `ACT_FK_TASK_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
    CONSTRAINT `ACT_FK_TASK_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`),
    CONSTRAINT `ACT_FK_TASK_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;

-- ----------------------------
-- Table structure for ACT_RU_VARIABLE
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_VARIABLE`;
CREATE TABLE `ACT_RU_VARIABLE`
(
    `ID_`           varchar(64) COLLATE utf8_bin  NOT NULL,
    `REV_`          int(11)                        DEFAULT NULL,
    `TYPE_`         varchar(255) COLLATE utf8_bin NOT NULL,
    `NAME_`         varchar(255) COLLATE utf8_bin NOT NULL,
    `EXECUTION_ID_` varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `PROC_INST_ID_` varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `TASK_ID_`      varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `BYTEARRAY_ID_` varchar(64) COLLATE utf8_bin   DEFAULT NULL,
    `DOUBLE_`       double                         DEFAULT NULL,
    `LONG_`         bigint(20)                     DEFAULT NULL,
    `TEXT_`         varchar(4000) COLLATE utf8_bin DEFAULT NULL,
    `TEXT2_`        varchar(4000) COLLATE utf8_bin DEFAULT NULL,
    PRIMARY KEY (`ID_`),
    KEY `ACT_IDX_VARIABLE_TASK_ID` (`TASK_ID_`),
    KEY `ACT_FK_VAR_EXE` (`EXECUTION_ID_`),
    KEY `ACT_FK_VAR_PROCINST` (`PROC_INST_ID_`),
    KEY `ACT_FK_VAR_BYTEARRAY` (`BYTEARRAY_ID_`),
    CONSTRAINT `ACT_FK_VAR_BYTEARRAY` FOREIGN KEY (`BYTEARRAY_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
    CONSTRAINT `ACT_FK_VAR_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
    CONSTRAINT `ACT_FK_VAR_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COLLATE = utf8_bin;



SET FOREIGN_KEY_CHECKS = 1;