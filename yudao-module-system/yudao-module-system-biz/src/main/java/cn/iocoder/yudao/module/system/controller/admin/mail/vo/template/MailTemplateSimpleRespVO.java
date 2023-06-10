package cn.iocoder.yudao.module.system.controller.admin.mail.vo.template;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 邮件模版的精简 Response VO")
@Data
public class MailTemplateSimpleRespVO {

    @Schema(description = "模版编号", required = true, example = "1024")
    private Long id;

    @Schema(description = "模版名字", required = true, example = "哒哒哒")
    private String name;

}