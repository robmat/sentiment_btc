package com.bator.input;

import java.util.List;

import org.apache.log4j.Logger;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

public class TwitterInput implements Input {

    private static final Logger log = Logger.getLogger(TwitterInput.class);

    @Override
    public List<InputChunk> gather(int retryCount) {
        try {
            Twitter twitter = TwitterFactory.getSingleton();
            Query query = new Query("#bitcoin");
            query.setResultType(Query.ResultType.recent);
            query.setLang("en");
            QueryResult result = twitter.search(query);
            for (Status status : result.getTweets()) {
                System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
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
        return null;
    }
}
