package com.bator.input;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import static org.apache.log4j.Logger.getLogger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RedditInputTest {

    private static final Logger log = getLogger(RedditInputTest.class);

    private RedditInput redditInput = new RedditInput();

    @Before
    public void setUp() throws Exception {
        redditInput.setItemCount(1);
        redditInput.setSubredditName("Bitcoin");
    }

    @Test
    public void gather() {
        List<InputChunk> result = redditInput.gather(1);

        result.forEach(chunk -> {
            log.debug(chunk);
            assertNotNull(chunk.getUtcPostDate());
            assertEquals(chunk.getSource(), redditInput.getClass().getSimpleName() + " Bitcoin");
        });
    }
}