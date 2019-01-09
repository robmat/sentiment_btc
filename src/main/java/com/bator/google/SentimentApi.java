package com.bator.google;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;

import static java.math.BigDecimal.ZERO;

public class SentimentApi extends
        ApiCall<SentimentApi.AnalyzeSentimentResponse, SentimentApi.AnalyzeSentimentRequest> {

    private static final Logger log = Logger.getLogger(SentimentApi.class);

    public SentimentApi() {
        super(AnalyzeSentimentResponse.class);
    }

    public AnalyzeSentimentResponse sentiment(String text) throws IOException {
        String apiUrl = "https://language.googleapis.com/v1/documents:analyzeSentiment";
        AnalyzeSentimentRequest postBody = new AnalyzeSentimentRequest(text);

        try {
            return apiPostCall(apiUrl, postBody);
        } catch (RuntimeException e) {
            if (Pattern.compile("The language .. is not supported for document_sentiment analysis").matcher(e.getMessage()).find()) {
                log.warn("RuntimeException about language not supported, will return 0 sentiment and magnitude", e);
                return AnalyzeSentimentResponse.builder()
                        .documentSentiment(DocumentSentiment.builder()
                                .magnitude(ZERO)
                                .score(ZERO)
                                .build())
                        .build();
            }
            throw e;
        }
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
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnalyzeSentimentResponse {

        DocumentSentiment documentSentiment;
        String language = "en";
        List<Sentence> sentences;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
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
