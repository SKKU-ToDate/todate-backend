package com.todate.backend;

import com.todate.backend.auth.config.GoogleOAuth2Props;
import com.todate.backend.auth.jwt.JwtProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({JwtProps.class, GoogleOAuth2Props.class})
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }

}
