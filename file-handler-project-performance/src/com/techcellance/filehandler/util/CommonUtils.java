package com.techcellance.filehandler.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techcellance.filehandler.beans.CreditBatchEntryRecord;
import com.techcellance.filehandler.beans.FailedEntryInfo;
import com.techcellance.filehandler.beans.ResponseInfo;
import com.techcellance.filehandler.dao.AbstractFileHandlerServiceDao;
import com.techcellance.filehandler.enums.MonthAbr;
import com.techcellance.filehandler.enums.ResponseCode;

public class CommonUtils {

	private static Logger LGR = LogManager.getLogger(CommonUtils.class);

	public static ResponseInfo getFinalResponseCode(ResponseInfo... responseInfoList) {
		ResponseInfo finalResponse = new ResponseInfo(ResponseCode.SUCCESS.getRespCode(),
				ResponseCode.SUCCESS.getRespDesc());

		for (ResponseInfo responseInfo : responseInfoList) {
			if (!ResponseCode.SUCCESS.getRespCode().equals(responseInfo.getRespCode())) {
				finalResponse.setRespCode(responseInfo.getRespCode());
				finalResponse.setRespDesc(responseInfo.getRespDesc());
				finalResponse.setReturnData(responseInfo.getReturnData());
				break;
			}
		}

		return finalResponse;
	}

	public static void populateResponseInfo(ResponseInfo responseInfo, String respCode, String respDesc,
			Object returnData) {
		responseInfo.setRespCode(respCode);
		responseInfo.setRespDesc(respDesc);
		responseInfo.setReturnData(returnData);
	}

	public static boolean isNullOrEmptyMap(@SuppressWarnings("rawtypes") Map map) {
		return (map == null || map.isEmpty());
	}

	public static boolean isNullOrEmptyString(String string) {
		if (null == string || "".equals(string)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isNullOrEmptyStringBuffer(StringBuffer buffer) {
		if (buffer == null || buffer.length() <= 0) {
			return true;
		} else {
			return false;
		}
	}

	public static void closeRsStmt(ResultSet rs, Statement stmt) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException err) {
				logSqlException(LGR, err);
			}
		}

		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException err) {
				logSqlException(LGR, err);
			}
		}
	}

	public static void closeRsStmt(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException err) {
				logSqlException(LGR, err);
			}
		}
	}

	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException err) {
				logSqlException(LGR, err);
			}
		}
	}

	public static void returnConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException err) {
				logSqlException(LGR, err);
			}
		}
	}

	public static void setAutoCommit(Connection conn, boolean isAutoCommit) {
		if (conn != null) {
			try {
				conn.setAutoCommit(isAutoCommit);
			} catch (SQLException err) {
				logSqlException(LGR, err);
			}
		}
	}

	public static void commitData(Connection conn) {
		if (conn != null) {
			try {
				conn.commit();
			} catch (SQLException err) {
				logSqlException(LGR, err);
			}
		}
	}

	public static void rollbackData(Connection conn) {
		if (conn != null) {
			try {
				conn.rollback();
			} catch (SQLException err) {
				logSqlException(LGR, err);
			}
		}
	}

	public static void logSqlException(Logger logger, SQLException ex) {
		for (Throwable e : ex) {
			if (e instanceof SQLException) {
				logger.error("##SQLException## SQL State : " + ((SQLException) e).getSQLState() + ", Error Code : "
						+ ((SQLException) e).getErrorCode() + ", Error Message : " + e.getMessage(), e);
			}
		}
	}

	public static String getCurrentFormatDate(String format,int nDays) {
		String date = "";

		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
		
			date = sdf.format(java.sql.Date.valueOf(LocalDate.now().minusDays(nDays)));
			LGR.debug("<- Current date is ---->" + date);
		} catch (Exception e) {
			LGR.error("Exception in Getting DB Format date ---> ", e);
		}

		return date;
	}

	public static String getCurrentFormatTime(String format) {
		String date = "";

		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			date = sdf.format(new java.util.Date());
			LGR.debug(" <- Current Time is ---->" + date);
		} catch (Exception e) {
			LGR.error("Exception in Getting DB Format Time ---> ", e);
		}

		return date;
	}

	public static String toString(int value) {
		return String.valueOf(value);
	}

	public static String toString(long value) {
		return String.valueOf(value);
	}

	public static boolean isNullOrEmptyCollection(@SuppressWarnings("rawtypes") Collection collection) {
		return (collection == null || collection.isEmpty());
	}

	public static boolean isNullObject(Object object) {
		return (object == null);
	}

	public static void sleep(long sleepTime) {
		try {
			java.util.concurrent.TimeUnit.MILLISECONDS.sleep(sleepTime);
		} catch (InterruptedException e) {
			LGR.warn("##InterruptedException##", e);
		}
	}

	public static void closeStatement(CallableStatement callableStatement) {
		if (callableStatement != null) {
			try {
				callableStatement.close();
				callableStatement = null;

			} catch (SQLException e) {
				LGR.error("## SQL Exception ## :" + e.getSQLState() + " : " + e.getErrorCode(), e);
			}
		}
	}

	public static void closeStatement(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
				statement = null;

			} catch (SQLException e) {
				LGR.error("## SQL Exception ## :" + e.getSQLState() + " : " + e.getErrorCode(), e);
			}
		}
	}

	public static void closeStatement(PreparedStatement preparedStatement) {
		if (preparedStatement != null) {
			try {
				preparedStatement.close();
				preparedStatement = null;

			} catch (SQLException e) {
				LGR.error("## SQL Exception ## :" + e.getSQLState() + " : " + e.getErrorCode(), e);
			}
		}
	}

	public static void closeBufferReader(BufferedReader br) {

		try {
			if (null != br) {

				br.close();
			}
		} catch (IOException e) {
			LGR.warn("##IOEXCEPTION##", e);
		}
	}

	public static Long getInsertedKey(Statement stmt) throws SQLException {
		long insertedSerialNo = -1;

		ResultSet rsGenKeys = null;

		try {
			rsGenKeys = stmt.getGeneratedKeys();

			if (rsGenKeys.next()) {
				insertedSerialNo = rsGenKeys.getLong(1);
			}
		} finally {
			closeRsStmt(rsGenKeys, null);
		}

		return insertedSerialNo;
	}

	public static String getMaskedCardNumber(String cardNumber) {
		if (CommonUtils.isNullOrEmptyString(cardNumber)) {

			return null;
		}
		cardNumber = StringUtils.overlay(cardNumber, StringUtils.repeat("X", cardNumber.length() - 4), 0,
				cardNumber.length() - 4);
		return cardNumber;
	}

	public static Timestamp getCurrentTime() {

		return new java.sql.Timestamp(new java.util.Date().getTime());

	}

	public static String removeLeadingZeors(String numeric) {

		return numeric.replaceFirst("^0+(?!$)", "");

	}

	public static String getMonthInNumeric(String monthAbr) {

		String month = null;
		switch (monthAbr) {
		case "JAN":
			month = MonthAbr.JANUARY.getMonthNumeric();
			break;
		case "FEB":
			month = MonthAbr.FEBUARY.getMonthNumeric();
			break;

		case "MAR":
			month = MonthAbr.MARCH.getMonthNumeric();
			break;

		case "APR":
			month = MonthAbr.APRIL.getMonthNumeric();
			break;

		case "MAY":
			month = MonthAbr.MAY.getMonthNumeric();
			break;

		case "JUN":
			month = MonthAbr.JUNE.getMonthNumeric();
			break;

		case "JUL":
			month = MonthAbr.JULY.getMonthNumeric();
			break;

		case "AUG":
			month = MonthAbr.AUGUST.getMonthNumeric();
			break;

		case "SEP":
			month = MonthAbr.SEPTEMBER.getMonthNumeric();
			break;

		case "OCT":
			month = MonthAbr.OCTOBER.getMonthNumeric();
			break;

		case "NOV":
			month = MonthAbr.NOVEMBER.getMonthNumeric();
			break;

		case "DEC":
			month = MonthAbr.DECEMBER.getMonthNumeric();
			break;
		default:
			month = "";
			break;
		}

		return month;
	}

	public static String prepareCommaSeperatedString(List<String> variables) {

		return variables.stream().collect(Collectors.joining("','", "'", "'"));

	}

	public static String getDepartureDateYear(String departureMonth, String departureDay) {

		String year = "";
		try {

			Calendar currentDate = Calendar.getInstance();
			Integer month = Integer.parseInt(departureMonth);
			Integer day = Integer.parseInt(departureDay);

			if (month > (currentDate.get(Calendar.MONTH) + 1)) {

				year = Integer.toString(currentDate.get(Calendar.YEAR));
			} else if (month < (currentDate.get(Calendar.MONTH) + 1)) {

				year = Integer.toString(currentDate.get(Calendar.YEAR) + 1);

			} else if (month == (currentDate.get(Calendar.MONTH) + 1)) {

				if (day >= currentDate.get(Calendar.DAY_OF_MONTH)) {

					year = Integer.toString(currentDate.get(Calendar.YEAR));
				} else if (day < currentDate.get(Calendar.DAY_OF_MONTH)) {

					year = Integer.toString(currentDate.get(Calendar.YEAR) + 1);
				}

			}

		} catch (Exception ex) {

			LGR.warn("##Exception##", ex);
			return year;
		}

		return year;

	}

	public static void removeAlreadyPocessedFilesFromTheList(List<String> files)throws Exception {

		AbstractFileHandlerServiceDao dao = AbstractFileHandlerServiceDao.getInstance();
		List<String> alreadyProcessedFiles = dao.fetchAllProcessedFileIfExist(files);

		files.removeIf(f -> alreadyProcessedFiles.contains(f));
		LGR.info(LGR.isDebugEnabled() ? "Files after filtering the already processed files are: " + files.toString()
				: null);
	}

	public static String getFileNameConvention(String fileNameConvention, int nDaysBeforeFile) {

		String fileRegExpression = null;

		if (CommonUtils.isNullOrEmptyString(fileNameConvention)) {

			return Constants.FILE_EXT_ON_SFTP;
		}
		fileRegExpression = fileNameConvention;
		if (fileNameConvention.contains(Constants.FILE_DATE_FORMAT)) {

			fileRegExpression = fileNameConvention.replace(Constants.FILE_DATE_FORMAT,
					CommonUtils.getCurrentFormatDate(Constants.FILE_DATE_FORMAT,nDaysBeforeFile));
			LGR.debug(LGR.isDebugEnabled()
					? "Final File name regular expression after appending date" + fileRegExpression : null);
		}
		return fileRegExpression;
	}

	public static long getFirstElevenDigitOfCardNumber(String cardNumber)throws Exception {
		return (!CommonUtils.isNullOrEmptyString(cardNumber) && cardNumber.length() >11 )?Long.parseLong(cardNumber.substring(0, 11)):0;
	}

	public static Date GetDateTime(String invoiceDate) {

		Date formatedDate = new Date();

		try {

			if (CommonUtils.isNullOrEmptyString(invoiceDate)) {

				return formatedDate;
			}

			DateFormat df = new SimpleDateFormat("yyMMdd");
			formatedDate = df.parse(invoiceDate);

		} catch (Exception ex) {

			LGR.error("##EXCEPTION##", ex);
			return formatedDate;
		}
		return formatedDate;
	}
	
	public static List<String> convertStringToCommaSeperatedList(String line)throws Exception {
		
		String[] commaSeparatedArr = line.split("\\s*,\\s*",-1);
	    List<String> result = Arrays.stream(commaSeparatedArr).collect(Collectors.toList());
		return result;
	}

	public static String getRecNumbers(List<FailedEntryInfo> failedEntryInfos) {

		StringBuilder recNumber = new StringBuilder(); 
		for (FailedEntryInfo failedEntryInfo : failedEntryInfos) {
			
			recNumber.append(failedEntryInfo.getEntryRecNo());
			recNumber.append(",");
		}
		
		return recNumber.toString().replaceAll(",$", "");
	}
	
	public static String getDocumentNumbner(List<CreditBatchEntryRecord> creditBatchEntryRecords) {

		StringBuilder documentNumber = new StringBuilder(); 
		for (CreditBatchEntryRecord entry : creditBatchEntryRecords) {
			
			documentNumber.append(entry.getDocumentNumber());
			documentNumber.append(",");
		}
		
		return documentNumber.toString().replaceAll(",$", "");
	}

	public static void populateCurrentDepartureDate(CreditBatchEntryRecord credEntryRecord) {
		
		Calendar currentDate = Calendar.getInstance();
		credEntryRecord.setDepartureMonth(Integer.toString(currentDate.get(Calendar.MONTH)));
		credEntryRecord.setDepartureDate(Integer.toString(currentDate.get(Calendar.DAY_OF_MONTH)));
		credEntryRecord.setDepartureYear(Integer.toString(currentDate.get(Calendar.YEAR)));
		
	}


}
