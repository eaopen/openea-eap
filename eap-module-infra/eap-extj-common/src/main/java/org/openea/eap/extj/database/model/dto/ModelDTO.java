package org.openea.eap.extj.database.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.ResultSet;

@Data
@AllArgsConstructor
public class ModelDTO {
    private ResultSet resultSet;
    private String dbEncode;
}
