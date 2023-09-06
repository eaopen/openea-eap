//package jnpf.database.util;
//
//import org.openea.eap.extj.constant.MsgCode;
//import org.openea.eap.extj.database.enums.DbAliasEnum;
//import org.openea.eap.extj.database.model.dbfield.JdbcColumnModel;
//import org.openea.eap.extj.database.model.dto.ModelDTO;
//import org.openea.eap.extj.database.model.dto.PrepareSqlDTO;
//import org.openea.eap.extj.database.model.interfaces.JdbcCreUpDel;
//import org.openea.eap.extj.database.model.interfaces.JdbcGetMod;
//import org.openea.eap.extj.database.model.page.JdbcPageMod;
//import org.openea.eap.extj.database.source.DbBase;
//import org.openea.eap.extj.database.sql.util.SqlFastUtil;
//import org.openea.eap.extj.exception.DataException;
//import org.openea.eap.extj.util.CollectionUtils;
//import org.openea.eap.extj.util.XSSEscape;
//import lombok.Cleanup;
//import lombok.extern.slf4j.Slf4j;
//
//import java.sql.*;
//import java.util.*;
//
///**
// * jdbc自定义工具类
// *
// */
//@Slf4j
//@D
//public class JdbcUtil {
//
//    /**
//     * 别名小写开关
//     */
//    public final static Boolean DEFAULT_IS_LOWERCASE = false;
//
//    /**
//     * 别名开关
//     */
//    public final static Boolean DEFAULT_IS_ALIAS = false;
//
//    /**
//     * 批量执行sql语句(适合增、删、改)
//     * CRUD：增加(Create)、检索(Retrieve)、更新(Update)、删除(Delete)
//     */
//    public static Boolean creUpDe(PrepareSqlDTO dto) throws SQLException {
//        Connection conn = dto.getConn();
//        String sql = dto.getPrepareSql();
//        List<?> dataList = dto.getDataList();
//        return JdbcCreUpDel.get(conn, () -> {
//            // ''单引号的字符串内容使用占位符，不会因为字符串内存在封号，而导致分割错误
//            String[] batchSql = sql.split(";");
//            boolean flag = false;
//            /*  事务自动提交（默认true，即自动提交事务）
//                注意：表引擎不为InnoDB，回滚失败，MySQL中，DDL创建Create、删除Drop和更改Alter表结构等操作回滚无效。*/
//            if(batchSql.length > 1){
//                for (String sqlOne : batchSql) {
//                    // 若使用封号多段SQL，无法定位不同SQL对应的参数，固不使用占位符参数
//                    @Cleanup PreparedStatement statement = conn.prepareStatement(sqlOne);
//                    JdbcCreUpDel.setData(statement, null);
//                    flag = statement.execute();
//                }
//            }else {
//                @Cleanup PreparedStatement statement = conn.prepareStatement(sql);
//                JdbcCreUpDel.setData(statement, dataList);
//                flag = statement.execute();
//            }
//            return flag;
//        });
//    }
//
//    /**
//     * update更新语句
//     */
//    public static Integer update(PrepareSqlDTO dto) throws SQLException {
//        Connection conn = dto.getConn();
//        return JdbcCreUpDel.get(dto.getConn(), () -> {
//            @Cleanup PreparedStatement statement = conn.prepareStatement(XSSEscape.escapeEmpty(dto.getPrepareSql()));
//            JdbcCreUpDel.setData(statement, dto.getDataList());
//            return statement.executeUpdate();
//        });
//    }
//
//    /**
//     * 不同语句批量执行
//     * 同数据源
//     * @param conn 数据源源连接
//     * @param dtoList sql参数集合
//     */
//    public static List<Boolean> creUpDeBatch(Connection conn, List<PrepareSqlDTO> dtoList) throws SQLException {
//        return JdbcCreUpDel.get(conn, () -> {
//            LinkedList<Boolean> flags = new LinkedList<>();
//            for (PrepareSqlDTO dto : dtoList) {
//                @Cleanup PreparedStatement statement = conn.prepareStatement(dto.getPrepareSql());
//                JdbcCreUpDel.setData(statement, dto.getDataList());
//                flags.add(statement.execute());
//            }
//            return flags;
//        });
//    }
//
//    /**
//     * 同一条语句批量执行
//     * 同数据源
//     * @param dto SQL语句参数对象
//     * @return 执行结果
//     * @throws SQLException ignore
//     */
//    public static int[] creUpDeBatchOneSql(PrepareSqlDTO dto) throws SQLException {
//        Connection conn = dto.getConn();
//        return JdbcCreUpDel.get(conn, () -> {
//            @Cleanup PreparedStatement statement = conn.prepareStatement(dto.getPrepareSql());
//            for(List<?> data : dto.getMultiDataList()){
//                JdbcCreUpDel.setData(statement, data);
//                statement.addBatch();
//            }
//            return statement.executeBatch();
//        });
//    }
//
//    /**
//     * 插入数据并返回自增ID
//     * @param conn 数据源源连接
//     * @param insertSql 插入语句
//     * @return 自增ID
//     */
//    public Integer insertReturnAutoId(Connection conn, String insertSql) throws DataException, SQLException {
//        @Cleanup Statement pstmt = conn.createStatement();
//        int count = pstmt.executeUpdate(insertSql, Statement.RETURN_GENERATED_KEYS);
//        if (count > 0) {
//            @Cleanup ResultSet resultSet = pstmt.getGeneratedKeys();
//            if (resultSet.next()) {
//                return resultSet.getInt(1);
//            }
//        }
//        throw new DataException("没有找到自增ID");
//    }
//
//    /*========================query查询语句==============================*/
//
//    /**
//     * 通用：多条查询
//     */
//    public static List<Map<String, Object>> queryList(PrepareSqlDTO statementDTO) throws Exception {
//        @Cleanup ResultSet rs = query(statementDTO);
//        return getModel0(rs, DEFAULT_IS_LOWERCASE, DEFAULT_IS_ALIAS);
//    }
//
//    /**
//     * 通用：多条查询（查询别名）
//     */
//    public static List<Map<String, Object>> queryListAlias(PrepareSqlDTO statementDTO)throws Exception{
//        @Cleanup ResultSet rs = query(statementDTO);
//        return getModel0(rs, DEFAULT_IS_LOWERCASE, true);
//    }
//
//    /**
//     * 通用：多条查询（开启小写）
//     */
//    public static List<Map<String, Object>> queryListLowercase(PrepareSqlDTO statementDTO) throws Exception {
//        @Cleanup ResultSet rs = query(statementDTO);
//        return getModel0(rs, true, DEFAULT_IS_ALIAS);
//    }
//
//    /**
//     * 通用：单条查询
//     */
//    public static Map<String, Object> queryOne(PrepareSqlDTO statementDTO) throws Exception {
//        List<Map<String, Object>> mapList = queryList(statementDTO);
//        return mapList.size() > 0 ? mapList.get(0) : new HashMap<>();
//    }
//
//    /**
//     * 查单条Int类型返回值
//     */
//    public static Integer queryOneInt(PrepareSqlDTO statementDTO, String keyWord) throws Exception {
//        List<Map<String, Object>> mapList = queryList(statementDTO);
//        if (mapList.size() > 0) {
//            keyWord = DbAliasEnum.getAsByDb(DbTypeUtil.getDb(statementDTO.getConn()), keyWord);
//            Map<String, Object> map = CollectionUtils.mapKeyToLower(queryOne(statementDTO));
//            return Integer.parseInt(String.valueOf(map.get(keyWord.toLowerCase())));
//        } else {
//            throw new DataException(MsgCode.FA020.get());
//        }
//    }
//
//    /**
//     * 专用：打印模板使用
//     */
//    public static List<List<JdbcColumnModel>> queryTableFields(PrepareSqlDTO statementDTO) throws Exception {
//        @Cleanup ResultSet rs = query(statementDTO);
//        return getModel1(rs, DEFAULT_IS_LOWERCASE, false);
//    }
//
//    /**
//     * 专用：查询模板
//     * 说明：DbJdbcModel对象，为通用的数据返回对象，每条信息不同字段对应的数据，包含此相应字段的信息
//     */
//    public static List<List<JdbcColumnModel>> queryIncludeFieldMods(PrepareSqlDTO statementDTO) throws Exception {
//        @Cleanup ResultSet rs = query(statementDTO);
//        return getModel1(rs, DEFAULT_IS_LOWERCASE, true);
//    }
//
//    /**
//     * 自定义模板查询
//     */
//    public static <T extends JdbcGetMod> List<T> queryCustomMods(PrepareSqlDTO statementDTO, Class<T> modType) throws Exception {
//        @Cleanup ResultSet rs = query(statementDTO);
//        return getModel2(rs, modType, DbTypeUtil.getDb(statementDTO.getConn()));
//    }
//
//    public static <T> JdbcPageMod<T> queryPage(PrepareSqlDTO statementDTO, String sortType, Integer currentPage, Integer pageSize) throws Exception {
//        return queryPage(statementDTO, sortType, currentPage, pageSize, DEFAULT_IS_LOWERCASE, true);
//    }
//
//    public static <T> JdbcPageMod<T> queryPage(PrepareSqlDTO statementDTO, String sortType, Integer currentPage, Integer pageSize, Boolean isLowercase, Boolean isAlias) throws Exception {
//        DbBase db = DbTypeUtil.getDb(statementDTO.getConn());
//        String orderSign = null;
//        String[] split = sortType.split(",");
//        if (split.length>1){
//            sortType = split[0];
//            orderSign = split[1];
//        }
//        String[] sqlArray = SqlFastUtil.getPageSql(db, statementDTO.getPrepareSql(), sortType, orderSign, currentPage, pageSize);
//        JdbcPageMod pageModel = new JdbcPageMod<>();
//        String selectSql= "";
//        try {
//            selectSql = sqlArray[0];
//            //方便测试打印到控制台
//            System.out.println("列表sql语句为:" +  selectSql);
//            statementDTO.setPrepareSql(selectSql);
//            @Cleanup ResultSet rs = query(statementDTO);
//            List<?> resultData = getModel0(rs, isLowercase, isAlias);
//            pageModel.setDataList(resultData);
//            selectSql = sqlArray[1];
//            statementDTO.setPrepareSql(selectSql);
//            pageModel.setTotalRecord(queryOneInt(statementDTO, DbAliasEnum.TOTAL_RECORD.asByDb(db)));
//            pageModel.setCurrentPage(currentPage);
//            pageModel.setPageSize(pageSize);
//        } catch (Exception e) {
//            log.error("在线列表sql语句错误："+selectSql);
//            throw new DataException("sql异常：" + selectSql);
//        }
//        return pageModel;
//    }
//
//    /*=====================================================*/
//    /**
//     * Jdbc查询
//     *
//     * @param statementDTO 数据库执行相关信息
//     * @return 查询结果
//     * @throws DataException ignore
//     */
//    private static ResultSet query(PrepareSqlDTO statementDTO) throws Exception {
//        String sql = statementDTO.getPrepareSql();
//        Connection conn = statementDTO.getConn();
//        List<?> prepareDataList = statementDTO.getDataList();
//        PreparedStatement statement = conn.prepareStatement(XSSEscape.escapeEmpty(sql));
//        if(!prepareDataList.isEmpty()){
//            for (int i = 0 ; i < prepareDataList.size() ; i++) {
//                Object dataObject = prepareDataList.get(i);
//                statement.setObject(i + 1, dataObject);
//            }
//        }
//        ResultSet result = statement.executeQuery();
//        if (result != null) {
//            return result;
//        } else {
//            throw new DataException(MsgCode.DB004.get());
//        }
//    }
//
//    /**
//     * ResultSet转Map
//     *
//     * @return 结果集的Map集合
//     * @throws SQLException ignore
//     */
//    private static List<Map<String, Object>> getModel0(ResultSet rs, Boolean isLowercase, Boolean isAlias) throws SQLException {
//        List<Map<String, Object>> mapMods = new ArrayList<>();
//        while (rs.next()) {
//            ResultSetMetaData md = rs.getMetaData();
//            Map<String, Object> map = new HashMap<>();
//            //获取字段集合信息
//            int columnCount = md.getColumnCount();
//            for (int i = 1; i <= columnCount; i++) {
//                String fieldName = isAlias ? md.getColumnLabel(i) : md.getColumnName(i);
//                fieldName = isLowercase ? fieldName.toLowerCase() : fieldName;
//                map.put(fieldName, XSSEscape.escapeEmpty(rs.getString(i)));
//            }
//            mapMods.add(map);
//        }
//        return mapMods;
//    }
//
//    /**
//     * 基本对象模型 （包含类型）
//     *
//     * @return 包含字段信息的结果集对象
//     * @throws SQLException ignore
//     */
//    private static List<List<JdbcColumnModel>> getModel1(ResultSet rs, Boolean isLowercase, Boolean isValue) throws SQLException {
//        List<List<JdbcColumnModel>> includeFieldMods = new ArrayList<>();
//        if(isValue){
//            while (rs.next()) {
//                includeFieldMods.add(JdbcColumnModel.getList(rs, isLowercase, true));
//            }
//        }else {
//            //DbJdbcModel集合保底为1条数据，为了返回字段相关信息
//            includeFieldMods.add(JdbcColumnModel.getList(rs, isLowercase, false));
//        }
//        return includeFieldMods;
//    }
//
//    /**
//     * 获取自定义对象模型集合
//     *
//     * @param <T> 自定义对象模型类型
//     * @return 自定义对象集合
//     * @throws SQLException ignore
//     */
//    private static <T extends JdbcGetMod> List<T> getModel2(ResultSet rs, Class<T> modType, DbBase dbBase) throws Exception {
//        List<T> customMods = new ArrayList<>();
//        while (rs.next()) {
//            T t = modType.newInstance();
//            t.setMod(new ModelDTO(rs, dbBase));
//            t = XSSEscape.escapeObj(t);
//            customMods.add(t);
//        }
//        //返回值：自定义jdbc模型对象
//        return customMods;
//    }
//
//}
