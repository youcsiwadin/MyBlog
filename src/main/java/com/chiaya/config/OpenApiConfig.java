package com.chiaya.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger / OpenAPI 文档配置。
 *
 * <p>访问地址：</p>
 * <ul>
 *   <li>接口文档（Swagger UI）：/swagger-ui/index.html</li>
 *   <li>OpenAPI JSON：/v3/api-docs</li>
 * </ul>
 *
 * <p>调试需登录的接口时：先在文档里调用 POST /api/users/login 拿到 token，
 * 再点击页面右上角「Authorize」把 token 填进去（不必自己加 Bearer 前缀）。</p>
 */
@Configuration
public class OpenApiConfig {

    /** 安全方案名称，Swagger UI 的 Authorize 按钮对应它 */
    private static final String BEARER_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI blogOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Blog 博客系统 API")
                        .version("1.0.0")
                        .description("""
                                Blog 项目后端接口文档。

                                多数 /api 接口需要登录：先调用 POST /api/users/login 获取 token，
                                再点击右上角「Authorize」填入 token，即可在文档页直接调试接口。
                                未登录时访问这些接口会返回 401。"""))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME_NAME, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("登录接口返回的 token（只填 token 本身，不用加 Bearer 前缀）")))
                // 全局声明该安全方案，Swagger UI 的「Try it out」会自动带上 Authorization 头
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME_NAME));
    }
}
