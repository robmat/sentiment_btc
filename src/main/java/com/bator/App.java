package com.bator;

import java.util.List;

import com.bator.db.ChunkInserter;
import com.bator.input.InputChunk;
import com.bator.input.RedditInput;
import lombok.Data;

@Data
public class App {

    private RedditInput redditInput = new RedditInput();

    private ChunkInserter chunkInserter = new ChunkInserter();

    public static void main(String[] args)  {
       new App().start(args);
    }

    void start(String[] args) {
        redditInput.setItemCount(1); //TODO for testing, remove

        chunkInserter.insert(redditInput.gather());
    }
}
