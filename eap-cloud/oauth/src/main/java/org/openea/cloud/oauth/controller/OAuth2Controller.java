package org.openea.cloud.oauth.controller;

import org.openea.cloud.oauth.config.EapOauthProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@SessionAttributes("authorizationRequest")
public class OAuth2Controller {
    @Autowired
    private EapOauthProperties properties;

    @Resource
    private JwtAccessTokenConverter converter;

    @Autowired
    @Qualifier("consumerTokenServices")
    private ConsumerTokenServices consumerTokenServices;

    @RequestMapping("/oauth/public_key")
    @ResponseBody
    public Map<String, String> publicKey() {
        return converter.getKey();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/oauth/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response, @RequestParam("access_token") String access_token) {
        consumerTokenServices.revokeToken(access_token);
        new SecurityContextLogoutHandler().logout(request, null, null);
        return "redirect:" + request.getHeader("referer");
    }

    @GetMapping("/auth/login")
    public String loginPage(Model model){
        model.addAttribute("loginProcessUrl",properties.getLoginProcessUrl());
        model.addAttribute("loginPageTitle",properties.getLoginPageTitle());
        return "login";
    }

    @RequestMapping("/custom/confirm_access")
    public ModelAndView getAccessConfirmation(Map<String, Object> model, HttpServletRequest request) throws Exception {
        AuthorizationRequest authorizationRequest = (AuthorizationRequest) model.get("authorizationRequest");
        ModelAndView view = new ModelAndView();
        view.setViewName("grant_confirm");
        view.addObject("clientId", authorizationRequest.getClientId());
        return view;
    }
}
