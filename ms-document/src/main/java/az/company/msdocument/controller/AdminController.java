package az.company.msdocument.controller;

import az.company.msdocument.dao.repository.AuditLogRepository;
import az.company.msdocument.dao.repository.DocumentRepository;
import az.company.msdocument.model.dto.response.AuditLogResponse;
import az.company.msdocument.model.dto.response.DocumentResponseDTO;
import az.company.msdocument.model.mapper.DocumentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DocumentRepository documentRepository;
    private final AuditLogRepository auditLogRepository;

    @GetMapping("/documents")
    public List<DocumentResponseDTO> getAllDocuments(@RequestParam(required = false) String status) {
        if (status != null) {
            return documentRepository.findByStatus(
                    Enum.valueOf(az.company.msdocument.model.enums.DocumentStatus.class, status.toUpperCase())
            ).stream().map(DocumentMapper::toDTO).collect(Collectors.toList());
        }
        return documentRepository.findAll().stream().map(DocumentMapper::toDTO).collect(Collectors.toList());
    }

    @GetMapping("/audit/{fileId}/csv")
    public ResponseEntity<byte[]> exportAuditCsv(@PathVariable UUID fileId) {
        List<AuditLogResponse> logs = auditLogRepository.findByFileIdOrderByActionAtDesc(fileId)
                .stream()
                .map(a -> AuditLogResponse.builder()
                        .fileId(a.getFileId())
                        .action(a.getActionType().name())
                        .performerId(a.getPerformerId())
                        .timestamp(a.getActionAt())
                        .details(null)
                        .build())
                .collect(Collectors.toList());

        String csv = "Action,Performed By,Timestamp,Details\n" +
                logs.stream()
                        .map(l -> String.format("%s,%s,%s,%s",
                                l.getAction(),
                                l.getPerformerId(),
                                l.getTimestamp(),
                                l.getDetails() != null ? l.getDetails() : ""))
                        .collect(Collectors.joining("\n"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"audit_" + fileId + ".csv\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }
}
