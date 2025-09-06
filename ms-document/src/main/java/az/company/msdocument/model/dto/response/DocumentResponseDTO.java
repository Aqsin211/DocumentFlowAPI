package az.company.msdocument.model.dto.response;

import az.company.msdocument.model.enums.DocumentStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponseDTO {

    private UUID fileId;

    private String filename;

    private Long uploaderId;

    private LocalDateTime uploadedAt;

    private DocumentStatus status;

    private String bucketName;
}
