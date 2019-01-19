package com.bator.input;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class TwitterInputTest {

    TwitterInput twitterInput = new TwitterInput();

    @Test
    public void gather() {
        twitterInput.pageSize = 2;
        twitterInput.pageCount = 2;

        List<InputChunk> result = twitterInput.gather(1);

        assertTrue(result.size() > 1);

        assertNotEquals(result.get(1), result.get(0));
        assertNotEquals(result.get(2), result.get(1));
        assertNotEquals(result.get(2), result.get(0));
    }
}