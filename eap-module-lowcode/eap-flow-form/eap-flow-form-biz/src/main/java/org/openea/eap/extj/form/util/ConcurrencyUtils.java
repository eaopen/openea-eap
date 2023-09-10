package org.openea.eap.extj.form.util;

import cn.hutool.core.bean.BeanUtil;
import org.openea.eap.extj.database.model.dbfield.DbFieldModel;
import org.openea.eap.extj.database.model.dbfield.base.DbFieldModelBase;
import org.openea.eap.extj.database.model.dbtable.DbTableFieldModel;
import org.openea.eap.extj.exception.WorkFlowException;
import org.openea.eap.extj.model.visualJson.TableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ConcurrencyUtils {

	@Autowired
	private ServiceBaseUtil serviceUtil;

	//创建锁字段
	public void createVersion(String table, String linkId) throws Exception {
		addFeild(table, linkId,TableFeildsEnum.VERSION);
	}

	//创建flowTaskId
	public void createFlowTaskId(String table, String linkId) throws Exception {
		addFeild(table, linkId,TableFeildsEnum.FLOWTASKID);
	}

	/**
	 * 创建租户id
	 * @param table
	 * @param linkId
	 * @throws Exception
	 */
	public void createTenantId(String table, String linkId) throws Exception {
		addFeild(table, linkId,TableFeildsEnum.TENANTID);
	}

	/**
	 * 创建删除字段
	 * @param
	 * @return
	 */
	public void creDeleteMark(String table, String linkId) throws Exception {
		addFeild(table, linkId,TableFeildsEnum.DELETEMARK);
	}

	/**
	 * 创建流程引擎id字段
	 *
	 * @param
	 * @return
	 */
	public void createFlowEngine(String table, String linkId) throws Exception {
		addFeild(table, linkId,TableFeildsEnum.FLOWID);
	}

	/**
	 * 新增字段通用方法
	 * @param table
	 * @param linkId
	 * @param tableFeildsEnum
	 * @throws Exception
	 */
	private void addFeild(String table, String linkId,TableFeildsEnum tableFeildsEnum) throws Exception {
		List<DbFieldModelBase> fieldList = serviceUtil.getDbTableModel(linkId, table);
		if (fieldList==null){
			return;
		}
		DbFieldModelBase dbFieldModel = fieldList.stream().filter(f -> f.getField().equalsIgnoreCase(tableFeildsEnum.getField())).findFirst().orElse(null);
		boolean hasVersion = dbFieldModel!=null;
		if (!hasVersion){
			DbTableFieldModel dbTableFieldModel = new DbTableFieldModel();
			DbFieldModel dbTableModel1 = this.getDbFieldModel(tableFeildsEnum);
			List<DbFieldModel> fieldOneList = new ArrayList<>();
			fieldOneList.add(dbTableModel1);
			dbTableFieldModel.setDbFieldModelList(fieldOneList);
			dbTableFieldModel.setUpdateNewTable(table);
			dbTableFieldModel.setUpdateOldTable(table);
			dbTableFieldModel.setDbLinkId(linkId);
			serviceUtil.addField(dbTableFieldModel);
		}
	}

	/**
	 * 根据枚举获取字段对象
	 * @param
	 * @return
	 */
	public static DbFieldModel getDbFieldModel(TableFeildsEnum tableFeildsEnum){
		DbFieldModel dbFieldModel=new DbFieldModel();
		BeanUtil.copyProperties(tableFeildsEnum,dbFieldModel);
		dbFieldModel.setIsPrimaryKey(tableFeildsEnum.getPrimaryKey());
		return dbFieldModel;
	}


	/**
	 * 判断表是否是自增id
	 * @param primaryKeyPolicy
	 * @param dbLinkId
	 * @param tableList
	 * @return
	 * @throws Exception
	 */
	public  boolean checkAutoIncrement(int primaryKeyPolicy,String dbLinkId,List<TableModel> tableList) throws Exception {
		boolean isIncre = primaryKeyPolicy == 2;
		String strategy = primaryKeyPolicy == 1 ? "[雪花ID]" : "[自增长id]";
		for (TableModel tableModel : tableList) {
			List<DbFieldModel> data = serviceUtil.getFieldList(dbLinkId, tableModel.getTable());
			DbFieldModel dbFieldModel = data.stream().filter(DbFieldModel::getIsPrimaryKey).findFirst().orElse(null);
			if (data.size()==0){
				return true;
			}
			if (dbFieldModel == null) {
				throw new WorkFlowException("表[" + tableModel.getTable() + " ]无主键!");
			}
//			if (!isIncre == (dbFieldModel.getIsAutoIncrement() != null && dbFieldModel.getIsAutoIncrement())) {
//				throw new WorkFlowException("主键策略:" + strategy + ",表[ " + tableModel.getTable() + " ]主键设置不支持!");
//			}
		}
		return true;
	}
}
