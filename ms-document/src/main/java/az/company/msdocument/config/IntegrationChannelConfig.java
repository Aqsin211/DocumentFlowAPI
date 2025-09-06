package az.company.msdocument.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

@Configuration
public class IntegrationChannelConfig {

    @Bean
    public MessageChannel documentSubmissionChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel approvalResponseChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel notificationChannel() {
        return new DirectChannel();
    }
}
