package org.openea.eap.framework.security.core.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import java.util.HashMap;
import java.util.Map;

public class PwdEncoderUtil {

    private static Map<String, PasswordEncoder> encoders = new HashMap<>();
    static{
        // 常用加密方式，兼容多重密码，用于旧系统升级
        encoders.put("bcrypt", new BCryptPasswordEncoder());
        encoders.put("ldap", new org.springframework.security.crypto.password.LdapShaPasswordEncoder());
        encoders.put("MD5", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("MD5"));
        encoders.put("SHA-1", new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-1"));
        encoders.put("SHA-256",
                new org.springframework.security.crypto.password.MessageDigestPasswordEncoder("SHA-256"));
        encoders.put("sha256", new org.springframework.security.crypto.password.StandardPasswordEncoder());
        encoders.put("sha2", new CustShaPwdEncoder()); // 兼容obpm加密
    }
    public static void addPasswordEncoder(String encodingId, PasswordEncoder passwordEncoder){
        encoders.put(encodingId, passwordEncoder);
    }
    public static PasswordEncoder getPasswordEncoder(String encodingId){
        return encoders.get(encodingId);
    }

    /**
     * createDelegatingPasswordEncoder
     * 兼容旧系统密码
     * @return PasswordEncoder
     * @see org.springframework.security.crypto.factory.PasswordEncoderFactories
     */
    public static PasswordEncoder getDelegatingPasswordEncoder(String encodingId) {
        Assert.isTrue(encoders.containsKey(encodingId), encodingId + " is not found in idToPasswordEncoder");
        DelegatingPasswordEncoder delegatingPasswordEncoder = new CustDelegatingPasswordEncoder(encodingId, encoders);
        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(encoders.get(encodingId));
        return delegatingPasswordEncoder;

    }
}
