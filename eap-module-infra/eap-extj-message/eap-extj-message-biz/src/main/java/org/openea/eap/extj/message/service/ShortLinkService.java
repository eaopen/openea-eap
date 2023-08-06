
package org.openea.eap.extj.message.service;

import org.openea.eap.extj.base.service.SuperService;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.openea.eap.extj.message.entity.ShortLinkEntity;
import org.openea.eap.extj.message.entity.SmsFieldEntity;
import org.openea.eap.extj.message.model.messagetemplateconfig.*;

/**
 *
 * 消息模板（新）
 */
public interface ShortLinkService extends SuperService<ShortLinkEntity> {

    String shortLink (String link);

    ShortLinkEntity getInfoByLink(String link);
}
