package org.openea.eap.extj.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.v3.oas.annotations.media.Schema;
import org.openea.eap.extj.base.vo.PageListVO;
import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.constant.MsgCode;

import java.util.List;

@JsonInclude(Include.NON_NULL)
public class ActionResult<T> {
    @Schema(
            description = "状态码"
    )
    private Integer code;
    @Schema(
            description = "返回信息"
    )
    private String msg;
    @Schema(
            description = "返回数据"
    )
    private T data;

    public static <T> ActionResult<T> success() {
        ActionResult<T> jsonData = new ActionResult();
        jsonData.setCode(200);
        jsonData.setMsg(MsgCode.SU000.get());
        return jsonData;
    }

    public static <T> ActionResult<T> success(String msg) {
        ActionResult<T> jsonData = new ActionResult();
        jsonData.setCode(200);
        jsonData.setMsg(msg);
        return jsonData;
    }

    public static <T> ActionResult<T> success(T object) {
        ActionResult<T> jsonData = new ActionResult();
        jsonData.setData(object);
        jsonData.setCode(200);
        jsonData.setMsg(MsgCode.SU000.get());
        return jsonData;
    }

    public static <T> ActionResult<T> success(String msg, T object) {
        ActionResult<T> jsonData = new ActionResult();
        jsonData.setData(object);
        jsonData.setCode(200);
        jsonData.setMsg(msg);
        return jsonData;
    }

    public static <T> ActionResult<T> fail(Integer code, String message) {
        ActionResult<T> jsonData = new ActionResult();
        jsonData.setCode(code);
        jsonData.setMsg(message);
        return jsonData;
    }

    public static ActionResult<String> fail(String msg, String data) {
        ActionResult<String> jsonData = new ActionResult();
        jsonData.setMsg(msg);
        jsonData.setData(data);
        return jsonData;
    }

    public static <T> ActionResult<T> fail(String msg) {
        ActionResult<T> jsonData = new ActionResult();
        jsonData.setMsg(msg);
        jsonData.setCode(400);
        return jsonData;
    }

    public static <T> ActionResult<PageListVO<T>> page(List<T> list, PaginationVO pagination) {
        ActionResult<PageListVO<T>> jsonData = new ActionResult();
        PageListVO<T> vo = new PageListVO();
        vo.setList(list);
        vo.setPagination(pagination);
        jsonData.setData(vo);
        jsonData.setCode(200);
        jsonData.setMsg(MsgCode.SU000.get());
        return jsonData;
    }

    public static <T> ActionResult<DataInterfacePageListVO<T>> page(List<T> list, PaginationVO pagination, String dataProcessing) {
        ActionResult<DataInterfacePageListVO<T>> jsonData = new ActionResult();
        DataInterfacePageListVO<T> vo = new DataInterfacePageListVO();
        vo.setList(list);
        vo.setPagination(pagination);
        vo.setDataProcessing(dataProcessing);
        jsonData.setCode(200);
        jsonData.setData(vo);
        jsonData.setMsg(MsgCode.SU000.get());
        return jsonData;
    }

    public ActionResult() {
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    public T getData() {
        return this.data;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ActionResult)) {
            return false;
        } else {
            ActionResult<?> other = (ActionResult)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label47: {
                    Object this$code = this.getCode();
                    Object other$code = other.getCode();
                    if (this$code == null) {
                        if (other$code == null) {
                            break label47;
                        }
                    } else if (this$code.equals(other$code)) {
                        break label47;
                    }

                    return false;
                }

                Object this$msg = this.getMsg();
                Object other$msg = other.getMsg();
                if (this$msg == null) {
                    if (other$msg != null) {
                        return false;
                    }
                } else if (!this$msg.equals(other$msg)) {
                    return false;
                }

                Object this$data = this.getData();
                Object other$data = other.getData();
                if (this$data == null) {
                    if (other$data != null) {
                        return false;
                    }
                } else if (!this$data.equals(other$data)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof ActionResult;
    }

    public int hashCode() {
        int result = 1;
        Object $code = this.getCode();
        result = result * 59 + ($code == null ? 43 : $code.hashCode());
        Object $msg = this.getMsg();
        result = result * 59 + ($msg == null ? 43 : $msg.hashCode());
        Object $data = this.getData();
        result = result * 59 + ($data == null ? 43 : $data.hashCode());
        return result;
    }

    public String toString() {
        return "ActionResult(code=" + this.getCode() + ", msg=" + this.getMsg() + ", data=" + this.getData() + ")";
    }
}