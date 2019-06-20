package org.openea.admin.rest.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * spring mvc 相关的配置
 *
 */
@Configuration
public class WebMvcConfigurers {
	
	//自定义字符串转换器
	@Bean
     public StringHttpMessageConverter stringHttpMessageConverter(){
         StringHttpMessageConverter converter  = new StringHttpMessageConverter(Charset.forName("UTF-8"));
         return converter;
     }
	
	//自定义fastjson转换器
	@Bean
	public HttpMessageConverters fastJsonHttpMessageConverters(){
	    //1.需要定义一个convert转换消息的对象;
	    FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
	    
	    //2:添加fastJson的配置信息;
	    SerializerFeature[] serializerList = {SerializerFeature.WriteDateUseDateFormat};
	    
	    //3处理中文乱码问题
	    List<MediaType> fastMediaTypes = new ArrayList<>();
	    fastMediaTypes.add(MediaType.TEXT_HTML);
	    fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
	    
	    //4.在convert中添加配置信息.
	    fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
	    fastJsonHttpMessageConverter.setFeatures(serializerList);
	    
	    HttpMessageConverter<?> converter = fastJsonHttpMessageConverter;
	    return new HttpMessageConverters(converter);

	}
	
	
}
