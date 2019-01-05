package com.bator.google;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class SentimentApi {

    private static final Logger log = Logger.getLogger(SentimentApi.class);

    Properties properties = new Properties();

    public SentimentApi() {
        try (InputStream inputStream = getClass().getResourceAsStream("/private.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("IOException", e);
        }
    }

    public AnalyzeSentimentResponse sentiment(String text) throws IOException {
        URL url = new URL("https://language.googleapis.com/v1/documents:analyzeSentiment?key=" + properties.getProperty("google.natural.language.api.key"));
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        String json = new ObjectMapper().writeValueAsString(new AnalyzeSentimentRequest(text));
        IOUtils.write(json, conn.getOutputStream());
        int response = conn.getResponseCode();
        if (response == 200) {
            String resultString = IOUtils.toString(conn.getInputStream());
            log.debug(resultString);
            return new ObjectMapper().readValue(resultString, AnalyzeSentimentResponse.class);
        } else {
            String result = IOUtils.toString(conn.getErrorStream());
            throw new RuntimeException(result);
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
    public static class AnalyzeSentimentResponse {
        DocumentSentiment documentSentiment;
        String language;
        List<Sentence> sentences;
    }

    @Data
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
