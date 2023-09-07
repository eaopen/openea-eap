package org.openea.eap.extj.permission.model.user.form;

import lombok.Data;

/**
 *
 * 
 */
@Data
public class UserPasswordForm {
    private String oldPassword;
    private String password;
    private String code;
}
