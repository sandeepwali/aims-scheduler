package com.solum.repository.scheduler;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.solum.entity.scheduler.JobStatus;

@Repository
public interface JobStatusRepository extends JpaRepository<JobStatus, String> {

	@Query(value = "Select * from JobStatus where jobGroup= ?1 and jobName = ?2 ORDER BY jobId",
			countQuery="Select * from JobStatus where jobGroup= ?1 and jobName = ?2",
			nativeQuery = true)
	List<JobStatus> findByGroupAndJobNameHistory(String jobGroup, String jobName, Pageable pageable);
	
	@Query(value = "Select * from JobStatus where jobGroup= ?1 and jobName = ?2 and jobId = ?3 ORDER BY jobId",
			countQuery="Select * from JobStatus where jobGroup= ?1 and jobName = ?2 and jobId = ?3",
			nativeQuery = true)
	List<JobStatus> findByGroupAndJobNameHistory(String jobGroup, String jobName, String jobId, Pageable pageable);


}
