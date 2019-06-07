package org.openea.eap.cloud.gateway.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SecureDao {
    List<String> listByUserName(String username);
}