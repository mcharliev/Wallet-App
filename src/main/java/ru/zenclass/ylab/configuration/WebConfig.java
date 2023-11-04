package ru.zenclass.ylab.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.zenclass.ylab.util.JwtInterceptor;


import java.util.List;

/**
 * Конфигурационный класс для настройки веб-приложения.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Autowired
    public WebConfig(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
    }

    /**
     * Регистрирует интерсептор JwtInterceptor для обработки JWT-токенов.
     *
     * @param registry Реестр интерсепторов, тип {@link InterceptorRegistry}.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/players/**", "/transactions/**")
                .excludePathPatterns("/players/register","/players/login");
    }

    /**
     * Настраивает конвертеры сообщений HTTP для работы с JSON.
     *
     * @param converters Список конвертеров сообщений HTTP, тип {@link List}.
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
                .indentOutput(true);
        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
    }

}
