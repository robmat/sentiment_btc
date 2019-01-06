package com.bator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.bator.db.ChunkInserter;
import com.bator.input.Input;
import com.bator.input.InputChunk;
import com.bator.input.RedditInput;
import com.bator.service.AddSentimentService;
import lombok.Data;

@Data
public class App {

    private ChunkInserter chunkInserter = new ChunkInserter();

    private ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 3);

    private AddSentimentService addSentimentService = new AddSentimentService();

    public static void main(String[] args) throws InterruptedException {
       new App().start(args);
    }

    void start(String[] args) throws InterruptedException {
        List<InputChunk> inputChunks = new ArrayList<>();
        for (String subredditName : new String[] {"Bitcoin", "btc", "BitcoinBeginners", "CryptoMarkets", "bitcoin_uncensored", "BitcoinMarkets"}) {
            final RedditInput redditInput = new RedditInput();
            redditInput.setSubredditName(subredditName);
            redditInput.setItemCount(5); //TODO for testing, remove

            executor.submit(() -> inputChunks.addAll(redditInput.gather()));
        }

        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.MINUTES);
        chunkInserter.insert(inputChunks);

        addSentimentService.addSentimentToChunksWithout();
    }
}
