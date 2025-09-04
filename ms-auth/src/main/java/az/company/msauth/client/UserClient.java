package az.company.msauth.client;

import az.company.msauth.client.decoder.CustomErrorDecoder;
import az.company.msauth.model.request.AuthRequest;
import az.company.msauth.model.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "ms-user",
        url = "http://localhost:8082/user",
        configuration = {CustomErrorDecoder.class}
)
public interface UserClient {
    @PostMapping("/validation")
    Boolean userValid(@RequestHeader("X-User-ID") String userId,
                      @RequestHeader("X-role") String role,
                      @RequestBody AuthRequest authRequest);

    @GetMapping("/name")
    ResponseEntity<UserResponse> getUserByUsername(@RequestHeader("X-User-ID") String userId,
                                                   @RequestHeader("X-role") String role,
                                                   @RequestHeader("X-Username") String username);

}