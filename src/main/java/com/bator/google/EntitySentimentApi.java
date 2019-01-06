package com.bator.google;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import lombok.Data;
import org.apache.log4j.Logger;

public class EntitySentimentApi extends ApiCall<EntitySentimentApi.EntitySentimentResponse, EntitySentimentApi.EntitySentimentRequest> {

    private static final Logger log = Logger.getLogger(EntitySentimentApi.class);

    public EntitySentimentApi() {
        super(EntitySentimentResponse.class);
    }

    public EntitySentimentResponse sentiment(String text) throws IOException {
        String apiUrl = "https://language.googleapis.com/v1/documents:analyzeEntitySentiment";
        EntitySentimentRequest postBody = new EntitySentimentRequest(text);
        return apiPostCall(apiUrl, postBody);
    }

    @Data
    public static class EntitySentimentRequest {
        public Document document;
        public String encodingType = "UTF8";

        EntitySentimentRequest(String text) {
            this.document = new Document(text);
        }
    }

    @Data
    public static class EntitySentimentResponse {
        List<Entity> entities;
        String language;
    }

    @Data
    public static class Sentiment {
        BigDecimal magnitude;
        BigDecimal score;
    }

    @Data
    public static class Entity {
        String name;
        String type;
        Map<String, String> metadata;
        BigDecimal salience;
        Sentiment sentiment;
        List<EntityMention> mentions;
    }

    @Data
    public static class Document {
        public String content;
        public String type = "PLAIN_TEXT";

        Document(String text) {
            content = text;
        }
    }

    @Data
    private static class EntityMention {
        TextSpan text;
        String type;
        Sentiment sentiment;
    }

    @Data
    private static class TextSpan {
        String content;
        Integer beginOffset;
    }
}
