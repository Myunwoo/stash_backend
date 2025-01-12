package com.ihw.stash.adapter.in;

import com.ihw.stash.adapter.in.auth.dto.*;
import com.ihw.stash.adapter.in.auth.web.AuthControllerApi;
import com.ihw.stash.application.port.in.AuthUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthControllerApi {

    private final AuthUseCase authUseCase;

    @Override
    public ResponseEntity<CreateUserOutDTO> createUser(CreateUserInDTO createUserInDTO) throws Exception {
        return ResponseEntity.ok(authUseCase.createUser(createUserInDTO));
    }

    @Override
    public ResponseEntity<String> login(LoginInDTO loginInDTO) throws Exception {
        return authUseCase.login(loginInDTO);
    }

    @Override
    public ResponseEntity<RefreshOutDTO> refresh(RefreshInDTO refreshInDTO) throws Exception {
        return authUseCase.refresh(refreshInDTO.getSrt());
    }

    @Override
    public ResponseEntity<GetUserOutDTO> getUser(GetUserInDTO getUserInDTO) throws Exception {
        return ResponseEntity.ok(authUseCase.getUserByUsername(getUserInDTO.getUsername()));
    }

    @Override
    public ResponseEntity<UpdateUserOutDTO> updateUser(UpdateUserInDTO updateUserInDTO) throws Exception {
        return ResponseEntity.ok(authUseCase.updateUser(updateUserInDTO));
    }
}
