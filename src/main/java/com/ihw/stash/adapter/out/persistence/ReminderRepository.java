package com.ihw.stash.adapter.out.persistence;

import com.ihw.stash.adapter.in.stash.dto.GetReminderOutDTO;
import com.ihw.stash.adapter.in.stash.dto.Reminder;
import com.ihw.stash.adapter.in.stash.dto.Stash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    Optional<Reminder> findByReminderId(Long reminderId);
}
