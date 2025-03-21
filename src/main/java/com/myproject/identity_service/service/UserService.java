package com.myproject.identity_service.service;

import com.myproject.identity_service.dto.request.UserCreationRequest;
import com.myproject.identity_service.dto.request.UserUpdateRequest;
import com.myproject.identity_service.dto.response.UserResponse;
import com.myproject.identity_service.entity.User;
import com.myproject.identity_service.enums.Role;
import com.myproject.identity_service.exception.AppException;
import com.myproject.identity_service.exception.ErrorCode;
import com.myproject.identity_service.mapper.UserMapper;
import com.myproject.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Service
public class UserService {
     final UserRepository userRepository;
     final UserMapper userMapper;

    public UserResponse createUser(UserCreationRequest request) {
        if(userRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toUser(request);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<String> roles = new HashSet<>();
        roles.add(Role.USER.name());

        user.setRoles(roles);

        return userMapper.toUserResponse(userRepository.save(user));
    }
    public List<UserResponse> getUsers(){
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse).toList();
    }
    public UserResponse getUser(String id){
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found")));
    }
    public UserResponse updateUser(String UserId, UserUpdateRequest request){
        User user = userRepository.findById(UserId)
                .orElseThrow(()->new AppException(ErrorCode.USER_NOT_EXISTED));
        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }
    public void deleteUser(String UserId){
        userRepository.deleteById(UserId);
    }
}
