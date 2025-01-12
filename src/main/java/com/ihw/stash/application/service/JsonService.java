package com.ihw.stash.application.service;

import com.ihw.stash.adapter.in.json.dto.*;
import com.ihw.stash.adapter.out.persistence.JsonRepository;
import com.ihw.stash.application.port.in.JsonUseCase;
import com.ihw.stash.common.advice.StashException;
import com.ihw.stash.common.util.MessageUtil;
import com.ihw.stash.common.util.UserUtil;
import com.ihw.stash.type.StashType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsonService implements JsonUseCase {

    private final JsonRepository jsonRepository;
    private final MessageUtil messageUtil;
    private final ModelMapper modelMapper;
    private final UserUtil userUtil;

    @Override
    public JsonDetailOutDTO createJson(CreateJsonInDTO createJsonInDTO) throws Exception {
        validateCreateJson(createJsonInDTO);
        String username = userUtil.getUsername();
        JsonData jsonData = modelMapper.map(createJsonInDTO, JsonData.class);
        jsonData.setUsername(username);
        JsonData savedJson = jsonRepository.save(jsonData);
        return modelMapper.map(savedJson, JsonDetailOutDTO.class);
    }

    private void validateCreateJson(CreateJsonInDTO createJsonInDTO) {
        validateJsonTitle(createJsonInDTO.getTitle());
    }

    @Override
    public void deleteJson(Long id) throws Exception {
        String username = userUtil.getUsername();
        validateDeleteJson(id, username);
        jsonRepository.deleteById(id);
    }

    private void validateDeleteJson(Long id, String username) {
        JsonData jsonData = validateJsonExist(id);
        validateJsonOwner(jsonData, username);
    }

    private void validateJsonOwner(JsonData jsonData, String username) {
        if (!Objects.equals(jsonData.getUsername(), username)) {
            throw new StashException(messageUtil.getFormattedMessage("MSG0104"));
        }
    }

    @Override
    public JsonDetailOutDTO getJsonDetail(Long id) throws Exception {
        return jsonRepository.findByJsonId(id)
                .map(jsonData -> modelMapper.map(jsonData, JsonDetailOutDTO.class))
                .orElse(null);
    }

    @Override
    public JsonListDTO getJsonList() throws Exception {
        String username = userUtil.getUsername();
        JsonListDTO jsonListDTO = new JsonListDTO();
        jsonListDTO.setJsonDtlList(jsonRepository.findAllByUsername(username).stream()
                .map(json -> modelMapper.map(json, JsonDetailOutDTO.class))
                .toList());
        return jsonListDTO;
    }

    @Override
    public JsonDetailOutDTO updateJson(UpdateJsonInDTO updateJsonInDTO) throws Exception {
        String username = userUtil.getUsername();
        JsonData found = validateUpdateJson(updateJsonInDTO, username);
        found.setTitle(updateJsonInDTO.getTitle());
        found.setJsonContent(updateJsonInDTO.getJsonContent());
        JsonData savedJson = jsonRepository.save(found);
        return modelMapper.map(savedJson, JsonDetailOutDTO.class);
    }

    private JsonData validateUpdateJson(UpdateJsonInDTO updateJsonInDTO, String username) {
        validateJsonTitle(updateJsonInDTO.getTitle());
        return validateJsonExist(updateJsonInDTO.getId());
    }

    private void validateJsonTitle(String title) {
        if (title.length() > StashType.TITLE_MAX_LENGTH.getValue()) {
            throw new StashException(messageUtil.getFormattedMessage("MSG0101"));
        }
    }

    private JsonData validateJsonExist(Long id) {
        return jsonRepository.findByJsonId(id)
                .orElseThrow(() -> new StashException(messageUtil.getFormattedMessage("MSG0103")));
    }
}
