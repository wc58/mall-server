package top.chao58.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import top.chao58.propertis.SwaggerProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableOpenApi
@EnableKnife4j
public class SwaggerConfig {

    @Autowired
    private SwaggerProperties swaggerProperties;

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;


    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
                // 是否开启
                .enable(swaggerProperties.getEnable())
                // 元信息
                .apiInfo(apiInfo())
                .host(swaggerProperties.getTryHost())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
        //todo-chao 2021/5/14 16:51：swagger3自定义header
               /* .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());*/
    }

    private List<SecurityContext> securityContexts() {
        ArrayList<SecurityContext> securityContexts = new ArrayList<>();
        ArrayList<SecurityReference> securityReferences = new ArrayList<>();
        SecurityReference securityReference = SecurityReference.builder().scopes(new AuthorizationScope[0]).reference(tokenHeader).build();
        securityReferences.add(securityReference);
        SecurityContext securityContext = SecurityContext.builder().securityReferences(securityReferences).operationSelector(o -> o.requestMappingPattern().matches("/.*")).build();
        securityContexts.add(securityContext);
        return securityContexts;
    }

    /**
     * 设置请求头信息
     */
    private List<SecurityScheme> securitySchemes() {
        return Collections.singletonList(HttpAuthenticationScheme.JWT_BEARER_BUILDER.name(tokenHeader).build());
    }

    /**
     * 头部信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(swaggerProperties.getApplicationName())
                .description(swaggerProperties.getApplicationDescription())
                .contact(new Contact("chaoSir.", "https://chao58.top", "whr_chao@aliyun.com"))
                .version("Application Version: " + swaggerProperties.getApplicationVersion() + ", Spring Boot Version: " + SpringBootVersion.getVersion())
                .build();
    }

}
