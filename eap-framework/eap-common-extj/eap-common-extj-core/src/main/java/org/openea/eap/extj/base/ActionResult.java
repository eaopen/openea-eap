package org.openea.eap.extj.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.openea.eap.extj.base.vo.DataInterfacePageListVO;
import org.openea.eap.extj.base.vo.PageListVO;
import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.constant.MsgCode;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 返回值
 *
 *
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionResult<T> {

    @ApiModelProperty("状态码")
    private Integer code;

    @ApiModelProperty("返回信息")
    private String msg;

    @ApiModelProperty("返回数据")
    private T data;

    /* ============== success ============ */

    public static <T> ActionResult<T> success() {
        ActionResult<T> jsonData = new ActionResult<>();
        jsonData.setCode(200);
        jsonData.setMsg(MsgCode.SU000.get());
        return jsonData;
    }

    public static <T> ActionResult<T> success(String msg) {
        ActionResult<T> jsonData = new ActionResult<>();
        jsonData.setCode(200);
        jsonData.setMsg(msg);
        return jsonData;
    }

    public static <T> ActionResult<T> success(T object) {
        ActionResult<T> jsonData = new ActionResult<>();
        jsonData.setData(object);
        jsonData.setCode(200);
        jsonData.setMsg(MsgCode.SU000.get());
        return jsonData;
    }

    public static <T> ActionResult<T> success(String msg, T object) {
        ActionResult<T> jsonData = new ActionResult<>();
        jsonData.setData(object);
        jsonData.setCode(200);
        jsonData.setMsg(msg);
        return jsonData;
    }

    public static ActionResult<Map<String, Object>> success(Object rows, PageModel pageModel) {
        ActionResult<Map<String, Object>> jsonData = new ActionResult<>();
        Map<String, Object> map = new HashMap<>(16);
        map.put("page", pageModel.getPage());
        map.put("records", pageModel.getRecords());
        map.put("rows", rows);
        map.put("total", pageModel.getTotal());
        jsonData.setData(map);
        jsonData.setCode(200);
        jsonData.setMsg(MsgCode.SU000.get());
        return jsonData;
    }

    /* ============== fail ============ */

    public static <T> ActionResult<T> fail(Integer code, String message) {
        ActionResult<T> jsonData = new ActionResult<>();
        jsonData.setCode(code);
        jsonData.setMsg(message);
        return jsonData;
    }

    public static ActionResult<String> fail(String msg, String data) {
        ActionResult<String> jsonData = new ActionResult<>();
        jsonData.setMsg(msg);
        jsonData.setData(data);
        return jsonData;
    }

    public static <T> ActionResult<T> fail(String msg) {
        ActionResult<T> jsonData = new ActionResult<>();
        jsonData.setMsg(msg);
        jsonData.setCode(400);
        return jsonData;
    }

    /* ============== page ============ */

    public static <T> ActionResult<PageListVO<T>> page(List<T> list, PaginationVO pagination) {
        ActionResult<PageListVO<T>> jsonData = new ActionResult<>();
        PageListVO<T> vo = new PageListVO<>();
        vo.setList(list);
        vo.setPagination(pagination);
        jsonData.setData(vo);
        jsonData.setCode(200);
        jsonData.setMsg(MsgCode.SU000.get());
        return jsonData;
    }

    public static <T> ActionResult<DataInterfacePageListVO<T>> page(List<T> list, PaginationVO pagination, String dataProcessing) {
        ActionResult<DataInterfacePageListVO<T>> jsonData = new ActionResult<>();
        DataInterfacePageListVO<T> vo = new DataInterfacePageListVO<>();
        vo.setList(list);
        vo.setPagination(pagination);
        vo.setDataProcessing(dataProcessing);
        jsonData.setCode(200);
        jsonData.setData(vo);
        jsonData.setMsg(MsgCode.SU000.get());
        return jsonData;
    }

}
