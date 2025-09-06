package az.company.msdocument.client;

import az.company.msdocument.client.decoder.CustomErrorDecoder;
import az.company.msdocument.model.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "ms-user",
        url = "http://localhost:8082/user",
        configuration = CustomErrorDecoder.class
)
public interface UserClient {
    @GetMapping
    ResponseEntity<UserResponse> getUserById(@RequestHeader("X-User-ID") String userId);

}
