package az.company.msdocument.controller;

import az.company.msdocument.model.dto.request.ApprovalRequest;
import az.company.msdocument.model.dto.response.AuditLogResponse;
import az.company.msdocument.model.dto.response.DocumentResponseDTO;
import az.company.msdocument.model.enums.DocumentStatus;
import az.company.msdocument.service.AuditLogService;
import az.company.msdocument.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/approver")
@RequiredArgsConstructor
public class ApproverController {

    private final DocumentService documentService;
    private final AuditLogService auditLogService;

    @PostMapping("/approve")
    public ResponseEntity<Void> approveOrReject(@RequestBody ApprovalRequest request) {
        documentService.processApproval(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/documents/pending")
    public ResponseEntity<List<DocumentResponseDTO>> listPendingDocuments() {
        return ResponseEntity.ok(documentService.listDocumentsByStatus(DocumentStatus.PENDING_APPROVAL));
    }

    @GetMapping("/audit/{fileId}/csv")
    public ResponseEntity<byte[]> downloadOwnActionAudit(@PathVariable UUID fileId) {
        Long approverId = Long.parseLong((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        List<AuditLogResponse> logs = auditLogService.getLogs(fileId).stream()
                .filter(logResponse -> approverId.equals(logResponse.getPerformerId()))
                .collect(Collectors.toList());

        String csv = "Action,Performed By,Timestamp,Details\n" +
                logs.stream()
                        .map(logResponse -> String.format("%s,%s,%s,%s",
                                logResponse.getAction(),
                                logResponse.getPerformerId(),
                                logResponse.getTimestamp(),
                                logResponse.getDetails() != null ? logResponse.getDetails() : ""))
                        .collect(Collectors.joining("\n"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"audit_" + fileId + ".csv\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csv.getBytes(StandardCharsets.UTF_8));
    }
}
