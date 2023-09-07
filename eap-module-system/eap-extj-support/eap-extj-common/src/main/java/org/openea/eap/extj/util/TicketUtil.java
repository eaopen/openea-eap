package org.openea.eap.extj.util;

//import cn.dev33.satoken.SaManager;
//import cn.dev33.satoken.temp.SaTempUtil;
import org.springframework.stereotype.Component;

/**
 * todo
 */
@Component
public class TicketUtil {
    private static RedisUtil redisUtil;

    public TicketUtil(RedisUtil redisUtil) {
        TicketUtil.redisUtil = redisUtil;
    }

    public static String createTicket(String s, long l) {
        return null;
    }

    public static Boolean parseTicket(String token) {
        return true;
    }

    public static void deleteTicket(String token) {
    }

//    public static String createTicket(Object value, long timeout) {
//        return SaTempUtil.createToken(value, timeout);
//    }
//
//    public static <T> T parseTicket(String ticket) {
//        return SaTempUtil.parseToken(ticket);
//    }
//
//    public static void deleteTicket(String ticket) {
//        SaTempUtil.deleteToken(ticket);
//    }
//
//    public static void updateTicket(String ticket, Object value, Long timeout) {
//        Object obj = parseTicket(ticket);
//        if (obj != null) {
//            String key = getTicketKey(ticket);
//            if (timeout != null) {
//                SaManager.getSaTokenDao().setObject(key, value, timeout);
//            } else {
//                SaManager.getSaTokenDao().updateObject(key, value);
//            }
//
//        }
//    }
//
//    private static String getTicketKey(String ticket) {
//        return SaManager.getSaTemp().splicingKeyTempToken(ticket);
//    }
}
