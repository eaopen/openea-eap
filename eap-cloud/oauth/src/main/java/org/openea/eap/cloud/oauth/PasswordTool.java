package org.openea.eap.cloud.oauth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;

public class PasswordTool {
    public static void main(String[] args) {
        String pass = "admin";
        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
        System.out.println(encode.encode(pass));

        System.out.println(new Date(1548748418952L).toString());
    }
}
