package com.bator.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.apache.log4j.Logger;

import static org.apache.commons.lang3.time.DateUtils.addDays;

public class DataCutter {

    private static final Logger log = Logger.getLogger(DataCutter.class);

    String chunksDb = "chunks";
    String chunksTable = "chunks";

    Date cutOffDate = addDays(new Date(), -7);

    public void cut() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + chunksDb + ".db");
             Statement statement = connection.createStatement()) {
            int deleted = statement.executeUpdate("DELETE FROM " + chunksTable + " WHERE creationDate <= " + cutOffDate.getTime() +
                    " AND score IS NULL AND magnitude IS NULL");
            log.debug("deleted before " + cutOffDate + " count " + deleted);
            statement.executeUpdate("vacuum");
        } catch (SQLException e) {
            throw new RuntimeException("SQLException", e);
        }
    }
}