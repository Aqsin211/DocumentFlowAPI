package az.company.msdocument.controller;

import az.company.msdocument.model.dto.request.DocumentRequestDTO;
import az.company.msdocument.model.dto.response.AuditLogResponse;
import az.company.msdocument.model.dto.response.DocumentResponseDTO;
import az.company.msdocument.model.dto.response.DownloadResult;
import az.company.msdocument.service.AuditLogService;
import az.company.msdocument.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/submitter")
@RequiredArgsConstructor
public class SubmitterController {

    private final DocumentService documentService;
    private final AuditLogService auditLogService;

    @PostMapping("/documents")
    public ResponseEntity<DocumentResponseDTO> submitDocument(
            @RequestPart("file") MultipartFile file
    ) {
        Long uploaderId = Long.parseLong((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        DocumentRequestDTO request = DocumentRequestDTO.builder()
                .file(file)
                .uploaderId(uploaderId)
                .build();

        DocumentResponseDTO response = documentService.submitDocument(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/documents")
    public ResponseEntity<List<DocumentResponseDTO>> listOwnDocuments() {
        Long userId = Long.parseLong((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return ResponseEntity.ok(documentService.listDocumentsByUser(userId));
    }

    @GetMapping("/documents/{fileId}/download")
    public ResponseEntity<byte[]> downloadOwnDocument(@PathVariable UUID fileId) {
        Long userId = Long.parseLong((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        DownloadResult result = documentService.downloadDocument(fileId, userId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(result.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + result.getFileName() + "\"")
                .body(result.getData());
    }

    @GetMapping("/audit/{fileId}/csv")
    public ResponseEntity<byte[]> downloadOwnAuditCsv(@PathVariable UUID fileId) {
        Long userId = Long.parseLong((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        List<AuditLogResponse> logs = auditLogService.getLogs(fileId).stream()
                .filter(l -> userId.equals(l.getPerformerId()))
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
