

-- 插入单点登录客户端的授权 ETECH_ADMIN
INSERT INTO oauth_client_detail (CLIENT_ID, RESOURCE_IDS, CLIENT_SECRET, SCOPE, AUTHORIZED_GRANT_TYPES, WEB_SERVER_REDIRECT_URI, AUTHORITIES, ACCESS_TOKEN_VALIDITY, REFRESH_TOKEN_VALIDITY, ADDITIONAL_INFORMATION, AUTOAPPROVE)
VALUES ('ETECH_ADMIN', 'OAUTH_DEMO', '$2a$10$cEnjtK3Eso.uEtT.ZFXbyuh7DPm5dWgR7JLvp7r4LDMDcnXEy5gU.', 'OAUTH_DEMO', 'password,refresh_token,implicit,authorization_code', 'http://www.baidu.com', null, 1800, 86400, null, 'true');

-- http://localhost:7010/oauth/authorize?response_type=token&client_id=ETECH_ADMIN&redirect_uri=http://www.baidu.com

--
