package com.ihw.stash.application.port.in;

import com.ihw.stash.adapter.in.json.dto.CreateJsonInDTO;
import com.ihw.stash.adapter.in.json.dto.JsonDetailOutDTO;
import com.ihw.stash.adapter.in.json.dto.JsonListDTO;
import com.ihw.stash.adapter.in.json.dto.UpdateJsonInDTO;
import org.springframework.stereotype.Service;

@Service
public interface JsonUseCase {
    JsonDetailOutDTO createJson(CreateJsonInDTO createJsonInDTO) throws Exception;

    void deleteJson(Long id) throws Exception;

    JsonDetailOutDTO getJsonDetail(Long id) throws Exception;

    JsonListDTO getJsonList() throws Exception;

    JsonDetailOutDTO updateJson(UpdateJsonInDTO updateJsonInDTO) throws Exception;
}
