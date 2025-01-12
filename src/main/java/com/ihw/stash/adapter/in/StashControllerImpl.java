package com.ihw.stash.adapter.in;
import com.ihw.stash.adapter.in.stash.dto.*;
import com.ihw.stash.adapter.in.stash.web.StashControllerApi;
import com.ihw.stash.application.port.in.StashUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StashControllerImpl implements StashControllerApi {

    private final StashUseCase stashUseCase;

    // API_STS_2001, Stash 상세 조회
    @Override
    public ResponseEntity<StashDetailOutDTO> getStashDetail(Long stashId) throws Exception {
        return ResponseEntity.ok(stashUseCase.getStashDetail(stashId));
    }

    // API_STS_2002, Stash 목록 조회
    @Override
    public ResponseEntity<StashListDTO> getStashList() throws Exception {
        return ResponseEntity.ok(stashUseCase.getStashList());
    }

    // API_STS_2101, Stash 생성
    @Override
    public ResponseEntity<StashDetailOutDTO> createStash(CreateStashInDTO createStashInDTO) throws Exception {
        return ResponseEntity.ok(stashUseCase.createStash(createStashInDTO));
    }

    // API_STS_2201, Stash 수정
    @Override
    public ResponseEntity<StashDetailOutDTO> updateStash(UpdateStashInDTO updateStashInDTO) throws Exception {
        return ResponseEntity.ok(stashUseCase.updateStash(updateStashInDTO));
    }

    // API_STS_2301, Stash 삭제
    @Override
    public ResponseEntity<Void> deleteStash(Long stashId) throws Exception {
        stashUseCase.deleteStash(stashId);
        return ResponseEntity.ok().build();
    }
}
