package org.openea.eap.extj.exception;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

public class WxError implements Serializable {
    private static final long serialVersionUID = 7869786563361406291L;
    @JSONField(
            name = "errcode"
    )
    private int errorCode;
    @JSONField(
            name = "errmsg"
    )
    private String errorMsg;
    private String json;

    public WxError() {
    }

    public static WxError fromJson(String json) {
        WxError error = (WxError) JSONObject.parseObject(json, WxError.class);
        error.setJson(json);
        return error;
    }

    public static WxError fromJson(JSONObject jsonObject) {
        WxError error = newBuilder().setErrorCode(jsonObject.getInteger("errcode")).setErrorMsg(jsonObject.getString("errmsg")).build();
        return error;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getJson() {
        return this.json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public String toString() {
        return this.json != null ? this.json : "错误: Code=" + this.errorCode + ", Msg=" + this.errorMsg;
    }

    public static class Builder {
        private int errorCode;
        private String errorMsg;

        public Builder() {
        }

        public Builder setErrorCode(int errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
            return this;
        }

        public WxError build() {
            WxError wxError = new WxError();
            wxError.setErrorCode(this.errorCode);
            wxError.setErrorMsg(this.errorMsg);
            return wxError;
        }
    }
}

