package az.company.msuser.model.mapper;

import az.company.msuser.dao.entity.UserEntity;
import az.company.msuser.model.enums.UserRoles;
import az.company.msuser.model.request.UserRequest;
import az.company.msuser.model.response.UserResponse;

public enum UserMapper {
    USER_MAPPER;

    public UserEntity mapRequestToEntity(UserRequest userRequest) {
        return UserEntity.builder()
                .username(userRequest.getUsername())
                .phoneNumber(userRequest.getPhoneNumber())
                .password(userRequest.getPassword())
                .gmail(userRequest.getGmail())
                .role(UserRoles.valueOf(userRequest.getRole()))
                .build();
    }

    public UserResponse mapEntityToResponse(UserEntity userEntity) {
        return UserResponse.builder()
                .userId(userEntity.getUserId())
                .username(userEntity.getUsername())
                .gmail(userEntity.getGmail())
                .phoneNumber(userEntity.getPhoneNumber())
                .role(String.valueOf(userEntity.getRole()))
                .build();
    }
}
