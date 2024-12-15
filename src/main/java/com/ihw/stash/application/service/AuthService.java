package com.ihw.stash.application.service;

import com.ihw.stash.adapter.in.auth.dto.*;
import com.ihw.stash.adapter.in.stash.dto.User;
import com.ihw.stash.adapter.out.persistence.UserRepository;
import com.ihw.stash.application.port.in.AuthUseCase;
import com.ihw.stash.common.advice.StashException;
import com.ihw.stash.common.util.JwtUtil;
import com.ihw.stash.common.util.MessageUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService implements AuthUseCase {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MessageUtil messageUtil;

    @Override
    public GetUserOutDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new StashException(messageUtil.getFormattedMessage("MSG0004")));
        return modelMapper.map(user, GetUserOutDTO.class);
    }

    @Override
    @Transactional
    public CreateUserOutDTO createUser(CreateUserInDTO createUserInDTO) {
        validateCreateUser(createUserInDTO);
        User user = modelMapper.map(createUserInDTO, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, CreateUserOutDTO.class);
    }

    private void validateCreateUser(CreateUserInDTO createUserInDTO) {
        validateExistUser(createUserInDTO.getUsername());
    }

    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UpdateUserOutDTO updateUser(UpdateUserInDTO updateUserInDTO) {
        User existingUser = userRepository.findByUsername(updateUserInDTO.getUsername())
                .orElseThrow(() -> new StashException(messageUtil.getFormattedMessage("MSG0004")));

        modelMapper.map(updateUserInDTO, existingUser);
        if (updateUserInDTO.getPassword() != null && !updateUserInDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updateUserInDTO.getPassword()));
        }
        User savedUser = userRepository.save(existingUser);
        return modelMapper.map(savedUser, UpdateUserOutDTO.class);
    }


    public ResponseEntity<String> login(LoginInDTO loginInDTO) {
        if (!isUsernameExists(loginInDTO.getUsername())) {
            return ResponseEntity.status(404).body(messageUtil.getFormattedMessage("MSG0001"));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginInDTO.getUsername(), loginInDTO.getPassword()
                    )
            );

            String accessToken = jwtUtil.generateAccessToken(loginInDTO.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(loginInDTO.getUsername());

            ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .build();

            return ResponseEntity.ok()
                    .header("SRT", refreshTokenCookie.toString())
                    .body(accessToken);

        } catch (AuthenticationException e) {
            log.info("비밀번호 오류");
            return ResponseEntity.status(404).body(messageUtil.getFormattedMessage("MSG0002"));
        }
    }

    public ResponseEntity<RefreshOutDTO> refresh(String refreshToken) {
        try {
            if (!jwtUtil.validateRefreshToken(refreshToken)) {
                return ResponseEntity.status(403).body(null);
            }
            String username = jwtUtil.extractUsername(refreshToken);
            String newAccessToken = jwtUtil.generateAccessToken(username);
            RefreshOutDTO refreshOutDTO = new RefreshOutDTO().sat(newAccessToken);
            return ResponseEntity.ok(refreshOutDTO);
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(401).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    private String getUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
    }

    private void validateExistUser(String username) {
        if (isUsernameExists(username)) {
            throw new StashException(messageUtil.getFormattedMessage("MSG0003"));
        }
    }
}
