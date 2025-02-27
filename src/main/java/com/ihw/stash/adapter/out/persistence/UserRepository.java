package com.ihw.stash.adapter.out.persistence;


import com.ihw.stash.adapter.in.auth.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public Optional<User> findByUsername(String username);
    public boolean existsByUsername(String username);
}
