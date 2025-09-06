package az.company.msdocument;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableIntegration
@EnableRetry
@EnableFeignClients
public class MsDocumentApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsDocumentApplication.class, args);
    }

}
