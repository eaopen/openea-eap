package org.openea.eap.extj.util;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.temp.SaTempUtil;
import org.springframework.stereotype.Component;



@Component
public class TicketUtil {


    /**
     * 创建临时TOKEN
     * @param value 值
     * @param timeout 有效时间, 秒
     * @return
     */
    public static String createTicket(Object value, long timeout){
        return SaTempUtil.createToken(value, timeout);
    }

    /**
     * 获取临时TOKEN内的数据
     * @see #createTicket(Object, long)
     * @param ticket 票据
     * @return
     * @param <T>
     */
    public static <T> T parseTicket(String ticket){
        return (T) SaTempUtil.parseToken(ticket);
    }

    /**
     * 移除临时Token
     * @param ticket
     */
    public static void deleteTicket(String ticket){
        SaTempUtil.deleteToken(ticket);
    }

    /**
     * 更新Ticket内的内容
     * @see #createTicket(Object, long)
     * @param ticket 票据
     * @param value 新值
     * @param timeout 超时时间, 秒, 可空为不更新
     */
    public static void updateTicket(String ticket, Object value, Long timeout){
        Object obj = parseTicket(ticket);
        if(obj == null) return;
        String key = getTicketKey(ticket);
        if(timeout != null){
            SaManager.getSaTokenDao().setObject(key, value, timeout);
        }else{
            SaManager.getSaTokenDao().updateObject(key, value);
        }
    }

    private static String getTicketKey(String ticket){
        return SaManager.getSaTemp().splicingKeyTempToken(ticket);
    }
}
