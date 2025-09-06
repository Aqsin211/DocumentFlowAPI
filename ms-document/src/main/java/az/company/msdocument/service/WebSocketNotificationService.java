package az.company.msdocument.service;

import az.company.msdocument.dao.entity.DocumentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyDocumentStatusChange(DocumentEntity documentEntity) {
        String destination = "/topic/document-status";
        messagingTemplate.convertAndSend(destination, documentEntity);
    }
}
