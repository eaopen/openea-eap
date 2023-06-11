package org.openea.eap.framework.sms.core.client.impl.debug;

import org.openea.eap.framework.common.exception.ErrorCode;
import org.openea.eap.framework.common.exception.enums.GlobalErrorCodeConstants;
import org.openea.eap.framework.sms.core.client.SmsCodeMapping;
import org.openea.eap.framework.sms.core.enums.SmsFrameworkErrorCodeConstants;

import java.util.Objects;

/**
 * 钉钉的 SmsCodeMapping 实现类
 *
 */
public class DebugDingTalkCodeMapping implements SmsCodeMapping {

    @Override
    public ErrorCode apply(String apiCode) {
        return Objects.equals(apiCode, "0") ? GlobalErrorCodeConstants.SUCCESS : SmsFrameworkErrorCodeConstants.SMS_UNKNOWN;
    }

}
