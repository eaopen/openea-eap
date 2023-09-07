package org.openea.eap.extj.base.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.openea.eap.extj.database.model.dto.ModelDTO;
import org.openea.eap.extj.database.model.interfaces.JdbcGetMod;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * 打印模板-流程经办记录
 *
 * 
 */
@Data
@TableName("flow_taskoperatorrecord")
public class OperatorRecordEntity extends SuperBaseEntity.SuperTBaseEntity<String> implements JdbcGetMod {

    /**
     * 节点编码
     */
    @TableField("F_NODECODE")
    private String nodeCode;

    /**
     * 节点名称
     */
    @TableField("F_NODENAME")
    private String nodeName;

    /**
     * 经办状态 0-拒绝、1-同意、2-提交、3-撤回、4-终止、5-指派、6-加签、7-转办
     */
    @TableField("F_HANDLESTATUS")
    private Integer handleStatus;

    /**
     * 经办人员
     */
    @TableField("F_HANDLEID")
    private String handleId;

    /**
     * 经办人员
     */
    @TableField(exist = false)
    private String userName;

    /**
     * 经办时间
     */
    @TableField("F_HANDLETIME")
    private Date handleTimeOrigin;

    /**
     * 经办时间（时间戳）
     */
    private Long handleTime;

    /**
     * 经办理由
     */
    @TableField("F_HANDLEOPINION")
    private String handleOpinion;

    /**
     * 流转操作人
     */
    @TableField("F_OPERATORID")
    private String operatorId;

    /**
     * 经办主键
     */
    @TableField(value="F_TASKOPERATORID",fill = FieldFill.UPDATE)
    private String taskOperatorId;

    /**
     * 节点主键
     */
    @TableField(value="F_TASKNODEID",fill = FieldFill.UPDATE)
    private String taskNodeId;

    /**
     * 任务主键
     */
    @TableField("F_TASKID")
    private String taskId;

    /**
     * 签名图片
     */
    @TableField("F_SIGNIMG")
    private String signImg;

    /**
     * 0.进行数据 -1.作废数据 1.加签数据 3.已办不显示数据
     */
    @TableField("F_STATUS")
    private String status;

    @Override
    public void setMod(ModelDTO modelDTO){
        try{
            ResultSet resultSet = modelDTO.getResultSet();
            this.setId(resultSet.getString("F_ID"));
            this.setNodeCode(resultSet.getString("F_NODECODE"));
            this.setNodeName(resultSet.getString("F_NODENAME"));
            this.setHandleStatus(resultSet.getInt("F_HANDLESTATUS"));
            this.setHandleId(resultSet.getString("F_HANDLEID"));
            this.setHandleTimeOrigin(resultSet.getTimestamp("F_HANDLETIME"));
            this.setHandleOpinion(resultSet.getString("F_HANDLEOPINION"));
            this.setOperatorId(resultSet.getString("F_OPERATORID"));
            this.setTaskOperatorId(resultSet.getString("F_TASKOPERATORID"));
            this.setTaskNodeId(resultSet.getString("F_TASKNODEID"));
            this.setTaskId(resultSet.getString("F_TASKID"));
            this.setSignImg(resultSet.getString("F_SIGNIMG"));
            this.setStatus(resultSet.getString("F_STATUS"));
            this.setStatus(resultSet.getString("F_TENANTID"));
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
