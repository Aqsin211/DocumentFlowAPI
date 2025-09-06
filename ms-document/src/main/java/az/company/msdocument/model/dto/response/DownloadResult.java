package az.company.msdocument.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DownloadResult {
    private byte[] data;
    private String fileName;
    private String contentType;
}