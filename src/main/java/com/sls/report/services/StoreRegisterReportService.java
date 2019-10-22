package com.sls.report.services;

import java.sql.Date;
import java.util.List;

import com.sls.report.dto.DrCrNoteRegisterDTO;
import com.sls.report.dto.HOStoresReceiptCumBillPassReportDTO;
import com.sls.report.dto.InwardRegisterReportDTO;
import com.sls.report.dto.MillStoreRecieptRegisterDTO;
import com.sls.report.dto.StoreApprovalNoteDTO;
import com.sls.report.dto.StoreReceiptNoteReportDTO;
import com.sls.report.dto.StoreReportIndentIN01DTO;

public interface StoreRegisterReportService {

	List<DrCrNoteRegisterDTO> getAllDrCrNoteRegisterReport();
	
	List<HOStoresReceiptCumBillPassReportDTO> getAllHOStoresReceiptCumBillPassReport();
	List<HOStoresReceiptCumBillPassReportDTO> getAllHOStoresReceiptCumBillPassReportNew();
	List<HOStoresReceiptCumBillPassReportDTO> getAllHOStoresReceiptCumBillPassReportNewBySuppCode(String suppCode);
	
	List<InwardRegisterReportDTO> getAllInwardRegisterReport();
	
	public List<MillStoreRecieptRegisterDTO> getAllMillStoreRecieptRegister();
	public MillStoreRecieptRegisterDTO getAllMillStoreRecieptRegisterByReceiveNo(long receiveNo);
	
	List<StoreApprovalNoteDTO> getAllStoreApprovalNote(Date srDate);
	List<StoreReceiptNoteReportDTO> getAllStoreReceiptNoteReport(Date date);
	List<StoreReportIndentIN01DTO> getAllStoreReportIndentIN01();
	
}
