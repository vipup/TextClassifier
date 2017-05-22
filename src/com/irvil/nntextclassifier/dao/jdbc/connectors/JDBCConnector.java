package com.irvil.nntextclassifier.dao.jdbc.connectors;

import java.sql.Connection;

public interface JDBCConnector {
  Connection getDBConnection();
}
