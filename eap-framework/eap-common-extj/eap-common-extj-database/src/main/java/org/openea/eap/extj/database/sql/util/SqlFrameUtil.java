package org.openea.eap.extj.database.sql.util;

import org.openea.eap.extj.database.sql.enums.base.SqlFrameBase;
import org.openea.eap.extj.util.text.CharsetKit;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.HtmlUtils;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 类功能
 *
 * 
 */
public class SqlFrameUtil {

    /**
     * 去除模糊查询前后空白
     */
    public static String keyWordTrim(String keyWord){
        return keyWord.trim();
    }

    public static String htmlE(String str){
        if (str != null){
            return HtmlUtils.htmlEscape(str, CharsetKit.UTF_8);
        }
        return null;
    }

    private static final Pattern BRACKET = Pattern.compile("\\<.+?\\>");

    private static final Pattern BRACKET2 = Pattern.compile("\\[.+?\\]");

    /**
     * 正则处理
     */
    public static String formatSqlFrame(String sqlFrame){
        sqlFrame = sqlFrame.replace("<", "").replace(">", "")
                .replace("[", "").replace("]", "");
//        if (StringUtil.isEmpty(TenantDataSourceUtil.getTenantSchema())) {
//            // 去除<param>的<>框架，<>：固定关键词，{}：参数，[]：可选关键词
//            return sqlFrame.replace("《schema》.", "");
//        }

        // 去除<param>的<>框架，<>：固定关键词，{}：参数，[]：可选关键词
        return sqlFrame.replace("《schema》.", "");
    }

    /**
     * Sql框架参数设置
     * @param sqlFrameEnum SQL框架枚举
     * @param frameParamList 框架指定参数集合
     * @param params 实际参数集合
     * @return Sql语句
     */
    public static String outSqlCommon(SqlFrameBase sqlFrameEnum, List<String> frameParamList, String... params){
        String sqlFrame = sqlFrameEnum.getSqlFrame();
        List<String> paramList = Arrays.asList(params);
        // 组成paramsMap
        Map<String, String> paramsMap = new HashMap<>();
        for (int i = 0; i < paramList.size(); i++) {
            paramsMap.put(frameParamList.get(i), paramList.get(i));
        }
        sqlFrame = sqlFrameEnum.createIncrement(sqlFrame, paramsMap);
        // 对SQL框架指定参数设置为占位符
        for (int i = 0; i < frameParamList.size(); i++) {
            sqlFrame = sqlFrame.replace(frameParamList.get(i), "?_" + i);
        }
        sqlFrame = SqlFrameUtil.formatSqlFrame(sqlFrame);
        for (int i = 0; i < paramList.size(); i++) {
            String param = paramList.get(i) != null ? paramList.get(i) : "";
            sqlFrame = sqlFrame.replace("?_" + i, param);
        }
        return sqlFrame;
    }

    /**
     * 按下标循环分割字符串
     */
    public static List<String> splitStrRepeat(String str, Integer index){
        List<String> splitList = new ArrayList<>();
        while (str.length() > index){
            String[] splitStrArrays = {str.substring(0, index), str.substring(index)};
            splitList.add(splitStrArrays[0]);
            str = splitStrArrays[1];
        }
        // 最后一个小于index的字符串
        if(StringUtils.isNotEmpty(str)){
            splitList.add(str);
        }
        return splitList;
    }


}
