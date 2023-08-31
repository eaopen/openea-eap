
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for base_imcontent
-- ----------------------------
DROP TABLE IF EXISTS `base_imcontent`;
CREATE TABLE `base_imcontent` (
  `F_Id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '自然主键',
  `F_SendUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送者',
  `F_SendTime` datetime DEFAULT NULL COMMENT '发送时间',
  `F_ReceiveUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接收者',
  `F_ReceiveTime` datetime DEFAULT NULL COMMENT '接收时间',
  `F_Content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '内容',
  `F_ContentType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '内容',
  `F_State` int DEFAULT NULL COMMENT '状态',
  `F_SENDDELETEMARK` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接收者删除标记',
  `F_DELETEMARK` int DEFAULT NULL COMMENT '删除标记',
  `F_TenantId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天内容';

-- ----------------------------
-- Records of base_imcontent
-- ----------------------------
BEGIN;
INSERT INTO `base_imcontent` (`F_Id`, `F_SendUserId`, `F_SendTime`, `F_ReceiveUserId`, `F_ReceiveTime`, `F_Content`, `F_ContentType`, `F_State`, `F_SENDDELETEMARK`, `F_DELETEMARK`, `F_TenantId`) VALUES ('453313141883797509', '349057407209541', '2023-07-25 00:04:37', 'e51681ed-5f00-4872-acd1-1f4ea14b538e', NULL, '[怒]', 'text', 0, NULL, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for base_imreply
-- ----------------------------
DROP TABLE IF EXISTS `base_imreply`;
CREATE TABLE `base_imreply` (
  `F_Id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键',
  `F_UserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户主键',
  `F_ReceiveUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接收用户',
  `F_ReceiveTime` datetime DEFAULT NULL COMMENT '接收时间',
  `F_ImreplySendDeleteMark` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接收者删除标记',
  `F_ImreplyDeleteMark` int DEFAULT NULL COMMENT '删除标记',
  `F_TenantId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天会话';

-- ----------------------------
-- Records of base_imreply
-- ----------------------------
BEGIN;
INSERT INTO `base_imreply` (`F_Id`, `F_UserId`, `F_ReceiveUserId`, `F_ReceiveTime`, `F_ImreplySendDeleteMark`, `F_ImreplyDeleteMark`, `F_TenantId`) VALUES ('1683508492287545345', '349057407209541', 'e51681ed-5f00-4872-acd1-1f4ea14b538e', '2023-07-25 00:04:37', NULL, 0, NULL);
COMMIT;



SET FOREIGN_KEY_CHECKS = 1;
