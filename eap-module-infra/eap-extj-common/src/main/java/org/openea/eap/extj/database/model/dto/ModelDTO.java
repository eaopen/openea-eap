package org.openea.eap.extj.database.model.dto;

import lombok.Data;

import java.sql.ResultSet;

@Data
public class ModelDTO {
    private ResultSet resultSet;
    private String dbEncode;
}
