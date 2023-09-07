package org.openea.eap.extj.message.model.message;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * 企业微信获取成员的对象模型
 */
@Data
public class QyWebChatUserModel {
    /**
     * 执行返回执行代码
     */
    private String errcode;
    /**
     * 执行返回执行消息
     */
    private String errmsg;
    /**
     * 用户ID
     */
    private String userid;
    /**
     * 用户名称
     */
    private String name;
    /**
     * 成员别名
     */
    private String alias;
    /**
     * 手机号码。企业内必须唯一，mobile/email二者不能同时为空
     */
    private String mobile;
    /**
     * 成员所属部门id列表,不超过100个
     */
    private List<Long> department;
    /**
     * 部门内的排序值，默认为0，成员次序以创建时间从小到大排列
     * 个数必须和参数department的个数一致，数值越大排序越前面
     */
    private String order;
    /**
     * 职务信息
     */
    private String position;
    /**
     * 性别。1表示男性，2表示女性
     */
    private String gender;
    /**
     * 邮箱。长度6~64个字节，且为有效的email格式。
     * 企业内必须唯一，mobile/email二者不能同时为空
     */
    private String email;
    /**
     * 座机。32字节以内，由纯数字或’-‘号组成。
     */
    private String telephone;
    /**
     * 个数必须和参数department的个数一致，表示在所在的部门内是否为上级。1表示为上级，0表示非上级。
     * 在审批等应用里可以用来标识上级审批人
     */
    private String is_leader_in_dept;
    /**
     * 成员头像的mediaid，通过素材管理接口上传图片获得的mediaid
     */
    private String avatar_mediaid;
    /**
     * 启用/禁用成员。1表示启用成员，0表示禁用成员
     */
    private String enable;
    /**
     * 激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业。
     */
    private Integer status;
    /**
     * 自定义字段
     */
    private String extattr;
    /**
     * 是否邀请该成员使用企业微信（将通过微信服务通知或短信或邮件下发邀请，每天自动下发一次，最多持续3个工作日），默认值为true。
     */
    private boolean to_invite;
    /**
     * 成员对外属性
     */
    private String external_profile;
    /**
     * 对外职务，如果设置了该值，则以此作为对外展示的职务，否则以position来展示。
     * 长度12个汉字内
     */
    private String external_position;
    /**
     * 地址
     */
    private String address;
    /**
     * 主部门
     */
    private String main_department;

    /**
     * 部门列表
     */
    private LinkedList<Long> deptIdList;
}
