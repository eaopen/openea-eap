
package org.openea.eap.extj.message.service;

import org.openea.eap.extj.base.service.SuperService;


import java.util.*;

import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.message.entity.AccountConfigEntity;
import org.openea.eap.extj.message.model.accountconfig.*;

/**
 * 账号配置功能
 */
public interface AccountConfigService extends SuperService<AccountConfigEntity> {


    List<AccountConfigEntity> getList(AccountConfigPagination accountConfigPagination);

    List<AccountConfigEntity> getTypeList(AccountConfigPagination accountConfigPagination, String dataType);


    AccountConfigEntity getInfo(String id);

    void delete(AccountConfigEntity entity);

    void create(AccountConfigEntity entity);

    boolean update(String id, AccountConfigEntity entity);

    /**
     *
     * @param type 配置类型 1：站内信，2：邮件，3：短信，4：钉钉，5：企业微信，6：webhook
     * @return
     */
    List<AccountConfigEntity> getListByType(String type);

    /**
     * 验证名称
     *
     * @param fullName 名称
     * @param id       主键值
     * @return ignore
     */
    boolean isExistByFullName(String fullName, String id);

    /**
     * 验证编码
     *
     * @param enCode 编码
     * @param id     主键值
     * @return ignore
     */
    boolean isExistByEnCode(String enCode, String id,String type);

    /**
     * 账号配置导入
     *
     * @param entity 实体对象
     * @return ignore
     * @throws DataException ignore
     */
    ActionResult ImportData(AccountConfigEntity entity) throws DataException;

//  子表方法

    //列表子表数据方法

    //验证表单
    boolean checkForm(AccountConfigForm form, int i,String type,String id);

    /**
     * 验证微信公众号原始id唯一性
     * @param gzhId 微信公众号原始id
     * @param i
     * @param type
     * @param id
     * @return
     */
    boolean checkGzhId(String gzhId, int i,String type,String id);

    AccountConfigEntity getInfoByType(String appKey, String type);
}
