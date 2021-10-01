package com.wuqr.rabbitmq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author wql78
 * @title: SwaggerConfig
 * @description: @TODO
 * @date 2021-10-01 12:53:24
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket webApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("webApi") // 分组
                .apiInfo(webApiInfo())
                .select()
                .build();
    }
    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder()
                .title("rabbitmq 接口文档") // 文档标题
                .description("本文档描述了 rabbitmq 微服务接口定义") // 描述
                .version("1.0") // 版本
                .contact(new Contact("enjoy6288", "http://atguigu.com",
                        "wql789@qq.com")) // 联系人
                .build();
    }
}
