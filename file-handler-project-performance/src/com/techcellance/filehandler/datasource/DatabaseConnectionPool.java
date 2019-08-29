package com.techcellance.filehandler.datasource;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

import com.techcellance.filehandler.util.Constants;

public final class DatabaseConnectionPool {

    private static DatabaseConnectionPool    datasource;
    private BasicDataSource dataSource;

    private DatabaseConnectionPool() throws IOException, SQLException, PropertyVetoException,Exception {
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName(Constants.DATA_BASE_CLASS_NAME);
        dataSource.setUsername(Constants.DATA_BASE_USER);
        dataSource.setPassword(Constants.DATA_BASE_PASS);
        dataSource.setUrl(Constants.DATA_BASE_URL);
        dataSource.setValidationQuery("select 1 from como_file_configurations");
        
        }

    public static DatabaseConnectionPool getInstance() throws IOException, SQLException, PropertyVetoException,Exception {
        if (datasource == null) {
            datasource = new DatabaseConnectionPool();
            return datasource;
        } else {
            return datasource;
        }
    }

    public Connection getConnection() throws SQLException,Exception {
    	return this.dataSource.getConnection();
     }
}
