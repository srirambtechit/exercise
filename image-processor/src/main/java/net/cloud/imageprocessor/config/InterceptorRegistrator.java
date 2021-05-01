package net.cloud.imageprocessor.config;

import net.cloud.imageprocessor.util.JwtHelper;
import net.cloud.imageprocessor.util.JwtValidationInterceptor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorRegistrator implements WebMvcConfigurer {

    private final ApplicationContext applicationContext;

    public InterceptorRegistrator(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JwtValidationInterceptor(applicationContext.getBean(JwtHelper.class)))
                .addPathPatterns("/job/**");
    }
}
