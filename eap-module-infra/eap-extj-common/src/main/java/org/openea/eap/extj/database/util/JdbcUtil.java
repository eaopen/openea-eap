package org.openea.eap.extj.database.util;

import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.openea.eap.extj.base.Pagination;
import org.openea.eap.extj.constant.MsgCode;
import org.openea.eap.extj.database.enums.DbAliasEnum;
import org.openea.eap.extj.database.model.dbfield.JdbcColumnModel;
import org.openea.eap.extj.database.model.dto.JdbcResult;
import org.openea.eap.extj.database.model.dto.ModelDTO;
import org.openea.eap.extj.database.model.dto.PrepSqlDTO;
import org.openea.eap.extj.database.model.interfaces.JdbcCreUpDel;
import org.openea.eap.extj.database.model.interfaces.JdbcGetMod;
import org.openea.eap.extj.database.model.page.JdbcPageMod;
import org.openea.eap.extj.database.sql.util.SqlFastUtil;
import org.openea.eap.extj.exception.DataException;
import org.openea.eap.extj.util.XSSEscape;
import org.openea.eap.extj.util.context.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

public class JdbcUtil {
    private static final Logger log = LoggerFactory.getLogger(JdbcUtil.class);
    private static final String SELECT_MAPPER = "org.openea.eap.extj.database.mapper.JdbcMapper.getList";
    private static final String UPDATE_MAPPER = "org.openea.eap.extj.database.mapper.JdbcMapper.update";
    private static final String UPDATE_MAPPERS = "org.openea.eap.extj.database.mapper.JdbcMapper.updates";
    private static final String DELETE_MAPPER = "org.openea.eap.extj.database.mapper.JdbcMapper.delete";
    private static final String INSERT_MAPPER = "org.openea.eap.extj.database.mapper.JdbcMapper.insert";

    public JdbcUtil() {
    }

    private static SqlSessionFactory getSqlSessionFactory() {
        return (SqlSessionFactory) SpringContext.getBean(SqlSessionFactory.class);
    }

    public static int creUpDe(PrepSqlDTO dto) throws Exception {
        dto.switchConn();

        int var2;
        try {
            SqlSession sqlSession = getSqlSessionFactory().openSession();

            try {
                var2 = sqlSession.update("org.openea.eap.extj.database.mapper.JdbcMapper.update", dto.getMapParams());
            } finally {
                if (Collections.singletonList(sqlSession).get(0) != null) {
                    sqlSession.close();
                }

            }
        } finally {
            DynamicDataSourceUtil.clearSwitchDataSource();
        }

        return var2;
    }

    public static int update(PrepSqlDTO dto) throws Exception {
        dto.switchConn();

        int var2;
        try {
            SqlSession sqlSession = getSqlSessionFactory().openSession();

            try {
                var2 = sqlSession.update("org.openea.eap.extj.database.mapper.JdbcMapper.updates", dto.getMapParams());
            } finally {
                if (Collections.singletonList(sqlSession).get(0) != null) {
                    sqlSession.close();
                }

            }
        } finally {
            DynamicDataSourceUtil.clearSwitchDataSource();
        }

        return var2;
    }

    public static int delete(PrepSqlDTO dto) throws Exception {
        dto.switchConn();

        int var2;
        try {
            SqlSession sqlSession = getSqlSessionFactory().openSession();

            try {
                var2 = sqlSession.update("jnpf.database.mapper.JdbcMapper.delete", dto.getMapParams());
            } finally {
                if (Collections.singletonList(sqlSession).get(0) != null) {
                    sqlSession.close();
                }

            }
        } finally {
            DynamicDataSourceUtil.clearSwitchDataSource();
        }

        return var2;
    }

    public static int insert(PrepSqlDTO dto) throws Exception {
        dto.switchConn();

        int var2;
        try {
            SqlSession sqlSession = getSqlSessionFactory().openSession();

            try {
                var2 = sqlSession.update("jnpf.database.mapper.JdbcMapper.insert", dto.getMapParams());
            } finally {
                if (Collections.singletonList(sqlSession).get(0) != null) {
                    sqlSession.close();
                }

            }
        } finally {
            DynamicDataSourceUtil.clearSwitchDataSource();
        }

        return var2;
    }

    public static int[] creUpDeBatchOneSql(PrepSqlDTO dto) throws Exception {
        Connection conn = ConnUtil.getConnOrDefault(dto.getDbLinkEntity());

        int[] var2;
        try {
            var2 = (int[]) JdbcCreUpDel.get(conn, () -> {
                PreparedStatement statement = conn.prepareStatement(dto.getSql());

                try {
                    Iterator var3 = dto.getMultiDataList().iterator();

                    while(var3.hasNext()) {
                        List<?> data = (List)var3.next();
                        JdbcCreUpDel.setData(statement, data);
                        statement.addBatch();
                    }

                    int[] var8 = statement.executeBatch();
                    return var8;
                } finally {
                    if (Collections.singletonList(statement).get(0) != null) {
                        statement.close();
                    }

                }
            });
        } finally {
            if (Collections.singletonList(conn).get(0) != null) {
                conn.close();
            }

        }

        return var2;
    }

    public static JdbcResult<List<Map<String, Object>>> queryList(PrepSqlDTO dto) throws Exception {
        return new JdbcResult((jdbcResult) -> {
            List<Map<String, Object>> mybatisModel0 = getMybatisModel0(dto, ((JdbcResult) jdbcResult).getIsLowerCase(), ((JdbcResult) jdbcResult).getIsAlias());
            return mybatisModel0;
        });
    }

    public static Map<String, Object> queryOne(PrepSqlDTO dto) throws Exception {
        List<Map<String, Object>> mapList = (List)queryList(dto).get();
        return (Map)(mapList.size() > 0 ? (Map)mapList.get(0) : new HashMap());
    }

    public static Integer queryOneInt(PrepSqlDTO dto, String keyWord) throws Exception {
        Map<String, Object> map = queryOne(dto);
        if (map != null && map.size() > 0) {
            Iterator var3 = map.entrySet().iterator();

            while(var3.hasNext()) {
                Map.Entry<String, Object> mapEntity = (Map.Entry)var3.next();
                if (((String)mapEntity.getKey()).equalsIgnoreCase(keyWord)) {
                    return Integer.parseInt(String.valueOf(mapEntity.getValue()));
                }
            }
        }

        throw new DataException(MsgCode.FA020.get());
    }

    public static JdbcResult<JdbcPageMod> queryPage(PrepSqlDTO dto, String sortType, Integer currentPage, Integer pageSize) throws Exception {
        return new JdbcResult((jdbcResult) -> {
            String sortTypeStr = sortType;
            String orderSign = null;
            String dbEncode = dto.getDbLinkEntity().getDbType();
            String[] split = sortType.split(",");
            if (split.length > 1) {
                sortTypeStr = split[0];
                orderSign = split[1];
            }

            String selectSql = "";
            JdbcPageMod pageModel = new JdbcPageMod();

            try {
                String[] sqlArray = SqlFastUtil.getPageSql(dbEncode, dto.getSql(), sortTypeStr, orderSign, currentPage, pageSize);
                selectSql = sqlArray[0];
                System.out.println("列表sql语句为:" + selectSql);
                dto.setSql(selectSql);
                List<?> resultData = getMybatisModel0(dto, ((JdbcResult) jdbcResult).getIsLowerCase(), ((JdbcResult) jdbcResult).getIsAlias());
                pageModel.setDataList(resultData);
                selectSql = sqlArray[1];
                dto.setSql(selectSql);
                pageModel.setTotalRecord(queryOneInt(dto, DbAliasEnum.TOTAL_RECORD.getAlias(dbEncode)));
                pageModel.setCurrentPage(currentPage);
                pageModel.setPageSize(pageSize);
                return pageModel;
            } catch (Exception var13) {
                log.error("在线列表sql语句错误：" + selectSql);
                throw new DataException("sql异常：" + selectSql);
            }
        });
    }

    public static <T extends JdbcGetMod> JdbcResult<Pagination> queryModelPage(PrepSqlDTO dto, Pagination pagination, Class<T> modType) throws Exception {
        return new JdbcResult((jdbcResult) -> {
            String dbEncode = dto.getDbLinkEntity().getDbType();
            String[] sqlArray = SqlFastUtil.getPageSql(dbEncode, dto.getSql(), pagination);
            pagination.setData(getMybatisModel2(dto.setSql(sqlArray[0]), modType), (long)queryOneInt(dto.setSql(sqlArray[1]), DbAliasEnum.TOTAL_RECORD.getAlias(dbEncode)));
            return pagination;
        });
    }

    public static JdbcResult<List<List<JdbcColumnModel>>> queryJdbcColumns(PrepSqlDTO dto) throws Exception {
        return new JdbcResult((jdbcResult) -> {
            List<List<JdbcColumnModel>> mybatisModel1 = getMybatisModel1(dto,((JdbcResult) jdbcResult).getIsLowerCase(), ((JdbcResult) jdbcResult).getIsValue());
            return mybatisModel1;
        });
    }

    public static <T extends JdbcGetMod> List<T> queryCustomMods(PrepSqlDTO dto, Class<T> modType) throws Exception {
        return getMybatisModel2(dto, modType);
    }

    public static List<Map<String, Object>> getMybatisModel0(PrepSqlDTO dto, Boolean isLowercase, Boolean isAlias) throws Exception {
        dto.switchConn();

        List<Map<String, Object>> mapMods = new ArrayList();
        try {

            ResultHandler<?> handler = (handle) -> {
                try {
                    ResultSet rs = ResetSetHolder.getResultSet();
                    ResultSetMetaData md = rs.getMetaData();
                    Map<String, Object> map = new HashMap();
                    int columnCount = md.getColumnCount();

                    for(int i = 1; i <= columnCount; ++i) {
                        String fieldName = isAlias ? md.getColumnLabel(i) : md.getColumnName(i);
                        fieldName = isLowercase ? fieldName.toLowerCase() : fieldName;
                        map.put(fieldName, XSSEscape.escapeObj(rs.getObject(i)));
                    }

                    mapMods.add(map);
                } catch (Exception var10) {
                    var10.printStackTrace();
                }

            };
            SqlSession sqlSession = getSqlSessionFactory().openSession();

            try {
                sqlSession.select("jnpf.database.mapper.JdbcMapper.getList", dto.getMapParams(), handler);
            } finally {
                if (Collections.singletonList(sqlSession).get(0) != null) {
                    sqlSession.close();
                }

            }
        } finally {
            DynamicDataSourceUtil.clearSwitchDataSource();
        }

        return mapMods;
    }

    public static List<List<JdbcColumnModel>> getMybatisModel1(PrepSqlDTO dto, Boolean isLowercase, Boolean isValue) throws Exception {
        dto.switchConn();

        List<List<JdbcColumnModel>> includeFieldMods = new ArrayList();
        try {

            ResultHandler<?> handler = (handle) -> {
                try {
                    ResultSet rs = ResetSetHolder.getResultSet();

                    do {
                        includeFieldMods.add(JdbcColumnModel.getList(rs, isLowercase, isValue));
                    } while(rs.next());
                } catch (Exception var5) {
                    var5.printStackTrace();
                }

            };
            SqlSession sqlSession = getSqlSessionFactory().openSession();

            try {
                sqlSession.select("jnpf.database.mapper.JdbcMapper.getList", dto.getMapParams(), handler);
            } finally {
                if (Collections.singletonList(sqlSession).get(0) != null) {
                    sqlSession.close();
                }

            }
        } finally {
            DynamicDataSourceUtil.clearSwitchDataSource();
        }

        return includeFieldMods;
    }

    public static <T extends JdbcGetMod> List<T> getMybatisModel2(PrepSqlDTO dto, Class<T> modType) throws Exception {
        dto.switchConn();

        List<T> customMods = new ArrayList();
        try {

            String dbEncode = dto.getDbLinkEntity().getDbType();
            ResultHandler<?> handler = (handle) -> {
                try {
                    T t = modType.newInstance();
                    t.setMod(new ModelDTO(ResetSetHolder.getResultSet(), dbEncode));
                    t =  XSSEscape.escapeObj(t);
                    customMods.add(t);
                } catch (Exception var5) {
                    var5.printStackTrace();
                }

            };
            SqlSession sqlSession = getSqlSessionFactory().openSession();

            try {
                sqlSession.select("jnpf.database.mapper.JdbcMapper.getList", dto.getMapParams(), handler);
            } finally {
                if (Collections.singletonList(sqlSession).get(0) != null) {
                    sqlSession.close();
                }

            }
        } finally {
            DynamicDataSourceUtil.clearSwitchDataSource();
        }

        return customMods;
    }
}

