package com.unand.asrama.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String base1 = Paths.get("uploads").toAbsolutePath().toUri().toString();
        String base2 = Paths.get("../uploads").toAbsolutePath().toUri().toString();
        String base3 = new File("c:/Users/USER/Downloads/PRESENSI ASRAMA UNAND/uploads").toURI().toString();
        String base4 = new File("c:/Users/USER/Downloads/PRESENSI ASRAMA UNAND/backend/uploads").toURI().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(base1, base2, base3, base4, "file:uploads/", "file:../uploads/");
    }
}
