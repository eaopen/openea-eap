package org.openea.eap.extj.database.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.util.DateUtil;
import org.openea.eap.extj.util.UserProvider;
import org.openea.eap.extj.util.context.SpringContext;

import java.util.Date;

/**
 * MybatisPlus配置类
 *
 * @deprecated
 */
//@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {


    private UserProvider userProvider;

    @Override
    public void insertFill(MetaObject metaObject) {
                userProvider = SpringContext.getBean(UserProvider.class);
                UserInfo userInfo= userProvider.get();
                Object enabledMark = this.getFieldValByName("enabledMark", metaObject);
                Object creatorUserId = this.getFieldValByName("creatorUserId", metaObject);
                Object creatorTime = this.getFieldValByName("creatorTime", metaObject);
                Object creatorUser = this.getFieldValByName("creatorUser", metaObject);
                if (enabledMark == null) {
                    this.setFieldValByName("enabledMark", 1, metaObject);
                }
                if (creatorUserId == null) {
                    this.setFieldValByName("creatorUserId", userInfo.getUserId(), metaObject);
                }
                if (creatorTime == null) {
                    this.setFieldValByName("creatorTime", DateUtil.getNowDate(), metaObject);
                }
                if (creatorUser == null) {
                    this.setFieldValByName("creatorUser", userInfo.getUserId(), metaObject);
                }
        }

    @Override
    public void updateFill(MetaObject metaObject) {
        userProvider = SpringContext.getBean(UserProvider.class);
        UserInfo userInfo = userProvider.get();
        this.setFieldValByName("lastModifyTime", new Date(), metaObject);
        this.setFieldValByName("lastModifyUserId", userInfo.getUserId(), metaObject);
        this.setFieldValByName("lastModifyUser", userInfo.getUserId(), metaObject);

    }


}
