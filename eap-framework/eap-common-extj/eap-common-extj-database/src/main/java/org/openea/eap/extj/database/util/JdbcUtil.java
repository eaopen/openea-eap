package org.openea.eap.extj.database.util;

import org.openea.eap.extj.database.model.dbfield.JdbcColumnModel;
import org.openea.eap.extj.database.model.dto.JdbcResult;
import org.openea.eap.extj.database.model.dto.ModelDTO;
import org.openea.eap.extj.database.model.dto.PrepSqlDTO;
import org.openea.eap.extj.database.model.interfaces.JdbcGetMod;
import org.openea.eap.extj.database.model.page.JdbcPageMod;
import org.openea.eap.extj.base.Pagination;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.enums.DbAliasEnum;
import org.openea.eap.extj.database.sql.util.SqlFastUtil;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.util.CollectionUtils;
import org.openea.eap.extj.util.XSSEscape;
import org.openea.eap.extj.util.context.SpringContext;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JDBC Dynamic
 *
 * 
 */
@Slf4j
public class JdbcUtil {

    private final static String SELECT_MAPPER = "org.openea.eap.extj.database.mapper.JdbcMapper.getList";
    private final static String UPDATE_MAPPER = "org.openea.eap.extj.database.mapper.JdbcMapper.update";
    private final static String UPDATE_MAPPERS = "org.openea.eap.extj.database.mapper.JdbcMapper.updates";
    private final static String DELETE_MAPPER = "org.openea.eap.extj.database.mapper.JdbcMapper.delete";
    private final static String INSERT_MAPPER = "org.openea.eap.extj.database.mapper.JdbcMapper.insert";

    private static SqlSessionFactory getSqlSessionFactory(){
        return SpringContext.getBean(SqlSessionFactory.class);
    }

    /**
     * 批量执行一条SQL语句(适合增、删、改)
     * CRUD：增加(Create)、检索(Retrieve)、更新(Update)、删除(Delete)
     * @return 返回值：i>0 成功条数，i=0 执行失败
     */
    public static int creUpDe(PrepSqlDTO dto) throws Exception {
        dto.switchConn();
        try{
            @Cleanup SqlSession sqlSession = getSqlSessionFactory().openSession();
            return sqlSession.update(UPDATE_MAPPER, dto.getMapParams());
        }finally{
            DynamicDataSourceUtil.clearSwitchDataSource();
        }
    }

    /**
     * update更新语句
     */
    public static int update(PrepSqlDTO dto) throws Exception {
        dto.switchConn();
        try{
            @Cleanup SqlSession sqlSession = getSqlSessionFactory().openSession();
            return sqlSession.update(UPDATE_MAPPERS, dto.getMapParams());
        }finally{
            DynamicDataSourceUtil.clearSwitchDataSource();
        }
    }

    public static int delete(PrepSqlDTO dto) throws Exception {
        dto.switchConn();
        try{
            @Cleanup SqlSession sqlSession = getSqlSessionFactory().openSession();
            return sqlSession.update(DELETE_MAPPER, dto.getMapParams());
        }finally{
            DynamicDataSourceUtil.clearSwitchDataSource();
        }
    }

    public static int insert(PrepSqlDTO dto) throws Exception {
        dto.switchConn();
        try{
            @Cleanup SqlSession sqlSession = getSqlSessionFactory().openSession();
            return sqlSession.update(INSERT_MAPPER, dto.getMapParams());
        }finally{
            DynamicDataSourceUtil.clearSwitchDataSource();
        }
    }

//    /**
//     * 同一条语句批量执行
//     * 同数据源
//     * @param dto SQL语句参数对象
//     * @return 执行结果
//     * @throws SQLException ignore
//     */
//    public static int[] creUpDeBatchOneSql(PrepSqlDTO dto) throws Exception {
//        @Cleanup Connection conn = ConnUtil.getConnOrDefault(dto.getDbLinkEntity());
//        return JdbcCreUpDel.get(conn, () -> {
//            @Cleanup PreparedStatement statement = conn.prepareStatement(dto.getSql());
//            for(List<?> data : dto.getMultiDataList()){
//                JdbcCreUpDel.setData(statement, data);
//                statement.addBatch();
//            }
//            return statement.executeBatch();
//        });
//    }

    /*========================query查询语句==============================*/

    /*============ 模式一：MapList ============*/
    /**
     * 通用：单字段多条查询
     */
    public static JdbcResult<List<Map<String, Object>>> queryList(PrepSqlDTO dto) throws Exception {
       return new JdbcResult<>((jdbcResult)-> JdbcUtil.getMybatisModel0(dto, jdbcResult.getIsLowerCase(), jdbcResult.getIsAlias()));
    }

    /**
     * 通用：单字段单条查询
     */
    public static Map<String, Object> queryOne(PrepSqlDTO dto) throws Exception {
        List<Map<String, Object>> mapList = queryList(dto).get();
        return mapList.size() > 0 ? mapList.get(0) : new HashMap<>();
    }

    /**
     * 查单条Int类型返回值
     */
    public static Integer queryOneInt(PrepSqlDTO dto, String keyWord) throws Exception {
        keyWord = DbAliasEnum.getAlias(dto.getDbLinkEntity().getDbType(), keyWord);
        Map<String, Object> map = CollectionUtils.mapKeyToLower(queryOne(dto));
        if (map != null && map.size() > 0) {
            return Integer.parseInt(String.valueOf(map.get(keyWord.toLowerCase())));
        } else {
            throw new DataException(MsgCode.FA020.get());
        }
    }

    public static JdbcResult<JdbcPageMod> queryPage(PrepSqlDTO dto, String sortType, Integer currentPage, Integer pageSize) throws Exception {
        final String finalSortType = sortType;
        return new JdbcResult<>((jdbcResult)-> {
            String sortTypeStr = finalSortType;
            String orderSign = null;
            String dbEncode = dto.getDbLinkEntity().getDbType();
            String[] split = sortTypeStr.split(",");
            if (split.length > 1) {
                sortTypeStr = split[0];
                orderSign = split[1];
            }
            String selectSql = "";
            JdbcPageMod pageModel = new JdbcPageMod();
            try {
                String[] sqlArray = SqlFastUtil.getPageSql(dbEncode, dto.getSql(), sortTypeStr, orderSign, currentPage, pageSize);
                selectSql = sqlArray[0];
                //方便测试打印到控制台
                System.out.println("列表sql语句为:" + selectSql);
                dto.setSql(selectSql);
                List<?> resultData = getMybatisModel0(dto, jdbcResult.getIsLowerCase(), jdbcResult.getIsAlias());
                pageModel.setDataList(resultData);
                selectSql = sqlArray[1];
                dto.setSql(selectSql);
                pageModel.setTotalRecord(queryOneInt(dto, DbAliasEnum.TOTAL_RECORD.getAlias(dbEncode)));
                pageModel.setCurrentPage(currentPage);
                pageModel.setPageSize(pageSize);
            } catch (Exception e) {
                log.error("在线列表sql语句错误：" + selectSql);
                throw new DataException("sql异常：" + selectSql);
            }
            return pageModel;
        });
    }

    public static <T extends JdbcGetMod>JdbcResult<Pagination> queryModelPage(PrepSqlDTO dto, Pagination pagination, Class<T> modType) throws Exception {
        return new JdbcResult<>((jdbcResult)-> {
            String dbEncode = dto.getDbLinkEntity().getDbType();
            String[] sqlArray = SqlFastUtil.getPageSql(dbEncode, dto.getSql(), pagination);
            pagination.setData(
                    getMybatisModel2(dto.setSql(sqlArray[0]), modType),
                    queryOneInt(dto.setSql(sqlArray[1]), DbAliasEnum.TOTAL_RECORD.getAlias(dbEncode)));
            return pagination;
        });
    }

    /*============ 模式二：JdbcColumnModel ============*/
    /**
     * 专用：查询模板
     * 说明：DbJdbcModel对象，为通用的数据返回对象，每条信息不同字段对应的数据，包含此相应字段的信息
     */
    public static JdbcResult<List<List<JdbcColumnModel>>> queryJdbcColumns(PrepSqlDTO dto) throws Exception {
        return new JdbcResult<>((jdbcResult)-> getMybatisModel1(dto, jdbcResult.getIsLowerCase(), jdbcResult.getIsValue()));
    }

    /*============ 模式三：JdbcColumnModel ============*/
    /**
     * 自定义模板查询
     */
    public static <T extends JdbcGetMod> List<T> queryCustomMods(PrepSqlDTO dto, Class<T> modType) throws Exception {
        return getMybatisModel2(dto, modType);
    }

    /*=========================== 基础方法 ==========================*/

    /**
     * 模式一：
     * ResultSet转Map
     *
     * @return 结果集的Map集合
     */
    public static List<Map<String, Object>> getMybatisModel0(PrepSqlDTO dto, Boolean isLowercase, Boolean isAlias) throws Exception {
        dto.switchConn();
        try {
            List<Map<String, Object>> mapMods = new ArrayList<>();
            ResultHandler<?> handler = (handle) -> {
                try {
                    ResultSet rs = ResetSetHolder.getResultSet();
                    ResultSetMetaData md = rs.getMetaData();
                    Map<String, Object> map = new HashMap<>();
                    //获取字段集合信息
                    int columnCount = md.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        String fieldName = isAlias ? md.getColumnLabel(i) : md.getColumnName(i);
                        fieldName = isLowercase ? fieldName.toLowerCase() : fieldName;
                        map.put(fieldName, XSSEscape.escapeObj(rs.getObject(i)));
                    }
                    mapMods.add(map);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            @Cleanup SqlSession sqlSession = getSqlSessionFactory().openSession();
            sqlSession.select(SELECT_MAPPER, dto.getMapParams(), handler);
            return mapMods;
        }finally {
            DynamicDataSourceUtil.clearSwitchDataSource();
        }
    }

    /**
     * 模式二：
     * 内置基础结果对象 （包含类型）
     *
     * @return 包含字段信息的结果集对象
     */
    public static List<List<JdbcColumnModel>> getMybatisModel1(PrepSqlDTO dto, Boolean isLowercase, Boolean isValue) throws Exception {
        dto.switchConn();
        try {
            List<List<JdbcColumnModel>> includeFieldMods = new ArrayList<>();
            ResultHandler<?> handler = (handle) -> {
                try {
                    ResultSet rs = ResetSetHolder.getResultSet();
                    do{
                        includeFieldMods.add(JdbcColumnModel.getList(rs, isLowercase, isValue));
                    }while (rs.next());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            @Cleanup SqlSession sqlSession = getSqlSessionFactory().openSession();
            sqlSession.select(SELECT_MAPPER, dto.getMapParams(), handler);
            return includeFieldMods;
        }finally {
            DynamicDataSourceUtil.clearSwitchDataSource();
        }
    }

    /**
     * 模式三：
     * 获取自定义对象模型集合
     *
     * @param <T> 自定义对象模型类型
     * @return 自定义对象集合
     * @throws SQLException ignore
     */
    public static <T extends JdbcGetMod> List<T> getMybatisModel2(PrepSqlDTO dto, Class<T> modType) throws Exception {
        dto.switchConn();
        try {
            List<T> customMods = new ArrayList<>();
            String dbEncode = dto.getDbLinkEntity().getDbType();
            ResultHandler<?> handler = (handle) -> {
                try {
                    T t = modType.newInstance();
                    t.setMod(new ModelDTO(ResetSetHolder.getResultSet(), dbEncode));
                    t = XSSEscape.escapeObj(t);
                    customMods.add(t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            @Cleanup SqlSession sqlSession = getSqlSessionFactory().openSession();
            sqlSession.select(SELECT_MAPPER, dto.getMapParams(), handler);
            //返回值：自定义jdbc模型对象
            return customMods;
        }finally{
            DynamicDataSourceUtil.clearSwitchDataSource();
        }
    }




}
