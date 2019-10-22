package com.sls.report.services.serviceImpl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sls.report.component.DepartmentComponent;
import com.sls.report.component.ItemGroupDeptDao;
import com.sls.report.component.ItemMasterComponent;
import com.sls.report.component.PhysicalStockComponent;
import com.sls.report.component.ScmIssueHdrComponent;
import com.sls.report.component.ScmIssueLineItemComponent;
import com.sls.report.component.ScmSrHdrComponent;
import com.sls.report.component.ScmSrLineItemComponent;
import com.sls.report.component.UomMasterComponent;
import com.sls.report.dto.IS22SummaryReportDTO;
import com.sls.report.dto.IssueCheckListReportDTO;
import com.sls.report.dto.IssueConsumptionReportForMonthDTO;
import com.sls.report.dto.StoreConsumptionReportDTO;
import com.sls.report.dto.StoreConsumptionSummaryReportDTO;
import com.sls.report.dto.StoreIssueReportDTO;
import com.sls.report.dto.StoreIssueReportIS19DTO;
import com.sls.report.entity.Department;
import com.sls.report.entity.ItemGroupDept;
import com.sls.report.entity.ItemMaster;
import com.sls.report.entity.PhysicalStock;
import com.sls.report.entity.ScmIssueHdr;
import com.sls.report.entity.ScmIssueLineItem;
import com.sls.report.entity.ScmSrLineItem;
import com.sls.report.entity.UomMaster;
import com.sls.report.services.IssueReportService;


@Service
public class IssueReportServiceImpl implements IssueReportService{

	@Autowired
	ScmIssueHdrComponent issuehdrDao;
	
	@Autowired
	ScmIssueLineItemComponent issuelineitemDao;
	
	@Autowired
	ItemMasterComponent itemDao;
	
	@Autowired
	UomMasterComponent uomDao;
	
	@Autowired
	DepartmentComponent deptDao;
	
	@Autowired
	PhysicalStockComponent stockDao;
	
	@Autowired
	ScmSrLineItemComponent srlineitemDao;
	
	@Autowired
	ScmSrHdrComponent srhdrDao;
	
	@Autowired
	ItemGroupDeptDao grpdeptDao;

	@Override
	public List<IssueCheckListReportDTO> getAllIssueCheckListReport(Date startDate, Date endDate) {
		List<IssueCheckListReportDTO> issuechecklists = new ArrayList<>();
		try {
//			List<ScmIssueHdr> issues = issuehdrDao.getAllIssueHdrByIssueDate(startDate,endDate);
			List<ScmIssueHdr> issues = issuehdrDao.getAllIssueHdr();
			issues.forEach(issue->{
				issuechecklists.add(prepareIssueCheckListDTO(issue));
			});
		}catch(Exception e) {
			e.printStackTrace();
		}
		return issuechecklists;
	}

	private IssueCheckListReportDTO prepareIssueCheckListDTO(ScmIssueHdr issue) {
		IssueCheckListReportDTO issuechecklistreportDTO = new IssueCheckListReportDTO();
		List<ScmIssueLineItem> issuelineitems = issuelineitemDao.getIssueLineItemByIssueNo(String.valueOf(issue.getId()));
		ItemMaster item = itemDao.getItemMasterById(issuelineitems.get(0).getItemCode());
		UomMaster uom = uomDao.getUomMasterById(issuelineitems.get(0).getUomCode());
		Department dept = deptDao.getDepartmentByDeptId(Long.valueOf(issue.getDeptId()));
		List<PhysicalStock> stocks = stockDao.getPhysicalStockByItem(issuelineitems.get(0).getItemCode());
		List<ScmSrLineItem> srlineitems = srlineitemDao.getScmSrLineItemByItemCode(issuelineitems.get(0).getItemCode());
		
		long rate = 0;
		for(int y = 0 ; y < srlineitems.size() ; y++) {
			rate = srlineitems.get(y).getReceivedPrice();
		}
		
//		issuechecklistreportDTO.setApprovalQuantity(approvalQuantity);
		issuechecklistreportDTO.setDept(dept.getdepartmentName());
		issuechecklistreportDTO.setDescription(item.getitemDsc());
		issuechecklistreportDTO.setIssueNo(issue.getId());
		issuechecklistreportDTO.setItemCode(item.getgrpCode()+item.getlegacyItemCode());
		
//		issuechecklistreportDTO.setLotNo(lotNo);
//		issuechecklistreportDTO.setMachineNo(machineNo);
		float qnt = 0.0f;
		for(int i = 0 ; i < issuelineitems.size() ; i++) {
			qnt = qnt + issuelineitems.get(i).getIssueQty();
		}
		issuechecklistreportDTO.setQuantity(qnt);
		
		float stockQnt = 0.0f;
		for(int j = 0 ; j < stocks.size() ; j++) {
			stockQnt = stockQnt + stocks.get(j).getTotalStock();
		}
		issuechecklistreportDTO.setStockQuantity(stockQnt);
		issuechecklistreportDTO.setItemValue((stockQnt*rate));
		issuechecklistreportDTO.setUnit(uom.getuomDsc());
		
		
		return issuechecklistreportDTO;
	}

	@Override
	public List<IssueConsumptionReportForMonthDTO> getAllIssueConsumptionIncreasingReportForMonth(String date) {
		List<IssueConsumptionReportForMonthDTO> reportDTO = new ArrayList<>();
		try {
			System.out.println(date);
			String d = date + "-01";
			String d1 = date + "-30";
//			java.util.Date datesss = new java.util.Date(date.getTime());
//			List<ScmIssueHdr> issuess = issuehdrDao.getAllIssueHdr();
			List<ScmIssueHdr> issues = issuehdrDao.getAllIssueHdrByIssueDate(Date.valueOf(d), Date.valueOf(d1));
//			LocalDate date1 = date.toLocalDate().minusDays(1);
			
			issues.forEach(issue->{
				reportDTO.add(prepareIssueComparisionIncreasingDTO(issue,Date.valueOf(d)));
			});
		}catch(Exception e) {
			e.printStackTrace();
		}
		return reportDTO;
	}

	private IssueConsumptionReportForMonthDTO prepareIssueComparisionIncreasingDTO(ScmIssueHdr issue, Date date) {
		IssueConsumptionReportForMonthDTO issueDTO = new IssueConsumptionReportForMonthDTO();
		
		Date dates = issue.getIssueDate();
		
		LocalDate udates = new java.sql.Date(dates.getTime()).toLocalDate();
		String umonths = String.valueOf(udates.getMonthValue());
		String uyears = String.valueOf(udates.getYear());
		String utog = umonths+uyears;
		
		LocalDate udate = new java.sql.Date(date.getTime()).toLocalDate();
		String umonth = String.valueOf(udate.getMonthValue());
		String uyear = String.valueOf(udate.getYear());
		String mmyy = umonth+uyear;
		
//		System.out.println("Month "+umonth+" year "+uyear+" together "+mmyy);
		System.out.println("db "+utog+" user "+mmyy);
		
		if(utog.equals(mmyy)) {
			List<ScmIssueLineItem> issuelineitems = issuelineitemDao.getIssueLineItemByIssueNo(String.valueOf(issue.getId()));
			ItemMaster item = itemDao.getItemMasterById(issuelineitems.get(0).getItemCode());
			UomMaster uom = uomDao.getUomMasterById(issuelineitems.get(0).getUomCode());
			List<ScmSrLineItem> srlineitems = srlineitemDao.getScmSrLineItemByItemCode(issuelineitems.get(0).getItemCode());
			
//			LocalDate udate = new java.sql.Date(date.getTime()).toLocalDate();
//			String umonth = String.valueOf(udate.getMonthValue());
//			String uyear = String.valueOf(udate.getYear());
//			String mmyy = umonth+uyear;
			long userdate = Long.valueOf(mmyy);
			
			long rate = 0;
			for(int y = 0 ; y < srlineitems.size() ; y++) {
				rate = srlineitems.get(y).getReceivedPrice();
			}
			
			issueDTO.setItemCode(item.getId());
			issueDTO.setDescription(item.getitemDsc());
			issueDTO.setUnit(uom.getuomDsc());
			
			LocalDate localdate = new java.sql.Date(issue.getIssueDate().getTime()).toLocalDate();
			String srmonth = String.valueOf(localdate.getMonthValue());
			String sryear = String.valueOf(localdate.getYear());
			String mmyyyy = srmonth+sryear;
			long dbdate = Long.valueOf(mmyyyy);
			System.out.println("Month "+srmonth+" year "+sryear+" together "+dbdate);
			
			float prevqnt = 0.0f;
			float prevvalue = 0.0f;
			
			/*if(dbdate<userdate) {
				List<ScmIssueHdr> issssss = issuehdrDao.getAllIssueHdrByIssueDate(
						Date.valueOf(String.valueOf(localdate.getYear()+"-"+String.valueOf(localdate.getMonthValue()-1)+"-01")),
						Date.valueOf(localdate.minusMonths(1)));
				for(int j = 0 ; j < issssss.size() ; j++) {
					prevqnt = prevqnt + issuelineitems.get(j).getIssueQty();
					prevvalue = rate * prevqnt;
				}
				
				issueDTO.setPrevQnt(prevqnt);
				issueDTO.setPrevValue(prevvalue);
			}*/
			
			List<ScmIssueHdr> issssss = issuehdrDao.getAllIssueHdrByIssueDate(
					Date.valueOf(String.valueOf(localdate.getYear()+"-"+String.valueOf(localdate.getMonthValue()-1)+"-01")),
					Date.valueOf(localdate.minusMonths(1)));
			for(int j = 0 ; j < issssss.size() ; j++) {
				List<ScmIssueLineItem> issuelines = issuelineitemDao.getIssueLineItemByIssueNo(issssss.get(j).getId());
				float qnt = 0.0f;
				for(int a = 0 ; a < issuelines.size() ; a++) {
					qnt = qnt + issuelines.get(a).getIssueQty();
				}
				prevqnt = prevqnt + qnt;
				prevvalue = rate * prevqnt;
			}
			
			issueDTO.setPrevQnt(prevqnt);
			issueDTO.setPrevValue(prevvalue);
			
			float nextqnt = 0.0f;
			float nextvalue = 0.0f;
			if(dbdate>=userdate) {
				for(int j = 0 ; j < issuelineitems.size() ; j++) {
					nextqnt = nextqnt + issuelineitems.get(j).getIssueQty();
					nextvalue = rate * nextqnt;
				}
				
				issueDTO.setNextQnt(nextqnt);
				issueDTO.setNextValue(nextvalue);
			}
			
			float diffqnt = nextqnt-prevqnt;
			float diffvalue = nextvalue-prevvalue;
			issueDTO.setDiffQnt(diffqnt);
			issueDTO.setDiffValue(diffvalue);
			
			float per = (diffqnt/nextqnt)*100;
			
			if((nextqnt != 0.0f) || (diffqnt != 0.0f))
					issueDTO.setPercentage(per);
			if(diffvalue<0) {
				issueDTO = null;
			}
			System.out.println(prevqnt+" "+prevvalue+" dabdate "+dbdate+" userdate "+userdate+" "+Date.valueOf(localdate.minusMonths(1))
			+" "+Date.valueOf(String.valueOf(localdate.getYear()+"-"+String.valueOf(localdate.getMonthValue()-1)+"-01")));
			
			return issueDTO;
		}else {
			return null;
		}
	}

	@Override
	public List<IssueConsumptionReportForMonthDTO> prepareIssueConsumptionDecreasingDTO(String date) {
		List<IssueConsumptionReportForMonthDTO> reportDTO = new ArrayList<>();
		try {
			System.out.println(date);
			String d = date + "-01";
			String d1 = date + "-30";
//			java.util.Date datesss = new java.util.Date(date.getTime());
//			List<ScmIssueHdr> issuess = issuehdrDao.getAllIssueHdr();
			List<ScmIssueHdr> issues = issuehdrDao.getAllIssueHdrByIssueDate(Date.valueOf(d), Date.valueOf(d1));
//			LocalDate date1 = date.toLocalDate().minusDays(1);
			
			issues.forEach(issue->{
				reportDTO.add(prepareIssueComaprisionDecresingDTO(issue,Date.valueOf(d)));
			});
		}catch(Exception e) {
			e.printStackTrace();
		}
		return reportDTO;
	}

	private IssueConsumptionReportForMonthDTO prepareIssueComaprisionDecresingDTO(ScmIssueHdr issue, Date date) {
		IssueConsumptionReportForMonthDTO issueDTO = new IssueConsumptionReportForMonthDTO();
		
		Date dates = issue.getIssueDate();
		
		LocalDate udates = new java.sql.Date(dates.getTime()).toLocalDate();
		String umonths = String.valueOf(udates.getMonthValue());
		String uyears = String.valueOf(udates.getYear());
		String utog = umonths+uyears;
		
		LocalDate udate = new java.sql.Date(date.getTime()).toLocalDate();
		String umonth = String.valueOf(udate.getMonthValue());
		String uyear = String.valueOf(udate.getYear());
		String mmyy = umonth+uyear;
		
//		System.out.println("Month "+umonth+" year "+uyear+" together "+mmyy);
		System.out.println("db "+utog+" user "+mmyy);
		
		if(utog.equals(mmyy)) {
			List<ScmIssueLineItem> issuelineitems = issuelineitemDao.getIssueLineItemByIssueNo(String.valueOf(issue.getId()));
			ItemMaster item = itemDao.getItemMasterById(issuelineitems.get(0).getItemCode());
			UomMaster uom = uomDao.getUomMasterById(issuelineitems.get(0).getUomCode());
			List<ScmSrLineItem> srlineitems = srlineitemDao.getScmSrLineItemByItemCode(issuelineitems.get(0).getItemCode());
			
//			LocalDate udate = new java.sql.Date(date.getTime()).toLocalDate();
//			String umonth = String.valueOf(udate.getMonthValue());
//			String uyear = String.valueOf(udate.getYear());
//			String mmyy = umonth+uyear;
			long userdate = Long.valueOf(mmyy);
			
			long rate = 0;
			for(int y = 0 ; y < srlineitems.size() ; y++) {
				rate = srlineitems.get(y).getReceivedPrice();
			}
			
			issueDTO.setItemCode(item.getId());
			issueDTO.setDescription(item.getitemDsc());
			issueDTO.setUnit(uom.getuomDsc());
			
			LocalDate localdate = new java.sql.Date(issue.getIssueDate().getTime()).toLocalDate();
			String srmonth = String.valueOf(localdate.getMonthValue());
			String sryear = String.valueOf(localdate.getYear());
			String mmyyyy = srmonth+sryear;
			long dbdate = Long.valueOf(mmyyyy);
			System.out.println("Month "+srmonth+" year "+sryear+" together "+dbdate);
			
			float prevqnt = 0.0f;
			float prevvalue = 0.0f;
			
			/*if(dbdate<userdate) {
				List<ScmIssueHdr> issssss = issuehdrDao.getAllIssueHdrByIssueDate(
						Date.valueOf(String.valueOf(localdate.getYear()+"-"+String.valueOf(localdate.getMonthValue()-1)+"-01")),
						Date.valueOf(localdate.minusMonths(1)));
				for(int j = 0 ; j < issssss.size() ; j++) {
					prevqnt = prevqnt + issuelineitems.get(j).getIssueQty();
					prevvalue = rate * prevqnt;
				}
				
				issueDTO.setPrevQnt(prevqnt);
				issueDTO.setPrevValue(prevvalue);
			}*/
			
			List<ScmIssueHdr> issssss = issuehdrDao.getAllIssueHdrByIssueDate(
					Date.valueOf(String.valueOf(localdate.getYear()+"-"+String.valueOf(localdate.getMonthValue()-1)+"-01")),
					Date.valueOf(localdate.minusMonths(1)));
			for(int j = 0 ; j < issssss.size() ; j++) {
				List<ScmIssueLineItem> issuelines = issuelineitemDao.getIssueLineItemByIssueNo(issssss.get(j).getId());
				float qnt = 0.0f;
				for(int a = 0 ; a < issuelines.size() ; a++) {
					qnt = qnt + issuelines.get(a).getIssueQty();
				}
				prevqnt = prevqnt + qnt;
				prevvalue = rate * prevqnt;
			}
			
			issueDTO.setPrevQnt(prevqnt);
			issueDTO.setPrevValue(prevvalue);
			
			float nextqnt = 0.0f;
			float nextvalue = 0.0f;
			if(dbdate>=userdate) {
				for(int j = 0 ; j < issuelineitems.size() ; j++) {
					nextqnt = nextqnt + issuelineitems.get(j).getIssueQty();
					nextvalue = rate * nextqnt;
				}
				
				issueDTO.setNextQnt(nextqnt);
				issueDTO.setNextValue(nextvalue);
			}
			
			float diffqnt = nextqnt-prevqnt;
			float diffvalue = nextvalue-prevvalue;
			issueDTO.setDiffQnt(diffqnt);
			issueDTO.setDiffValue(diffvalue);
			
			float per = (diffqnt/nextqnt)*100;
			
			if((nextqnt != 0.0f) || (diffqnt != 0.0f))
					issueDTO.setPercentage(per);
			if(diffvalue>0) {
				issueDTO = null;
			}
			System.out.println(prevqnt+" "+prevvalue+" dabdate "+dbdate+" userdate "+userdate+" "+Date.valueOf(localdate.minusMonths(1))
			+" "+Date.valueOf(String.valueOf(localdate.getYear()+"-"+String.valueOf(localdate.getMonthValue()-1)+"-01")));
			
			return issueDTO;
		}else {
			return null;
		}
	}

	@Override
	public List<StoreConsumptionReportDTO> getAllStoreConsumptionReport(String startDate, String endDate) {
		List<StoreConsumptionReportDTO> storeconsumptionDTOs = new ArrayList<>();
		try {
			String sd = startDate+"-01";
			String ed = endDate+"-01";
			Date sd1 = Date.valueOf(sd);
			Date ed1 = Date.valueOf(ed);
			List<ScmIssueHdr> issues = issuehdrDao.getAllIssueHdrByIssueDate(sd1, ed1);
			List<ItemMaster> items = new ArrayList<>();
			if(!issues.isEmpty())
				items = itemDao.getAllItemMaster();
			items.forEach(item->{
				storeconsumptionDTOs.add(prepareStoreConsumptionDTO(item));
			});
		}catch(Exception e) {
			e.printStackTrace();
		}
		return storeconsumptionDTOs;
	}
	
	@Override
	public List<StoreConsumptionReportDTO>
								getAllStoreConsumptionReportGroupWise(String grpCode, String startDate, String endDate){
		List<StoreConsumptionReportDTO> storeconsumptionDTOs = new ArrayList<>();
		try {
			System.out.println("grpcode "+grpCode+" startDate "+startDate+" endDate "+endDate);
			String sd = startDate+"-01";
			String ed = endDate+"-31";
			Date sd1 = Date.valueOf(startDate+"-01");
			Date ed1 = Date.valueOf(endDate+"-31");
			List<ScmIssueHdr> issues = issuehdrDao.getAllIssueHdrByIssueDate(sd1, ed1);
			List<ItemMaster> items = new ArrayList<>();
			if(!issues.isEmpty())
				items = itemDao.getItemMasterByGroupCode(grpCode);
			items.forEach(item->{
				storeconsumptionDTOs.add(prepareStoreConsumptionDTO(item));
			});
		}catch(Exception e) {
			e.printStackTrace();
		}
		return storeconsumptionDTOs;
	}
	
	@Override
	public List<StoreConsumptionReportDTO> 
					getAllStoreConsumptionReportDepartmentWise(long deptCode, String startDate, String endDate){
		List<StoreConsumptionReportDTO> storeconsumptionDTOs = new ArrayList<>();
		try {
			List<ItemGroupDept> grpdept = grpdeptDao.getItemGroupDeptMappingByDept(deptCode);
			String sd = startDate+"-01";
			String ed = endDate+"-31";
			Date sd1 = Date.valueOf(startDate+"-01");
			Date ed1 = Date.valueOf(endDate+"-31");
			List<ScmIssueHdr> issues = issuehdrDao.getAllIssueHdrByIssueDate(sd1, ed1);
			List<ItemMaster> items = new ArrayList<>();
			if(!issues.isEmpty())
				for(int i = 0  ; i < grpdept.size(); i++) {
					String d = grpdept.get(i).getItemGrp();
					items = itemDao.getItemMasterByGroupCode(String.valueOf(d));
					items.forEach(item->{
						storeconsumptionDTOs.add(prepareStoreConsumptionDTO(item));
					});
				}
				
		}catch(Exception e) {
			e.printStackTrace();
		}
		return storeconsumptionDTOs;
	}
	
	@Override
	public List<IS22SummaryReportDTO> getAllStoreConsumptionSummaryReportGroupWise(String grpCode, String startDate, String endDate){
		List<IS22SummaryReportDTO> storeconsumptionDTOs = new ArrayList<>();
		try {
			System.out.println("grpcode "+grpCode+" startDate "+startDate+" endDate "+endDate);
			String sd = startDate+"-01";
			String ed = endDate+"-31";
			Date sd1 = Date.valueOf(startDate+"-01");
			Date ed1 = Date.valueOf(endDate+"-31");
			List<ScmIssueHdr> issues = issuehdrDao.getAllIssueHdrByIssueDate(sd1, ed1);
			List<ItemMaster> items = new ArrayList<>();
			if(!issues.isEmpty())
				items = itemDao.getItemMasterByGroupCode(grpCode);
			items.forEach(item->{
				storeconsumptionDTOs.add(prepareStoreConsumptionSummaryDTO(item));
			});
		}catch(Exception e) {
			e.printStackTrace();
		}
		return storeconsumptionDTOs;
	}
	
	private IS22SummaryReportDTO prepareStoreConsumptionSummaryDTO(ItemMaster item) {
		IS22SummaryReportDTO summaryDTO = new IS22SummaryReportDTO();
		List<ScmIssueLineItem> issues = issuelineitemDao.getLineItemsByItem(item.getId());
		List<ScmSrLineItem> srs = srlineitemDao.getScmSrLineItemByItemCode(item.getId());
		
		float rate = 0.0f;
		for(int j = 0 ; j < srs.size() ; j++) {
			rate = rate + srs.get(j).getReceivedPrice();
		}
		
		float totqnt = 0.0f;
		float totvalue = 0.0f;
		float avgqnt = 0.0f;
		float avgvalue = 0.0f;
		
		for(int i = 0 ; i < issues.size() ; i++) {
			totqnt = totqnt + issues.get(i).getIssueQty();
			totvalue = totqnt * rate;
		}
		
		avgqnt = totqnt/12;
		avgvalue = avgqnt * rate;
		
		summaryDTO.setAvgqnt(avgqnt);
		summaryDTO.setAvgvalue(avgvalue);
		summaryDTO.setTotqnt(totqnt);
		summaryDTO.setTotvalue(totvalue);
		
		summaryDTO.setDescription(item.getitemDsc());
		summaryDTO.setItemCode(item.getId());
		summaryDTO.setUnit(item.getuomCode());
		
		return summaryDTO;
	}

	private StoreConsumptionReportDTO prepareStoreConsumptionDTO(ItemMaster item) {
		StoreConsumptionReportDTO storeconsumptionDTO = new StoreConsumptionReportDTO();
		
		storeconsumptionDTO.setDescription(item.getitemDsc());
		storeconsumptionDTO.setItemCode(item.getgrpCode()+item.getlegacyItemCode());
		storeconsumptionDTO.setUnit(item.getuomCode());
		
		float total = 0.0f;
		
		float jan = 0.0f;
		float feb = 0.0f;
		float march = 0.0f;
		float apr = 0.0f;
		float may = 0.0f;
		float june = 0.0f;
		float july = 0.0f;
		float aug = 0.0f;
		float sept = 0.0f;
		float oct = 0.0f;
		float nov = 0.0f;
		float dec = 0.0f;
		
		List<ScmIssueLineItem> issuelineitems = issuelineitemDao.getLineItemsByItem(item.getId());
		
		for(int i = 0 ; i < issuelineitems.size() ; i++) {
			ScmIssueHdr issue = issuehdrDao.getScmIssuHdrBySrHdr(issuelineitems.get(i).getIssueNo());
			LocalDate issueDate = issue.getIssueDate().toLocalDate();
			int month = issueDate.getMonthValue();
			if(month == 1)
				jan = jan + issuelineitems.get(i).getIssueQty();
			else if(month == 2)
				feb = feb + issuelineitems.get(i).getIssueQty();
			else if(month == 3)
				march = march + issuelineitems.get(i).getIssueQty();
			else if(month == 4)
				apr = apr + issuelineitems.get(i).getIssueQty();
			else if(month == 5)
				may = may + issuelineitems.get(i).getIssueQty();
			else if(month == 6)
				june = june + issuelineitems.get(i).getIssueQty();
			else if(month == 7)
				july = july + issuelineitems.get(i).getIssueQty();
			else if(month == 8)
				aug = aug + issuelineitems.get(i).getIssueQty();
			else if(month == 9)
				sept = sept + issuelineitems.get(i).getIssueQty();
			else if(month == 10)
				oct = oct + issuelineitems.get(i).getIssueQty();
			else if(month == 11)
				nov = nov + issuelineitems.get(i).getIssueQty();
			else if(month == 12)
				dec = dec + issuelineitems.get(i).getIssueQty();
			
		}
		
		storeconsumptionDTO.setJan(jan);
		storeconsumptionDTO.setFeb(feb);
		storeconsumptionDTO.setMarch(march);
		storeconsumptionDTO.setApr(apr);
		storeconsumptionDTO.setMay(may);
		storeconsumptionDTO.setJune(june);
		storeconsumptionDTO.setJuly(july);
		storeconsumptionDTO.setAug(aug);
		storeconsumptionDTO.setSept(sept);
		storeconsumptionDTO.setOct(oct);
		storeconsumptionDTO.setNov(nov);
		storeconsumptionDTO.setDec(dec);
		
//		storeconsumptionDTO.setQuantity(quantity);
		total = jan + feb + march + apr + may + june + july + aug + sept + oct + nov + dec;
		storeconsumptionDTO.setTotal(total);
		storeconsumptionDTO.setAvg(total/12);
		
		return storeconsumptionDTO;
	}

	@Override
	public List<StoreIssueReportIS19DTO> getAllStoreIssueReportIS19(String startDate, String endDate) {
		List<StoreIssueReportIS19DTO> storeissuereportDTO = new ArrayList<>();
		try {
			Date sd = Date.valueOf(String.valueOf(startDate+"-01"));
			Date ed = Date.valueOf(String.valueOf(endDate+"-30"));
			List<ScmIssueHdr> issuehdr = issuehdrDao.getAllIssueHdrByIssueDate(sd, ed);
			
			for(int i = 0 ; i < issuehdr.size() ; i++) {
//				List<ScmIssueLineItem> lineitems = issuelineitemDao.getIssueLineItemByIssueNo(issue.getId());
				
				List<ScmIssueLineItem> lineitems = 
							issuelineitemDao.getIssueLineitemOrderByIssueQty(issuehdr.get(i).getId());
				if(!lineitems.isEmpty()) {
					storeissuereportDTO.add(prepareStoreIssueReport19(lineitems));
				}
			}
			

		}catch(Exception e) {
			e.printStackTrace();
		}
		return storeissuereportDTO;
	}

	private StoreIssueReportIS19DTO prepareStoreIssueReport19(List<ScmIssueLineItem> lineitems) {
		StoreIssueReportIS19DTO storeissuereportDTO = new StoreIssueReportIS19DTO();
		
		String itemCode = null;
		String description = null;
		String unit = null;
		float quantity = 0.0f;
		float amount = 0.0f;
		float avgConsumption = 0.0f;
		
		List<ScmIssueLineItem> issues = issuelineitemDao.getAllScmIssueLineItem();
		List<ScmIssueLineItem> issueqtys = issuelineitemDao.getTop100LineItemsByIssueQnt();
		float totqnt = 0.0f;
		for(int j = 0 ; j < issues.size() ; j++) {
			totqnt = totqnt + issues.get(j).getIssueQty();
		}
		float issueqt = 0.0f;
		for(int x = 0 ; x < issueqtys.size() ; x++) {
			issueqt = issueqt + issueqtys.get(x).getIssueQty();
		}
		 
		if(((issueqt/totqnt)*100)>47) {
			for(int i = 0 ; i < lineitems.size() ; i++) {
				
//				List<ScmIssueLineItem> lineitem = issuelineitemDao.getIssueLineitemOrderByIssueQty();
				
				/*for(int j = 0 ; j < lineitem.size() ; j++) {
					ItemMaster item = itemDao.getItemMasterById(lineitems.get(i).getItemCode());
					List<ScmIssueLineItem> lineitem1 =  issuelineitemDao.getLineItemsByItem(item.getId());
					List<ScmSrLineItem> srlineitems = srlineitemDao.getScmSrLineItemByItemCode(item.getId());
					float rate = 0.0f;
					for(int l = 0 ; l < srlineitems.size() ; l++) {
						rate = rate + srlineitems.get(l).getReceivedPrice();
					}
					for(int k = 0 ; k < lineitem1.size() ; k++) {
						quantity = lineitem1.get(k).getIssueQty();
						amount = quantity * rate;
					}
					
					itemCode = item.getgrpCode()+item.getlegacyItemCode();
					description = item.getitemDsc();
					unit = item.getuomCode();
//					quantity = quantity + lineitem.get(j).getIssueQty();
//					amount = quantity*rate;
					avgConsumption = quantity/lineitem.size();
					
				}*/
				
				ItemMaster item = itemDao.getItemMasterById(lineitems.get(i).getItemCode());
				List<ScmIssueLineItem> lineitem1 =  issuelineitemDao.getLineItemsByItem(item.getId());
				List<ScmSrLineItem> srlineitems = srlineitemDao.getScmSrLineItemByItemCode(item.getId());
				float rate = 0.0f;
				
				for(int l = 0 ; l < srlineitems.size() ; l++) {
					rate = rate + srlineitems.get(l).getReceivedPrice();
				}
				
				for(int k = 0 ; k < lineitem1.size() ; k++) {
					itemCode = item.getgrpCode()+item.getlegacyItemCode();
					description = item.getitemDsc();
					unit = item.getuomCode();
					quantity = quantity + lineitem1.get(k).getIssueQty();
					amount = quantity * rate;
					avgConsumption = quantity/lineitems.size();
				}
				
//				itemCode = item.getgrpCode()+item.getlegacyItemCode();
//				description = item.getitemDsc();
//				unit = item.getuomCode();
//				quantity = quantity + lineitem.get(j).getIssueQty();
//				amount = quantity*rate;
				
				
				storeissuereportDTO.setMajorAmount(amount);
				storeissuereportDTO.setMajorAvgConsumption(avgConsumption);
				storeissuereportDTO.setMajorDescription(item.getitemDsc());
				storeissuereportDTO.setMajorItemCode(item.getgrpCode()+item.getlegacyItemCode());
				storeissuereportDTO.setMajorQuantity(quantity);
				storeissuereportDTO.setMajorUnit(item.getuomCode());
				
			}
		}
		System.out.println("per "+((issueqt/totqnt)*100));
		System.out.println("quantity "+totqnt);
		System.out.println("top two "+issueqt);
		System.out.println("top two size "+issueqtys);
		System.out.println(issues.size());
		return storeissuereportDTO;
	
	}

	@Override
	public List<StoreIssueReportDTO> getStoreIssueReportService(String startDate, String endDate) {
		List<StoreIssueReportDTO> storeissuereportDTO = new ArrayList<>();
		try {
			String sd = startDate+"-01";
			String ed = endDate + "-01";
			List<ScmIssueHdr> issues = issuehdrDao.getAllIssueHdrByIssueDate(Date.valueOf(sd),Date.valueOf(ed));
//			List<ScmIssueHdr> issues = issuehdrDao.getAllIssueHdr();
			issues.forEach(issue->{
				storeissuereportDTO.add(prepareStoreIssueDTO(issue));
			});
		}catch(Exception e) {
			e.printStackTrace();
		}
		return storeissuereportDTO;
	}

	private StoreIssueReportDTO prepareStoreIssueDTO(ScmIssueHdr issue) {
		StoreIssueReportDTO issuereportDTO = new StoreIssueReportDTO();
		List<ScmIssueLineItem> issuelineitems = issuelineitemDao.getIssueLineItemByIssueNo(String.valueOf(issue.getId()));
		ItemMaster item = itemDao.getItemMasterById(issuelineitems.get(0).getItemCode());
		UomMaster uom = uomDao.getUomMasterById(issuelineitems.get(0).getUomCode());
		Department dept = deptDao.getDepartmentByDeptId(Long.valueOf(issue.getDeptId()));
		List<PhysicalStock> stocks = stockDao.getPhysicalStockByItem(issuelineitems.get(0).getItemCode());
		List<ScmSrLineItem> srlineitems = srlineitemDao.getScmSrLineItemByItemCode(issuelineitems.get(0).getItemCode());
		
		
		long rate = 0;
		for(int y = 0 ; y < srlineitems.size() ; y++) {
			rate = srlineitems.get(y).getReceivedPrice();
		}
//		issuereportDTO.setAv(av);
//		issuereportDTO.setConLastMn(conLastMn);
		issuereportDTO.setDescription(item.getitemDsc());
		issuereportDTO.setItemCode(item.getgrpCode()+item.getlegacyItemCode());
		float qnt = 0.0f;
		for(int i = 0 ; i < issuelineitems.size() ; i++) {
			qnt = qnt + issuelineitems.get(i).getIssueQty();
		}
		issuereportDTO.setQuantity(qnt);
		issuereportDTO.setAmmount(qnt*rate);
		issuereportDTO.setUnit(uom.getuomDsc());
		
		return issuereportDTO;
	}

	@Override
	public List<StoreConsumptionSummaryReportDTO> getAllStoreConsumptionSummaryReport(String startDate, String endDate) {
		List<StoreConsumptionSummaryReportDTO> storeconsumptionsummaryDTOs = new ArrayList<>();
		try {
			Date sd = Date.valueOf(startDate+"-01");
			Date ed = Date.valueOf(endDate + "-31");
			List<ItemMaster> items = itemDao.getAllItemMaster();
			List<ScmIssueLineItem> lines = new ArrayList<>();
			for(int i = 0 ; i < items.size() ; i++) {
				List<ScmIssueLineItem> issuelineitems = issuelineitemDao.getLineItemsByItem(items.get(i).getId());
				List<ScmIssueHdr> issues = issuehdrDao.getAllIssueHdrByIssueDate(sd, ed);
				
				for(int j = 0 ; j < issues.size() ; j++) {
					lines = issuelineitemDao.getIssueLineItemByIssueNo(issues.get(j).getId());
				}
//				if(lines.equals(issuelineitems))
					storeconsumptionsummaryDTOs.add(prepareStoreConsumptionSummaryAllItemDTO(items.get(i),issuelineitems));
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return storeconsumptionsummaryDTOs;
	}

	private StoreConsumptionSummaryReportDTO prepareStoreConsumptionSummaryAllItemDTO(ItemMaster item, List<ScmIssueLineItem> issuelineitems) {
		StoreConsumptionSummaryReportDTO storeconsumptionDTO = new StoreConsumptionSummaryReportDTO();
		
		List<ScmSrLineItem> srlineitems = srlineitemDao.getScmSrLineItemByItemCode(item.getId());
		
		float rate = 0.0f;
		for(int j = 0 ; j < srlineitems.size() ; j++) {
			rate = rate + srlineitems.get(j).getReceivedPrice();
		}
		
		float quantity = 0.0f;
		for(int i = 0 ; i < issuelineitems.size() ; i++) {
			quantity = quantity + issuelineitems.get(i).getIssueQty();
		}
		float avgQuantity = quantity/12;
		storeconsumptionDTO.setDescription(item.getitemDsc());
//		storeconsumptionDTO.setItemCode(item.getgrpCode()+item.getlegacyItemCode());
		storeconsumptionDTO.setItemCode(item.getId());
		storeconsumptionDTO.setUnit(item.getuomCode());
		
		storeconsumptionDTO.setAvgQuantity(avgQuantity);
		storeconsumptionDTO.setAvgValue(avgQuantity*rate);
		storeconsumptionDTO.setQauntity(quantity);
		
		return storeconsumptionDTO;
	}
	

}
