package org.openea.eap.extj.database.sql.util;

import org.apache.commons.lang3.StringUtils;
import org.openea.eap.extj.database.sql.enums.SqlFrameBase;
import org.springframework.web.util.HtmlUtils;

import java.util.*;
import java.util.regex.Pattern;

public class SqlFrameUtil {

    private static final Pattern BRACKET = Pattern.compile("\\<.+?\\>");
    private static final Pattern BRACKET2 = Pattern.compile("\\[.+?\\]");

    public SqlFrameUtil() {
    }

    public static String keyWordTrim(String keyWord) {
        return keyWord.trim();
    }

    public static String htmlE(String str) {
        return str != null ? HtmlUtils.htmlEscape(str, "UTF-8") : null;
    }

    public static String formatSqlFrame(String sqlFrame) {
        sqlFrame = sqlFrame.replace("<", "").replace(">", "").replace("[", "").replace("]", "");
        return sqlFrame.replace("《schema》.", "");
    }

    public static String outSqlCommon(SqlFrameBase sqlFrameEnum, List<String> frameParamList, String... params) {
        String sqlFrame = sqlFrameEnum.getSqlFrame();
        List<String> paramList = Arrays.asList(params);
        Map<String, String> paramsMap = new HashMap();

        int i;
        for(i = 0; i < paramList.size(); ++i) {
            paramsMap.put(frameParamList.get(i), paramList.get(i));
        }

        sqlFrame = sqlFrameEnum.createIncrement(sqlFrame, paramsMap);

        for(i = 0; i < frameParamList.size(); ++i) {
            sqlFrame = sqlFrame.replace((CharSequence)frameParamList.get(i), "?_" + i);
        }

        sqlFrame = formatSqlFrame(sqlFrame);

        for(i = 0; i < paramList.size(); ++i) {
            String param = paramList.get(i) != null ? (String)paramList.get(i) : "";
            sqlFrame = sqlFrame.replace("?_" + i, param);
        }

        return sqlFrame;
    }

    public static List<String> splitStrRepeat(String str, Integer index) {
        ArrayList splitList;
        String[] splitStrArrays;
        for(splitList = new ArrayList(); str.length() > index; str = splitStrArrays[1]) {
            splitStrArrays = new String[]{str.substring(0, index), str.substring(index)};
            splitList.add(splitStrArrays[0]);
        }

        if (StringUtils.isNotEmpty(str)) {
            splitList.add(str);
        }

        return splitList;
    }
}
