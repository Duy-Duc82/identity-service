package com.myproject.identity_service.controller;

import com.myproject.identity_service.dto.request.ApiResponse;
import com.myproject.identity_service.dto.request.AuthenticationRequest;
import com.myproject.identity_service.dto.request.IntrospectRequest;
import com.myproject.identity_service.dto.response.AuthenticationResponse;
import com.myproject.identity_service.dto.response.IntrospectResponse;
import com.myproject.identity_service.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class AuthenticationController {
    AuthenticationService authenticationService;
    @PostMapping("/get-token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
       var result =  authenticationService.authenticate(request);
       return ApiResponse.<AuthenticationResponse>builder().
               result(result)
               .build();
    }
    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) {
        var result = authenticationService.introspect(request);

        return ApiResponse.<IntrospectResponse>builder()
                .message(result.isValid() ? "Token is valid" : "Token is invalid") // Message for client
                .code(result.isValid() ? 200 : 401) // 200 for valid, 401 for invalid token
                .result(result)
                .build();
    }

}
