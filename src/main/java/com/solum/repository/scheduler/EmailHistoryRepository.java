package com.solum.repository.scheduler;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.solum.entity.scheduler.EmailHistory;

@Repository
public interface EmailHistoryRepository extends JpaRepository<EmailHistory, String> {


}
