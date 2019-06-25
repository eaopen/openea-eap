


demo


INSERT INTO oauth_client_detail (CLIENT_ID, RESOURCE_IDS, CLIENT_SECRET, SCOPE, AUTHORIZED_GRANT_TYPES, WEB_SERVER_REDIRECT_URI, AUTHORITIES, ACCESS_TOKEN_VALIDITY, REFRESH_TOKEN_VALIDITY, ADDITIONAL_INFORMATION, AUTOAPPROVE)
VALUES ('ETECH_ADMIN', 'OAUTH_DEMO', '$2a$10$cEnjtK3Eso.uEtT.ZFXbyuh7DPm5dWgR7JLvp7r4LDMDcnXEy5gU.', 'OAUTH_DEMO', 'password,refresh_token,implicit,authorization_code', 'http://www.baidu.com', null, 1800, 86400, null, 'true');

* request

http://localhost:7010/oauth/authorize?response_type=token&client_id=ETECH_ADMIN&redirect_uri=http://www.baidu.com

http://localhost:7010/oauth/authorize?response_type=token&client_id=EAP_ADMIN&redirect_uri=http://www.baidu.com

* redirect

http://www.baidu.com#access_token=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiT0FVVEhfREVNTyJdLCJ1c2VyX25hbWUiOiJhZG1pbiIsInNjb3BlIjpbIk9BVVRIX0RFTU8iXSwiZXhwIjoxNTYxNDU2NTI0LCJhdXRob3JpdGllcyI6WyJ1c2VyIl0sImp0aSI6ImE1YzQ4YWNlLTIwMjktNDdlMS1hYzAzLTg0OGQwMTEzZDI2OSIsImNsaWVudF9pZCI6IkVURUNIX0FETUlOIn0.TTJ9iG5amsSZ2Fc09ITiWaB5v0MrpIjSid7qLulYtEk-CQGVNPXyUlNV8WpYTejP9ttU9kiVFtzF04mtkUXdkzrIESsMp7a3pY950tRDh9LOYcqFxPD6pKtv7GFbRIsFX7l9dhan7gahr01T_i0VSLTjM56EP3syctZkDvNVcDmzZU6AuomEIkt-9b_S0fiGnAN0_GR8KSSJmkGHmEqhbLea897UTbHEhihkZrcEj3iXPkJuW25sm6XIm-U-RGlW4xMb59xWV09hlbGJDEBM318i1Se0RWEDk1X4QZdCfQ395vb8F_ZCYadIGrrciZYGJeQ4oXdambgU6jd0P-8TtQ&token_type=bearer&expires_in=1799&scope=OAUTH_DEMO&jti=a5c48ace-2029-47e1-ac03-848d0113d269
