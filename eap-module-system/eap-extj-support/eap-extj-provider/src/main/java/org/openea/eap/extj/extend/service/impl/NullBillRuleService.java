package org.openea.eap.extj.extend.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.base.Pagination;
import org.openea.eap.extj.base.mapper.SuperMapper;
import org.openea.eap.extj.base.model.dataInterface.PaginationDataInterface;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.extend.entity.BillRuleEntity;
import org.openea.eap.extj.extend.service.BillRuleService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class NullBillRuleService implements BillRuleService {
    /**
     * 列表
     *
     * @param pagination 条件
     * @return 单据规则列表
     */
    @Override
    public List<BillRuleEntity> getList(PaginationDataInterface pagination) {
        return null;
    }

    /**
     * 列表
     *
     * @return 单据规则集合
     */
    @Override
    public List<BillRuleEntity> getList() {
        return null;
    }

    /**
     * 信息
     *
     * @param id 主键值
     * @return 单据规则
     */
    @Override
    public BillRuleEntity getInfo(String id) {
        return null;
    }

    /**
     * 验证名称
     *
     * @param fullName 名称
     * @param id       主键值
     * @return ignore
     */
    @Override
    public boolean isExistByFullName(String fullName, String id) {
        return false;
    }

    /**
     * 验证编码
     *
     * @param enCode 编码
     * @param id     主键值
     * @return ignore
     */
    @Override
    public boolean isExistByEnCode(String enCode, String id) {
        return false;
    }

    /**
     * 获取流水号
     *
     * @param enCode 流水编码
     * @return ignore
     * @throws DataException ignore
     */
    @Override
    public String getNumber(String enCode) throws DataException {
        return null;
    }

    /**
     * 创建
     *
     * @param entity 实体对象
     */
    @Override
    public void create(BillRuleEntity entity) {

    }

    /**
     * 更新
     *
     * @param id     主键值
     * @param entity 实体对象
     * @return ignore
     */
    @Override
    public boolean update(String id, BillRuleEntity entity) {
        return false;
    }

    /**
     * 删除
     *
     * @param entity 实体对象
     */
    @Override
    public void delete(BillRuleEntity entity) {

    }

    /**
     * 上移
     *
     * @param id 主键值
     * @return ignore
     */
    @Override
    public boolean first(String id) {
        return false;
    }

    /**
     * 下移
     *
     * @param id 主键值
     * @return ignore
     */
    @Override
    public boolean next(String id) {
        return false;
    }

    @Override
    public String getBillNumber(String rule, boolean b) {
        return null;
    }

    /**
     * 使用单据流水号（注意：必须是缓存的单据才可以调用这个方法，否则无效）
     *
     * @param enCode 流水编码
     */
    @Override
    public void useBillNumber(String enCode) {

    }

    /**
     * 单据规则导入
     *
     * @param entity 实体对象
     * @return ignore
     * @throws DataException ignore
     */
    @Override
    public ActionResult ImportData(BillRuleEntity entity) throws DataException {
        return null;
    }

    /**
     * @param id
     * @param pagination 根据业务条件
     * @return 单据规则列表
     */
    @Override
    public List<BillRuleEntity> getListByCategory(String id, Pagination pagination) {
        return null;
    }

    @Override
    public boolean saveBatch(Collection<BillRuleEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<BillRuleEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<BillRuleEntity> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(BillRuleEntity entity) {
        return false;
    }

    @Override
    public BillRuleEntity getOne(Wrapper<BillRuleEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<BillRuleEntity> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<BillRuleEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    /**
     * 获取对应 entity 的 BaseMapper
     *
     * @return BaseMapper
     */
    @Override
    public SuperMapper<BillRuleEntity> getBaseMapper() {
        return null;
    }

    @Override
    public Class<BillRuleEntity> getEntityClass() {
        return null;
    }

    /**
     * 批量修改插入
     *
     * @param entityList 实体对象集合
     * @param batchSize  每次的数量
     */
    @Override
    public boolean saveOrUpdateBatchIgnoreLogic(Collection<BillRuleEntity> entityList, int batchSize) {
        return false;
    }

    /**
     * 根据ID 批量更新
     *
     * @param entityList 实体对象集合
     * @param batchSize  更新批次数量
     */
    @Override
    public boolean updateBatchByIdIgnoreLogic(Collection<BillRuleEntity> entityList, int batchSize) {
        return false;
    }

    /**
     * TableId 注解存在更新记录，否插入一条记录
     *
     * @param entity 实体对象
     */
    @Override
    public boolean saveOrUpdateIgnoreLogic(BillRuleEntity entity) {
        return false;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     * @param throwEx      有多个 result 是否抛出异常
     */
    @Override
    public BillRuleEntity getOneIgnoreLogic(Wrapper<BillRuleEntity> queryWrapper, boolean throwEx) {
        return null;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     */
    @Override
    public Map<String, Object> getMapIgnoreLogic(Wrapper<BillRuleEntity> queryWrapper) {
        return null;
    }

    /**
     * 根据 Wrapper，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类 {@link QueryWrapper}
     * @param mapper       转换函数
     */
    @Override
    public <V> V getObjIgnoreLogic(Wrapper<BillRuleEntity> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }
}
