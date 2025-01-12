package com.ihw.stash.adapter.in;

import com.ihw.stash.adapter.in.json.dto.CreateJsonInDTO;
import com.ihw.stash.adapter.in.json.dto.JsonDetailOutDTO;
import com.ihw.stash.adapter.in.json.dto.JsonListDTO;
import com.ihw.stash.adapter.in.json.dto.UpdateJsonInDTO;
import com.ihw.stash.adapter.in.json.web.JsonControllerApi;
import com.ihw.stash.application.port.in.JsonUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JsonControllerImpl implements JsonControllerApi {

    private final JsonUseCase jsonUseCase;

    @Override
    public ResponseEntity<JsonDetailOutDTO> createJson(CreateJsonInDTO createJsonInDTO) throws Exception {
        return ResponseEntity.ok(jsonUseCase.createJson(createJsonInDTO));
    }

    @Override
    public ResponseEntity<Void> deleteJson(Long id) throws Exception {
        jsonUseCase.deleteJson(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<JsonDetailOutDTO> getJsonDetail(Long id) throws Exception {
        return ResponseEntity.ok(jsonUseCase.getJsonDetail(id));
    }

    @Override
    public ResponseEntity<JsonListDTO> getJsonList() throws Exception {
        return ResponseEntity.ok(jsonUseCase.getJsonList());
    }

    @Override
    public ResponseEntity<JsonDetailOutDTO> updateJson(UpdateJsonInDTO updateJsonInDTO) throws Exception {
        return ResponseEntity.ok(jsonUseCase.updateJson(updateJsonInDTO));
    }
}
