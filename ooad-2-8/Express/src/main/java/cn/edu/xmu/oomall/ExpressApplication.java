package cn.edu.xmu.oomall;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
//@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableConfigurationProperties
public class ExpressApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExpressApplication.class, args);
    }
}