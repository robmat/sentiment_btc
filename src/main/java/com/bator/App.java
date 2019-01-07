package com.bator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.bator.db.ChunkInserter;
import com.bator.input.InputChunk;
import com.bator.input.RedditInput;
import com.bator.service.AddSentimentService;
import com.bator.ui.GraphShower;
import lombok.Data;

@Data
public class App {

    private static String[] subreddits = {"Bitcoin", "btc", "BitcoinBeginners", "CryptoMarkets",
        "bitcoin_uncensored", "BitcoinMarkets"};

    private ChunkInserter chunkInserter = new ChunkInserter();

    private ExecutorService executor = Executors.newFixedThreadPool(subreddits.length);

    private AddSentimentService addSentimentService = new AddSentimentService();

    private GraphShower graphShower = new GraphShower();

    public static void main(String[] args) throws InterruptedException {
       new App().start(args);
    }

    void start(String[] args) throws InterruptedException {
        if (Objects.nonNull(args)) {
            if (Arrays.asList(args).contains("-sentiment")) {
                fillInSentiment();
            }
            if (Arrays.asList(args).contains("-showGraph")) {
                showGraph();
            }
        }
    }

    private void fillInSentiment() throws InterruptedException {
        List<InputChunk> inputChunks = new ArrayList<>();

        for (String subredditName : subreddits) {
            final RedditInput redditInput = new RedditInput();
            redditInput.setSubredditName(subredditName);
            //redditInput.setItemCount(8); //for testing, remove for real use

            executor.submit(() -> inputChunks.addAll(redditInput.gather()));
        }

        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.MINUTES);
        chunkInserter.insert(inputChunks);

        addSentimentService.addSentimentToChunksWithout();
    }

    private void showGraph() {
        graphShower.start();
    }
}
