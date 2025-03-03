package com.myproject.identity_service.service;

import com.myproject.identity_service.dto.request.UserCreationRequest;
import com.myproject.identity_service.dto.request.UserUpdateRequest;
import com.myproject.identity_service.entity.User;
import com.myproject.identity_service.exception.AppException;
import com.myproject.identity_service.exception.ErrorCode;
import com.myproject.identity_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User createUser(UserCreationRequest request) {
        User user = new User();
        if(userRepository.existsByName(request.getName()))
            throw new RuntimeException("ErrorCode.USER_EXISTED");
        user.setName(request.getName());
        user.setPassword(request.getPassword());
        user.setDob(request.getDob());

        return userRepository.save(user);
    }
    public List<User> getUsers(){
            return userRepository.findAll();
    }
    public User getUser(UUID id){
        return userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("User not found"));
    }
    public User updateUser(UUID UserId, UserUpdateRequest request){
        User user = getUser(UserId);
        user.setName(request.getName());
        user.setPassword(request.getPassword());
        user.setDob(request.getDob());

        return userRepository.save(user);
    }
    public void deleteUser(UUID UserId){
        userRepository.deleteById(UserId);
    }
}
