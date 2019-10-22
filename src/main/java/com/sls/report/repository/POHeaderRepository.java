package com.sls.report.repository;


import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.sls.report.entity.POHeader;

public interface POHeaderRepository extends JpaRepository<POHeader, String> {

   List<POHeader> findByStatus(String status);
   
   @Query("SELECT t FROM POHeader t where t.mukam= ?1 AND t.supplierId = ?2")
   public  List<POHeader> findByMukamAndSupplierId(String mukam, String supplierId);
   
   public List<POHeader> findBySupplierId(String suppCode);
   
   public List<POHeader> findByPoDateBetween(Date startDate, Date endDate);
   public List<POHeader> findByPoDate(Date date);
   public List<POHeader> findByPoDateBetweenAndSupplierId(Date startDate, Date endDate, String suppCode);
   public List<POHeader> findByPoDateAndStatus(Date date, String status);

}
