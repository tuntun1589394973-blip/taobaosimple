package com.example.taobaosimple.common.config;

import com.example.taobaosimple.common.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security 安全配置（基于 JWT，无状态）
 */
@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                // 禁用 CSRF（前后端分离使用 JWT）
                .csrf(AbstractHttpConfigurer::disable)

                // 启用 CORS（使用自定义配置）
                .cors(Customizer.withDefaults())

                // 无状态会话（不使用 Session）
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // 白名单与需要认证的接口划分
                .authorizeHttpRequests(auth -> auth
                        // 放行所有预检请求
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        // 放行注册/登录/用户名校验、静态资源
                        .requestMatchers(
                                "/api/v1/user/register",
                                "/api/v1/user/login",
                                "/api/v1/user/checkUsername",
                                "/images/**",
                                "/error"
                        ).permitAll()

                        // 放行只读商品与分类接口
                        .requestMatchers(
                                "/api/v1/goods/**",
                                "/api/v1/goods-types/**"
                        ).permitAll()

                        // 其他请求需认证
                        .anyRequest().authenticated()
                )

                // 未认证返回 RestResp 格式的 401
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                (request, response, e) -> {
                                    response.setStatus(401);
                                    response.setCharacterEncoding("UTF-8");
                                    response.setContentType(
                                            MediaType.APPLICATION_JSON_VALUE
                                    );
                                    response.getWriter().write(
                                            "{\"code\":\"4010\",\"message\":\"未登录或登录已过期\",\"data\":null}"
                                    );
                                }
                        )
                )

                // 禁用表单登录和 HTTP 基本认证
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 在用户名密码过滤器之前加入 JWT 过滤器
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * 全局 CORS 配置
     */
    @Bean
    public CorsConfigurationSource
    corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        // 允许的前端来源
        configuration.setAllowedOrigins(List.of(
                "http://localhost:8080",
                "http://localhost:5173"
        ));

        // 允许的 HTTP 方法
        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS"
        ));

        // 允许所有请求头
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        // 对所有路径生效
        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}
