package com.bator.input;

import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class TwitterInputTest {

    TwitterInput twitterInput = new TwitterInput();

    @Test
    public void gather() {
        List<InputChunk> result = twitterInput.gather(1);
    }
}