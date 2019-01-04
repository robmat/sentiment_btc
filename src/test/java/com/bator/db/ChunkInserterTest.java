package com.bator.db;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Date;

import com.bator.input.InputChunk;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ChunkInserterTest {

    ChunkInserter chunkInserter = new ChunkInserter();

    @Before
    public void setUp() throws Exception {
        chunkInserter.chunksDb = "testChunks";
        chunkInserter.chunksTable = "testChunks";
    }

    @After
    public void tearDown() throws Exception {
        Files.delete(Paths.get(chunkInserter.chunksDb + ".db"));
    }

    @Test
    public void insert() {
        InputChunk inputChunk = InputChunk.builder()
                .text("xXxXxXasda")
                .source("ascf")
                .utcPostDate(new Date())
                .build();
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + chunkInserter.chunksDb + ".db");
             Statement statement = connection.createStatement()) {

            //when
            chunkInserter.insert(Collections.singletonList(inputChunk));

            ResultSet rs = statement.executeQuery("SELECT body, source FROM " + chunkInserter.chunksTable + " WHERE body = '" + inputChunk.getText() + "' AND source = '" + inputChunk.getSource() + "'");
            rs.next();
            assertEquals(rs.getString(1), inputChunk.getText());
            assertEquals(rs.getString(2), inputChunk.getSource());
            assertFalse(rs.next());
        } catch (SQLException e) {
            throw new RuntimeException("SQLException", e);
        }

    }
}