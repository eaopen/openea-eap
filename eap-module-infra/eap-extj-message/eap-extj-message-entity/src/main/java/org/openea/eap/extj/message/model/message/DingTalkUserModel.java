package org.openea.eap.extj.message.model.message;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;


/**
 * 钉钉的用户模型
 */
@Data
public class DingTalkUserModel {
    /**
     * 员工唯一标识ID（不可修改），企业内必须唯一
     * 长度为1~64个字符，如果不传，将自动生成一个userid
     */
    @TableField("userid")
    private String userid;

    /**
     * 员工名称，长度最大80个字符 (必填项)
     */
    @TableField("name")
    private String name;

    /**
     * 手机号码，企业内必须唯一，不可重复
     * 如果是国际号码，请使用+xx-xxxxxx的格式
     */
    @TableField("mobile")
    private String mobile;

    /**
     * 是否号码隐藏 通常不隐藏：false
     */
    @TableField("hide_mobile")
    private Boolean hideMobile;


    /**
     * 分机号，长度最大50个字符
     * 企业内必须唯一，不可重复
     */
    @TableField("telephone")
    private String telephone;

    /**
     * 员工工号，长度最大为50个字符
     */
    @TableField("job_number")
    private String jobNumber;

    /**
     * 职位，长度最大为200个字符
     */
    @TableField("title")
    private String title;

    /**
     * 员工邮箱，长度最大50个字符。企业内必须唯一，不可重复
     */
    @TableField("email")
    private String email;

    /**
     * 员工的企业邮箱，长度最大100个字符
     * 员工的企业邮箱已开通，才能增加此字段,通常不用
     */
    @TableField("org_email")
    private String orgEmail;

    /**
     * 办公地点，长度最大100个字符
     */
    @TableField("work_place")
    private String workPlace;

    /**
     * 备注，长度最大2000个字符
     */
    @TableField("remark")
    private String remark;

    /**
     * 所属部门id列表,如："2,3,4"
     */
    @TableField("dept_id_list")
    private String deptIdList;

    /**
     * 员工在对应的部门中的排序 DeptOrder[]
     */
    @TableField("dept_order_list")
    private String deptOrderList;

    /**
     * 员工在对应的部门中的职位 DeptTitle[]
     */
    @TableField("dept_title_list")
    private String deptTitleList;


    /**
     * 扩展属性，可以设置多种属性，最大长度2000个字符
     * 格式：{"爱好":"旅游","年龄":"24"}
     */
    @TableField("extension")
    private String extension;

    /**
     * 是否开启高管模式
     * true：开启
     * 开启后，手机号码对所有员工隐藏。普通员工无法对其发DING、发起钉钉免费商务电话。高管之间不受影响。
     * false：不开启
     */
    @TableField("senior_mode")
    private Boolean seniorMode;

    /**
     * 入职时间，Unix时间戳，单位毫秒
     */
    @TableField("hired_date")
    private Long hiredDate;

    /**
     * 登录邮箱
     */
    @TableField("login_email")
    private String loginEmail;

    /**
     * 是否专属帐号
     * 为true时，不能指定loginEmail或mobile
     */
    @TableField("exclusive_account")
    private Boolean exclusiveAccount;

    /**
     * 专属帐号类型：
     * sso：企业自建专属帐号
     * dingtalk：钉钉自建专属帐号
     */
    @TableField("exclusive_account_type")
    private String exclusiveAccountType;

    /**
     * 钉钉专属帐号登录名
     */
    @TableField("login_id")
    private String loginId;

    /**
     * 钉钉专属帐号初始密码
     */
    @TableField("init_password")
    private String initPassword;

}
