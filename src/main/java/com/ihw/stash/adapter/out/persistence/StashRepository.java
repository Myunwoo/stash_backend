package com.ihw.stash.adapter.out.persistence;

import com.ihw.stash.adapter.in.stash.dto.Stash;
import com.ihw.stash.adapter.in.stash.dto.StashDetailOutDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StashRepository extends JpaRepository<Stash, Long> {

    Optional<Stash> findByStashId(Long stashId);
    List<Stash> findAllByUsername(String username);
}
