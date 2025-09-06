package az.company.msdocument.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class AuditLogResponse {
    private UUID fileId;
    private String action;
    private Long performerId;
    private LocalDateTime timestamp;
    private String details;
}
