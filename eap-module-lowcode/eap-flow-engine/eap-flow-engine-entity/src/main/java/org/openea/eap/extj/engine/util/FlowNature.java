package org.openea.eap.extj.engine.util;

import lombok.Data;

/**
 * 在线工作流开发
 *
 * 
 */
@Data
public class FlowNature {

    /**
     * 驳回开始
     **/
    public static String START = "0";

    /**
     * 驳回上一节点
     **/
    public static String UP = "1";

    /**
     * 自选节点
     **/
    public static String Reject = "2";

    /**
     * 候选人
     **/
    public static String Candidates = "1";

    /**
     * 异常人
     **/
    public static String CandidatesError = "2";

    /**
     * 重新审批
     **/
    public static Integer RestartType = 1;

    /**
     * 当前审批
     **/
    public static Integer PresentType = 2;

    /**
     * 待办事宜
     **/
    public static String WAIT = "1";

    /**
     * 已办事宜
     **/
    public static String TRIAL = "2";

    /**
     * 抄送事宜
     **/
    public static String CIRCULATE = "3";

    /**
     * 抄送事宜
     **/
    public static String BATCH = "4";

    /**
     * 工作流完成
     **/
    public static String CompletionEnd = "100";

    /**
     * 流程父节点
     **/
    public static String ParentId = "0";

    /**
     * 加签人
     **/
    public static String RecordStatus = "0";

    /**
     * 审批开始
     **/
    public static String NodeStart = "start";

    /**
     * 审批结束
     **/
    public static String NodeEnd = "end";

    /**
     * 子流程
     **/
    public static String NodeSubFlow = "subFlow";

    /**
     * 结束节点
     **/
    public static String EndRound = "endround";

    /**
     * 或签
     **/
    public static Integer FixedApprover = 0;

    /**
     * 会签
     **/
    public static Integer FixedJointlyApprover = 1;

    /**
     * 依次
     **/
    public static Integer ImproperApprover = 2;

    /**
     * 通过
     **/
    public static Integer AuditCompletion = 1;

    /**
     * 拒绝
     **/
    public static Integer RejectCompletion = -1;

    /**
     * 进行
     **/
    public static Integer ProcessCompletion = 0;

    /**
     * 子流程同步
     **/
    public static Integer ChildSync = 0;

    /**
     * 子流程异步
     **/
    public static Integer ChildAsync = 1;

    /**
     * 完成情况
     */
    public static Integer Progress = 100;

    /**
     * 流程标题
     */
    public static Integer TitleType = 0;

    /**
     * 加签回流
     */
    public static Integer Reflux = 0;

    /**
     * 加签前
     */
    public static Integer Before = 1;

    /**
     * 加签后
     */
    public static Integer Later = 2;

}
