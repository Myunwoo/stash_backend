package com.ihw.stash.adapter.out.persistence;

import com.ihw.stash.adapter.in.json.dto.JsonData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JsonRepository extends JpaRepository<JsonData, Long> {
    Optional<JsonData> findByJsonId(Long jsonId);
    List<JsonData> findAllByUsername(String username);
}
