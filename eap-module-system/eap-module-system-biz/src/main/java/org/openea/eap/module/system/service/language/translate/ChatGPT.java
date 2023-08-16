package org.openea.eap.module.system.service.language.translate;

import cn.hutool.core.util.ObjectUtil;
import io.github.asleepyfish.annotation.EnableChatGPT;
import io.github.asleepyfish.config.ChatGPTProperties;
import io.github.asleepyfish.service.OpenAiProxyService;
import io.github.asleepyfish.util.OpenAiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableChatGPT
@Slf4j
public class ChatGPT {

    private static ChatGPT _instance;
    public static ChatGPT getInstance(){
        if(_instance==null){
            _instance = new ChatGPT();
        }
        return _instance;
    }

    private ChatGPTProperties properties;

    public ChatGPTProperties loadChatGPTProperties(){
        if(properties==null){
            properties = ChatGPTProperties.builder().token("sk-")
                    .proxyHost("127.0.0.1")
                    .proxyPort(7890)
                    .build();
        }
        return  properties;
    }
    public void chat() {
        OpenAiProxyService openAiProxyService = new OpenAiProxyService(loadChatGPTProperties());
        System.out.println(openAiProxyService.chatCompletion("Go写个程序"));
    }

    public String chat2(String prompt){
        OpenAiProxyService openAiProxyService = new OpenAiProxyService(loadChatGPTProperties());
        return openAiProxyService.chatCompletion(prompt).get(0);
    }

    public String chat(String content) {
        return OpenAiUtils.createChatCompletion(content).get(0);
    }

    private String promptTemplate4menu = "作为IT专业人员帮助翻译menu菜单(通常为名词)或button按钮(通常为动词)，翻译结果返回格式为单层json " +
            "{\"en-US\":\"System Mgt\",\"zh-CN\":\"系统管理\",\n\"ja-JP\":\"システム\"}  " +
            "输入中文%s \"%s\",中文保持不变，翻译英文和日文，\"%s\"可作为英文翻译参考，" +
            "尽量保留相同的长度，一个中文对应两个英文字符。每次只返回一组翻译。";

    public String queryMenuI18n(String type, String key, String name, int len) {
        String prompt = String.format(promptTemplate4menu, type, name, key);
//        String result = chat2(prompt);
        String result = chat(prompt);
        log.debug("prompt="+prompt);
        log.debug("result="+result);
        if(ObjectUtil.isNotEmpty(result) && !result.startsWith("{")){
            result = result.substring(result.lastIndexOf("{"), result.lastIndexOf("}")+1);
            log.debug("result="+result);
        }
        return result;
    }

    public static void main(String[] args){
        //new ChatGPT().chat();
        new ChatGPT().queryMenuI18n("menu","post","岗位管理",0);
        new ChatGPT().queryMenuI18n("menu","notice","通知公告",0);
        new ChatGPT().queryMenuI18n("button","roleUpdate","角色修改",0);
    }


}
