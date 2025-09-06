package az.company.msdocument.config;

import az.company.msdocument.dao.entity.DocumentEntity;
import az.company.msdocument.service.NotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.messaging.MessageChannel;

@Configuration
public class DocumentIntegrationConfig {

    private final NotificationService notificationService;


    public DocumentIntegrationConfig(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Bean
    public IntegrationFlow approvalResponseFlow(MessageChannel approvalResponseChannel) {
        return flow -> flow.channel(approvalResponseChannel)
                .handle((payload, headers) -> {
                    DocumentEntity documentEntity = (DocumentEntity) payload;
                    boolean approved = documentEntity.getStatus().name().equals("APPROVED");
                    notificationService.notifySubmitter(documentEntity, approved);
                    return null;
                });
    }

    @Bean
    public IntegrationFlow documentSubmissionFlow(MessageChannel documentSubmissionChannel) {
        return IntegrationFlow.from(documentSubmissionChannel)
                .handle((payload, headers) -> {
                    DocumentEntity documentEntity = (DocumentEntity) payload;
                    notificationService.notifyApprovers(documentEntity);
                    return null;
                })
                .get();
    }

}
