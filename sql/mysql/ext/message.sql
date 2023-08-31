SET NAMES utf8mb4;
SET
FOREIGN_KEY_CHECKS = 0;


-- ----------------------------
-- Table structure for base_message
-- ----------------------------
DROP TABLE IF EXISTS `base_message`;
CREATE TABLE `base_message`
(
    `F_Id`               varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '自然主键',
    `F_Type`             int                                                           DEFAULT NULL COMMENT '类别',
    `F_Title`            varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
    `F_BodyText`         longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '正文',
    `F_PriorityLevel`    int                                                           DEFAULT NULL COMMENT '优先',
    `F_ToUserIds`        longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '收件用户',
    `F_IsRead`           int                                                           DEFAULT NULL COMMENT '是否阅读',
    `F_Description`      text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '描述',
    `F_SortCode`         bigint                                                        DEFAULT NULL COMMENT '排序',
    `F_EnabledMark`      int                                                           DEFAULT NULL COMMENT '有效标志',
    `F_CreatorTime`      datetime                                                      DEFAULT NULL COMMENT '创建时间',
    `F_CreatorUserId`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '创建用户',
    `F_LastModifyTime`   datetime                                                      DEFAULT NULL COMMENT '修改时间',
    `F_LastModifyUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '修改用户',
    `F_DeleteMark`       int                                                           DEFAULT NULL COMMENT '删除标志',
    `F_DeleteTime`       datetime                                                      DEFAULT NULL COMMENT '删除时间',
    `F_DeleteUserId`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '删除用户',
    `F_Files`            longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '附件',
    `F_TenantId`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '租户id',
    `F_FlowType`         int                                                           DEFAULT NULL COMMENT '流程类型',
    `F_CoverImage`       varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面图片',
    `F_ExpirationTime`   datetime                                                      DEFAULT NULL COMMENT '过期时间',
    `F_Category`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '分类 1-公告 2-通知',
    `F_RemindCategory`   int                                                           DEFAULT NULL COMMENT '提醒方式 1-站内信 2-自定义 3-不通知',
    `F_SendConfigId`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '发送配置',
    `F_Excerpt`          text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '摘要',
    `F_DefaultTitle`     varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息模板标题',
    PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息实例';

-- ----------------------------
-- Records of base_message
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for base_message_account_config
-- ----------------------------
DROP TABLE IF EXISTS `base_message_account_config`;
CREATE TABLE `base_message_account_config`
(
    `F_Id`               varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键id',
    `F_Type`             varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '配置类型',
    `F_FullName`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '名称',
    `F_EnCode`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '编码',
    `F_AddressorName`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '发件人昵称',
    `F_SmtpServer`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT 'SMTP服务器',
    `F_SmtpPort`         int                                                           DEFAULT NULL COMMENT 'SMTP端口',
    `F_SslLink`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT 'SSL安全链接',
    `F_SmtpUser`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT 'SMTP用户',
    `F_SmtpPassword`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT 'SMTP密码',
    `F_Channel`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '渠道',
    `F_SmsSignature`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '短信签名',
    `F_AppId`            varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '应用ID',
    `F_AppSecret`        varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '应用Secret',
    `F_EndPoint`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT 'EndPoint（阿里云）',
    `F_SdkAppId`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT 'SDK AppID（腾讯云）',
    `F_AppKey`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT 'AppKey（腾讯云）',
    `F_ZoneName`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '地域域名（腾讯云）',
    `F_ZoneParam`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '地域参数（腾讯云）',
    `F_EnterpriseId`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '企业id',
    `F_AgentId`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT 'AgentID',
    `F_WebhookType`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT 'WebHook类型',
    `F_WebhookAddress`   varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'WebHook地址',
    `F_ApproveType`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '认证类型',
    `F_Bearer`           varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'Bearer令牌',
    `F_UserName`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '用户名（基本认证）',
    `F_Password`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '密码（基本认证）',
    `F_SortCode`         int                                                           DEFAULT NULL COMMENT '排序',
    `F_EnabledMark`      int                                                           DEFAULT NULL COMMENT '状态',
    `F_Description`      text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '说明',
    `F_CreatorTime`      datetime                                                      DEFAULT NULL COMMENT '创建时间',
    `F_CreatorUserId`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '创建用户',
    `F_LastModifyTime`   datetime                                                      DEFAULT NULL COMMENT '修改时间',
    `F_LastModifyUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '修改用户',
    `F_DeleteMark`       int                                                           DEFAULT NULL COMMENT '删除标志',
    `F_DeleteTime`       datetime                                                      DEFAULT NULL COMMENT '删除时间',
    `F_DeleteUserId`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '删除用户',
    `F_TenantId`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '租户id',
    PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账号配置表';

-- ----------------------------
-- Records of base_message_account_config
-- ----------------------------
BEGIN;
INSERT INTO `base_message_account_config` (`F_Id`, `F_Type`, `F_FullName`, `F_EnCode`, `F_AddressorName`,
                                           `F_SmtpServer`, `F_SmtpPort`, `F_SslLink`, `F_SmtpUser`, `F_SmtpPassword`,
                                           `F_Channel`, `F_SmsSignature`, `F_AppId`, `F_AppSecret`, `F_EndPoint`,
                                           `F_SdkAppId`, `F_AppKey`, `F_ZoneName`, `F_ZoneParam`, `F_EnterpriseId`,
                                           `F_AgentId`, `F_WebhookType`, `F_WebhookAddress`, `F_ApproveType`,
                                           `F_Bearer`, `F_UserName`, `F_Password`, `F_SortCode`, `F_EnabledMark`,
                                           `F_Description`, `F_CreatorTime`, `F_CreatorUserId`, `F_LastModifyTime`,
                                           `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`, `F_DeleteUserId`,
                                           `F_TenantId`)
VALUES ('400306899347595525', '2', '邮箱', '01', '测试', '323213', 25, '0', '321', '3123', NULL, NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 0, 1, '', '2023-02-28 17:36:44',
        '349057407209541', NULL, NULL, NULL, NULL, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for base_message_data_type
-- ----------------------------
DROP TABLE IF EXISTS `base_message_data_type`;
CREATE TABLE `base_message_data_type`
(
    `F_Id`               varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键id',
    `F_Type`             varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数据类型（1：消息类型，2：渠道，3：webhook类型，4：消息来源）',
    `F_FullName`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数据名称',
    `F_EnCode`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '数据编码（为防止与系统后续更新的功能的数据编码冲突，客户自定义添加的功能的数据编码请以ZDY开头。例如：ZDY1）',
    `F_CreatorTime`      datetime                                                     DEFAULT NULL COMMENT '创建时间',
    `F_CreatorUserId`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人员',
    `F_LastModifyTime`   datetime                                                     DEFAULT NULL COMMENT '修改时间',
    `F_LastModifyUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人员',
    `F_DeleteMark`       int                                                          DEFAULT NULL COMMENT '删除标志',
    `F_DeleteTime`       datetime                                                     DEFAULT NULL COMMENT '删除时间',
    `F_DeleteUserId`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除用户',
    `F_EnabledMark`      int                                                          DEFAULT NULL COMMENT '状态',
    `F_TenantId`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
    PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息中心模块数据类型表';

-- ----------------------------
-- Records of base_message_data_type
-- ----------------------------
BEGIN;
INSERT INTO `base_message_data_type` (`F_Id`, `F_Type`, `F_FullName`, `F_EnCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                      `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                      `F_DeleteUserId`, `F_EnabledMark`, `F_TenantId`)
VALUES ('329908453034176901', '1', '站内信', '1', '2022-08-18 11:18:26', 'admin', NULL, '', NULL, NULL, NULL, NULL,
        NULL);
INSERT INTO `base_message_data_type` (`F_Id`, `F_Type`, `F_FullName`, `F_EnCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                      `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                      `F_DeleteUserId`, `F_EnabledMark`, `F_TenantId`)
VALUES ('329908525486584197', '1', '邮件', '2', '2022-08-18 11:18:43', 'admin', NULL, '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_data_type` (`F_Id`, `F_Type`, `F_FullName`, `F_EnCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                      `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                      `F_DeleteUserId`, `F_EnabledMark`, `F_TenantId`)
VALUES ('329908596168995205', '1', '短信', '3', '2022-08-18 11:19:00', 'admin', NULL, '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_data_type` (`F_Id`, `F_Type`, `F_FullName`, `F_EnCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                      `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                      `F_DeleteUserId`, `F_EnabledMark`, `F_TenantId`)
VALUES ('329908643526881669', '1', '钉钉', '4', '2022-08-18 11:19:12', 'admin', NULL, '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_data_type` (`F_Id`, `F_Type`, `F_FullName`, `F_EnCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                      `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                      `F_DeleteUserId`, `F_EnabledMark`, `F_TenantId`)
VALUES ('329908713206854021', '1', '企业微信', '5', '2022-08-18 11:19:28', 'admin', NULL, '', NULL, NULL, NULL, NULL,
        NULL);
INSERT INTO `base_message_data_type` (`F_Id`, `F_Type`, `F_FullName`, `F_EnCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                      `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                      `F_DeleteUserId`, `F_EnabledMark`, `F_TenantId`)
VALUES ('329908779447497093', '1', 'webhook', '6', '2022-08-18 11:19:44', 'admin', NULL, '', NULL, NULL, NULL, NULL,
        NULL);
INSERT INTO `base_message_data_type` (`F_Id`, `F_Type`, `F_FullName`, `F_EnCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                      `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                      `F_DeleteUserId`, `F_EnabledMark`, `F_TenantId`)
VALUES ('329908942480093573', '2', '阿里云', '1', '2022-08-18 11:20:23', 'admin', NULL, '', NULL, NULL, NULL, NULL,
        NULL);
INSERT INTO `base_message_data_type` (`F_Id`, `F_Type`, `F_FullName`, `F_EnCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                      `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                      `F_DeleteUserId`, `F_EnabledMark`, `F_TenantId`)
VALUES ('329909007839932805', '2', '腾讯云', '2', '2022-08-18 11:20:38', 'admin', NULL, '', NULL, NULL, NULL, NULL,
        NULL);
INSERT INTO `base_message_data_type` (`F_Id`, `F_Type`, `F_FullName`, `F_EnCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                      `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                      `F_DeleteUserId`, `F_EnabledMark`, `F_TenantId`)
VALUES ('329909358081094021', '3', '企业微信', '2', '2022-08-18 11:22:02', 'admin', NULL, '', NULL, NULL, NULL, NULL,
        NULL);
INSERT INTO `base_message_data_type` (`F_Id`, `F_Type`, `F_FullName`, `F_EnCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                      `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                      `F_DeleteUserId`, `F_EnabledMark`, `F_TenantId`)
VALUES ('329909393992724869', '3', '钉钉', '1', '2022-08-18 11:22:10', 'admin', NULL, '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_data_type` (`F_Id`, `F_Type`, `F_FullName`, `F_EnCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                      `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                      `F_DeleteUserId`, `F_EnabledMark`, `F_TenantId`)
VALUES ('329909484858126725', '4', '流程', '2', '2022-08-18 11:22:32', 'admin', NULL, '', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_data_type` (`F_Id`, `F_Type`, `F_FullName`, `F_EnCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                      `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                      `F_DeleteUserId`, `F_EnabledMark`, `F_TenantId`)
VALUES ('329909484858126728', '4', '系统', '3', '2022-08-18 11:22:32', '347042038562885', NULL, '', NULL, NULL, NULL,
        NULL, NULL);
INSERT INTO `base_message_data_type` (`F_Id`, `F_Type`, `F_FullName`, `F_EnCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                      `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                      `F_DeleteUserId`, `F_EnabledMark`, `F_TenantId`)
VALUES ('351622834386141957', '1', '微信公众号', '7', '2022-10-17 09:23:38', 'admin', NULL, '', NULL, NULL, NULL, NULL,
        NULL);
INSERT INTO `base_message_data_type` (`F_Id`, `F_Type`, `F_FullName`, `F_EnCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                      `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                      `F_DeleteUserId`, `F_EnabledMark`, `F_TenantId`)
VALUES ('351622834386148897', '4', '日程', '4', '2022-10-17 09:23:38', '347042038562885', NULL, NULL, NULL, NULL, NULL,
        NULL, NULL);
INSERT INTO `base_message_data_type` (`F_Id`, `F_Type`, `F_FullName`, `F_EnCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                      `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                      `F_DeleteUserId`, `F_EnabledMark`, `F_TenantId`)
VALUES ('401025792072495493', '4', '公告', '1', '2023-03-02 17:12:34', '349057407209541', NULL, NULL, NULL, NULL, NULL,
        NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for base_message_monitor
-- ----------------------------
DROP TABLE IF EXISTS `base_message_monitor`;
CREATE TABLE `base_message_monitor`
(
    `F_Id`                varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键id',
    `F_AccountId`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '账号id',
    `F_AccountName`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '账号名称',
    `F_AccountCode`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '账号编码',
    `F_MessageType`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '消息类型',
    `F_MessageSource`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '消息来源',
    `F_SendTime`          datetime                                                      DEFAULT NULL COMMENT '发送时间',
    `F_MessageTemplateId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '消息模板id',
    `F_Title`             varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标题',
    `F_ReceiveUser`       text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '接收人',
    `F_Content`           longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '内容',
    `F_CreatorTime`       datetime                                                      DEFAULT NULL COMMENT '创建时间',
    `F_CreatorUserId`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '创建人员',
    `F_LastModifyTime`    datetime                                                      DEFAULT NULL COMMENT '修改时间',
    `F_LastModifyUserId`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '修改人员',
    `F_DeleteMark`        int                                                           DEFAULT NULL COMMENT '删除标志',
    `F_DeleteTime`        datetime                                                      DEFAULT NULL COMMENT '删除时间',
    `F_DeleteUserId`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '删除用户',
    `F_EnabledMark`       int                                                           DEFAULT NULL COMMENT '状态',
    `F_TenantId`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '租户id',
    PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息监控表';

-- ----------------------------
-- Records of base_message_monitor
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for base_message_send_config
-- ----------------------------
DROP TABLE IF EXISTS `base_message_send_config`;
CREATE TABLE `base_message_send_config`
(
    `F_Id`               varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键id',
    `F_FullName`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '名称',
    `F_EnCode`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '编码',
    `F_TemplateType`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模板类型',
    `F_MessageSource`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息来源',
    `F_SortCode`         int                                                          DEFAULT NULL COMMENT '排序',
    `F_EnabledMark`      int                                                          DEFAULT NULL COMMENT '状态',
    `F_Description`      text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '说明',
    `F_UsedId`           longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '被引用id',
    `F_CreatorTime`      datetime                                                     DEFAULT NULL COMMENT '创建时间',
    `F_CreatorUserId`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人员',
    `F_LastModifyTime`   datetime                                                     DEFAULT NULL COMMENT '修改时间',
    `F_LastModifyUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人员',
    `F_DeleteMark`       int                                                          DEFAULT NULL COMMENT '删除标志',
    `F_DeleteTime`       datetime                                                     DEFAULT NULL COMMENT '删除时间',
    `F_DeleteUserId`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除用户',
    `F_TenantId`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
    PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息发送配置表';

-- ----------------------------
-- Records of base_message_send_config
-- ----------------------------
BEGIN;
INSERT INTO `base_message_send_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                        `F_SortCode`, `F_EnabledMark`, `F_Description`, `F_UsedId`, `F_CreatorTime`,
                                        `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`,
                                        `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('333972597509210693', '待办', 'PZXTLC001', '1', '2', 0, 1, '', NULL, '2022-08-29 16:27:54', '349057407209541',
        NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                        `F_SortCode`, `F_EnabledMark`, `F_Description`, `F_UsedId`, `F_CreatorTime`,
                                        `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`,
                                        `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('333972930574697029', '同意', 'PZXTLC002', '1', '2', 0, 1, '', NULL, '2022-08-29 16:29:14', '349057407209541',
        NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                        `F_SortCode`, `F_EnabledMark`, `F_Description`, `F_UsedId`, `F_CreatorTime`,
                                        `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`,
                                        `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('333973394783486533', '退回', 'PZXTLC003', '1', '2', 0, 1, '', NULL, '2022-08-29 16:31:04', '349057407209541',
        NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                        `F_SortCode`, `F_EnabledMark`, `F_Description`, `F_UsedId`, `F_CreatorTime`,
                                        `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`,
                                        `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('333973567337153093', '催办', 'PZXTLC004', '1', '2', 0, 1, '', NULL, '2022-08-29 16:31:45', '349057407209541',
        NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                        `F_SortCode`, `F_EnabledMark`, `F_Description`, `F_UsedId`, `F_CreatorTime`,
                                        `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`,
                                        `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('333973688447681093', '指派', 'PZXTLC005', '1', '2', 0, 1, '', NULL, '2022-08-29 16:32:14', '349057407209541',
        NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                        `F_SortCode`, `F_EnabledMark`, `F_Description`, `F_UsedId`, `F_CreatorTime`,
                                        `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`,
                                        `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('333973810141217349', '转办', 'PZXTLC006', '1', '2', 0, 1, '', NULL, '2022-08-29 16:32:43', '349057407209541',
        NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                        `F_SortCode`, `F_EnabledMark`, `F_Description`, `F_UsedId`, `F_CreatorTime`,
                                        `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`,
                                        `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('333974038072279621', '抄送', 'PZXTLC007', '1', '2', 0, 1, '', NULL, '2022-08-29 16:33:38', '349057407209541',
        NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                        `F_SortCode`, `F_EnabledMark`, `F_Description`, `F_UsedId`, `F_CreatorTime`,
                                        `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`,
                                        `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('333974172122235461', '提醒', 'PZXTLC008', '1', '2', 0, 1, '', NULL, '2022-08-29 16:34:10', '349057407209541',
        NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                        `F_SortCode`, `F_EnabledMark`, `F_Description`, `F_UsedId`, `F_CreatorTime`,
                                        `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`,
                                        `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('333974395192099397', '超时', 'PZXTLC009', '1', '2', 0, 1, '', NULL, '2022-08-29 16:35:03', '349057407209541',
        NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                        `F_SortCode`, `F_EnabledMark`, `F_Description`, `F_UsedId`, `F_CreatorTime`,
                                        `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`,
                                        `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('333974499412165189', '结束', 'PZXTLC010', '1', '2', 0, 1, '', NULL, '2022-08-29 16:35:28', '349057407209541',
        NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                        `F_SortCode`, `F_EnabledMark`, `F_Description`, `F_UsedId`, `F_CreatorTime`,
                                        `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`,
                                        `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('333974605049905733', '发起子流程', 'PZXTLC011', '1', '2', 0, 1, '', NULL, '2022-08-29 16:35:53',
        '349057407209541', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                        `F_SortCode`, `F_EnabledMark`, `F_Description`, `F_UsedId`, `F_CreatorTime`,
                                        `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`,
                                        `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('390828003917048133', '日程邀请提醒', 'PZXTRC001', '1', '4', 0, 1, '', NULL, '2023-02-02 13:50:59',
        '349057407209541', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                        `F_SortCode`, `F_EnabledMark`, `F_Description`, `F_UsedId`, `F_CreatorTime`,
                                        `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`,
                                        `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('390828003917048134', '日程修改提醒', 'PZXTRC002', '1', '4', 0, 1, '', NULL, '2023-02-02 13:50:59',
        '349057407209541', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                        `F_SortCode`, `F_EnabledMark`, `F_Description`, `F_UsedId`, `F_CreatorTime`,
                                        `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`,
                                        `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('390828003917048135', '日程删除提醒', 'PZXTRC003', '1', '4', 0, 1, '', NULL, '2023-02-02 13:50:59',
        '349057407209541', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                        `F_SortCode`, `F_EnabledMark`, `F_Description`, `F_UsedId`, `F_CreatorTime`,
                                        `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`,
                                        `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('392697159901795845', '密码过期提醒', 'PZXTXX001', '1', '3', 0, 1, '', NULL, '2023-02-07 17:38:21',
        '349057407209541', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                        `F_SortCode`, `F_EnabledMark`, `F_Description`, `F_UsedId`, `F_CreatorTime`,
                                        `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`,
                                        `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('401028936038887813', '公告提醒', 'PZXTGG001', '1', '1', 0, 1, NULL, NULL, '2023-03-02 17:25:18',
        '349057407209541', NULL, NULL, NULL, NULL, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for base_message_send_record
-- ----------------------------
DROP TABLE IF EXISTS `base_message_send_record`;
CREATE TABLE `base_message_send_record`
(
    `F_Id`               varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键id',
    `F_SendConfigId`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送配置id',
    `F_MessageSource`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息来源',
    `F_UsedId`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '被引用id',
    `F_CreatorTime`      datetime                                                     DEFAULT NULL COMMENT '创建时间',
    `F_CreatorUserId`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人员',
    `F_LastModifyTime`   datetime                                                     DEFAULT NULL COMMENT '修改时间',
    `F_LastModifyUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人员',
    `F_DeleteMark`       int                                                          DEFAULT NULL COMMENT '删除标志',
    `F_DeleteTime`       datetime                                                     DEFAULT NULL COMMENT '删除时间',
    `F_DeleteUserId`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除人员',
    `F_TenantId`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
    `F_EnabledMark`      int                                                          DEFAULT NULL COMMENT '状态',
    PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账号配置使用记录表';

-- ----------------------------
-- Records of base_message_send_record
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for base_message_send_template
-- ----------------------------
DROP TABLE IF EXISTS `base_message_send_template`;
CREATE TABLE `base_message_send_template`
(
    `F_Id`               varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键id',
    `F_SendConfigId`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息发送配置id',
    `F_MessageType`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息类型',
    `F_TemplateId`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息模板id',
    `F_AccountConfigId`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '账号配置id',
    `F_SortCode`         int                                                          DEFAULT NULL COMMENT '排序',
    `F_EnabledMark`      int                                                          DEFAULT NULL COMMENT '状态',
    `F_Description`      text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '说明',
    `F_CreatorTime`      datetime                                                     DEFAULT NULL COMMENT '创建时间',
    `F_CreatorUserId`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人员',
    `F_LastModifyTime`   datetime                                                     DEFAULT NULL COMMENT '修改时间',
    `F_LastModifyUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人员',
    `F_DeleteMark`       int                                                          DEFAULT NULL COMMENT '删除标志',
    `F_DeleteTime`       datetime                                                     DEFAULT NULL COMMENT '删除时间',
    `F_DeleteUserId`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除用户',
    `F_TenantId`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
    PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发送配置模板表';

-- ----------------------------
-- Records of base_message_send_template
-- ----------------------------
BEGIN;
INSERT INTO `base_message_send_template` (`F_Id`, `F_SendConfigId`, `F_MessageType`, `F_TemplateId`,
                                          `F_AccountConfigId`, `F_SortCode`, `F_EnabledMark`, `F_Description`,
                                          `F_CreatorTime`, `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`,
                                          `F_DeleteMark`, `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('333972597580513861', '333972597509210693', '1', '333884500318634053', '', 0, 1, '', NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_template` (`F_Id`, `F_SendConfigId`, `F_MessageType`, `F_TemplateId`,
                                          `F_AccountConfigId`, `F_SortCode`, `F_EnabledMark`, `F_Description`,
                                          `F_CreatorTime`, `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`,
                                          `F_DeleteMark`, `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('333972930637611589', '333972930574697029', '1', '333938764202131589', '', 0, 1, '', NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_template` (`F_Id`, `F_SendConfigId`, `F_MessageType`, `F_TemplateId`,
                                          `F_AccountConfigId`, `F_SortCode`, `F_EnabledMark`, `F_Description`,
                                          `F_CreatorTime`, `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`,
                                          `F_DeleteMark`, `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('333973394817040965', '333973394783486533', '1', '333938964589199493', '', 0, 1, '', NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_template` (`F_Id`, `F_SendConfigId`, `F_MessageType`, `F_TemplateId`,
                                          `F_AccountConfigId`, `F_SortCode`, `F_EnabledMark`, `F_Description`,
                                          `F_CreatorTime`, `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`,
                                          `F_DeleteMark`, `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('333973567374901829', '333973567337153093', '1', '333939134353653893', '', 0, 1, '', NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_template` (`F_Id`, `F_SendConfigId`, `F_MessageType`, `F_TemplateId`,
                                          `F_AccountConfigId`, `F_SortCode`, `F_EnabledMark`, `F_Description`,
                                          `F_CreatorTime`, `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`,
                                          `F_DeleteMark`, `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('333973688477041221', '333973688447681093', '1', '333939476977959045', '', 0, 1, '', NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_template` (`F_Id`, `F_SendConfigId`, `F_MessageType`, `F_TemplateId`,
                                          `F_AccountConfigId`, `F_SortCode`, `F_EnabledMark`, `F_Description`,
                                          `F_CreatorTime`, `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`,
                                          `F_DeleteMark`, `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('333973810195743301', '333973810141217349', '1', '333940818928746693', '', 0, 1, '', NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_template` (`F_Id`, `F_SendConfigId`, `F_MessageType`, `F_TemplateId`,
                                          `F_AccountConfigId`, `F_SortCode`, `F_EnabledMark`, `F_Description`,
                                          `F_CreatorTime`, `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`,
                                          `F_DeleteMark`, `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('333974038105834053', '333974038072279621', '1', '333942094613725381', '', 0, 1, '', NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_template` (`F_Id`, `F_SendConfigId`, `F_MessageType`, `F_TemplateId`,
                                          `F_AccountConfigId`, `F_SortCode`, `F_EnabledMark`, `F_Description`,
                                          `F_CreatorTime`, `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`,
                                          `F_DeleteMark`, `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('333974172172567109', '333974172122235461', '1', '333946756767170757', '', 0, 1, '', NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_template` (`F_Id`, `F_SendConfigId`, `F_MessageType`, `F_TemplateId`,
                                          `F_AccountConfigId`, `F_SortCode`, `F_EnabledMark`, `F_Description`,
                                          `F_CreatorTime`, `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`,
                                          `F_DeleteMark`, `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('333974395255013957', '333974395192099397', '1', '333956753337106757', '', 0, 1, '', NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_template` (`F_Id`, `F_SendConfigId`, `F_MessageType`, `F_TemplateId`,
                                          `F_AccountConfigId`, `F_SortCode`, `F_EnabledMark`, `F_Description`,
                                          `F_CreatorTime`, `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`,
                                          `F_DeleteMark`, `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('333974499437331013', '333974499412165189', '1', '333966894795080197', '', 0, 1, '', NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_template` (`F_Id`, `F_SendConfigId`, `F_MessageType`, `F_TemplateId`,
                                          `F_AccountConfigId`, `F_SortCode`, `F_EnabledMark`, `F_Description`,
                                          `F_CreatorTime`, `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`,
                                          `F_DeleteMark`, `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('333974605091848773', '333974605049905733', '1', '333968566967616005', '', 0, 1, '', NULL, NULL, NULL, NULL,
        NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_template` (`F_Id`, `F_SendConfigId`, `F_MessageType`, `F_TemplateId`,
                                          `F_AccountConfigId`, `F_SortCode`, `F_EnabledMark`, `F_Description`,
                                          `F_CreatorTime`, `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`,
                                          `F_DeleteMark`, `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('390828930870815045', '390828003917048133', '1', '390828509955631429', '', 0, 1, '', '2023-02-02 13:54:40',
        '349057407209541', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_template` (`F_Id`, `F_SendConfigId`, `F_MessageType`, `F_TemplateId`,
                                          `F_AccountConfigId`, `F_SortCode`, `F_EnabledMark`, `F_Description`,
                                          `F_CreatorTime`, `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`,
                                          `F_DeleteMark`, `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('390828930870815046', '390828003917048134', '1', '390828509955631430', '', 0, 1, '', '2023-02-02 13:54:40',
        '349057407209541', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_template` (`F_Id`, `F_SendConfigId`, `F_MessageType`, `F_TemplateId`,
                                          `F_AccountConfigId`, `F_SortCode`, `F_EnabledMark`, `F_Description`,
                                          `F_CreatorTime`, `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`,
                                          `F_DeleteMark`, `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('390828930870815047', '390828003917048135', '1', '390828509955631431', '', 0, 1, '', '2023-02-02 13:54:40',
        '349057407209541', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_template` (`F_Id`, `F_SendConfigId`, `F_MessageType`, `F_TemplateId`,
                                          `F_AccountConfigId`, `F_SortCode`, `F_EnabledMark`, `F_Description`,
                                          `F_CreatorTime`, `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`,
                                          `F_DeleteMark`, `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('392697159947933189', '392697159901795845', '1', '392696570841159173', '', 0, 1, '', '2023-02-07 17:38:21',
        'admin', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_template` (`F_Id`, `F_SendConfigId`, `F_MessageType`, `F_TemplateId`,
                                          `F_AccountConfigId`, `F_SortCode`, `F_EnabledMark`, `F_Description`,
                                          `F_CreatorTime`, `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`,
                                          `F_DeleteMark`, `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('400307537301233925', '400306547156082949', '2', '400306974597603589', '400306899347595525', 0, 1, '',
        '2023-02-28 17:39:16', '349057407209541', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_send_template` (`F_Id`, `F_SendConfigId`, `F_MessageType`, `F_TemplateId`,
                                          `F_AccountConfigId`, `F_SortCode`, `F_EnabledMark`, `F_Description`,
                                          `F_CreatorTime`, `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`,
                                          `F_DeleteMark`, `F_DeleteTime`, `F_DeleteUserId`, `F_TenantId`)
VALUES ('401032487041511813', '401028936038887813', '1', '401029872182374789', '', 0, 1, '', '2023-03-02 17:39:58',
        '349057407209541', NULL, NULL, NULL, NULL, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for base_message_short_link
-- ----------------------------
DROP TABLE IF EXISTS `base_message_short_link`;
CREATE TABLE `base_message_short_link`
(
    `F_Id`               varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '自然主键',
    `F_ShortLink`        varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '短链接',
    `F_RealPcLink`       longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT 'PC端链接',
    `F_RealAppLink`      longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT 'App端链接',
    `F_BodyText`         longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '流程内容',
    `F_IsUsed`           int                                                           DEFAULT NULL COMMENT '是否点击后失效',
    `F_ClickNum`         int                                                           DEFAULT NULL COMMENT '点击次数',
    `F_UnableNum`        int                                                           DEFAULT NULL COMMENT '失效次数',
    `F_UnableTime`       datetime                                                      DEFAULT NULL COMMENT '失效时间',
    `F_UserId`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '用户id',
    `F_TenantId`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '租户id',
    `F_CreatorTime`      datetime                                                      DEFAULT NULL COMMENT '创建时间',
    `F_CreatorUserId`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '创建用户',
    `F_LastModifyTime`   datetime                                                      DEFAULT NULL COMMENT '修改时间',
    `F_LastModifyUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '修改用户',
    `F_DeleteMark`       int                                                           DEFAULT NULL COMMENT '删除标志',
    `F_DeleteTime`       datetime                                                      DEFAULT NULL COMMENT '删除时间',
    `F_DeleteUserId`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '删除用户',
    `F_EnabledMark`      int                                                           DEFAULT NULL COMMENT '状态',
    PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息链接表';

-- ----------------------------
-- Records of base_message_short_link
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for base_message_sms_field
-- ----------------------------
DROP TABLE IF EXISTS `base_message_sms_field`;
CREATE TABLE `base_message_sms_field`
(
    `F_Id`               varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键id',
    `F_TemplateId`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息模板id',
    `F_FieldId`          varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '参数id',
    `F_SmsField`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '短信变量',
    `F_Field`            varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '参数',
    `F_CreatorTime`      datetime                                                     DEFAULT NULL COMMENT '创建时间',
    `F_CreatorUserId`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人员',
    `F_LastModifyTime`   datetime                                                     DEFAULT NULL COMMENT '修改时间',
    `F_LastModifyUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人员',
    `F_DeleteMark`       int                                                          DEFAULT NULL COMMENT '删除标志',
    `F_DeleteTime`       datetime                                                     DEFAULT NULL COMMENT '删除时间',
    `F_DeleteUserId`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除用户',
    `F_EnabledMark`      int                                                          DEFAULT NULL COMMENT '状态',
    `F_IsTitle`          int                                                          DEFAULT NULL COMMENT '是否标题',
    `F_TenantId`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
    PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='短信变量表';

-- ----------------------------
-- Records of base_message_sms_field
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for base_message_template_config
-- ----------------------------
DROP TABLE IF EXISTS `base_message_template_config`;
CREATE TABLE `base_message_template_config`
(
    `F_Id`               varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键id',
    `F_FullName`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '名称',
    `F_EnCode`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '编码',
    `F_TemplateType`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模板类型',
    `F_MessageSource`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息来源',
    `F_MessageType`      varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息类型',
    `F_EnabledMark`      int                                                          DEFAULT NULL COMMENT '状态',
    `F_SortCode`         int                                                          DEFAULT NULL COMMENT '排序',
    `F_Description`      text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '说明',
    `F_Title`            varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息标题',
    `F_Content`          longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '消息内容',
    `F_TemplateCode`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模板编号',
    `F_CreatorTime`      datetime                                                     DEFAULT NULL COMMENT '创建时间',
    `F_CreatorUserId`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人员',
    `F_LastModifyTime`   datetime                                                     DEFAULT NULL COMMENT '修改时间',
    `F_LastModifyUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人员',
    `F_DeleteMark`       int                                                          DEFAULT NULL COMMENT '删除标志',
    `F_DeleteTime`       datetime                                                     DEFAULT NULL COMMENT '删除时间',
    `F_DeleteUserId`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除用户',
    `F_WxSkip`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '跳转方式',
    `F_XcxAppId`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '小程序id',
    `F_TenantId`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
    PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息模板表';

-- ----------------------------
-- Records of base_message_template_config
-- ----------------------------
BEGIN;
INSERT INTO `base_message_template_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                            `F_MessageType`, `F_EnabledMark`, `F_SortCode`, `F_Description`, `F_Title`,
                                            `F_Content`, `F_TemplateCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                            `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                            `F_DeleteUserId`, `F_WxSkip`, `F_XcxAppId`, `F_TenantId`)
VALUES ('333884500318634053', '待办', 'MBXTLC001', '1', '2', '1', 1, 0, '', '{@Title}', '{@Title}', '',
        '2022-08-29 10:37:50', '349057407209541', '2022-08-29 14:21:58', 'admin', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_template_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                            `F_MessageType`, `F_EnabledMark`, `F_SortCode`, `F_Description`, `F_Title`,
                                            `F_Content`, `F_TemplateCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                            `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                            `F_DeleteUserId`, `F_WxSkip`, `F_XcxAppId`, `F_TenantId`)
VALUES ('333938764202131589', '同意', 'MBXTLC002', '1', '2', '1', 1, 0, '', '{@Title} 已被【同意】',
        '{@Title} 已被【同意】', '', '2022-08-29 14:13:28', '349057407209541', '2022-08-29 15:25:01', 'admin', NULL, NULL,
        NULL, NULL, NULL, NULL);
INSERT INTO `base_message_template_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                            `F_MessageType`, `F_EnabledMark`, `F_SortCode`, `F_Description`, `F_Title`,
                                            `F_Content`, `F_TemplateCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                            `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                            `F_DeleteUserId`, `F_WxSkip`, `F_XcxAppId`, `F_TenantId`)
VALUES ('333938964589199493', '退回', 'MBXTLC003', '1', '2', '1', 1, 0, '', '{@Title} 已被【退回】',
        '{@Title} 已被【退回】', '', '2022-08-29 14:14:15', '349057407209541', '2022-08-29 14:22:40', 'admin', NULL, NULL,
        NULL, NULL, NULL, NULL);
INSERT INTO `base_message_template_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                            `F_MessageType`, `F_EnabledMark`, `F_SortCode`, `F_Description`, `F_Title`,
                                            `F_Content`, `F_TemplateCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                            `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                            `F_DeleteUserId`, `F_WxSkip`, `F_XcxAppId`, `F_TenantId`)
VALUES ('333939134353653893', '催办', 'MBXTLC004', '1', '2', '1', 1, 0, '', '{@Title}', '{@Title}', '',
        '2022-08-29 14:14:56', '349057407209541', '2022-08-29 14:22:45', 'admin', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_template_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                            `F_MessageType`, `F_EnabledMark`, `F_SortCode`, `F_Description`, `F_Title`,
                                            `F_Content`, `F_TemplateCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                            `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                            `F_DeleteUserId`, `F_WxSkip`, `F_XcxAppId`, `F_TenantId`)
VALUES ('333939476977959045', '指派', 'MBXTLC005', '1', '2', '1', 1, 0, '', '{@Title} 已被【指派】',
        '{@Title} 已被【指派】', '', '2022-08-29 14:16:18', '349057407209541', '2022-08-29 14:22:49', 'admin', NULL, NULL,
        NULL, NULL, NULL, NULL);
INSERT INTO `base_message_template_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                            `F_MessageType`, `F_EnabledMark`, `F_SortCode`, `F_Description`, `F_Title`,
                                            `F_Content`, `F_TemplateCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                            `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                            `F_DeleteUserId`, `F_WxSkip`, `F_XcxAppId`, `F_TenantId`)
VALUES ('333940818928746693', '转办', 'MBXTLC006', '1', '2', '1', 1, 0, '', '{@Title} ', '{@Title} ', '',
        '2022-08-29 14:21:38', '349057407209541', '2022-08-29 14:23:00', 'admin', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_template_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                            `F_MessageType`, `F_EnabledMark`, `F_SortCode`, `F_Description`, `F_Title`,
                                            `F_Content`, `F_TemplateCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                            `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                            `F_DeleteUserId`, `F_WxSkip`, `F_XcxAppId`, `F_TenantId`)
VALUES ('333942094613725381', '抄送', 'MBXTLC007', '1', '2', '1', 1, 0, '', '{@Title} 已被【抄送】',
        '{@Title} 已被【抄送】', '', '2022-08-29 14:26:42', '349057407209541', '2022-08-29 16:05:28', 'admin', NULL, NULL,
        NULL, NULL, NULL, NULL);
INSERT INTO `base_message_template_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                            `F_MessageType`, `F_EnabledMark`, `F_SortCode`, `F_Description`, `F_Title`,
                                            `F_Content`, `F_TemplateCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                            `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                            `F_DeleteUserId`, `F_WxSkip`, `F_XcxAppId`, `F_TenantId`)
VALUES ('333946756767170757', '超时提醒', 'MBXTLC008', '1', '2', '1', 1, 0, '', '{@Title} 即将【超时】，请尽快【审批】',
        '{@Title} 即将【超时】，请尽快【审批】', '', '2022-08-29 14:45:13', '349057407209541', '2022-08-29 15:19:09',
        'admin', NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_template_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                            `F_MessageType`, `F_EnabledMark`, `F_SortCode`, `F_Description`, `F_Title`,
                                            `F_Content`, `F_TemplateCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                            `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                            `F_DeleteUserId`, `F_WxSkip`, `F_XcxAppId`, `F_TenantId`)
VALUES ('333956753337106757', '超时', 'MBXTLC009', '1', '2', '1', 1, 0, '', '{@Title} 已【超时】', '{@Title} 已【超时】',
        '', '2022-08-29 15:24:57', '349057407209541', '2022-08-29 15:25:21', 'admin', NULL, NULL, NULL, NULL, NULL,
        NULL);
INSERT INTO `base_message_template_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                            `F_MessageType`, `F_EnabledMark`, `F_SortCode`, `F_Description`, `F_Title`,
                                            `F_Content`, `F_TemplateCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                            `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                            `F_DeleteUserId`, `F_WxSkip`, `F_XcxAppId`, `F_TenantId`)
VALUES ('333966894795080197', '结束', 'MBXTLC010', '1', '2', '1', 1, 0, '', '{@Title} 已【结束】', '{@Title} 已【结束】',
        '', '2022-08-29 16:05:15', '349057407209541', '2022-08-29 16:05:19', 'admin', NULL, NULL, NULL, NULL, NULL,
        NULL);
INSERT INTO `base_message_template_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                            `F_MessageType`, `F_EnabledMark`, `F_SortCode`, `F_Description`, `F_Title`,
                                            `F_Content`, `F_TemplateCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                            `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                            `F_DeleteUserId`, `F_WxSkip`, `F_XcxAppId`, `F_TenantId`)
VALUES ('333968566967616005', '发起子流程', 'MBXTLC011', '1', '2', '1', 1, 0, '', '{@Title} 请发起【子流程】',
        '{@Title} 请发起【子流程】', '', '2022-08-29 16:11:53', '349057407209541', NULL, NULL, NULL, NULL, NULL, NULL,
        NULL, NULL);
INSERT INTO `base_message_template_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                            `F_MessageType`, `F_EnabledMark`, `F_SortCode`, `F_Description`, `F_Title`,
                                            `F_Content`, `F_TemplateCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                            `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                            `F_DeleteUserId`, `F_WxSkip`, `F_XcxAppId`, `F_TenantId`)
VALUES ('390828509955631429', '日程邀请提醒', 'MBXTRC001', '1', '4', '1', 1, 0, '', '{@CreatorUserName}的日程邀请',
        '{@CreatorUserName}的日程邀请', '', '2023-02-02 13:53:00', '349057407209541', NULL, NULL, NULL, NULL, NULL, '1',
        '', NULL);
INSERT INTO `base_message_template_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                            `F_MessageType`, `F_EnabledMark`, `F_SortCode`, `F_Description`, `F_Title`,
                                            `F_Content`, `F_TemplateCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                            `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                            `F_DeleteUserId`, `F_WxSkip`, `F_XcxAppId`, `F_TenantId`)
VALUES ('390828509955631430', '日程修改提醒', 'MBXTRC002', '1', '4', '1', 1, 0, '', '{@CreatorUserName}修改了日程',
        '{@CreatorUserName}修改了日程', '', '2023-02-02 13:53:00', '349057407209541', NULL, NULL, NULL, NULL, NULL, '1',
        '', NULL);
INSERT INTO `base_message_template_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                            `F_MessageType`, `F_EnabledMark`, `F_SortCode`, `F_Description`, `F_Title`,
                                            `F_Content`, `F_TemplateCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                            `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                            `F_DeleteUserId`, `F_WxSkip`, `F_XcxAppId`, `F_TenantId`)
VALUES ('390828509955631431', '日程删除提醒', 'MBXTRC003', '1', '4', '1', 1, 0, '',
        '{@CreatorUserName}的{@Title}日程已被删除', '{@CreatorUserName}的{@Title}日程已被删除', '',
        '2023-02-02 13:53:00', '349057407209541', NULL, NULL, NULL, NULL, NULL, '1', '', NULL);
INSERT INTO `base_message_template_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                            `F_MessageType`, `F_EnabledMark`, `F_SortCode`, `F_Description`, `F_Title`,
                                            `F_Content`, `F_TemplateCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                            `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                            `F_DeleteUserId`, `F_WxSkip`, `F_XcxAppId`, `F_TenantId`)
VALUES ('392696570841159173', '密码过期提醒', 'XTXXTX001', '1', '3', '1', 1, 0, '', '当前密码即将过期，请尽快修改',
        '当前密码即将过期，请尽快修改', '', '2023-02-07 17:36:00', '349057407209541', NULL, NULL, NULL, NULL, NULL, '1',
        '', NULL);
INSERT INTO `base_message_template_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                            `F_MessageType`, `F_EnabledMark`, `F_SortCode`, `F_Description`, `F_Title`,
                                            `F_Content`, `F_TemplateCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                            `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                            `F_DeleteUserId`, `F_WxSkip`, `F_XcxAppId`, `F_TenantId`)
VALUES ('400306974597603589', '邮箱', '2313123', '0', '2', '2', 1, 0, '', '3213', '<p>3213{@flowLink}11{test}</p>', '',
        '2023-02-28 17:37:02', '349057407209541', '2023-02-28 17:39:45', '349057407209541', NULL, NULL, NULL, '1', '',
        NULL);
INSERT INTO `base_message_template_config` (`F_Id`, `F_FullName`, `F_EnCode`, `F_TemplateType`, `F_MessageSource`,
                                            `F_MessageType`, `F_EnabledMark`, `F_SortCode`, `F_Description`, `F_Title`,
                                            `F_Content`, `F_TemplateCode`, `F_CreatorTime`, `F_CreatorUserId`,
                                            `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`, `F_DeleteTime`,
                                            `F_DeleteUserId`, `F_WxSkip`, `F_XcxAppId`, `F_TenantId`)
VALUES ('401029872182374789', '公告提醒', 'MBXTGG001', '1', '1', '1', 1, 0, NULL, '{@Title}', '{@Title}', NULL,
        '2023-03-02 17:37:03', '349057407209541', '2023-03-02 17:32:09', NULL, NULL, NULL, NULL, NULL, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for base_message_template_param
-- ----------------------------
DROP TABLE IF EXISTS `base_message_template_param`;
CREATE TABLE `base_message_template_param`
(
    `F_Id`               varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键id',
    `F_TemplateId`       varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息模板id',
    `F_Field`            varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '参数名称',
    `F_FieldName`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '参数说明',
    `F_CreatorTime`      datetime                                                     DEFAULT NULL COMMENT '创建时间',
    `F_CreatorUserId`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人员',
    `F_LastModifyTime`   datetime                                                     DEFAULT NULL COMMENT '修改时间',
    `F_LastModifyUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人员',
    `F_DeleteMark`       int                                                          DEFAULT NULL COMMENT '删除标志',
    `F_DeleteTime`       datetime                                                     DEFAULT NULL COMMENT '删除时间',
    `F_DeleteUserId`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除用户',
    `F_EnabledMark`      int                                                          DEFAULT NULL COMMENT '状态',
    `F_TenantId`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
    PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息模板参数表';

-- ----------------------------
-- Records of base_message_template_param
-- ----------------------------
BEGIN;
INSERT INTO `base_message_template_param` (`F_Id`, `F_TemplateId`, `F_Field`, `F_FieldName`, `F_CreatorTime`,
                                           `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`,
                                           `F_DeleteTime`, `F_DeleteUserId`, `F_EnabledMark`, `F_TenantId`)
VALUES ('400306974723432709', '400306974597603589', '@flowLink', '流程链接', '2023-02-28 17:37:02', '349057407209541',
        '2023-02-28 17:39:45', '349057407209541', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `base_message_template_param` (`F_Id`, `F_TemplateId`, `F_Field`, `F_FieldName`, `F_CreatorTime`,
                                           `F_CreatorUserId`, `F_LastModifyTime`, `F_LastModifyUserId`, `F_DeleteMark`,
                                           `F_DeleteTime`, `F_DeleteUserId`, `F_EnabledMark`, `F_TenantId`)
VALUES ('400307189564072197', '400306974597603589', 'test', '111', '2023-02-28 17:37:53', '349057407209541',
        '2023-02-28 17:39:45', '349057407209541', NULL, NULL, NULL, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for base_message_wechat_user
-- ----------------------------
DROP TABLE IF EXISTS `base_message_wechat_user`;
CREATE TABLE `base_message_wechat_user`
(
    `F_Id`               varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键id',
    `F_GzhId`            varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信公众号id',
    `F_UserId`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户id',
    `F_OpenId`           varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '微信公众号用户id',
    `F_CloseMark`        int                                                          DEFAULT NULL COMMENT '是否关注',
    `F_CreatorTime`      datetime                                                     DEFAULT NULL COMMENT '创建时间',
    `F_CreatorUserId`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人员',
    `F_LastModifyTime`   datetime                                                     DEFAULT NULL COMMENT '修改时间',
    `F_LastModifyUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改人员',
    `F_DeleteMark`       int                                                          DEFAULT NULL COMMENT '删除标志',
    `F_DeleteTime`       datetime                                                     DEFAULT NULL COMMENT '删除时间',
    `F_DeleteUserId`     varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除人员',
    `F_TenantId`         varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
    `F_EnabledMark`      int                                                          DEFAULT NULL COMMENT '状态',
    PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='微信公众号用户关联表';

-- ----------------------------
-- Records of base_message_wechat_user
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for base_messagereceive
-- ----------------------------
DROP TABLE IF EXISTS `base_messagereceive`;
CREATE TABLE `base_messagereceive`
(
    `F_Id`        varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '自然主键',
    `F_MessageId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息主键',
    `F_UserId`    varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户主键',
    `F_IsRead`    int                                                          DEFAULT NULL COMMENT '是否阅读',
    `F_ReadTime`  datetime                                                     DEFAULT NULL COMMENT '阅读时间',
    `F_ReadCount` int                                                          DEFAULT NULL COMMENT '阅读次数',
    `F_BodyText`  longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '内容',
    `F_TenantId`  varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
    `F_Excerpt`   text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '摘要',
    PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息接收';

-- ----------------------------
-- Records of base_messagereceive
-- ----------------------------
BEGIN;
INSERT INTO `base_messagereceive` (`F_Id`, `F_MessageId`, `F_UserId`, `F_IsRead`, `F_ReadTime`, `F_ReadCount`,
                                   `F_BodyText`, `F_TenantId`, `F_Excerpt`)
VALUES ('420851789189918598', '420851789189918597', '349057407209541', 0, NULL, NULL,
        '{\"groupId\":\"420305665500687493\",\"id\":\"420305665517464709\",\"type\":\"3\"}', NULL, NULL);
COMMIT;

SET
FOREIGN_KEY_CHECKS = 1;