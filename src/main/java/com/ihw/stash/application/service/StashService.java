package com.ihw.stash.application.service;

import com.ihw.stash.adapter.in.stash.dto.*;
import com.ihw.stash.adapter.out.persistence.ReminderRepository;
import com.ihw.stash.adapter.out.persistence.StashRepository;
import com.ihw.stash.application.port.in.StashUseCase;
import com.ihw.stash.common.advice.StashException;
import com.ihw.stash.common.util.MessageUtil;
import com.ihw.stash.type.StashType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StashService implements StashUseCase {

    private final StashRepository stashRepository;
    private final ReminderRepository reminderRepository;
    private final MessageUtil messageUtil;
    private final ModelMapper modelMapper;

    @Override
    public StashListDTO getStashList() {
        String username = getUsername();
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
        String username = getUsername();
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
    @Transactional
    public CreateReminderOutDTO createReminder(CreateReminderInDTO createReminderInDTO) {
        Reminder reminder = modelMapper.map(createReminderInDTO, Reminder.class);
        Reminder savedReminder = reminderRepository.save(reminder);
        return modelMapper.map(savedReminder, CreateReminderOutDTO.class);
    }

    @Override
    public StashDetailOutDTO getStashDetail(Long stashId) {
        return stashRepository.findByStashId(stashId)
                .map(stash -> modelMapper.map(stash, StashDetailOutDTO.class))
                .orElse(null);
    }

    @Override
    public GetReminderOutDTO getReminder(Long reminderId) {
        return reminderRepository.findByReminderId(reminderId)
                .map(reminder -> modelMapper.map(reminder, GetReminderOutDTO.class))
                .orElse(null);
    }

    @Override
    public GetReminderListOutDTO getReminderList() {
        GetReminderListOutDTO getReminderListOutDTO = new GetReminderListOutDTO();
        getReminderListOutDTO.setReminderList(reminderRepository.findAll().stream()
                .map(reminder -> modelMapper.map(reminder, GetReminderOutDTO.class))
                .toList());
        return getReminderListOutDTO;
    }

    @Override
    @Transactional
    public StashDetailOutDTO updateStash(UpdateStashInDTO updateStashInDTO) {
        String username = getUsername();
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
        String username = getUsername();
        validateDeleteStash(stashId, username);
        stashRepository.deleteById(stashId);
    }

    private void validateDeleteStash(Long stashId, String username) {
        Stash stash = validateStashExist(stashId);
        validateStashOwner(stash, username);
    }

    @Override
    @Transactional
    public UpdateReminderOutDTO updateReminder(UpdateReminderInDTO updateReminderInDTO) {
        Optional<Reminder> found = reminderRepository.findById(updateReminderInDTO.getReminderId());
        if (found.isEmpty()) {
            throw new RuntimeException("Reminder not found with id: " + updateReminderInDTO.getReminderId());
        }
        Reminder reminder = found.get();
        reminder.setMessage(updateReminderInDTO.getMessage());
        reminder.setTime(updateReminderInDTO.getTime());
        reminder.setUsername(updateReminderInDTO.getUsername());
        Reminder savedReminder = reminderRepository.save(reminder);
        return modelMapper.map(savedReminder, UpdateReminderOutDTO.class);
    }

    @Override
    @Transactional
    public void deleteReminder(Long reminderId) {
        if (!reminderRepository.existsById(reminderId)) {
            throw new RuntimeException("Reminder not found with id: " + reminderId);
        }
        reminderRepository.deleteById(reminderId);
    }

    private String getUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString();
        }
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
