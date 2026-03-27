package com.example.paymentsystem;

import com.example.paymentsystem.common.config.DotenvInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PaymentSystemApplication {

    public static void main(String[] args) {
     SpringApplication app = new SpringApplication(PaymentSystemApplication.class);
        app.addInitializers(new DotenvInitializer());
        app.run(args);
    }

}
