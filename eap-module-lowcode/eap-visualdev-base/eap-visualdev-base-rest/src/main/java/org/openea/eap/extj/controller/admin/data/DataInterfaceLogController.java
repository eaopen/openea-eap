package org.openea.eap.extj.controller.admin.data;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.Pagination;
import org.openea.eap.extj.base.controller.SuperController;
import org.openea.eap.extj.base.vo.PageListVO;
import org.openea.eap.extj.base.vo.PaginationVO;
import org.openea.eap.extj.permission.entity.UserEntity;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.extj.util.JsonUtil;
import org.openea.eap.extj.base.entity.DataInterfaceLogEntity;
import org.openea.eap.extj.base.model.dataInterface.DataInterfaceLogVO;
import org.openea.eap.extj.base.service.DataInterfaceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(description = "DataInterfaceLog", name = "数据接口调用日志")
@RestController
@RequestMapping("/api/system/DataInterfaceLog")
public class DataInterfaceLogController extends SuperController<DataInterfaceLogService, DataInterfaceLogEntity> {
    @Autowired
    private DataInterfaceLogService dataInterfaceLogService;
    @Autowired
    private UserService userService;

    /**
     * 获取数据接口调用日志列表
     *
     * @param id         主键
     * @param pagination 分页参数
     * @return ignore
     */
    @Operation(summary = "获取数据接口调用日志列表")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true)
    })
    @SaCheckPermission("systemData.dataInterface")
    @GetMapping("{id}")
    public ActionResult<PageListVO<DataInterfaceLogVO>> getList(@PathVariable("id") String id, Pagination pagination) {
        List<DataInterfaceLogEntity> list = dataInterfaceLogService.getList(id, pagination);
        List<DataInterfaceLogVO> voList = JsonUtil.getJsonToList(list, DataInterfaceLogVO.class);
        for (DataInterfaceLogVO vo : voList) {
            UserEntity entity = userService.getInfo(vo.getUserId());
            if (entity != null) {
                vo.setUserId(entity.getRealName() + "/" + entity.getAccount());
            }
        }
        PaginationVO vo = JsonUtil.getJsonToBean(pagination, PaginationVO.class);
        return ActionResult.page(voList, vo);
    }
}