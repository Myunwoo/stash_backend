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

    // API_STS_2004, 알림 조회
    @Override
    public ResponseEntity<GetReminderOutDTO> getReminder(Long reminderId) throws Exception {
        return ResponseEntity.ok(stashUseCase.getReminder(reminderId));
    }

    // API_STS_2005, 알림 리스트 조회
    @Override
    public ResponseEntity<GetReminderListOutDTO> getReminderList() throws Exception {
        return ResponseEntity.ok(stashUseCase.getReminderList());
    }

    // API_STS_2101, Stash 생성
    @Override
    public ResponseEntity<StashDetailOutDTO> createStash(CreateStashInDTO createStashInDTO) throws Exception {
        return ResponseEntity.ok(stashUseCase.createStash(createStashInDTO));
    }

    // API_STS_2102, Reminder 생성
    @Override
    public ResponseEntity<CreateReminderOutDTO> createReminder(CreateReminderInDTO createReminderInDTO) throws Exception {
        return ResponseEntity.ok(stashUseCase.createReminder(createReminderInDTO));
    }

    // API_STS_2201, Stash 수정
    @Override
    public ResponseEntity<StashDetailOutDTO> updateStash(UpdateStashInDTO updateStashInDTO) throws Exception {
        return ResponseEntity.ok(stashUseCase.updateStash(updateStashInDTO));
    }

    // API_STS_2202, Reminder 수정
    @Override
    public ResponseEntity<UpdateReminderOutDTO> updateReminder(UpdateReminderInDTO updateReminderInDTO) throws Exception {
        return ResponseEntity.ok(stashUseCase.updateReminder(updateReminderInDTO));
    }

    // API_STS_2301, Stash 삭제
    @Override
    public ResponseEntity<Void> deleteStash(Long stashId) throws Exception {
        stashUseCase.deleteStash(stashId);
        return ResponseEntity.ok().build();
    }

    // API_STS_2302, Reminder 삭제
    @Override
    public ResponseEntity<Void> deleteReminder(Long reminderId) throws Exception {
        stashUseCase.deleteReminder(reminderId);
        return ResponseEntity.ok().build();
    }
}
