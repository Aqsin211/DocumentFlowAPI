package az.company.msdocument.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentRequestDTO {

    @NotNull(message = "File must be provided")
    private MultipartFile file;

    @NotNull(message = "Uploader ID must be provided")
    private Long uploaderId;
}
