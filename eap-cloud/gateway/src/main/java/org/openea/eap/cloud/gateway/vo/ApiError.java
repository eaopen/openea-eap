package org.openea.eap.cloud.gateway.vo;

public class ApiError {
    public final static String MESSAGE_NO_AUTHORIZATION_HEADER = "请求头中缺少Authorization设置,请参考https://tools.ietf.org/html/rfc6750中的<2.1 Authorization Request Header Field>";
    public final static String MESSAGE_INVALID_TOKEN = "无效的token";
    public final static String MESSAGE_EXPIRED_TOKEN = "token已过期";
    public final static String MESSAGE_NO_THE_API_PERMISSION = "您没有此api的访问权限";
}
