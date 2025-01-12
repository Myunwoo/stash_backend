package com.ihw.stash.application.port.in;
import com.ihw.stash.adapter.in.stash.dto.*;
import org.springframework.stereotype.Service;

@Service
public interface StashUseCase {

    public StashListDTO getStashList();

    public StashDetailOutDTO createStash(CreateStashInDTO stashDetailInDTO);

    public StashDetailOutDTO getStashDetail(Long stashId);

    public StashDetailOutDTO updateStash(UpdateStashInDTO updateStashInDTO);

    public void deleteStash(Long stashId);
}
