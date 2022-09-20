package cn.guanjm.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CORSConfig {
    @Bean
    public CorsFilter corsFilter() {

        CorsConfiguration config = new CorsConfiguration();

        // 允许的域，不要写*，否则cookie就没法用了
        config.addAllowedOrigin("http://localhost:3000");

        // 是否发送cookie信息
        config.setAllowCredentials(true);

        // 允许的请求方式
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");

        // 允许的头
        config.addAllowedHeader("*");

        config.setMaxAge(3600L);

        // 添加映射路径， 我们拦截一切请求
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);

        // 3 返回新的CorsFilter
        return new CorsFilter(configSource);
    }
}
