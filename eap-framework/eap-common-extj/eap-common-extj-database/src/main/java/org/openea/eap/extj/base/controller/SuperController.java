package org.openea.eap.extj.base.controller;

import org.openea.eap.extj.base.service.SuperService;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;

public abstract class SuperController<S extends SuperService<Entity>, Entity> {


    @Autowired
    protected S baseService;
    Class<Entity> entityClass = null;

    public Class<Entity> getEntityClass() {
        if (entityClass == null) {
            this.entityClass = (Class<Entity>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        }
        return this.entityClass;
    }

    public S getBaseService() {
        return baseService;
    }

}
