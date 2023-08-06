package org.openea.eap.extj.base.controller;

import org.openea.eap.extj.base.service.SuperService;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;

public abstract class SuperController<S extends SuperService<Entity>, Entity> {
    @Autowired
    protected S baseService;
    Class<Entity> entityClass = null;

    public SuperController() {
    }

    public Class<Entity> getEntityClass() {
        if (this.entityClass == null) {
            this.entityClass = (Class)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        }

        return this.entityClass;
    }

    public S getBaseService() {
        return this.baseService;
    }
}
