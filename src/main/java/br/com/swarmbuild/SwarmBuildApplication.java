package br.com.swarmbuild;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SwarmBuildApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwarmBuildApplication.class, args);
    }
}
