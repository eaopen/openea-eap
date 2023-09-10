/*
 Navicat Premium Data Transfer

 Source Server         : rpa-db-sh02
 Source Server Type    : MySQL
 Source Server Version : 80022
 Source Host           : 10.0.4.13:3306
 Source Schema         : eoa-db

 Target Server Type    : MySQL
 Target Server Version : 80022
 File Encoding         : 65001

 Date: 10/09/2023 14:48:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for flow_candidates
-- ----------------------------
DROP TABLE IF EXISTS `flow_candidates`;
CREATE TABLE `flow_candidates` (
  `F_Id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '自然主键',
  `F_TaskNodeId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点id',
  `F_TaskId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务id',
  `F_HandleId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审批人id',
  `F_Account` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审批人账号',
  `F_Candidates` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '候选人',
  `F_TaskOperatorId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '经办主键',
  `F_Type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '审批类型',
  `F_TenantId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程候选人';

-- ----------------------------
-- Table structure for flow_comment
-- ----------------------------
DROP TABLE IF EXISTS `flow_comment`;
CREATE TABLE `flow_comment` (
  `F_Id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '自然主键',
  `F_TaskId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务主键',
  `F_Text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '文本',
  `F_Image` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '图片',
  `F_File` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '附件',
  `F_EnabledMark` int DEFAULT NULL COMMENT '有效标志',
  `F_CreatorTime` datetime DEFAULT NULL COMMENT '创建时间',
  `F_CreatorUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建用户',
  `F_LastModifyTime` datetime DEFAULT NULL COMMENT '修改时间',
  `F_LastModifyUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改用户',
  `F_DeleteMark` int DEFAULT NULL COMMENT '删除标志',
  `F_DeleteTime` datetime DEFAULT NULL COMMENT '删除时间',
  `F_DeleteUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除用户',
  `F_TenantId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程评论';

-- ----------------------------
-- Table structure for flow_delegate
-- ----------------------------
DROP TABLE IF EXISTS `flow_delegate`;
CREATE TABLE `flow_delegate` (
  `F_Id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '自然主键',
  `F_ToUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '被委托人',
  `F_ToUserName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '被委托人',
  `F_FlowId` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '委托流程（为空是全部流程）',
  `F_FlowName` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '委托流程名称',
  `F_FlowCategory` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程分类',
  `F_StartTime` datetime DEFAULT NULL COMMENT '开始时间',
  `F_EndTime` datetime DEFAULT NULL COMMENT '结束时间',
  `F_Description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `F_SortCode` bigint DEFAULT NULL COMMENT '排序',
  `F_EnabledMark` int DEFAULT NULL COMMENT '有效标志',
  `F_CreatorTime` datetime DEFAULT NULL COMMENT '创建时间',
  `F_CreatorUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建用户',
  `F_LastModifyTime` datetime DEFAULT NULL COMMENT '修改时间',
  `F_LastModifyUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改用户',
  `F_DeleteMark` int DEFAULT NULL COMMENT '删除标志',
  `F_DeleteTime` datetime DEFAULT NULL COMMENT '删除时间',
  `F_DeleteUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除用户',
  `F_TenantId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
  `F_UserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '委托人',
  `F_UserName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '委托人名称',
  `F_Type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '委托类型（0-发起委托，1-审批委托）',
  PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程委托';

-- ----------------------------
-- Table structure for flow_engine
-- ----------------------------
DROP TABLE IF EXISTS `flow_engine`;
CREATE TABLE `flow_engine` (
  `F_Id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '自然主键',
  `F_EnCode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程编码',
  `F_FullName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程名称',
  `F_Type` int DEFAULT NULL COMMENT '流程类型',
  `F_Category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程分类',
  `F_Form` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程表单',
  `F_VisibleType` int DEFAULT NULL COMMENT '可见类型',
  `F_Icon` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程图标',
  `F_IconBackground` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标背景色',
  `F_Version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程版本',
  `F_FlowTemplateJson` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '流程模板',
  `F_Description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `F_SortCode` bigint DEFAULT NULL COMMENT '排序码',
  `F_EnabledMark` int DEFAULT NULL COMMENT '有效标志',
  `F_CreatorTime` datetime DEFAULT NULL COMMENT '创建时间',
  `F_CreatorUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建用户',
  `F_LastModifyTime` datetime DEFAULT NULL COMMENT '修改时间',
  `F_LastModifyUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改用户',
  `F_DeleteMark` int DEFAULT NULL COMMENT '删除标志',
  `F_DeleteTime` datetime DEFAULT NULL COMMENT '删除时间',
  `F_DeleteUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除用户',
  `F_FormTemplateJson` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '表单模板',
  `F_FormType` int DEFAULT NULL COMMENT '表单分类',
  `F_Tables` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '关联的表',
  `F_DbLinkId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联数据连接id',
  `F_AppFormUrl` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'app表单路径',
  `F_FormUrl` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'pc表单路径',
  `F_TenantId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程引擎';

-- ----------------------------
-- Table structure for flow_engineform
-- ----------------------------
DROP TABLE IF EXISTS `flow_engineform`;
CREATE TABLE `flow_engineform` (
  `F_Id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '自然主键',
  `F_EnCode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单编码',
  `F_FullName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单名称',
  `F_Category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单分类',
  `F_UrlAddress` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT 'Web地址',
  `F_AppUrlAddress` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT 'APP地址',
  `F_PropertyJson` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '属性字段',
  `F_Description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `F_SortCode` bigint DEFAULT NULL COMMENT '排序码',
  `F_EnabledMark` int DEFAULT NULL COMMENT '有效标志',
  `F_CreatorTime` datetime DEFAULT NULL COMMENT '创建时间',
  `F_CreatorUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建用户',
  `F_LastModifyTime` datetime DEFAULT NULL COMMENT '修改时间',
  `F_LastModifyUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改用户',
  `F_DeleteMark` int DEFAULT NULL COMMENT '删除标志',
  `F_DeleteTime` datetime DEFAULT NULL COMMENT '删除时间',
  `F_DeleteUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除用户',
  `F_FlowType` int DEFAULT NULL COMMENT '流程类型',
  `F_FormType` int DEFAULT NULL COMMENT '表单类型',
  `F_InterfaceUrl` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '接口路径',
  `F_DraftJson` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '表单json草稿',
  `F_DbLinkId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联数据链接',
  `F_TableJson` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '关联的表',
  `F_FlowId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '引擎id',
  `F_TenantId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程表单';

-- ----------------------------
-- Table structure for flow_engineform_relation
-- ----------------------------
DROP TABLE IF EXISTS `flow_engineform_relation`;
CREATE TABLE `flow_engineform_relation` (
  `F_Id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键',
  `F_FlowId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程id',
  `F_FormId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单id',
  `F_TenantId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程表单关联表';

-- ----------------------------
-- Table structure for flow_enginevisible
-- ----------------------------
DROP TABLE IF EXISTS `flow_enginevisible`;
CREATE TABLE `flow_enginevisible` (
  `F_Id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '自然主键',
  `F_FlowId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程主键',
  `F_OperatorType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '经办类型',
  `F_OperatorId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '经办主键',
  `F_SortCode` bigint DEFAULT NULL COMMENT '排序码',
  `F_CreatorTime` datetime DEFAULT NULL COMMENT '创建时间',
  `F_CreatorUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建用户',
  `F_TenantId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
  `F_TYPE` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '1.发起 2.协管',
  PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程可见';

-- ----------------------------
-- Table structure for flow_rejectdata
-- ----------------------------
DROP TABLE IF EXISTS `flow_rejectdata`;
CREATE TABLE `flow_rejectdata` (
  `F_Id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键',
  `F_TaskJson` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '任务json',
  `F_TaskNodeJson` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '节点json',
  `F_TaskOperatorJson` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '经办json',
  `F_TenantId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
  `F_CreatorTime` datetime DEFAULT NULL COMMENT '创建时间',
  `F_CreatorUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建用户',
  `F_LastModifyTime` datetime DEFAULT NULL COMMENT '修改时间',
  `F_LastModifyUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改用户',
  `F_DeleteMark` int DEFAULT NULL COMMENT '删除标志',
  `F_DeleteTime` datetime DEFAULT NULL COMMENT '删除时间',
  `F_DeleteUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除用户',
  PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='冻结审批';

-- ----------------------------
-- Table structure for flow_task
-- ----------------------------
DROP TABLE IF EXISTS `flow_task`;
CREATE TABLE `flow_task` (
  `F_Id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '自然主键',
  `F_ProcessId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '实例进程',
  `F_EnCode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务编号',
  `F_FullName` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务标题',
  `F_FlowUrgent` int DEFAULT NULL COMMENT '紧急程度',
  `F_FlowId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程主键',
  `F_FlowCode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程编号',
  `F_FlowName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程名称',
  `F_FlowType` int DEFAULT NULL COMMENT '流程分类',
  `F_FlowCategory` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程类型',
  `F_FlowForm` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '流程表单',
  `F_FlowFormContentJson` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '表单内容',
  `F_FlowTemplateJson` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '流程模板',
  `F_FlowVersion` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程版本',
  `F_StartTime` datetime DEFAULT NULL COMMENT '开始时间',
  `F_EndTime` datetime DEFAULT NULL COMMENT '结束时间',
  `F_ThisStep` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '当前步骤',
  `F_ThisStepId` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '当前步骤Id',
  `F_Grade` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '重要等级',
  `F_Status` int DEFAULT NULL COMMENT '任务状态',
  `F_Completion` int DEFAULT NULL COMMENT '完成情况',
  `F_Description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `F_SortCode` bigint DEFAULT NULL COMMENT '排序码',
  `F_EnabledMark` int DEFAULT NULL COMMENT '有效标志',
  `F_CreatorTime` datetime DEFAULT NULL COMMENT '创建时间',
  `F_CreatorUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建用户',
  `F_LastModifyTime` datetime DEFAULT NULL COMMENT '修改时间',
  `F_LastModifyUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改用户',
  `F_DeleteMark` int DEFAULT NULL COMMENT '删除标志',
  `F_DeleteTime` datetime DEFAULT NULL COMMENT '删除时间',
  `F_DeleteUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除用户',
  `F_ParentId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '父流程id',
  `F_IsAsync` int DEFAULT NULL COMMENT '同步异步（0：同步，1：异步）',
  `F_IsBatch` int DEFAULT NULL COMMENT '是否批量（0：否，1：是）',
  `F_TaskNodeId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点主键',
  `F_FormType` int DEFAULT NULL COMMENT '表单分类',
  `F_TenantId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
  `F_TemplateId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程模板id',
  `F_DelegateUser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '委托用户',
  `F_RejectDataId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '冻结审批',
  `F_Suspend` int DEFAULT NULL COMMENT '挂起任务状态',
  PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程任务';

-- ----------------------------
-- Table structure for flow_taskcirculate
-- ----------------------------
DROP TABLE IF EXISTS `flow_taskcirculate`;
CREATE TABLE `flow_taskcirculate` (
  `F_Id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '自然主键',
  `F_ObjectType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '对象类型',
  `F_ObjectId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '对象主键',
  `F_NodeCode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点编号',
  `F_NodeName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点名称',
  `F_TaskNodeId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点主键',
  `F_TaskId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务主键',
  `F_CreatorTime` datetime DEFAULT NULL COMMENT '创建时间',
  `F_TenantId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程传阅';

-- ----------------------------
-- Table structure for flow_tasknode
-- ----------------------------
DROP TABLE IF EXISTS `flow_tasknode`;
CREATE TABLE `flow_tasknode` (
  `F_Id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '自然主键',
  `F_NodeCode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点编号',
  `F_NodeName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点名称',
  `F_NodeType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点类型',
  `F_NodePropertyJson` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '节点属性Json',
  `F_NodeUp` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '上一节点',
  `F_NodeNext` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
  `F_Completion` int DEFAULT NULL COMMENT '是否完成',
  `F_Description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `F_SortCode` bigint DEFAULT NULL COMMENT '排序码',
  `F_CreatorTime` datetime DEFAULT NULL COMMENT '创建时间',
  `F_TaskId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务主键',
  `F_State` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '状态',
  `F_Candidates` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '候选人',
  `F_CounterSign` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '依次审批',
  `F_DraftData` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '草稿数据',
  `F_TenantId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
  `F_FormId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '表单id',
  PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程节点';

-- ----------------------------
-- Table structure for flow_taskoperator
-- ----------------------------
DROP TABLE IF EXISTS `flow_taskoperator`;
CREATE TABLE `flow_taskoperator` (
  `F_Id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '自然主键',
  `F_HandleType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '经办对象',
  `F_HandleId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '经办主键',
  `F_HandleStatus` int DEFAULT NULL COMMENT '处理状态',
  `F_HandleTime` datetime DEFAULT NULL COMMENT '处理时间',
  `F_NodeCode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点编号',
  `F_NodeName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点名称',
  `F_Completion` int DEFAULT NULL COMMENT '是否完成',
  `F_Description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `F_CreatorTime` datetime DEFAULT NULL COMMENT '创建时间',
  `F_TaskNodeId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点主键',
  `F_TaskId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务主键',
  `F_Type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点类型',
  `F_State` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '状态',
  `F_ParentId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '父节点id',
  `F_DraftData` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '草稿数据',
  `F_Automation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '自动审批',
  `F_SortCode` bigint DEFAULT NULL COMMENT '排序码',
  `F_TenantId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
  `F_RollbackId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '回滚id(初始前签有值，后签无值)',
  `F_Reject` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '冻结审批人',
  PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程经办';

-- ----------------------------
-- Table structure for flow_taskoperatorrecord
-- ----------------------------
DROP TABLE IF EXISTS `flow_taskoperatorrecord`;
CREATE TABLE `flow_taskoperatorrecord` (
  `F_Id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '自然主键',
  `F_NodeCode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点编号',
  `F_NodeName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点名称',
  `F_HandleStatus` int DEFAULT NULL COMMENT '经办状态',
  `F_HandleId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '经办人员',
  `F_HandleTime` datetime DEFAULT NULL COMMENT '经办时间',
  `F_HandleOpinion` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '经办理由',
  `F_TaskOperatorId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '经办主键',
  `F_TaskNodeId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点主键',
  `F_TaskId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务主键',
  `F_SignImg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '签名图片',
  `F_Status` int DEFAULT NULL COMMENT '审批标识',
  `F_OperatorId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流转操作人',
  `F_FileList` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '经办文件',
  `F_TenantId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
  `F_DraftData` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '审批数据',
  `F_ApproverType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '加签类型',
  PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程经办记录';

-- ----------------------------
-- Table structure for flow_taskoperatoruser
-- ----------------------------
DROP TABLE IF EXISTS `flow_taskoperatoruser`;
CREATE TABLE `flow_taskoperatoruser` (
  `F_Id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键',
  `F_TaskId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务主键',
  `F_TaskNodeId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点主键',
  `F_HandleId` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '经办主键',
  `F_HandleType` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '经办对象',
  `F_NodeCode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点编号',
  `F_NodeName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点名称',
  `F_Completion` int DEFAULT NULL COMMENT '是否完成',
  `F_CreatorTime` datetime DEFAULT NULL COMMENT '创建时间',
  `F_Description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `F_Type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '节点类型',
  `F_ParentId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '父节点id',
  `F_Automation` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '自动审批',
  `F_SortCode` bigint DEFAULT NULL COMMENT '排序码',
  `F_State` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '状态',
  `F_TenantId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
  `F_Reject` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '冻结审批人',
  PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程依次审批';

-- ----------------------------
-- Table structure for flow_template
-- ----------------------------
DROP TABLE IF EXISTS `flow_template`;
CREATE TABLE `flow_template` (
  `F_Id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '自然主键',
  `F_EnCode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程编码',
  `F_FullName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程名称',
  `F_Type` int DEFAULT NULL COMMENT '流程类型',
  `F_Category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程分类',
  `F_VisibleType` int DEFAULT NULL COMMENT '可见类型',
  `F_Icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标',
  `F_IconBackground` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标背景色',
  `F_Description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '描述',
  `F_SortCode` bigint DEFAULT NULL COMMENT '排序码',
  `F_EnabledMark` int DEFAULT NULL COMMENT '有效标志',
  `F_CreatorTime` datetime DEFAULT NULL COMMENT '创建时间',
  `F_CreatorUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建用户',
  `F_LastModifyTime` datetime DEFAULT NULL COMMENT '修改时间',
  `F_LastModifyUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改用户',
  `F_DeleteMark` int DEFAULT NULL COMMENT '删除标志',
  `F_DeleteTime` datetime DEFAULT NULL COMMENT '删除时间',
  `F_DeleteUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除用户',
  `F_TenantId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程模板';

-- ----------------------------
-- Table structure for flow_templatejson
-- ----------------------------
DROP TABLE IF EXISTS `flow_templatejson`;
CREATE TABLE `flow_templatejson` (
  `F_Id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '自然主键',
  `F_TemplateId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程模板id',
  `F_FullName` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程名称',
  `F_VisibleType` int DEFAULT NULL COMMENT '可见类型',
  `F_Version` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '流程版本',
  `F_FlowTemplateJson` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '流程模板',
  `F_SortCode` bigint DEFAULT NULL COMMENT '排序码',
  `F_EnabledMark` int DEFAULT NULL COMMENT '有效标志',
  `F_CreatorTime` datetime DEFAULT NULL COMMENT '创建时间',
  `F_CreatorUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建用户',
  `F_LastModifyTime` datetime DEFAULT NULL COMMENT '修改时间',
  `F_LastModifyUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '修改用户',
  `F_DeleteMark` int DEFAULT NULL COMMENT '删除标志',
  `F_DeleteTime` datetime DEFAULT NULL COMMENT '删除时间',
  `F_DeleteUserId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '删除用户',
  `F_TenantId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
  `F_GroupId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分组id',
  PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程模板实例';

-- ----------------------------
-- Table structure for flow_user
-- ----------------------------
DROP TABLE IF EXISTS `flow_user`;
CREATE TABLE `flow_user` (
  `F_Id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键',
  `F_OrganizeId` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '组织主键',
  `F_PositionId` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '岗位主键',
  `F_ManagerId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '主管主键',
  `F_Superior` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '上级用户',
  `F_Subordinate` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '下属用户',
  `F_TaskId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '任务主键',
  `F_Department` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '公司下所有部门',
  `F_TenantId` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '租户id',
  PRIMARY KEY (`F_Id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='流程发起用户信息';

SET FOREIGN_KEY_CHECKS = 1;
