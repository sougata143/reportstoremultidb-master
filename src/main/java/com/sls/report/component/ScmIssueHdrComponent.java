package com.sls.report.component;

import java.sql.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sls.report.entity.ScmIssueHdr;
import com.sls.report.repository.ScmIssueHdrRepository;

@Component
public class ScmIssueHdrComponent {
	
	@Autowired
	ScmIssueHdrRepository issuehdrRepository;
	
	@Transactional
	public List<ScmIssueHdr> getAllIssueHdr(){
		return issuehdrRepository.findAll();
	}
	
	@Transactional
	public List<ScmIssueHdr> getScmIssuHdrByCreateDate(Date date){
		return issuehdrRepository.findByCreateDate(date);
	}
	
	@Transactional
	public ScmIssueHdr getScmIssuHdrBySrHdr(String hdrid){
		return issuehdrRepository.findOne(hdrid);
	}
	
	@Transactional
	public ScmIssueHdr getScmIssuHdrBySrHdrAndIssueDate(String hdrid, Date startDate, Date endDate){
		
		return issuehdrRepository.findByIdAndIssueDateBetween(hdrid,startDate,endDate);
	}
	
	@Transactional
	public List<ScmIssueHdr> getAllIssueHdrByIssueDate(Date startDate, Date endDate){
		return issuehdrRepository.findByIssueDateBetween(startDate, endDate);
	}
	
	@Transactional
	public List<ScmIssueHdr> getAllIssueHdrByIssueDt(Date startDate){
		return issuehdrRepository.findByIssueDate(startDate);
	}
}
