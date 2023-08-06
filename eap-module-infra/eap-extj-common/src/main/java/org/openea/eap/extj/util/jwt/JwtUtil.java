package org.openea.eap.extj.util.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.util.Date;

public class JwtUtil {
    public JwtUtil() {
    }

    public static String getRealToken(String token) {
        String realToken;
        try {
            SignedJWT sjwt = SignedJWT.parse(token.split(" ")[1]);
            JWTClaimsSet claims = sjwt.getJWTClaimsSet();
            realToken = String.valueOf(claims.getClaim("token"));
            return realToken;
        } catch (Exception var4) {
            realToken = null;
            return null;
        }
    }

    public static Integer getSingleLogin(String token) {
        try {
            SignedJWT sjwt = SignedJWT.parse(token.split(" ")[1]);
            JWTClaimsSet claims = sjwt.getJWTClaimsSet();
            int singleLogin = Integer.parseInt(String.valueOf(claims.getClaim("singleLogin")));
            return singleLogin;
        } catch (Exception var4) {
            return 1;
        }
    }

    public static Date getExp(String token) {
        Date date;
        try {
            SignedJWT sjwt = SignedJWT.parse(token.split(" ")[1]);
            JWTClaimsSet claims = sjwt.getJWTClaimsSet();
            date = (Date)claims.getClaim("exp");
            return date;
        } catch (Exception var4) {
            date = null;
            return null;
        }
    }
}
