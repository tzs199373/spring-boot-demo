package com.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                //apiInfoָ�������ĵ�������Ϣ���ⲿ�ֽ���ҳ��չʾ
                .apiInfo(apiInfo())
                .select()
                //apis() ������Щ�ӿڱ�¶��swagger��
                // RequestHandlerSelectors.any() ���ж���¶
                // RequestHandlerSelectors.basePackage("com.info.*")  ָ����λ��
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    //������Ϣ��ҳ��չʾ
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("������Ŀ����")
                .description("�ӿ�����")
                //��ϵ��ʵ����
                .contact(
                        new Contact("����", "��ַ", "����")
                )
                //�汾��
                .version("1.0.0-SNAPSHOT")
                .build();
    }
}
