package org.openea.eap.cloud.gateway.secure;

import org.apache.commons.lang.StringUtils;
import org.openea.eap.cloud.gateway.api.remote.OauthClient;
import org.openea.eap.cloud.gateway.config.ApplicationProperties;
import org.openea.eap.cloud.gateway.dao.SecureDao;
import org.openea.eap.cloud.gateway.vo.ApiError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthorityService {
    private Logger logger = LoggerFactory.getLogger(AuthorityService.class);

    @Autowired
    private ApplicationProperties applicationProperties;

    final String AUD = "aud";

    final String CLIENT_ID = "client_id";

    final String EXP = "exp";

    final String JTI = "jti";

    final String GRANT_TYPE = "grant_type";

    final String ATI = "ati";

    final String SCOPE = "scope";

    final String AUTHORITIES = "authorities";

    @Resource
    private SecureDao authorityDao;

    @Resource
    private OauthClient oauthClient;

    @PostConstruct
    public void init() {
        initVerifier();
    }

    public void initVerifier() {
        if (verifier != null) {
            return;
        }
        this.verifierKey = applicationProperties.getJwtPublicKey();
        if (StringUtils.isBlank(this.verifierKey)) {
            Map<String, String> publicKeyObject = null;
            if (oauthClient == null) {
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<Map> responseEntity = restTemplate.getForEntity(applicationProperties.getJwtPublicKeyUrl(), Map.class);
                publicKeyObject = responseEntity.getBody();
            } else {
                try {
                    publicKeyObject = oauthClient.publicKey();
                } catch (Exception e) {
                    logger.warn(e.getMessage());
                }
            }
            if (publicKeyObject != null) {
                this.verifierKey = publicKeyObject.get("value").toString();
            }
        }
        if (StringUtils.isNotBlank(this.verifierKey)) {
            this.verifierKey = this.verifierKey.replaceAll("\\|", "\n");
            SignatureVerifier verifier = new MacSigner(verifierKey);
            verifier = new RsaVerifier(verifierKey);
            this.verifier = verifier;
        }
    }

    private String verifierKey;
    private SignatureVerifier verifier;
    private JsonParser objectMapper = JsonParserFactory.getJsonParser();

    public Map<String, Object> decodeAccessToken(String token) {
        try {
            initVerifier();
            Jwt jwt = JwtHelper.decodeAndVerify(token, verifier);
            String claimsStr = jwt.getClaims();
            Map<String, Object> claims = objectMapper.parseMap(claimsStr);
            if (claims.containsKey(EXP) && claims.get(EXP) instanceof Integer) {
                Integer intValue = (Integer) claims.get(EXP);
                claims.put(EXP, new Long(intValue));
            }
            return claims;
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

    public Map<String, Boolean> listByUserName(String username) {
        List<String> list = authorityDao.listByUserName(username);
        Map<String, Boolean> map = new HashMap<>();
        if (list != null && list.size() > 0) {
            for (String path : list) {
                if (path.indexOf(",") == -1) {
                    map.put(path.trim(), true);
                } else {
                    String[] ps = path.split(",");
                    for (String path1 : ps) {
                        map.put(path1.trim(), true);
                    }
                }

            }
        }
        return map;
    }

    public Object[] canVisit(Map<String, Object> claims, String path) {
        if (claims == null) {
            return new Object[]{400, ApiError.MESSAGE_INVALID_TOKEN};
        } else {
            String username = (String) claims.get("user_name");
            if (StringUtils.isBlank(username)) {
                return new Object[]{400, ApiError.MESSAGE_INVALID_TOKEN};
            } else {
                boolean valid = new Date(Integer.valueOf(claims.get("exp").toString()).longValue() * 1000).after(new Date());
                if (!valid) {
                    return new Object[]{401, ApiError.MESSAGE_EXPIRED_TOKEN};
                }
                Integer userId = (Integer) claims.get("user_id");
                boolean hasRight = userId.intValue()==0
                        || path.equalsIgnoreCase("/user-info")
                        || path.equalsIgnoreCase("/avatar")
                        ||listByUserName(username).containsKey(path);
                if (!hasRight) {
                    return new Object[]{401, ApiError.MESSAGE_NO_THE_API_PERMISSION};
                }
                return new Object[]{0};
            }
        }
    }
}
