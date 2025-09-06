package az.company.msdocument.dao.repository;

import az.company.msdocument.dao.entity.DocumentEntity;
import az.company.msdocument.model.enums.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    Optional<DocumentEntity> findByFileId(UUID fileId);

    List<DocumentEntity> findByUploaderId(Long uploaderId);

    List<DocumentEntity> findByStatus(DocumentStatus status);

}
