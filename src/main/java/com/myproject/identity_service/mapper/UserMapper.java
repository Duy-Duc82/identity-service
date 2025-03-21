package com.myproject.identity_service.mapper;

import com.myproject.identity_service.dto.request.UserUpdateRequest;
import com.myproject.identity_service.dto.response.UserResponse;
import com.myproject.identity_service.entity.User;
import org.mapstruct.Mapper;
import com.myproject.identity_service.dto.request.UserCreationRequest;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
    UserResponse toUserResponse(User user);
}