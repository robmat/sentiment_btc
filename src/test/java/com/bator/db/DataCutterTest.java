package com.bator.db;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import com.bator.input.InputChunk;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static java.math.BigDecimal.ZERO;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class DataCutterTest {

    @InjectMocks
    DataCutter dataCutter = new DataCutter();

    @Before
    public void setUp() throws Exception {
        dataCutter.chunksDb = "testChunks";
        dataCutter.chunksTable = "testChunks";

        ChunkInserter chunkInserter = new ChunkInserter();
        chunkInserter.setChunksDb("testChunks");
        chunkInserter.setChunksTable("testChunks");
        InputChunk inputChunk1 = InputChunk.builder().hashCode(1).utcPostDate(new Date()).text("text").magnitude(ZERO).score(ZERO).build();
        InputChunk inputChunk2 = InputChunk.builder().hashCode(2).utcPostDate(new Date()).text("text").build();
        InputChunk inputChunk3 = InputChunk.builder().hashCode(3).utcPostDate(addDays(new Date(), -8)).text("text").magnitude(ZERO).score(ZERO).build();
        InputChunk inputChunk4 = InputChunk.builder().hashCode(4).utcPostDate(addDays(new Date(), -8)).text("text").build();
        chunkInserter.insert(Arrays.asList(inputChunk1, inputChunk2, inputChunk3, inputChunk4));
    }

    @After
    public void tearDown() throws Exception {
        Files.delete(Paths.get("testChunks.db"));
    }

    @Test
    public void cut() throws SQLException {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:testChunks.db");
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM testChunks")) {
            rs.next();
            assertEquals(4, rs.getInt(1));
        }

        dataCutter.cut();

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:testChunks.db");
             Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM testChunks")) {
            rs.next();
            assertEquals(2, rs.getInt(1));
        }
    }
}