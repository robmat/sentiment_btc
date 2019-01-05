package com.bator.google;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Properties;

import com.google.cloud.language.v1beta2.LanguageServiceClient;
import lombok.Data;

public class SentimentApi {

    Properties properties = new Properties();

    public SentimentApi() throws IOException {
        try (InputStream inputStream = getClass().getResourceAsStream("/resources/private.properties")){
            properties.load(inputStream);
        }
    }

    public void sentiment(String text) throws IOException {
        LanguageServiceClient languageServiceClient = LanguageServiceClient.create();
    }
}
