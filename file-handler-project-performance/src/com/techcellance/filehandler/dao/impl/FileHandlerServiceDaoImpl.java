package com.techcellance.filehandler.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techcellance.filehandler.beans.BinInformation;
import com.techcellance.filehandler.beans.CreditBatchEntryRecord;
import com.techcellance.filehandler.beans.CreditFile;
import com.techcellance.filehandler.beans.EmailInformation;
import com.techcellance.filehandler.beans.FileConfiguration;
import com.techcellance.filehandler.beans.FileElementAttirbutes;
import com.techcellance.filehandler.beans.MerchantInformation;
import com.techcellance.filehandler.dao.AbstractFileHandlerServiceDao;
import com.techcellance.filehandler.datasource.DatabaseConnectionPool;
import com.techcellance.filehandler.enums.EntryRecordAttribute;
import com.techcellance.filehandler.util.CommonUtils;
import com.techcellance.filehandler.util.Constants;

public class FileHandlerServiceDaoImpl extends AbstractFileHandlerServiceDao{

private static Logger LGR = LogManager.getLogger(FileHandlerServiceDaoImpl.class);
	
	public FileHandlerServiceDaoImpl() {
	
	}

	@Override
	public List<FileConfiguration> fetchComoFileConfigurations() {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int index = 0 ;
		List<FileConfiguration> fileConfigurations = new ArrayList<FileConfiguration>();
		Connection conn =null;
		try
		{
		
			LGR.info(LGR.isInfoEnabled()? "In method fetchComoFileConfigurations " : null);
			conn = DatabaseConnectionPool.getInstance().getConnection();
			
			pstmt = conn.prepareStatement(QRY_FETCH_COMO_FILE_CONFIGURATIONS);
			
			rs = pstmt.executeQuery();

			while (rs.next()) {
				index = 0 ;
				FileConfiguration fileConfiguration = new FileConfiguration();
				fileConfiguration.setFileType(rs.getInt(++index));
				fileConfiguration.setSftpUrl(rs.getString(++index));		
				fileConfiguration.setSoucrePath(rs.getString(++index));
				fileConfiguration.setSftpPort(rs.getInt(++index));
				fileConfiguration.setSftpUser(rs.getString(++index));
				fileConfiguration.setDestinationPath(rs.getString(++index));
				fileConfiguration.setSftpPassword(rs.getString(++index));
				fileConfiguration.setScheduledDateTme(rs.getDate(++index));
				fileConfiguration.setDescription(rs.getString(++index));
				fileConfiguration.setFileNameConvention(rs.getString(++index));
				LGR.info(LGR.isInfoEnabled()? "File Cofiguration : \n " + fileConfiguration.toString(): null);
				fileConfigurations.add(fileConfiguration);
			}
			
		
		LGR.info(LGR.isInfoEnabled()? "End of method fetchComoFileConfigurations with total configurations found : " +  fileConfigurations.size(): null);
		
		}catch(SQLException sqle)
			{
				LGR.error("SQLException in fetchCurrentFollowMeCard ---> Error code : " + sqle.getErrorCode(), sqle);
				CommonUtils.logSqlException(LGR, sqle);
			}catch(Exception ex){
				LGR.error("SQLException in fetchCurrentFollowMeCard ---> Error code : " , ex);
				
			}finally
			{
				CommonUtils.returnConnection(conn);
				CommonUtils.closeResultSet(rs);
				CommonUtils.closeStatement(pstmt);			
			}
		

		return fileConfigurations;
		
	}

	@Override
	public Long persistFile(String fileName) {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int index = 0 ;
		Long fileSrNo = 0L;
		Connection conn= null;
	
		try
		{
			LGR.info(LGR.isInfoEnabled()? "Going to persit file with file name  " + fileName : null);
			conn= DatabaseConnectionPool.getInstance().getConnection() ;
		
			pstmt = conn.prepareStatement(QRY_INSERT_CREDIT_FILES,PreparedStatement.RETURN_GENERATED_KEYS);
			pstmt.setString(++index,fileName);
			pstmt.setInt(++index,0);
			pstmt.setInt(++index,0);
			pstmt.setString(++index,Constants.IN_PROGRESS);
			pstmt.setTimestamp(++index, CommonUtils.getCurrentTime());
			
			 int count = pstmt.executeUpdate();
			 boolean isDataInserted = count > 0;
			  
			  LGR.debug("The result after inserting file data --->" + isDataInserted);
			  fileSrNo =  CommonUtils.getInsertedKey(pstmt);
			 
		LGR.info(LGR.isInfoEnabled()? "End of method persistFile file srno " : null);
		
		}catch(SQLException sqle){
				LGR.error("SQLException in fetchCurrentFollowMeCard ---> Error code : " + sqle.getErrorCode(), sqle);
				CommonUtils.logSqlException(LGR, sqle);
		}catch( Exception ex){
				LGR.error("##Exception## in persist file" , ex);
		}
		
			finally
			{
			
				CommonUtils.closeResultSet(rs);
				CommonUtils.closeStatement(pstmt);
				CommonUtils.returnConnection(conn);
			}
		
		return fileSrNo;
	}

	@Override
	public void persistCreditEntryRecord(int  fileType,Long fileSrno , List<CreditBatchEntryRecord> cRecords) {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int index = 0 ;
		Connection conn= null;
		
		try
		{

			conn= DatabaseConnectionPool.getInstance().getConnection() ;
			if(null == conn){
				
				LGR.warn("Unable to get  the connection for database persistance");
				return; 
			}
			
			
			pstmt = conn.prepareStatement(QRY_INSERT_CREDIT_ENTRY_RECORD);
			for (CreditBatchEntryRecord cRecord : cRecords) {
				
			LGR.debug(LGR.isDebugEnabled()? "Going to persit entry record with document number: " + cRecord.getDocumentNumber() : null);
			index = 0;
			pstmt.setString(++index,cRecord.getInvoiceNumber());
			pstmt.setString(++index,cRecord.getAgentCode());
			pstmt.setString(++index, cRecord.getAuthorizationId());
			pstmt.setString(++index, cRecord.getResponseCode());
			pstmt.setString(++index, cRecord.getResponseDescription());
			pstmt.setString(++index, CommonUtils.getMaskedCardNumber(cRecord.getCardNumber()));
			pstmt.setString(++index, cRecord.getDocumentNumber());
			pstmt.setString(++index, cRecord.getCardType());
			pstmt.setString(++index, cRecord.getApprovalCode());
			pstmt.setString(++index, cRecord.getPassenger());
			pstmt.setString(++index, cRecord.getAmmount());
			pstmt.setString(++index, cRecord.getTicketCode());
			pstmt.setString(++index, cRecord.getCurrency());
			pstmt.setString(++index, cRecord.getPassengerCode());				
			pstmt.setNull(++index, Types.NULL);
			pstmt.setNull(++index, Types.NULL);
			pstmt.setNull(++index, Types.NULL);
			pstmt.setNull(++index, Types.NULL);
			pstmt.setNull(++index, Types.NULL);

			pstmt.setString(++index, cRecord.getIssuerCity());
			pstmt.setString(++index, cRecord.getMerchantAgreementId());
			pstmt.setString(++index , cRecord.getInvoiceName());
		    pstmt.setString(++index , cRecord.getAirlineName());
			pstmt.setLong(++index, fileSrno);
			pstmt.setTimestamp(++index,  new Timestamp(CommonUtils.GetDateTime(cRecord.getInvoiceDate()).getTime()));
			pstmt.setString(++index, cRecord.getBatchNumber());
			pstmt.setString(++index, cRecord.getMidLookUpCode());
			pstmt.setString(++index, cRecord.getDepartureDate());
			pstmt.setString(++index, cRecord.getDepartureMonth());
			pstmt.setDouble(++index, cRecord.getEntryAdditionalInformation().getTaxAmount());			
			pstmt.setString(++index, cRecord.getTicketRestricted());
			pstmt.setString(++index, cRecord.getTransactionType());
			pstmt.setInt(++index, fileType);
			pstmt.setString(++index, cRecord.getStatus());
			
			pstmt.addBatch();
			
			}
			
			pstmt.executeBatch();
			  			 
		LGR.info(LGR.isInfoEnabled()? "End of method persistFile file srno " : null);
		
		}catch(SQLException sqle){
				LGR.error("SQLException in  persistCreditEntryRecord ---> Error code : " + sqle.getErrorCode(), sqle);
				CommonUtils.logSqlException(LGR, sqle);
			}catch( Exception  ex){
				LGR.error("##Exception## in persist file" , ex);
				}
			finally
			{
			
				CommonUtils.returnConnection(conn);
				CommonUtils.closeResultSet(rs);
				CommonUtils.closeStatement(pstmt);
			}
	}

	@Override
	public boolean updateFile(String fileName, Long fileSrNo ,AtomicInteger totalSuccessEntry,AtomicInteger totalFailedEntry,String fileStatus) {
		
		int success = 0; ;
		PreparedStatement updateFileStats = null;
		int psIndex = 0;
		LGR.debug("Inside Method updateFile()----->");
		Connection conn= null;
	
		try{			
				
			conn= DatabaseConnectionPool.getInstance().getConnection() ;
			
				updateFileStats  = conn.prepareStatement(QRY_UPDATE_CREDIT_FILES);
				updateFileStats.setInt(++psIndex, totalSuccessEntry.get()); 
				updateFileStats.setInt(++psIndex, totalFailedEntry.get()); 
				updateFileStats.setString(++psIndex,fileStatus ); 
				updateFileStats.setLong(++psIndex, fileSrNo); 
				
				success = updateFileStats.executeUpdate(); // execute the query
		
		} catch (SQLException e) {
			CommonUtils.logSqlException(LGR, e);
			return false;
		} catch (Exception ex) {
			LGR.error("##Exception## in persist file", ex);
		}
		finally{
			CommonUtils.closeStatement(updateFileStats);
			CommonUtils.returnConnection(conn);
		}
		LGR.debug("Exit Method updateFile()----->");

		return success >= 1 ;
		
	}

	@Override
	public void populateWorldPayConfiguration() throws SQLException,Exception{
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int index = 0;
		Connection conn = null ;
		try {
			
			
				LGR.info(LGR.isInfoEnabled()? "In method populateWorldPayConfiguration " : null);
				
				conn= DatabaseConnectionPool.getInstance().getConnection() ;
				
				pstmt = conn.prepareStatement(QRY_FETCH_WORLD_PAY_CONFIGURATION);
				pstmt.setString(++index, Constants.WORLD_PAY_ID);
				
				rs = pstmt.executeQuery();
				while (rs.next())
				{
					index = 0;
					Constants.WORLD_PAY_URL= rs.getString(++index);
					Constants.WORLD_PAY_USER=rs.getString(++index);		
					Constants.WORLD_PAY_PASS=rs.getString(++index);
				
				}
			LGR.info(LGR.isInfoEnabled()? "End of method populateWorldPayConfiguration": null);
		
			
		}catch(Exception ex) {
			LGR.error("##EXCEPTION##", ex);
			
		}finally{
			CommonUtils.closeStatement(pstmt);
			CommonUtils.returnConnection(conn);
			
		}
	
	}

	@Override
	public Map<String, FileElementAttirbutes> poplateFileDataConfiguration(int fileType) {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int index = 0;
		Connection conn = null ;
		String elementName= null;
		Map<String ,FileElementAttirbutes> fileAttrMap = new ConcurrentHashMap<String, FileElementAttirbutes>();
		FileElementAttirbutes fileAttirbutes =  null;
		try {
			
			
				LGR.info(LGR.isInfoEnabled()? "In method poplateFileDataConfiguration " : null);
				
				conn= DatabaseConnectionPool.getInstance().getConnection() ;
				
				pstmt = conn.prepareStatement(QRY_FETCH_FILE_DATA_CONFIGUEATION );
				pstmt.setInt(++index,fileType);
				rs = pstmt.executeQuery();
				while (rs.next())
				{
					index = 0;
					elementName = rs.getString(++index);
					fileAttirbutes= populatefileDataConfiguration(rs,elementName);
					if(!CommonUtils.isNullObject(fileAttirbutes)){
						
						fileAttrMap.put(elementName,fileAttirbutes);
					}
				
				}
			LGR.info(LGR.isInfoEnabled()? "End of method poplateFileDataConfiguration": null);
		
		} catch (SQLException e) {
			CommonUtils.logSqlException(LGR, e);
			return null;

		} catch (Exception ex) {
			LGR.error("##Exception## in persist file", ex);
		} finally {
			CommonUtils.closeStatement(pstmt);
			CommonUtils.returnConnection(conn);

		}

		return fileAttrMap; 
	}

	private FileElementAttirbutes  populatefileDataConfiguration(ResultSet rs, String elementName) throws SQLException {
		
		FileElementAttirbutes  fileElementAttirbutes = null; 
		int index = 1;	           
		List <EntryRecordAttribute> attributes = Arrays.asList(EntryRecordAttribute.values());
		
			if (attributes.stream().anyMatch(a-> a.name().equalsIgnoreCase(elementName))) {
	    		fileElementAttirbutes = new FileElementAttirbutes();
	    		fileElementAttirbutes.setElementName(elementName);
	    		fileElementAttirbutes.setIdentifier(rs.getString(++index));
	    		fileElementAttirbutes.setStatrtPos(rs.getInt(++index));
	    		fileElementAttirbutes.setLength(rs.getInt(++index));
	    		fileElementAttirbutes.setDescription(rs.getString(++index));
	    		LGR.debug(LGR.isDebugEnabled() ?" Element Name: "  + elementName  + " with follwoing configuration : " +fileElementAttirbutes.toString():null);
	    	}				
	       			
		return fileElementAttirbutes;
	}

	@Override
	public CreditFile fetchFileIfExitInHaltState(String fileName) {
	
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int index = 0;
		Connection conn = null ;
		CreditFile creditFile = null;
		
		try {
			
			
				LGR.info(LGR.isInfoEnabled()? "In method fetchFileIfExitInHaltState " : null);
				
				conn=DatabaseConnectionPool.getInstance().getConnection() ;
				
				pstmt = conn.prepareStatement(QRY_FETCH_FILES_WITH_HALT_STATUS );	
				pstmt.setString(++index, fileName);
				rs = pstmt.executeQuery();
				
				while (rs.next())
				{
					index = 0 ;
					if(CommonUtils.isNullObject(creditFile)) {
						creditFile = new CreditFile();
						creditFile.setFileSrNo(rs.getLong(++index));
						creditFile.setFileName( rs.getString(++index));
						creditFile.setTotalSuccessfullRecord(rs.getInt(++index));
						creditFile.setTotalFailedRecords(rs.getInt(++index));
						creditFile.setFileStatus(rs.getString(++index));;	
					}
					creditFile.getInProgressOrderNumbers().add(rs.getString("document_number"));
				}
			
				LGR.info(LGR.isInfoEnabled()? "End of method fetchFileIfExitInHaltState": null);
		
		} catch (SQLException e) {
			CommonUtils.logSqlException(LGR, e);
			return null;

		} catch (Exception ex) {
			LGR.error("##Exception## in persist file", ex);
		} finally {
			CommonUtils.closeStatement(pstmt);
			CommonUtils.returnConnection(conn);

		}

		return creditFile;
	}

	@Override
	public void populateComoFileParams() throws SQLException, Exception{
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int index = 0;
		Connection conn = null ;
		String param_id = null ;
		try {
			
			
				LGR.info(LGR.isInfoEnabled()? "In method populateComoFileParams " : null);
				
				conn= DatabaseConnectionPool.getInstance().getConnection();
				
				pstmt = conn.prepareStatement(QRY_FETCH_COMO_FILE_PARAMS);
				
				rs = pstmt.executeQuery();
				while (rs.next())
				{
					index = 0;
					param_id = rs.getString(++index);
					 
					if(Constants.PARAM_ID_CFH_THREAD_POOL_SIZE.equalsIgnoreCase(param_id)) {
					
						Constants.CFH_THREAD_POOL_SIZE= Integer.parseInt(rs.getString(++index));
						
					}else if(Constants.PARAM_ID_CBR_THREAD_POOL_SIZE.equalsIgnoreCase(param_id)) {
						
						Constants.CBR_THREAD_POOL_SIZE= Integer.parseInt(rs.getString(++index));
					}else if (Constants.PARAM_ID_BIN_RECORD_BATCH_SIZE.equalsIgnoreCase(param_id)) {
						Constants.BIN_RECORD_BATCH_SIZE = Integer.parseInt(rs.getString(++index));
					
					}else if(Constants.PARAM_ID_MERHCANT_RECORD_BATCH_SIZE.equalsIgnoreCase(param_id)){
						Constants.MERCHANT_RECORD_BATCH_SIZE = Integer.parseInt(rs.getString(++index));
					}else if(Constants.PARAM_ID_MID_FILE_NO_OF_DAYS.equalsIgnoreCase(param_id)) {
						Constants.MID_FILE_NO_OF_DAYS = Integer.parseInt(rs.getString(++index));
					}
				}
				
			LGR.info(LGR.isInfoEnabled()? "End of method ": null);
		
			
		}
		finally{
			CommonUtils.closeStatement(pstmt);
			CommonUtils.returnConnection(conn);
			
		}

		
	}

	@Override
	public List<String> fetchAllProcessedFileIfExist(List<String> files) {
	
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int index = 0;
		Connection conn = null ;
		List<String> filesAlreadyExitsInDatabase = new ArrayList<String>();
		try {
		
				LGR.info(LGR.isInfoEnabled()? "In method fetchAllProcessedFileIfExist " : null);
				conn= DatabaseConnectionPool.getInstance().getConnection() ;
				String query = QRY_FETCH_ALL_PROCESSED_FILE_IF_EXIST;

				String commaSepfiles = CommonUtils.prepareCommaSeperatedString(files);
				
				if(CommonUtils.isNullOrEmptyString(commaSepfiles)) {
					
					return filesAlreadyExitsInDatabase;
				}
				
				query = String.format(query,commaSepfiles);
				pstmt = conn.prepareStatement(query);
				rs = pstmt.executeQuery();
				
				while (rs.next())
				{
					index = 0 ;
					filesAlreadyExitsInDatabase.add(rs.getString(++index));
				}
			
				LGR.info(LGR.isInfoEnabled()? "End of method fetchFileIfExitInHaltState": null);
		
			
		} catch (SQLException e) {
			CommonUtils.logSqlException(LGR, e);	
			return filesAlreadyExitsInDatabase;
			
		}catch (Exception ex) {
			LGR.warn("##Exception## in fetchAllProcessedFileIfExist", ex );
			return filesAlreadyExitsInDatabase;
		}finally{
			CommonUtils.closeStatement(pstmt);
			CommonUtils.returnConnection(conn);
		}

		return filesAlreadyExitsInDatabase;
	}

	@Override
	public boolean persistBinFileInformation(List<BinInformation> binInformations,Long fileSrno) {

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int index = 0;
		Connection conn = null;
		try {

			conn = DatabaseConnectionPool.getInstance().getConnection();
			if (null == conn) {
				LGR.warn("Unable to get  the connection for database persistance");
				return false;
			}

			pstmt = conn.prepareStatement(QRY_INSERT_BIN_FILE_INFORMAITON);

			for (BinInformation record : binInformations) {
				index = 0;
				pstmt.setLong(++index, fileSrno);
				pstmt.setLong(++index, record.getRangeFrom());
				pstmt.setLong(++index, record.getRangeUntil());
				pstmt.setString(++index, record.getCountry());
				pstmt.setString(++index, record.getBrand());
				pstmt.setString(++index, record.getIssuer());
				pstmt.setString(++index, record.getFamily());
				pstmt.addBatch();
			}

			pstmt.executeBatch();

			
			LGR.info(LGR.isInfoEnabled() ? "End of method persistBinFileInformaiton  " : null);
			return true;
			
		} catch (SQLException sqle) {
			LGR.error("SQLException in  persistBinFileInformation ---> Error code : " + sqle.getErrorCode(), sqle);
			CommonUtils.logSqlException(LGR, sqle);
			return false;
		} catch (Exception ex) {
			LGR.error("##Exception## in persist file", ex);
			return false;
		} finally {

			CommonUtils.returnConnection(conn);
			CommonUtils.closeResultSet(rs);
			CommonUtils.closeStatement(pstmt);
		}
	}

	@Override
	public boolean persistMerchantFileInformation(List<MerchantInformation> merchantInformations, Long fileSrNo) {

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int index = 0;
		Connection conn = null;
		try {

			conn = DatabaseConnectionPool.getInstance().getConnection();
			if (null == conn) {
				LGR.warn("Unable to get  the connection for database persistance");
				return false;
			}

			pstmt = conn.prepareStatement(QRY_INSERT_MID_FILE_INFORMATION);
			
			for (MerchantInformation record : merchantInformations) {
				index = 0;
				pstmt.setLong(++index, fileSrNo);
				pstmt.setString(++index, record.getCaptureOnlyAgent());
				pstmt.setString(++index, record.getMerchantCode());
				pstmt.setString(++index, record.getAcquirerAccount());
				pstmt.setString(++index, record.getiATANumericalCarrierCode());
				pstmt.setString(++index, record.getAccAccountCountryCode());
				pstmt.setString(++index, record.getCurrencyCode());
				pstmt.setString(++index, record.getPaymentMethod());
				pstmt.setString(++index, record.getCompanyID());
				pstmt.setString(++index, record.getLegalEntityAddress1());
				pstmt.setString(++index, record.getLegalEntityCity());
				pstmt.setString(++index, record.getLegalentityPostCode());
				pstmt.setString(++index, record.getCountryCode());

				pstmt.addBatch();

				
			}
			
			pstmt.executeBatch();
			
			LGR.info(LGR.isInfoEnabled() ? "End of method persistBinFileInformaiton  " : null);
			return true;
			
		} catch (SQLException sqle) {
			LGR.error("SQLException in  persistBinFileInformation ---> Error code : " + sqle.getErrorCode(), sqle);
			CommonUtils.logSqlException(LGR, sqle);
			return false;
		} catch (Exception  ex) {
			LGR.error("##Exception## in persist file", ex);
			return false;
		} finally {

			CommonUtils.returnConnection(conn);
			CommonUtils.closeResultSet(rs);
			CommonUtils.closeStatement(pstmt);

		}
		
	}

	@Override
	public boolean populateAgentCodeInformation(CreditBatchEntryRecord record) throws SQLException,Exception{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int index = 0;
		Connection conn = null ;
		boolean isAgentCodePopulated = false;
		try {		
				LGR.info(LGR.isInfoEnabled()? "In method fetchAgentCodeInformation " : null);	
				conn=DatabaseConnectionPool.getInstance().getConnection() ;
				LGR.debug(LGR.isDebugEnabled() ? "Query to fetch mid look up : " + QRY_FETCH_AGENT_CODE_INFORMAITON+ " \n Parameters ["+record.getAgentCode() + ","+record.getCountryCode()+","+record.getCurrency()+"]":null);
				pstmt = conn.prepareStatement(QRY_FETCH_AGENT_CODE_INFORMAITON );
				pstmt.setString(++index, "%" + record.getCurrency()+ "%");
				pstmt.setString(++index,record.getCountryCode());
				pstmt.setString(++index, record.getAgentCode());
				pstmt.setLong(++index, CommonUtils.getFirstElevenDigitOfCardNumber(record.getCardNumber()));
				
				rs = pstmt.executeQuery();
				
				if (rs.next()){
					index = 0;
					record.setMidLookUpCode(rs.getString(++index));
					record.setLegalEntityAddress(rs.getString(++index));
					record.setLegalEntityCity(rs.getString(++index));
					record.setLegalPostalCode(rs.getString(++index));
					record.setLegalCountryCode(rs.getString(++index));
					isAgentCodePopulated = true; 
				}else {
					record.setStatus(Constants.IN_PROGRESS);
					isAgentCodePopulated = false;
				}
				
				LGR.info(LGR.isInfoEnabled()? "End of method fetchAgentCodeInformation ": null);
			
			return isAgentCodePopulated; 
		
		} finally {
			CommonUtils.closeStatement(pstmt);
			CommonUtils.returnConnection(conn);
		}
		
	}

	@Override
	public EmailInformation fetchEmailConfiguration(String templateId) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int index = 0;
		Connection conn = null ;
		EmailInformation emailInformation = null; 
		
		try {		
				LGR.info(LGR.isInfoEnabled()? "In method fetchEmailConfiguration" : null);	
				conn=DatabaseConnectionPool.getInstance().getConnection() ;
				LGR.debug(LGR.isDebugEnabled() ? "Query to fetch email information  : " + QRY_FETCH_EMAIL_INFORMATION+ " \n Template Id  ["+ templateId +"]":null);
				pstmt = conn.prepareStatement(QRY_FETCH_EMAIL_INFORMATION );
				pstmt.setString(++index,templateId );
				rs = pstmt.executeQuery();
				
				if (rs.next()){
					index = 0;
					emailInformation = new EmailInformation();
					emailInformation.setEmailTemplateId(templateId);
					emailInformation.setEmailFrom(rs.getString(++index));
					emailInformation.setEmailRecipient(rs.getString(++index));
					emailInformation.setEmailPass(rs.getString(++index));
					emailInformation.setEmailHost(rs.getString(++index));
					emailInformation.setEmailPort(rs.getInt(++index));
					emailInformation.setEmailSubject(StringEscapeUtils.unescapeJava(rs.getString(++index)));
					emailInformation.setEmailBody(StringEscapeUtils.unescapeJava(rs.getString(++index)));
					emailInformation.setEmailFooter(StringEscapeUtils.unescapeJava(rs.getString(++index)));
				}
				
				LGR.info(LGR.isInfoEnabled()? "End of method fetchEmailInformation ": null);
		
		} catch (Exception ex){
			
			LGR.error("Exception in fetchEmailConfiguration" , ex);
			return null;
			
		}finally {
			CommonUtils.closeStatement(pstmt);
			CommonUtils.returnConnection(conn);
		}
		return emailInformation;
	}

	@Override
	public void truncateMidInformation() {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn= null;
	
		try
		{
			LGR.info(LGR.isInfoEnabled()? "Going to truncate mid information from the database"  : null);
			conn= DatabaseConnectionPool.getInstance().getConnection() ;
		
			pstmt = conn.prepareStatement(QRY_TRUNCATE_MID_INFORMAITON);
			pstmt.executeUpdate();
			  
			 
		LGR.info(LGR.isInfoEnabled()? "End of method truncateMidInformation" : null);
		
		}catch(SQLException sqle){
				LGR.error("SQLException in truncateMidInformation ---> Error code : " + sqle.getErrorCode(), sqle);
				CommonUtils.logSqlException(LGR, sqle);
		}catch( Exception ex){
				LGR.error("##Exception## in truncateMidInformation" , ex);
		}finally{
				CommonUtils.closeResultSet(rs);
				CommonUtils.closeStatement(pstmt);
				CommonUtils.returnConnection(conn);
		}
		
		
	}

	@Override
	public void truncateBinInformation() {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn= null;
	
		try
		{
			LGR.info(LGR.isInfoEnabled()? "Going to truncate Bin Information from the database"  : null);
			conn= DatabaseConnectionPool.getInstance().getConnection() ;
		
			pstmt = conn.prepareStatement(QRY_TRUNCATE_BIN_INFORMATION);
			pstmt.executeUpdate();
			  
			 
		LGR.info(LGR.isInfoEnabled()? "End of method truncateBinInformation" : null);
		
		}catch(SQLException sqle){
				LGR.error("SQLException in truncateBinInformation ---> Error code : " + sqle.getErrorCode(), sqle);
				CommonUtils.logSqlException(LGR, sqle);
		}catch( Exception ex){
				LGR.error("##Exception## in truncateBinInformation" , ex);
		}finally{
				CommonUtils.closeResultSet(rs);
				CommonUtils.closeStatement(pstmt);
				CommonUtils.returnConnection(conn);
		}
		
	}
}
