package org.openea.eap.framework.security.core.util;

import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

public class CustDelegatingPasswordEncoder extends DelegatingPasswordEncoder {
    public CustDelegatingPasswordEncoder(String idForEncode, Map<String, PasswordEncoder> idToPasswordEncoder) {
        super(idForEncode, idToPasswordEncoder);
    }
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (!encodedPassword.startsWith("{")) {
            // 检查密码格式，检查常见加密bcrypt/md5
            PasswordEncoder noopPasswordEncoder = null;
            if(isBCryptHash(encodedPassword)) {
                noopPasswordEncoder = PwdEncoderUtil.getPasswordEncoder("bcrypt");
            }else if(encodedPassword.matches("^[a-fA-F0-9]{32}$")){
                noopPasswordEncoder = PwdEncoderUtil.getPasswordEncoder("md5");
            }else if(encodedPassword.matches("^[a-fA-F0-9]{40}$")){
                noopPasswordEncoder = PwdEncoderUtil.getPasswordEncoder("SHA-1");
            }else{
                // SHA-256/sha256/sha2都是采用SHA-256加密，无法有效区分
                // 符合44未则优先采用obpm所使用的sha2，不符合则默认为sha256
                // 具体项目可能需要调整
                if(encodedPassword.matches("^[a-fA-F0-9]{44}$")){
                    // sha2(44位,obpm)
                    noopPasswordEncoder = PwdEncoderUtil.getPasswordEncoder("sha2");
                }else{
                    noopPasswordEncoder = PwdEncoderUtil.getPasswordEncoder("sha256");
                }
            }
            return noopPasswordEncoder.matches(rawPassword, encodedPassword);
        }
        return super.matches(rawPassword, encodedPassword);
    }
    private  boolean isBCryptHash(String password) {
        return password.matches("^\\$2[ab?y?]\\$.{56}$");
    }

}
