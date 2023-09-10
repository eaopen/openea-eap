package org.openea.eap.extj.controller.admin.form;

import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.controller.SuperController;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.engine.enums.FlowStatusEnum;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.form.entity.SalesOrderEntity;
import org.openea.eap.extj.form.entity.SalesOrderEntryEntity;
import org.openea.eap.extj.form.model.salesorder.SalesOrderEntryEntityInfoModel;
import org.openea.eap.extj.form.model.salesorder.SalesOrderForm;
import org.openea.eap.extj.form.model.salesorder.SalesOrderInfoVO;
import org.openea.eap.extj.form.service.SalesOrderService;
import org.openea.eap.extj.util.JsonUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 销售订单
 *
 *
 */
@Tag(name = "销售订单", description = "SalesOrder")
@RestController
@RequestMapping("/Form/SalesOrder")
public class SalesOrderController extends SuperController<SalesOrderService, SalesOrderEntity> {

    @Autowired
    private SalesOrderService salesOrderService;

    /**
     * 获取销售订单信息
     *
     * @param id 主键值
     * @return
     */
    @Operation(summary = "获取销售订单信息")
    @GetMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
    })
    public ActionResult info(@PathVariable("id") String id) {
        SalesOrderEntity entity = salesOrderService.getInfo(id);
        List<SalesOrderEntryEntity> entityList = salesOrderService.getSalesEntryList(id);
        SalesOrderInfoVO vo = JsonUtil.getJsonToBean(entity, SalesOrderInfoVO.class);
        if (vo != null) {
            vo.setEntryList(JsonUtil.getJsonToList(entityList, SalesOrderEntryEntityInfoModel.class));
        }
        return ActionResult.success(vo);
    }

    /**
     * 新建销售订单
     *
     * @param salesOrderForm 表单对象
     * @return
     * @throws WorkFlowException
     */
    @Operation(summary = "新建销售订单")
    @PostMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "salesOrderForm", description = "销售模型", required = true),
    })
    public ActionResult create(@RequestBody SalesOrderForm salesOrderForm, @PathVariable("id") String id) throws WorkFlowException {
        SalesOrderEntity sales = JsonUtil.getJsonToBean(salesOrderForm, SalesOrderEntity.class);
        List<SalesOrderEntryEntity> salesEntryList = JsonUtil.getJsonToList(salesOrderForm.getEntryList(), SalesOrderEntryEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(salesOrderForm.getStatus())) {
            salesOrderService.save(id, sales, salesEntryList, salesOrderForm);
            return ActionResult.success(MsgCode.SU002.get());
        }
        salesOrderService.submit(id, sales, salesEntryList, salesOrderForm);
        return ActionResult.success(MsgCode.SU006.get());
    }

    /**
     * 修改销售订单
     *
     * @param salesOrderForm 表单对象
     * @param id             主键
     * @return
     * @throws WorkFlowException
     */
    @Operation(summary = "修改销售订单")
    @PutMapping("/{id}")
    @Parameters({
            @Parameter(name = "id", description = "主键", required = true),
            @Parameter(name = "salesOrderForm", description = "销售模型", required = true),
    })
    public ActionResult update(@RequestBody SalesOrderForm salesOrderForm, @PathVariable("id") String id) throws WorkFlowException {
        SalesOrderEntity sales = JsonUtil.getJsonToBean(salesOrderForm, SalesOrderEntity.class);
        sales.setId(id);
        List<SalesOrderEntryEntity> salesEntryList = JsonUtil.getJsonToList(salesOrderForm.getEntryList(), SalesOrderEntryEntity.class);
        if (FlowStatusEnum.save.getMessage().equals(salesOrderForm.getStatus())) {
            salesOrderService.save(id, sales, salesEntryList, salesOrderForm);
            return ActionResult.success(MsgCode.SU002.get());
        }
        salesOrderService.submit(id, sales, salesEntryList, salesOrderForm);
        return ActionResult.success(MsgCode.SU006.get());
    }
}
