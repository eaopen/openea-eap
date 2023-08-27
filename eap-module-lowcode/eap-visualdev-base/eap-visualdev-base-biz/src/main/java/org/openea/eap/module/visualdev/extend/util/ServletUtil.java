package org.openea.eap.module.visualdev.extend.util;

import org.openea.eap.extj.util.StringUtil;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

public class ServletUtil {
    private static AntPathMatcher matcher = new AntPathMatcher();

    public ServletUtil() {
    }

    public static HttpServletRequest getRequest() {
        try {
            return getRequestAttributes().getRequest();
        } catch (Exception var1) {
            return null;
        }
    }

    public static String getHeader(String name) {
        return getRequest() != null ? getRequest().getHeader(name) : null;
    }

    public static boolean getIsMobileDevice() {
        return isMobileDevice(getUserAgent());
    }

    public static boolean getIsMobileDevice(String userAgent) {
        return isMobileDevice(userAgent);
    }

    public static String getUserAgent() {
        return getHeader("User-Agent");
    }

    public static boolean isMobileDevice(String requestHeader) {
        String[] deviceArray = new String[]{"android", "windows phone", "iphone", "ios", "ipad", "mqqbrowser"};
        if (requestHeader == null) {
            return false;
        } else {
            requestHeader = requestHeader.toLowerCase();

            for(int i = 0; i < deviceArray.length; ++i) {
                if (requestHeader.indexOf(deviceArray[i]) > 0) {
                    return true;
                }
            }

            return false;
        }
    }

    public static String getServletPath() {
        return getRequest().getServletPath();
    }

    public static HttpServletResponse getResponse() {
        try {
            return getRequestAttributes().getResponse();
        } catch (Exception var1) {
            return null;
        }
    }

    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    public static ServletRequestAttributes getRequestAttributes() {
        try {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            return (ServletRequestAttributes)attributes;
        } catch (Exception var1) {
            return null;
        }
    }

    public static Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> map = new LinkedHashMap();
        Enumeration<String> enumeration = request.getHeaderNames();
        if (enumeration != null) {
            while(enumeration.hasMoreElements()) {
                String key = (String)enumeration.nextElement();
                String value = request.getHeader(key);
                map.put(key, value);
            }
        }

        return map;
    }

    public static String renderString(HttpServletResponse response, String string) {
        try {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        } catch (IOException var3) {
            var3.printStackTrace();
        }

        return null;
    }

    public static boolean isAjaxRequest(HttpServletRequest request) {
        String accept = request.getHeader("accept");
        if (accept != null && accept.indexOf("application/json") != -1) {
            return true;
        } else {
            String xRequestedWith = request.getHeader("X-Requested-With");
            if (xRequestedWith != null && xRequestedWith.indexOf("XMLHttpRequest") != -1) {
                return true;
            } else {
                String uri = request.getRequestURI();
                if (StringUtil.inStringIgnoreCase(uri, new String[]{".json", ".xml"})) {
                    return true;
                } else {
                    String ajax = request.getParameter("__ajax");
                    return StringUtil.inStringIgnoreCase(ajax, new String[]{"json", "xml"});
                }
            }
        }
    }

    public static Map<String, String> getPathVariables(String pattern, String path) {
        Map<String, String> vars = null;

        try {
            if (!StringUtil.isEmpty(path)) {
                vars = matcher.extractUriTemplateVariables(pattern, path);
            }
        } catch (Exception var4) {
        }

        if (vars == null) {
            vars = Collections.EMPTY_MAP;
        }

        return vars;
    }

    public static Map<String, String> getPathVariables(String pattern) {
        return getPathVariables(pattern, getServletPath());
    }

    public static String getRequestHost() {
        HttpServletRequest request = getRequest();
        if (request != null) {
            String host = request.getHeader("MY_HOST");
            if (StringUtil.isEmpty(host)) {
                host = request.getHeader("X-Forwarded-Host");
                if (StringUtil.isNotEmpty(host)) {
                    int index = host.lastIndexOf(",");
                    if (index != -1) {
                        return host.substring(index);
                    }

                    return host;
                }

                host = request.getHeader("Host");
            }

            if (StringUtil.isNotEmpty(host)) {
                return host;
            }
        }

        return "";
    }
}
