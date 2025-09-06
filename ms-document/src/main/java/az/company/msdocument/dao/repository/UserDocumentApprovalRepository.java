package az.company.msdocument.dao.repository;

import az.company.msdocument.dao.entity.DocumentApprovalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserDocumentApprovalRepository extends JpaRepository<DocumentApprovalEntity, Long> {
}
