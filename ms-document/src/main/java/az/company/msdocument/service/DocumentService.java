package az.company.msdocument.service;

import az.company.msdocument.client.UserClient;
import az.company.msdocument.dao.entity.DocumentApprovalEntity;
import az.company.msdocument.dao.entity.DocumentEntity;
import az.company.msdocument.dao.repository.DocumentRepository;
import az.company.msdocument.dao.repository.UserDocumentApprovalRepository;
import az.company.msdocument.exception.DocumentNotFoundException;
import az.company.msdocument.model.dto.request.DocumentRequestDTO;
import az.company.msdocument.model.dto.request.ApprovalRequest;
import az.company.msdocument.model.dto.response.DocumentResponseDTO;
import az.company.msdocument.model.dto.response.DownloadResult;
import az.company.msdocument.model.enums.ActionType;
import az.company.msdocument.model.enums.DocumentStatus;
import az.company.msdocument.model.mapper.DocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserDocumentApprovalRepository approvalRepository;
    private final WebSocketNotificationService webSocketNotificationService;
    private final MinIOStorageService minIOStorageService;
    private final MessageChannel documentSubmissionChannel;
    private final MessageChannel approvalResponseChannel;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final UserClient userClient;

    private final String PENDING_BUCKET = "pending";
    private final String APPROVED_BUCKET = "approved";
    private final String REJECTED_BUCKET = "rejected";

    @Transactional
    public DocumentResponseDTO submitDocument(DocumentRequestDTO request) {
        var file = request.getFile();
        UUID fileId = UUID.randomUUID();
        String internalFilename = fileId + "_" + file.getOriginalFilename();

        // Upload to pending bucket
        minIOStorageService.uploadFile(PENDING_BUCKET, internalFilename, file);

        // Save document entity
        DocumentEntity documentEntity = DocumentEntity.builder()
                .fileId(fileId)
                .filename(internalFilename)
                .uploaderId(request.getUploaderId())
                .status(DocumentStatus.PENDING_APPROVAL)
                .bucketName(PENDING_BUCKET)
                .build();
        documentRepository.save(documentEntity);

        // Log action
        auditLogService.logAction(fileId, ActionType.SUBMITTED, request.getUploaderId());

        // Send for approver notification (via integration channel)
        documentSubmissionChannel.send(MessageBuilder.withPayload(documentEntity).build());

        return DocumentMapper.toDTO(documentEntity);
    }

    @Transactional
    public void approveOrReject(UUID fileId, Long approverId, String approverUsername, boolean approve) {
        DocumentEntity documentEntity = documentRepository.findByFileId(fileId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found"));

        DocumentStatus newStatus = approve ? DocumentStatus.APPROVED : DocumentStatus.REJECTED;
        String targetBucket = approve ? APPROVED_BUCKET : REJECTED_BUCKET;

        // Move file in MinIO
        DownloadResult downloaded = minIOStorageService.downloadFile(documentEntity.getBucketName(), documentEntity.getFilename());
        minIOStorageService.deleteFile(documentEntity.getBucketName(), documentEntity.getFilename());
        minIOStorageService.uploadFile(targetBucket, documentEntity.getFilename(), downloaded.getData(), downloaded.getContentType());

        // Update document entity
        documentEntity.setStatus(newStatus);
        documentEntity.setBucketName(targetBucket);
        documentRepository.save(documentEntity);

        // Save approval record
        approvalRepository.save(DocumentApprovalEntity.builder()
                .fileId(fileId)
                .approverId(approverId)
                .approverUsername(approverUsername)
                .decision(newStatus)
                .build()
        );

        // Log approval/rejection
        auditLogService.logAction(fileId, approve ? ActionType.APPROVED : ActionType.REJECTED, approverId);

        // Notify submitter
        notificationService.notifySubmitter(documentEntity, approve);

        // Notify WebSocket clients and send integration message
        approvalResponseChannel.send(MessageBuilder.withPayload(documentEntity).build());
        webSocketNotificationService.notifyDocumentStatusChange(documentEntity);
    }

    @Transactional
    public void processApproval(ApprovalRequest request) {
        Long approverId = parseLongSafe(request.getApprover());
        approveOrReject(request.getFileId(), approverId, request.getApprover(), request.isApproved());
    }

    private Long parseLongSafe(String s) {
        if (s == null) return null;
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public List<DocumentResponseDTO> listDocumentsByUser(Long userId) {
        return documentRepository.findByUploaderId(userId)
                .stream()
                .map(DocumentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<DocumentResponseDTO> listDocumentsByStatus(DocumentStatus status) {
        return documentRepository.findByStatus(status)
                .stream()
                .map(DocumentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<DocumentResponseDTO> listAllDocuments() {
        return documentRepository.findAll()
                .stream()
                .map(DocumentMapper::toDTO)
                .collect(Collectors.toList());
    }
}
