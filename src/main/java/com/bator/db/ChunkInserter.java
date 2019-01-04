package com.bator.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.bator.input.InputChunk;

public class ChunkInserter {

    String chunksDb = "chunks";
    String chunksTable = "chunks";

    public void insert(List<InputChunk> chunks) {
        createTableIfNotExists();
        insertChunks(chunks);
    }

    private void insertChunks(List<InputChunk> chunks) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + chunksDb + ".db");
             PreparedStatement statement = connection.prepareStatement("INSERT INTO " + chunksTable + " (hash, body, creationDate, source) VALUES (?,?,?,?) ")) {
            connection.createStatement().executeUpdate("begin");
            for (InputChunk inputChunk : chunks) {
                statement.setInt(1, inputChunk.hashCode());
                statement.setString(2, inputChunk.getText());
                statement.setDate(3, new java.sql.Date(inputChunk.getUtcPostDate().getTime()));
                statement.setString(4, inputChunk.getSource());
                assert statement.executeUpdate() == 1;
            }
            connection.createStatement().executeUpdate("commit");
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
        } catch (SQLException e) {
            throw new RuntimeException("SQLException", e);
        }
    }
}
