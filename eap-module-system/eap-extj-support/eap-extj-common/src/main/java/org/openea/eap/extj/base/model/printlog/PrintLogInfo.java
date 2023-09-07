

package org.openea.eap.extj.base.model.printlog;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class PrintLogInfo {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "打印人")
    private String printMan;
    @Schema(description = "打印时间")
    private Date printTime;
    @Schema(description = "打印数量")
    private Integer printNum;
    @Schema(description = "打印标题")
    private String printTitle;
    @Schema(description = "打印id")
    private String printId;
}
