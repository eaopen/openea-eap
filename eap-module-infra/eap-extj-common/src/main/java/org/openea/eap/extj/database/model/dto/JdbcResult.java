package org.openea.eap.extj.database.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JdbcResult<R> {

    private Boolean isLowerCase = false;
    private Boolean isAlias = false;
    private Boolean isValue = true;
    private MyFunction<JdbcResult<R>, R> func;

    public JdbcResult(MyFunction<JdbcResult<R>, R> func) {
        this.func = func;
    }

    public R get() throws Exception {
        return this.func.apply(this);
    }

    public Boolean getIsLowerCase() {
        return this.isLowerCase;
    }

    public Boolean getIsAlias() {
        return this.isAlias;
    }

    public Boolean getIsValue() {
        return this.isValue;
    }

    public JdbcResult<R> setIsLowerCase(Boolean isLowerCase) {
        this.isLowerCase = isLowerCase;
        return this;
    }

    public JdbcResult<R> setIsAlias(Boolean isAlias) {
        this.isAlias = isAlias;
        return this;
    }

    public JdbcResult<R> setIsValue(Boolean isValue) {
        this.isValue = isValue;
        return this;
    }


    @FunctionalInterface
    public interface MyFunction<T, R> {
        R apply(T var1) throws Exception;
    }

}