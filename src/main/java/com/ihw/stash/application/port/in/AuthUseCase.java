package com.ihw.stash.application.port.in;

import com.ihw.stash.adapter.in.auth.dto.*;
import org.springframework.http.ResponseEntity;

public interface AuthUseCase {
    CreateUserOutDTO createUser(CreateUserInDTO createUserInDTO);
    GetUserOutDTO getUserByUsername(String username);
    ResponseEntity<String> login(LoginInDTO loginInDTO);
    ResponseEntity<RefreshOutDTO> refresh(String refreshToken);
    UpdateUserOutDTO updateUser(UpdateUserInDTO updateUserInDTO);
}
