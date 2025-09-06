package az.company.msdocument.dao.repository;

import az.company.msdocument.dao.entity.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {
    List<AuditLogEntity> findByFileIdOrderByActionAtDesc(UUID fileId);
}
