package com.bator.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Date;

import com.bator.db.ChunkInserter;
import com.bator.google.SentimentApi;
import com.bator.google.SentimentApi.AnalyzeSentimentResponse;
import com.bator.google.SentimentApi.DocumentSentiment;
import com.bator.input.InputChunk;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddSentimentServiceTest {

    @InjectMocks
    AddSentimentService addSentimentService = new AddSentimentService();

    @Mock
    SentimentApi sentimentApi;

    @Before
    public void setUp() throws Exception {
        addSentimentService.chunksDb = "testChunks";
        addSentimentService.chunksTable = "testChunks";
        addSentimentService.batchSize = 1;

        ChunkInserter chunkInserter = new ChunkInserter();
        chunkInserter.setChunksDb("testChunks");
        chunkInserter.setChunksTable("testChunks");
        chunkInserter.insert(Collections.singletonList(InputChunk.builder().hashCode(12).utcPostDate(new Date()).text("text").build()));
    }

    @After
    public void tearDown() throws Exception {
        Files.delete(Paths.get("testChunks.db"));
    }

    @Test
    public void addSentimentToChunksWithout() throws IOException, InterruptedException, SQLException {
        DocumentSentiment sentiment = DocumentSentiment.builder().score(BigDecimal.ONE).magnitude(BigDecimal.TEN).build();
        AnalyzeSentimentResponse sentimentResponse = AnalyzeSentimentResponse.builder().documentSentiment(sentiment).build();
        when(sentimentApi.sentiment(Matchers.anyString())).thenReturn(sentimentResponse);

        addSentimentService.addSentimentToChunksWithout();

        Connection connection = DriverManager.getConnection("jdbc:sqlite:testChunks.db");
        ResultSet rs = connection.createStatement().executeQuery("SELECT COUNT(*) FROM testChunks WHERE score = 1 AND magnitude = 10");
        rs.next();
        assertEquals(1, rs.getInt(1));
        rs.close();
        connection.close();
    }
}