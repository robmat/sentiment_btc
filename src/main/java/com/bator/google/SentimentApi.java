package com.bator.google;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import org.apache.log4j.Logger;

public class SentimentApi extends ApiCall<SentimentApi.AnalyzeSentimentResponse, SentimentApi.AnalyzeSentimentRequest> {

    private static final Logger log = Logger.getLogger(SentimentApi.class);

    public SentimentApi() {
       super(AnalyzeSentimentResponse.class);
    }

    public AnalyzeSentimentResponse sentiment(String text) throws IOException {
        String apiUrl = "https://language.googleapis.com/v1/documents:analyzeSentiment";
        AnalyzeSentimentRequest postBody = new AnalyzeSentimentRequest(text);

        return apiPostCall(apiUrl, postBody);
    }

    @Data
    public static class AnalyzeSentimentRequest {
        public Document document;
        public String encodingType = "UTF8";

        AnalyzeSentimentRequest(String text) {
            this.document = new Document(text);
        }
    }

    @Data
    @Builder
    public static class AnalyzeSentimentResponse {
        DocumentSentiment documentSentiment;
        String language;
        List<Sentence> sentences;
    }

    @Data
    @Builder
    public static class DocumentSentiment {
        BigDecimal magnitude;
        BigDecimal score;
    }

    @Data
    public static class Sentence {
        Text text;
        DocumentSentiment sentiment;
    }

    @Data
    public static class Text {
        String content;
        Integer beginOffset;
    }
    @Data
    public static class Document {
        public String content;
        public String type = "PLAIN_TEXT";

        Document(String text) {
            content = text;
        }
    }
}
