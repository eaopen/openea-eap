package org.openea.eap.extj.granter;

import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.exception.LoginException;
import org.openea.eap.extj.model.login.LoginVO;

import java.util.Map;


public interface TokenGranter {

    ActionResult<LoginVO> granter(Map<String, String> loginParameters) throws LoginException;


    ActionResult logout();

    boolean requiresAuthentication();

}
