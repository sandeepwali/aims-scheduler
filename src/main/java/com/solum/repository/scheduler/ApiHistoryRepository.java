package com.solum.repository.scheduler;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.solum.entity.scheduler.ApiHistory;

@Repository
public interface ApiHistoryRepository extends JpaRepository<ApiHistory, String> {


}
