package com.bator.google;

import java.io.IOException;

import com.bator.google.SentimentApi.AnalyzeSentimentResponse;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SentimentApiTest {

    private SentimentApi sentimentApi = new SentimentApi();

    @Test
    public void shouldSentiment() throws IOException {
        AnalyzeSentimentResponse sentiment = sentimentApi.sentiment("it feels great to have bitcoin, i'm very glad i've invested, to the moon!");
        assertEquals(sentiment.getDocumentSentiment().getMagnitude().signum(), 1);
        assertEquals(sentiment.getDocumentSentiment().getScore().signum(), 1);
        assertEquals(sentiment.getLanguage(), "en");
        assertEquals(sentiment.getSentences().size(), 1);

        sentiment = sentimentApi.sentiment("it feels horrible to have bitcoin, i'm very sad i've invested, I quit!");
        assertEquals(sentiment.getDocumentSentiment().getMagnitude().signum(), 1);
        assertEquals(sentiment.getDocumentSentiment().getScore().signum(), -1);
        assertEquals(sentiment.getLanguage(), "en");
        assertEquals(sentiment.getSentences().size(), 1);
    }
}