package com.myproject.identity_service.service;

import com.myproject.identity_service.dto.request.AuthenticationRequest;
import com.myproject.identity_service.dto.request.IntrospectRequest;
import com.myproject.identity_service.dto.response.AuthenticationResponse;
import com.myproject.identity_service.dto.response.IntrospectResponse;
import com.myproject.identity_service.entity.User;
import com.myproject.identity_service.exception.AppException;
import com.myproject.identity_service.exception.ErrorCode;
import com.myproject.identity_service.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class AuthenticationService {
    UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    @NonFinal
    protected static final String SIGNED_KEY= "OcoA2mh6ovcVX9r1Eol7lwCzgvnUAJXRx/ErvyYpuU/273j6840AJbVKc3uhdAmj"
;
    public IntrospectResponse introspect(IntrospectRequest request){
        var token = request.getToken();

        try {
            // Parse the JWT token
            JWSObject jwsObject = JWSObject.parse(token);

            // Verify the signature
            JWSVerifier verifier = new MACVerifier(SIGNED_KEY.getBytes());
            boolean valid = jwsObject.verify(verifier);

            // If the signature is invalid, return false
            if (!valid) {
                return IntrospectResponse.builder().valid(false).build();
            }

            // Extract claims from the token
            JWTClaimsSet claims = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());

            // Check if the token has expired
            Date expirationTime = claims.getExpirationTime();
            if (expirationTime == null || expirationTime.before(new Date())) {
                return IntrospectResponse.builder().valid(false).build();
            }

            // Check if the token contains required claims
            if (claims.getSubject() == null || claims.getIssuer() == null) {
                return IntrospectResponse.builder().valid(false).build();
            }

            return IntrospectResponse.builder().valid(true).build();
        } catch (Exception e) {
            log.error("Error while verifying token", e);
            return IntrospectResponse.builder().valid(false).build();
        }
    }
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByName(request.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(),
                user.getPassword());
        if(!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        var token = generateToken(user.getName());
        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(token)
                .build();

    }
    private String generateToken(String name){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(name)
                .issuer("identity-service")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .claim("custom", "custom-value")
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNED_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Error signing token", e);
            throw new RuntimeException(e);
        }
    }
}
