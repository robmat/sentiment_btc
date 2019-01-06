package com.bator.google;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class ApiCall<OUT, IN> {

    private static final Logger log = Logger.getLogger(ApiCall.class);

    final Properties properties = new Properties();
    Class inClass;

    public ApiCall(Class clazz) {
        try (InputStream inputStream = getClass().getResourceAsStream("/private.properties")) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("IOException", e);
        }
        inClass = clazz;
    }

    OUT apiPostCall(String apiUrl, IN postBody) throws IOException {
        URL url = new URL(apiUrl + "?key=" + properties.getProperty("google.natural.language.api.key"));
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        String json = new ObjectMapper().writeValueAsString(postBody);
        IOUtils.write(json, conn.getOutputStream());
        int response = conn.getResponseCode();
        log.debug("called " + apiUrl + " with " + postBody + " got " + response);
        if (response == 200) {
            String resultString = IOUtils.toString(conn.getInputStream());
            log.debug(resultString);
            return (OUT) new ObjectMapper().readValue(resultString, inClass);
        } else {
            String result = IOUtils.toString(conn.getErrorStream());
            throw new RuntimeException(result);
        }
    }
}
