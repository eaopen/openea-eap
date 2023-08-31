package org.openea.eap.extend.mp.controller.admin.statistics.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Schema(description = "管理后台 - 某一天的消息发送概况数据 Response VO")
@Data
public class MpStatisticsUserCumulateRespVO {

    @Schema(description = "日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private Date refDate;

    @Schema(description = "累计粉丝量", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer cumulateUser;

}
