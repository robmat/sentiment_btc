package com.bator.google;

import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EntitySentimentApiTest {

    EntitySentimentApi sentimentApi = new EntitySentimentApi();

    @Test
    public void shouldSentiment() throws IOException {
        EntitySentimentApi.EntitySentimentResponse sentiment = sentimentApi.sentiment("bitcoin is great, i'm very glad i've invested!");
        assertEquals(1, sentiment.getEntities().size());
        assertEquals(1, sentiment.getEntities().get(0).getSentiment().getScore().signum());
        assertEquals("bitcoin", sentiment.getEntities().get(0).getName());

        sentiment = sentimentApi.sentiment("it feels horrible to have bitcoin, i'm very sad i've invested, I quit!");
        assertEquals(1, sentiment.getEntities().size());
        assertEquals(-1, sentiment.getEntities().get(0).getSentiment().getScore().signum());
        assertEquals("bitcoin", sentiment.getEntities().get(0).getName());
    }
}