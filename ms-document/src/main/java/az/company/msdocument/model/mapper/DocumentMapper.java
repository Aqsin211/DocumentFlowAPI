package az.company.msdocument.model.mapper;

import az.company.msdocument.dao.entity.DocumentEntity;
import az.company.msdocument.model.dto.response.DocumentResponseDTO;

public class DocumentMapper {

    public static DocumentResponseDTO toDTO(DocumentEntity documentEntity) {
        if (documentEntity == null) return null;

        return DocumentResponseDTO.builder()
                .fileId(documentEntity.getFileId())
                .filename(documentEntity.getFilename())
                .uploaderId(documentEntity.getUploaderId())
                .uploadedAt(documentEntity.getUploadedAt())
                .status(documentEntity.getStatus())
                .bucketName(documentEntity.getBucketName())
                .build();
    }
}
