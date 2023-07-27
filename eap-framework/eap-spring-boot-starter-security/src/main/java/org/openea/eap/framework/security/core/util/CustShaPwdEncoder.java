package org.openea.eap.framework.security.core.util;

import java.util.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.MessageDigest;

/**
 * 兼容旧系统加密，根据项目需要定制
 */
public class CustShaPwdEncoder implements PasswordEncoder {

    /**
     * Encode the raw password.
     * Generally, a good encoding algorithm applies a SHA-1 or greater hash combined with an 8-byte or greater randomly
     * generated salt.
     */
    public String encode(CharSequence rawPassword) {
        String pwd = rawPassword.toString();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(pwd.getBytes("UTF-8"));
            return new String(Base64.getEncoder().encode(digest));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    /**
     * Verify the encoded password obtained from storage matches the submitted raw password after it too is encoded.
     * Returns true if the passwords match, false if they do not.
     * The stored password itself is never decoded.
     *
     * @param rawPassword     the raw password to encode and match
     * @param encodedPassword the encoded password from storage to compare with
     * @return true if the raw password, after encoding, matches the encoded password from storage
     */
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String enc = this.encode(rawPassword);
        return enc.equals(encodedPassword);
    }

}
