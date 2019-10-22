package com.sls.report.repository.prodtest;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sls.report.entity.ScmIndentHeader;

public interface ScmIndentHeaderRepository extends JpaRepository<ScmIndentHeader, String> {

	ScmIndentHeader findByIndentNoAndIndentType(String indentNo, String indentType);
	List<ScmIndentHeader> findByIndentDateBetween(Date startDate, Date endDate);
	List<ScmIndentHeader> findByIndentDate(Date indentDate);
	
}
