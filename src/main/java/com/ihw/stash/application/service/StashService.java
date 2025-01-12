package com.ihw.stash.application.service;

import com.ihw.stash.adapter.in.stash.dto.*;
import com.ihw.stash.adapter.out.persistence.StashRepository;
import com.ihw.stash.application.port.in.StashUseCase;
import com.ihw.stash.common.advice.StashException;
import com.ihw.stash.common.util.MessageUtil;
import com.ihw.stash.common.util.UserUtil;
import com.ihw.stash.type.StashType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class StashService implements StashUseCase {

    private final StashRepository stashRepository;
    private final MessageUtil messageUtil;
    private final ModelMapper modelMapper;
    private final UserUtil userUtil;

    @Override
    public StashListDTO getStashList() {
        String username = userUtil.getUsername();
        StashListDTO stashListDTO = new StashListDTO();
        stashListDTO.setStashDtlList(stashRepository.findAllByUsername(username).stream()
                .map(stash -> modelMapper.map(stash, StashDetailOutDTO.class))
                .toList());
        return stashListDTO;
    }

    @Override
    @Transactional
    public StashDetailOutDTO createStash(CreateStashInDTO stashDetailInDTO) {
        validateCreateStash(stashDetailInDTO);
        String username = userUtil.getUsername();
        Stash stash = modelMapper.map(stashDetailInDTO, Stash.class);
        stash.setUsername(username);
        Stash savedStash = stashRepository.save(stash);
        return modelMapper.map(savedStash, StashDetailOutDTO.class);
    }

    private void validateCreateStash(CreateStashInDTO stashDetailInDTO) {
        validateStashTitle(stashDetailInDTO.getTitle());
        validateStashDescription(stashDetailInDTO.getDescription());
    }

    @Override
    public StashDetailOutDTO getStashDetail(Long stashId) {
        return stashRepository.findByStashId(stashId)
                .map(stash -> modelMapper.map(stash, StashDetailOutDTO.class))
                .orElse(null);
    }

    @Override
    @Transactional
    public StashDetailOutDTO updateStash(UpdateStashInDTO updateStashInDTO) {
        String username = userUtil.getUsername();
        Stash found = validateUpdateStash(updateStashInDTO, username);
        found.setEndTime(updateStashInDTO.getEndTime());
        found.setStartTime(updateStashInDTO.getStartTime());
        found.setTitle(updateStashInDTO.getTitle());
        found.setDescription(updateStashInDTO.getDescription());
        Stash savedStash = stashRepository.save(found);
        return modelMapper.map(savedStash, StashDetailOutDTO.class);
    }

    private Stash validateUpdateStash(UpdateStashInDTO updateStashInDTO, String username) {
        validateStashTitle(updateStashInDTO.getTitle());
        validateStashDescription(updateStashInDTO.getDescription());
        return validateStashExist(updateStashInDTO.getStashId());
    }

    @Override
    @Transactional
    public void deleteStash(Long stashId) {
        String username = userUtil.getUsername();
        validateDeleteStash(stashId, username);
        stashRepository.deleteById(stashId);
    }

    private void validateDeleteStash(Long stashId, String username) {
        Stash stash = validateStashExist(stashId);
        validateStashOwner(stash, username);
    }

    private void validateStashTitle(String title) {
        if (title.length() > StashType.TITLE_MAX_LENGTH.getValue()) {
            throw new StashException(messageUtil.getFormattedMessage("MSG0101"));
        }
    }

    private void validateStashDescription(String description) {
        if (description.length() > StashType.DESCRIPTION_MAX_LENGTH.getValue()) {
            throw new StashException(messageUtil.getFormattedMessage("MSG0102"));
        }
    }

    private Stash validateStashExist(Long stashId) {
        return stashRepository.findById(stashId)
                .orElseThrow(() -> new StashException(messageUtil.getFormattedMessage("MSG0103")));
    }

    private void validateStashOwner(Stash stash, String username) {
        if (!Objects.equals(stash.getUsername(), username)) {
            throw new StashException(messageUtil.getFormattedMessage("MSG0104"));
        }
    }
}
