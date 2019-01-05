package com.bator.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.bator.input.InputChunk;
import org.apache.log4j.Logger;

public class ChunkInserter {

    private static final Logger log = Logger.getLogger(ChunkInserter.class);

    String chunksDb = "chunks";
    String chunksTable = "chunks";

    public void insert(List<InputChunk> chunks) {
        createTableIfNotExists();
        insertChunks(chunks);
    }

    private void insertChunks(List<InputChunk> chunks) {
        int count = 0;
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + chunksDb + ".db");
             PreparedStatement statement = connection.prepareStatement("INSERT INTO " + chunksTable + " (hash, body, creationDate, source) " +
                     "VALUES (?,?,?,?) ")) {
            for (InputChunk inputChunk : chunks) {
                int hashCode = inputChunk.hashCode();
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT COUNT(*) FROM " + chunksTable + " WHERE hash = " + hashCode);
                resultSet.next();
                int cnt = resultSet.getInt(1);
                resultSet.close();
                if (cnt == 0) {
                    statement.setInt(1, hashCode);
                    statement.setString(2, inputChunk.getText());
                    statement.setDate(3, new java.sql.Date(inputChunk.getUtcPostDate().getTime()));
                    statement.setString(4, inputChunk.getSource());
                    statement.executeUpdate();
                    statement.clearParameters();
                }
                count++;
                if (count % 100 == 0) {
                    log.debug("inserted " + count + "/" + chunks.size());
                }
            }
            ResultSet rs = connection.createStatement().executeQuery("SELECT COUNT(*) FROM " + chunksTable);
            rs.next();
            log.debug(chunksTable + " rows count " + rs.getInt(1));
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("SQLException", e);
        }
    }

    private void createTableIfNotExists() {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + chunksDb + ".db");
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + chunksTable + " (" +
                    "hash INTEGER," +
                    "body TEXT," +
                    "source TEXT," +
                    "creationDate DATETIME," +
                    "sentiment REAL," +
                    "salience REAL" +
                    ")");
            statement.executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS ux_hash_" + chunksTable + " ON " + chunksTable + "(hash)");
        } catch (SQLException e) {
            throw new RuntimeException("SQLException", e);
        }
    }
}
