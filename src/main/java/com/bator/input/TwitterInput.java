package com.bator.input;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class TwitterInput implements Input {

    private static final Logger log = Logger.getLogger(TwitterInput.class);
    public static final String filter = "bitcoin";

    int pageSize = 100;
    int pageCount = 100;
    private Long lastId;

    @Override
    public List<InputChunk> gather(int retryCount) {

        List<InputChunk> inputChunks = new ArrayList<>();
        try {
            Twitter twitter = TwitterFactory.getSingleton();

            for (int i = 0; i < pageCount; i++) {
                Query query = createQuery();
                QueryResult result = twitter.search(query);
                print(result.getTweets(), inputChunks);
            }

        } catch (Exception e) {
            if (retryCount >= 0) {
                log.error("twitter stopped trying exception", e);
                throw new RuntimeException("exception", e);
            } else {
                gather(--retryCount);
                log.warn("exception, but will retry " + e);
            }
        }
        return inputChunks;
    }

    private void print(List<Status> tweets, List<InputChunk> inputChunks) {
        log.debug("got tweets " + tweets.size());
        for (Status status : tweets) {
//            System.out.println("at " + status.getCreatedAt() + " @" + status.getUser().getScreenName() + " <:> " + status.getText());
            Date createdAtCet = status.getCreatedAt();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(createdAtCet.toInstant(), ZoneId.of("UTC"));
            inputChunks.add(InputChunk.builder()
                    .text(status.getText())
                    .source("Twitter " + filter)
                    .utcPostDate(Date.from(localDateTime.atZone(ZoneId.of("CET")).toInstant()))
                    .build());
            if (nonNull(lastId) && status.getId() < lastId || isNull(lastId)) {
                lastId = status.getId();
            }
        }
    }

    private Query createQuery() {
        Query query = new Query(
                filter + " -filter:retweets -filter:links -filter:replies -filter:images");
        query.setResultType(Query.ResultType.recent);
        query.setLang("en");
        query.setLocale("en");
        query.setCount(pageSize);
        if (nonNull(lastId)) {
            query.setMaxId(lastId - 1);
        }
        return query;
    }
}
