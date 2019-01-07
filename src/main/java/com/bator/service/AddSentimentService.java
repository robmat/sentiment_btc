package com.bator.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.bator.google.SentimentApi;
import com.bator.google.SentimentApi.DocumentSentiment;
import com.bator.input.InputChunk;
import lombok.Data;
import org.apache.log4j.Logger;

@Data
public class AddSentimentService {

    private static final Logger log = Logger.getLogger(AddSentimentService.class);

    private ExecutorService executor = Executors.newFixedThreadPool(4);
    private SentimentApi sentimentApi = new SentimentApi();

    String chunksDb = "chunks";
    String chunksTable = "chunks";
    int batchSize = 100;

    public void addSentimentToChunksWithout() throws InterruptedException {
        List<InputChunk> inputChunks = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:" + chunksDb + ".db");
            ResultSet rs = connection.createStatement().executeQuery("SELECT  hash, body FROM " + chunksTable + " WHERE score IS NULL AND magnitude IS NULL");
            while (rs.next()) {
                inputChunks.add(InputChunk.builder().hashCode(rs.getInt(1)).text(rs.getString(2)).build());
                if (inputChunks.size() >= batchSize) {
                    addSentiment(inputChunks, connection);
                    inputChunks = new ArrayList<>();
                }
            }
            rs.close();
            addSentiment(inputChunks, connection);

            executor.shutdown();
            executor.awaitTermination(15, TimeUnit.MINUTES);

            connection.close();
        } catch (SQLException e) {
            log.error("exception", e);
            throw new RuntimeException("SQLException", e);
        }
    }

    private void addSentiment(List<InputChunk> inputChunks, Connection connection) {
        Runnable runnable = () -> {
          try {
            int count = 0;
            for (InputChunk inputChunk : inputChunks) {
                try {
                    DocumentSentiment sentiment = sentimentApi.sentiment(inputChunk.getText()).getDocumentSentiment();
                    String sql = "UPDATE " + chunksTable + " SET score = " + sentiment.getScore()
                            + ", magnitude = " + sentiment.getMagnitude() + " WHERE hash = " + inputChunk.getHashCode();
                    int updates = connection.createStatement().executeUpdate(sql);
                    log.debug("updated " + (updates == 1) + " " + ++count + "/" + inputChunks.size());
                    if (updates != 1) {
                        log.warn("updates " + updates);
                        log.warn("sql " + sql);
                    }
                } catch (IOException e) {
                    log.error("exception", e);
                    throw new RuntimeException("IOException", e);
                } catch (SQLException e) {
                    log.error("exception", e);
                    throw new RuntimeException("SQLException", e);
                }
            }
          } catch (Exception e) {
            log.error("exception", e);
            throw new RuntimeException("Exception", e);
          }
        };
        executor.submit(runnable);
    }
}
