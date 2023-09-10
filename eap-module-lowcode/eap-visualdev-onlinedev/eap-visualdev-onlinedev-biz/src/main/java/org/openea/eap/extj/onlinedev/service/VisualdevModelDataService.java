package org.openea.eap.extj.onlinedev.service;

import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.entity.VisualdevEntity;
import org.openea.eap.extj.base.model.PaginationModel;
import org.openea.eap.extj.base.model.VisualDevJsonModel;
import org.openea.eap.extj.base.service.SuperService;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.form.model.flow.DataModel;
import org.openea.eap.extj.model.visualJson.FormDataField;
import org.openea.eap.extj.onlinedev.entity.VisualdevModelDataEntity;
import org.openea.eap.extj.onlinedev.model.PaginationModelExport;
import org.openea.eap.extj.onlinedev.model.VisualdevModelDataInfoVO;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface VisualdevModelDataService extends SuperService<VisualdevModelDataEntity> {

    /**
     * 获取表单主表属性下拉框
     * @return
     */
    List<FormDataField> fieldList(String id, Integer filterType);

    /**
     * 弹窗数据分页
     * @param visualdevEntity
     * @param paginationModel
     * @return
     */
    List<Map<String,Object>> getPageList(VisualdevEntity visualdevEntity, PaginationModel paginationModel);

    List<VisualdevModelDataEntity> getList(String modelId);

    VisualdevModelDataEntity getInfo(String id);

    VisualdevModelDataInfoVO infoDataChange(String id, VisualdevEntity visualdevEntity) throws IOException, ParseException, DataException, SQLException;

    void delete(VisualdevModelDataEntity entity);

    boolean tableDelete(String id, VisualDevJsonModel visualDevJsonModel) throws Exception;

    ActionResult tableDeleteMore(List<String> id, VisualDevJsonModel visualDevJsonModel) throws Exception;

    List<Map<String, Object>> exportData(String[] keys, PaginationModelExport paginationModelExport, VisualDevJsonModel visualDevJsonModel) throws IOException, ParseException, SQLException, DataException;

    ActionResult visualCreate(VisualdevEntity visualdevEntity, DataModel dataModel) throws WorkFlowException;

    ActionResult visualUpdate(VisualdevEntity visualdevEntity, DataModel dataModel) throws WorkFlowException;

}
