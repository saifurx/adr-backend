package com.kasa.adr;

import com.kasa.adr.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.util.TimeZone;
import java.util.concurrent.Executor;

@SpringBootApplication
@EnableWebSecurity
@EnableScheduling
@EnableAsync
public class AdrApplication {


    @Autowired
    UserService userService;

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+05:30"));
        SpringApplication.run(AdrApplication.class, args);
    }


    //  @PostConstruct
    private void addAdminUsers() {

        //userService.registerAdmin(User.builder().email("admin@kasadr.com").build());

    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("GithubLookup-");
        executor.initialize();
        return executor;
    }

}
