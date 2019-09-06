package com.techcellance.filehandler.dao;


import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.techcellance.filehandler.beans.BinInformation;
import com.techcellance.filehandler.beans.CreditBatchEntryRecord;
import com.techcellance.filehandler.beans.CreditFile;
import com.techcellance.filehandler.beans.EmailInformation;
import com.techcellance.filehandler.beans.FileConfiguration;
import com.techcellance.filehandler.beans.FileElementAttirbutes;
import com.techcellance.filehandler.beans.MerchantInformation;
import com.techcellance.filehandler.dao.impl.FileHandlerServiceDaoImpl;

public abstract  class AbstractFileHandlerServiceDao {


	public static FileHandlerServiceDaoImpl getInstance()
	{
		return new FileHandlerServiceDaoImpl();
	}
	
//	public  Connection getConnection()
//	{
//		
//		try {
//		
//		Connection con=DriverManager.getConnection( Constants.DATA_BASE_URL,Constants.DATA_BASE_USER,Constants.DATA_BASE_PASS);  
//		
//		return con;
//		
//		} catch(SQLException ex) {
//			LGR.warn("##SQLException##", ex);
//			return null;
//		}
//		
//	}
	
	protected  final static String QRY_FETCH_COMO_FILE_CONFIGURATIONS = "select file_type, sftp_url, source_path, sftp_port, sftp_user, dest_path, sftp_password, scheduled_date_time,description,file_name_convention from como_file_configurations order by file_type desc";
	protected  final static String QRY_INSERT_CREDIT_FILES = "INSERT INTO settlement_files (file_name, total_successfull_record, total_failed_records, status,recieved_at) VALUES (?,?, ?, ?, ?)";
	protected  final static String QRY_UPDATE_CREDIT_FILES = "UPDATE settlement_files SET total_successfull_record = ?, total_failed_records = ? , status = ? WHERE (file_sr_no = ?)";
	protected  final static String QRY_INSERT_CREDIT_ENTRY_RECORD = "INSERT INTO credit_entry_record(invoice_number, agent_code, authorization_id, response_code, response_description,card_number, document_number, card_type, passenger, amount, ticket_code, currency, passenger_code ,ariline_code, departure_airport, arrival_airport, stop_over_code,flight_code, issuer_city, merchant_agreement_id, invoice_name, airline_name, file_sr_no,invoice_date,batch_no,mid,departure_date,departure_month,tax_amount,ticket_restricted,transaction_type,file_type,status) VALUES (?,?,?,?,?,?,?,?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";
	protected  final static String QRY_UPDATE_CREDIT_ENTRY_RECORD = "update credit_entry_record set invoice_number=? , agent_code=? ,authorization_id=?, response_code=?, response_description=?,card_number=?, card_type=?, passenger=?, amount=?, ticket_code=?,currency=?, passenger_code =?,ariline_code=?, departure_airport=?, arrival_airport=?, stop_over_code=?,flight_code=?,issuer_city=?, merchant_agreement_id=?, invoice_name=?, airline_name=?, file_sr_no=?,invoice_date=?,batch_no=?,mid=?,departure_date=?,departure_month=?,tax_amount=?,ticket_restricted=?,transaction_type=?,file_type=?,status =? where document_number=? and file_sr_no = ? ";
	protected  final static String QRY_FETCH_WORLD_PAY_CONFIGURATION = "select url, user,password from payment_processor_configuration where configuration_name = ? ";
	protected  final static String QRY_FETCH_FILE_DATA_CONFIGUEATION = "select element_name,identifier,start_pos,length,description,file_type from file_data_configuration where file_type= ?";
	protected  final static String QRY_FETCH_FILES_WITH_HALT_STATUS = "select sf.file_sr_no , sf.file_name, sf.total_successfull_record,sf.total_failed_records,sf.status,cer.document_number from settlement_files  sf inner join credit_entry_record cer on (cer.file_sr_no = sf.file_sr_no)  where sf.status = 'H' and sf.file_name = ? and cer.status = 'I'";
	protected  final static String QRY_FETCH_COMO_FILE_PARAMS = "select param_id, param_value from como_file_params where is_active = 'Y'";
	protected  final static String QRY_FETCH_ALL_PROCESSED_FILE_IF_EXIST = "select file_name from settlement_files where status ='P' and  file_name in (%S)";
	protected  final static String QRY_INSERT_BIN_FILE_INFORMAITON = "insert into bin_file_information(file_sr_no, range_from,range_until,country, brand, issuer, family) values(?,?,?,?,?,?,?)";
	protected  final static String QRY_INSERT_MID_FILE_INFORMATION = "insert into mid_file_information(file_sr_no,capture_only_agent,merchant_code,acquirer_account,iata_carrier_code,aa_country_code,currency_code,payment_method,company_id,legal_entity_address,legal_entity_city,legal_post_code,country_code) values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
	protected  final static String QRY_FETCH_AGENT_CODE_INFORMAITON = "select mfi.acquirer_account,mfi.legal_entity_address,mfi.legal_entity_city,mfi.legal_post_code,mfi.country_code,mfi.merchant_code From  mid_file_information mfi , bin_file_information bfi where bfi.brand = mfi.payment_method  and mfi.currency_code like ? and mfi.aa_country_code = ? and mfi.iata_carrier_code =? and bfi.range_from <= ?  and ? <= bfi.range_until";
	protected  final static String QRY_FETCH_EMAIL_INFORMATION  = "select email_from, email_recipient, email_pass, email_host, email_port, email_subject, email_body, email_footer from emails  where is_active = 'Y' and email_template_id =? ";
	protected  final static String QRY_TRUNCATE_MID_INFORMAITON  = "TRUNCATE mid_file_information";
	protected  final static String QRY_TRUNCATE_BIN_INFORMATION  = "TRUNCATE bin_file_information";
	public abstract List<FileConfiguration> fetchComoFileConfigurations();
	public abstract Long persistFile(String fileName) throws SQLException, PropertyVetoException, Exception;
	public abstract void persistCreditEntryRecord(int fileType,Long fileSrno,List<CreditBatchEntryRecord> cRecords);
	public abstract boolean updateFile(String fileName, Long fileSrNo ,AtomicInteger successFulRecordCount,AtomicInteger failedRecordCount,String fileStatus);
	public abstract void populateWorldPayConfiguration() throws SQLException, Exception;
	public abstract Map<String,FileElementAttirbutes> poplateFileDataConfiguration(int fileType);
	public abstract CreditFile fetchFileIfExitInHaltState(String fileName);
	public abstract void  populateComoFileParams() throws SQLException, Exception;
	public abstract List<String> fetchAllProcessedFileIfExist(List<String> files); 
	public abstract boolean persistBinFileInformation(List<BinInformation> binInformations,Long fileSrNo);
	public abstract boolean persistMerchantFileInformation(List<MerchantInformation> merchantInformations, Long fileSrNo);
	public abstract boolean  populateAgentCodeInformation(CreditBatchEntryRecord record) throws SQLException,Exception;
	public abstract EmailInformation fetchEmailConfiguration(String templateId);
	public abstract void truncateMidInformation();
	public abstract void truncateBinInformation();
	public abstract void updateCreditEntryRecord(int fileType, Long fileSrNo,List<CreditBatchEntryRecord> cRecords);
}
