package com.bator.sentiment;

import com.bator.input.InputChunk;
import org.junit.Test;

import static org.junit.Assert.*;

public class StanfordNlpSentimentTest {

    private StanfordNlpSentiment stanfordNlpSentiment = new StanfordNlpSentiment();

    @Test
    public void findPositiveSentiment() {
        InputChunk inputChunk = InputChunk.builder().text("bitcoin is great, i'm very glad i've invested").build();

        stanfordNlpSentiment.findSentiment(inputChunk);

        System.out.println(inputChunk);
    }

    @Test
    public void findNegativeSentiment() {
        InputChunk inputChunk = InputChunk.builder().text(" it feels horrible to have bitcoin, i'm very sad i've invested, I quit").build();

        stanfordNlpSentiment.findSentiment(inputChunk);

        System.out.println(inputChunk);
    }
}