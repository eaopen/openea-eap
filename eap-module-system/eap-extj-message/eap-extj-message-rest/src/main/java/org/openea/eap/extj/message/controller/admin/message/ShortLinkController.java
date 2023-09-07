package org.openea.eap.extj.message.controller.admin.message;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import org.openea.eap.extj.base.controller.SuperController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.openea.eap.extj.base.ActionResult;
import org.openea.eap.extj.message.service.ShortLinkService;
import org.openea.eap.extj.config.ConfigValueUtil;
import org.openea.eap.extj.config.JnpfOauthConfig;
import org.openea.eap.extj.consts.DeviceType;
import org.openea.eap.extj.exception.LoginException;
import org.openea.eap.extj.message.entity.ShortLinkEntity;
import org.openea.eap.extj.permission.service.UserService;
import org.openea.eap.extj.util.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
@Tag(name = "短链接跳转", description = "message")
@RequestMapping("/api/message/ShortLink")
public class ShortLinkController extends SuperController<ShortLinkService, ShortLinkEntity> {
    @Autowired
    private UserProvider userProvider;
    @Autowired
    private UserService userService;
    @Autowired
    private ShortLinkService shortLinkService;
    @Autowired
    private ConfigValueUtil configValueUtil;
    @Autowired
    private JnpfOauthConfig jnpfOauthConfig;
    @Autowired
    protected AuthUtil authUtil;



    /**
     * 消息发送配置弹窗列表
     *
     *
     * @return
     */
//    @NoDataSourceBind
    @Operation(summary = "根据短链接获取实际链接地址")
    @Parameters({
            @Parameter(name = "shortLink", description = "短链接", required = true),
            @Parameter(name = "tenant", description = "租户")
    })
    @GetMapping(value = {"/{shortLink}/{tenant}","/{shortLink}"})
    public ActionResult getShortUrl(@PathVariable("shortLink") String shortLink,@PathVariable(value = "tenant" , required = false) String tenant,HttpServletResponse response) throws LoginException, IOException {
        if (configValueUtil.isMultiTenancy()) {
            if (StringUtil.isNotEmpty(tenant)) {
                //切换成租户库
//                TenantDataSourceUtil.switchTenant(tenant);
            } else {
                return ActionResult.fail("缺少租户信息");
            }
        }
        String link = new String();
        ShortLinkEntity entity = shortLinkService.getInfoByLink(shortLink);
        DeviceType type = UserProvider.getDeviceForAgent();
        if (entity != null) {
            String encode = "";
            String token = "";
//            String token = authUtil.loginTempUser(entity.getUserId(), tenant);
//            if (StringUtil.isEmpty(token)) {
//                return ActionResult.fail("获取token失败");
//            }
//            if(StringUtil.isNotEmpty(entity.getShortLink())) {
//                String bodyText = entity.getBodyText();
//                Map<String, Object> map = new HashMap<>();
//                map = JsonUtil.stringToMap(bodyText);
//                map.put("token", token);
//                bodyText = map.toString();
//                byte[] bytes = bodyText.getBytes(StandardCharsets.UTF_8);
//                encode = Base64.getEncoder().encodeToString(bytes);
//            }
            if (entity.getIsUsed() == 1) {
                if (entity.getClickNum() < entity.getUnableNum() && entity.getUnableTime().after(DateUtil.getNowDate())) {
                    if (DeviceType.PC.equals(type)) {
                        link = entity.getRealPcLink() + "&token=" + token;
                        entity.setClickNum(entity.getClickNum()+1);
                        shortLinkService.updateById(entity);
                    } else {
                        link = entity.getRealAppLink() + "&token=" + token;
                        entity.setClickNum(entity.getClickNum()+1);
                        shortLinkService.updateById(entity);
                    }
                } else {
                    return ActionResult.fail("链接已失效");
                }
            } else {
                if (entity.getUnableTime().after(DateUtil.getNowDate())) {
                    if (DeviceType.PC.equals(type)) {
                        link = entity.getRealPcLink() + "&token=" + token;
                        entity.setClickNum(entity.getClickNum()+1);
                        shortLinkService.updateById(entity);
                    } else {
                        link = entity.getRealAppLink() + "&token=" + token;
                        entity.setClickNum(entity.getClickNum()+1);
                        shortLinkService.updateById(entity);
                    }
                } else {
                    return ActionResult.fail("链接已失效");
                }
            }

        } else {
            return ActionResult.fail("无效链接");
        }
        response.sendRedirect(link);
        return ActionResult.success("");
    }

}
