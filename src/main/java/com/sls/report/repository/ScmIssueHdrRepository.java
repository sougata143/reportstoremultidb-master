package com.sls.report.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sls.report.entity.ScmIssueHdr;

public interface ScmIssueHdrRepository extends JpaRepository<ScmIssueHdr, String>{
	
	List<ScmIssueHdr> findByCreateDate(Date date);
	ScmIssueHdr findByIdAndIssueDateBetween(String id, Date startDate, Date endDate);
	List<ScmIssueHdr> findByIssueDateBetween(Date startDate, Date endDate);
	List<ScmIssueHdr> findByIssueDate(Date issueDate);
	
	
}
