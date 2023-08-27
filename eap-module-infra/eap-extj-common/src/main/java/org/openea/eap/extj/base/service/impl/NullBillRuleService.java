package org.openea.eap.extj.base.service.impl;

import org.openea.eap.extj.base.service.BillRuleService;
import org.springframework.stereotype.Service;

@Service
public class NullBillRuleService implements BillRuleService {
    @Override
    public String getBillNumber(String rule, boolean b) {
        return null;
    }
}
