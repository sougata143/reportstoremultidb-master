package com.sls.report.component;

import java.sql.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sls.report.entity.POHeader;
import com.sls.report.repository.POHeaderRepository;

@Component
public class POHdrComponent {
	
	@Autowired
	POHeaderRepository pohdrRepository;
	
	@Transactional
	public List<POHeader> getAllPOHeader(){
		return pohdrRepository.findAll();
	}
	
	@Transactional
	public POHeader getPOHeaderById(String id) {
		return pohdrRepository.findOne(id);
	}
	
	@Transactional
	public List<POHeader> getPOHeadersBySuppCOde(String suppCode){
		return pohdrRepository.findBySupplierId(suppCode);
	}
	
	@Transactional
	public List<POHeader> getAllPOHeaderByPODate(Date startDate, Date endDate){
		return pohdrRepository.findByPoDateBetween(startDate, endDate);
	}
	
	@Transactional
	public List<POHeader> getAllPOHeaderByPODateAndSuppCode(Date startDate, Date endDate, String suppCode){
		return pohdrRepository.findByPoDateBetweenAndSupplierId(startDate, endDate, suppCode);
	}
	
	@Transactional
	public List<POHeader> getPOHeaderByPODate(Date date){
		return pohdrRepository.findByPoDate(date);
	}
	
	@Transactional
	public List<POHeader> getCancelledPOHeaderByPODate(Date date){
		return pohdrRepository.findByPoDateAndStatus(date,"6") ;
	}

}
