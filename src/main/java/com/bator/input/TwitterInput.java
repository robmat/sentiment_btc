package com.bator.input;

import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

public class TwitterInput implements Input {

    private static final Logger log = Logger.getLogger(TwitterInput.class);
    public static final String filter = "bitcoin";

    int pageCount = 100;

    @Override
    public List<InputChunk> gather(int retryCount) {
        List<InputChunk> inputChunks = new ArrayList<>();
        try {
            Twitter twitter = TwitterFactory.getSingleton();
            Query query = createQuery(null);
            QueryResult result = twitter.search(query);

            print(result.getTweets(), inputChunks);

            query = createQuery(result.getMaxId());
            result = twitter.search(query);
            print(result.getTweets(), inputChunks);

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
        for (Status status : tweets) {
            //System.out.println("at " + status.getCreatedAt() + " @" + status.getUser().getScreenName() + " <:> " + status.getText());
            inputChunks.add(InputChunk.builder()
                .text(status.getText())
                .source("Twitter " + filter)
                .utcPostDate()
                .build());
        }
    }

    private Query createQuery(Long since) {
        Query query = new Query(
            filter + " -filter:retweets -filter:links -filter:replies -filter:images");
        query.setResultType(Query.ResultType.recent);
        query.setLang("en");
        query.setLocale("en");
        query.setCount(pageCount);
        if (nonNull(since)) {
            query.setSinceId(since);
        }
        return query;
    }
}
