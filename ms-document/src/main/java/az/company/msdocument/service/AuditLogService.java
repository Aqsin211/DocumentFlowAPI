package az.company.msdocument.service;

import az.company.msdocument.dao.entity.AuditLogEntity;
import az.company.msdocument.dao.repository.AuditLogRepository;
import az.company.msdocument.model.dto.response.AuditLogResponse;
import az.company.msdocument.model.enums.ActionType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAction(UUID fileId, ActionType action, Long performerId) {
        AuditLogEntity log = AuditLogEntity.builder()
                .fileId(fileId)
                .actionType(action)
                .performerId(performerId)
                .build();
        auditLogRepository.save(log);
    }

    public List<AuditLogResponse> getLogs(UUID fileId) {
        return auditLogRepository.findByFileIdOrderByActionAtDesc(fileId)
                .stream()
                .map(a -> AuditLogResponse.builder()
                        .fileId(a.getFileId())
                        .action(a.getActionType() != null ? a.getActionType().name() : null)
                        .performerId(a.getPerformerId())
                        .timestamp(a.getActionAt())
                        .build())
                .toList();
    }
}
