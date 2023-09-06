package org.openea.eap.extj.consts;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum DeviceType {

    /**
     * pc端
     */
    PC("PC"),

    /**
     * app端 手机都归为移动 自行扩展
     */
    APP("APP"),

    /**
     * 程序运行中使用的无限制临时用户
     */
    TEMPUSER("TEMPUSER"),


    /**
     * 程序运行中使用的限制临时用户， 不可访问主系统, CurrentUser接口报错
     */
    TEMPUSERLIMITED("TEMPUSERLIMITED");


    private final String device;

}
