package az.company.msdocument.model.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ApprovalRequest {
    private UUID fileId;
    private boolean approved;
    private String approver;
}
