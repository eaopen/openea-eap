package org.openea.eap.extj.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;

public class XSSEscape {
    private static final Logger log = LoggerFactory.getLogger(XSSEscape.class);
    private static final Pattern PATH_PATTERN = Pattern.compile("\\.\\.|~[/\\\\]|[<]|>|\"|[*]|[|]|[?]", 2);
    private static InputStream inputStream;
    private static Policy policy;
    private static Policy emptyPolicy;
    private static Policy imgOnlyBase64Policy;

    public XSSEscape() {
    }

    public static String escape(String character) {
        try {
            AntiSamy antiSamy = new AntiSamy();
            String str = character.replaceAll("&quot;", "\"");
            str = str.replaceAll("&amp;", "&");
            str = str.replaceAll("&lt;", "<");
            str = str.replaceAll("&gt;", ">");
            CleanResults scan = antiSamy.scan(str, policy);
            str = scan.getCleanHTML();
            return str;
        } catch (Exception var4) {
            log.error("转换错误：" + var4.getMessage());
            return null;
        }
    }

    public static String escapeImgOnlyBase64(String character) {
        try {
            AntiSamy antiSamy = new AntiSamy();
            String str = character.replaceAll("&quot;", "\"");
            str = str.replaceAll("&amp;", "&");
            str = str.replaceAll("&lt;", "<");
            str = str.replaceAll("&gt;", ">");
            CleanResults scan = antiSamy.scan(str, imgOnlyBase64Policy);
            str = scan.getCleanHTML();
            return str;
        } catch (Exception var4) {
            log.error("转换错误：" + var4.getMessage());
            return null;
        }
    }

    public static <T> T escapeObj(T character) {
        try {
            return (T)JsonUtil.getJsonToBean(escapeEmpty(character.toString()), character.getClass());
        } catch (Exception var2) {
            return character;
        }
    }

    public static String escapeEmpty(String character) {
        try {
            AntiSamy antiSamy = new AntiSamy();
            CleanResults scan = antiSamy.scan(character, emptyPolicy);
            return scan.getCleanHTML();
        } catch (Exception var3) {
            return character;
        }
    }

    public static String escapePath(String path) {
        Matcher matcher;
        while((matcher = PATH_PATTERN.matcher(path)).find()) {
            path = matcher.replaceAll("");
        }

        return escapeEmpty(path);
    }

    static {
        try {
            inputStream = XSSEscape.class.getClassLoader().getResourceAsStream("antisamy-ebay.xml");
            policy = Policy.getInstance(inputStream);
            inputStream.close();
            inputStream = XSSEscape.class.getClassLoader().getResourceAsStream("antisamy-ebay-imgonlybase64.xml");
            imgOnlyBase64Policy = Policy.getInstance(inputStream);
            inputStream.close();
            inputStream = XSSEscape.class.getClassLoader().getResourceAsStream("antisamy-empty.xml");
            emptyPolicy = Policy.getInstance(inputStream);
            inputStream.close();
        } catch (Exception var1) {
            var1.printStackTrace();
        }

    }
}
