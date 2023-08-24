package org.openea.eap.extj.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openea.eap.extj.base.UserInfo;
import org.openea.eap.extj.database.model.entity.DbLinkEntity;
import org.openea.eap.extj.model.visualJson.FieLdsModel;
import org.openea.eap.extj.model.visualJson.TableModel;
import org.openea.eap.extj.permission.entity.UserEntity;

import java.util.List;
import java.util.Map;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("数据模型")
public class DataModel {
    @ApiModelProperty("名称")
    private Map<String, Object> dataNewMap;
    @ApiModelProperty("字段列表")
    private List<FieLdsModel> fieLdsModelList;
    @ApiModelProperty("表列表")
    private List<TableModel> tableModelList;
    @ApiModelProperty("主表id")
    private String mainId;
    @ApiModelProperty("数据库链接")
    private DbLinkEntity link;
    @ApiModelProperty("转换")
    private Boolean convert;
    @ApiModelProperty("是否oracle")
    private Boolean isOracle;
    @ApiModelProperty("用户信息")
    private UserEntity userEntity;
    //是否开启安全锁
    @ApiModelProperty("安全锁策略")
    private Boolean concurrencyLock = false;
    @ApiModelProperty("主键策略")
    private Integer primaryKeyPolicy = 1;
    @ApiModelProperty("用户信息")
    private UserInfo userInfo;
    private Boolean flowEnable = true;
    @ApiModelProperty("外链开启")
    private boolean linkOpen = false;
}
