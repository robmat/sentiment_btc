package com.bator.sentiment;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.bator.input.InputChunk;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class StanfordNlpSentimentTest {

    private static final Logger log = Logger.getLogger(StanfordNlpSentimentTest.class);

    private StanfordNlpSentiment stanfordNlpSentiment = new StanfordNlpSentiment();

    @Test
    public void findPositiveSentiment() {
        InputChunk inputChunk = InputChunk.builder().text("bitcoin is great, i'm very glad i've invested").build();

        stanfordNlpSentiment.findSentiment(inputChunk);

        assertNotNull(inputChunk.getScoreStanford());
        log.debug(inputChunk);
    }

    @Test
    public void findNegativeSentiment() {
        InputChunk inputChunk = InputChunk.builder().text(" it feels horrible to have bitcoin, i'm very sad i've invested, I quit").build();

        stanfordNlpSentiment.findSentiment(inputChunk);

        assertNotNull(inputChunk.getScoreStanford());
        log.debug(inputChunk);
    }

    @Test
    public void shouldPrintSentimentUsingStanfordFromChunks() throws SQLException {
        String chunksDb = "chunks";
        String chunksTable = "chunks";
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + chunksDb + ".db");
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT body, score FROM " + chunksTable + " ORDER BY creationDate ASC LIMIT 10")) {
            while (resultSet.next()) {
                InputChunk inputChunk = InputChunk.builder().text(resultSet.getString(1)).build();
                stanfordNlpSentiment.findSentiment(inputChunk);

                log.debug("body: " + resultSet.getString(1));
                log.debug("score: " + resultSet.getBigDecimal(2) + " new score: " + inputChunk.getScore());

                assertNotNull(inputChunk.getScoreStanford());
            }
        }
    }
}