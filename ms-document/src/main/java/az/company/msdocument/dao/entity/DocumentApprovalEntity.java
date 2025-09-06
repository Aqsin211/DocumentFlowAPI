package az.company.msdocument.dao.entity;

import az.company.msdocument.model.enums.DocumentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_document_approvals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentApprovalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_id")
    private UUID fileId;

    @Column(name = "approver_id")
    private Long approverId;

    @Column(name = "approver_username")
    private String approverUsername;

    @Enumerated(EnumType.STRING)
    private DocumentStatus decision;

    @CreationTimestamp
    @Column(name = "decision_at")
    private LocalDateTime decisionAt;
}
