package com.solum.repository.scheduler;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.solum.entity.scheduler.SchedulerJobInfo;

@Repository
public interface SchedulerRepository extends JpaRepository<SchedulerJobInfo, Long> {

	SchedulerJobInfo findByJobName(String jobName);
	@Query(value = "Select * from SchedulerJobInfo where jobGroup= ?1 ORDER BY jobId",
			countQuery="Select * from SchedulerJobInfo where jobGroup= ?1" ,nativeQuery = true)
	public List<SchedulerJobInfo> findByGroupName(String groupName, Pageable pageable);
	@Query(value = "Select * from SchedulerJobInfo where jobGroup= ?1 and jobName = ?2 ORDER BY jobId",
			countQuery="Select * from SchedulerJobInfo where jobGroup= ?1 and jobName = ?2",
			nativeQuery = true)
	public List<SchedulerJobInfo> findByGroupNameAndJobName(String groupName, String jobName, Pageable pageable);
	
	@Query(value= "Select * from schedulerjobinfo where jobId = ?1", nativeQuery = true)
	public SchedulerJobInfo findById(String jobId);

}
