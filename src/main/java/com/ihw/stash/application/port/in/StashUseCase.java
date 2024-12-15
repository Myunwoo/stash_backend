package com.ihw.stash.application.port.in;
import com.ihw.stash.adapter.in.stash.dto.*;
import org.springframework.stereotype.Service;

@Service
public interface StashUseCase {

    public StashListDTO getStashList();

    public StashDetailOutDTO createStash(CreateStashInDTO stashDetailInDTO);

    public CreateReminderOutDTO createReminder(CreateReminderInDTO createReminderInDTO);

    public StashDetailOutDTO getStashDetail(Long stashId);

    public GetReminderOutDTO getReminder(Long reminderId);

    public GetReminderListOutDTO getReminderList();

    public StashDetailOutDTO updateStash(UpdateStashInDTO updateStashInDTO);

    public void deleteStash(Long stashId);

    public UpdateReminderOutDTO updateReminder(UpdateReminderInDTO updateReminderInDTO);

    public void deleteReminder(Long reminderId);
}
