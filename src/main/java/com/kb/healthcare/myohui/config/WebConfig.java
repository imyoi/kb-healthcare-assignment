package com.kb.healthcare.myohui.config;

import com.kb.healthcare.myohui.global.annotation.NoPrefix;
import com.kb.healthcare.myohui.global.constant.ApiUrl;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(ApiUrl.API_PREFIX,
            c -> (c.isAnnotationPresent(RestController.class) || c.isAnnotationPresent(Controller.class))
                && !c.isAnnotationPresent(NoPrefix.class)
                && !c.getPackage().getName().startsWith("org.springdoc"));
    }
}