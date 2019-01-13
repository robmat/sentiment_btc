package com.bator.input;

import ga.dryco.redditjerk.api.enums.FromPast;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ga.dryco.redditjerk.api.Reddit;
import ga.dryco.redditjerk.api.enums.Sorting;
import ga.dryco.redditjerk.implementation.RedditApi;
import ga.dryco.redditjerk.wrappers.Comment;
import ga.dryco.redditjerk.wrappers.Link;
import ga.dryco.redditjerk.wrappers.RedditThread;
import ga.dryco.redditjerk.wrappers.Subreddit;
import lombok.Data;
import org.apache.commons.lang3.Validate;
import org.apache.log4j.Logger;

@Data
public class RedditInput implements Input {

    private static final Logger log = Logger.getLogger(RedditInput.class);

    int itemCount = Integer.MAX_VALUE;
    String subredditName;

    @Override
    public List<InputChunk> gather(int retryCount) {
        ArrayList<InputChunk> result = new ArrayList<>();
        try {
            Validate.notNull(subredditName);


            Reddit red = RedditApi.getRedditInstance("sentiment_btc /u/robthebobr");

            Subreddit subreddit = red.getSubreddit(subredditName);

            List<Link> linkList = new ArrayList<>();

            linkList.addAll(subreddit.getTop(itemCount));
            linkList.addAll(subreddit.getRising(itemCount));
            linkList.addAll(subreddit.getNew(itemCount));
            linkList.addAll(subreddit.getControversial(itemCount));

            int linkCount = 0;
            for (Link link : linkList) {
                retry(result, red, link, retryCount);
                log.debug("links done " + ++linkCount + "/" + linkList.size() + " for subreddit " + subredditName);
            }
            return result;
        } catch (Exception e) {
            log.error(subredditName + " exception, but will return " + result.size() + " results", e);
        }
        return result;
    }

    private void retry(ArrayList<InputChunk> result, Reddit red, Link link, int retryCount) {
        try {
            RedditThread redditThread = red.getRedditThread("https://www.reddit.com" + link.getPermalink(), Sorting.NEW);
            redditThread.fetchMoreComments(true);
            List<Comment> comments = redditThread.getFlatComments();
            for (Comment comment : comments) {
                result.add(InputChunk.builder()
                        .text(comment.getBody())
                        .utcPostDate(new Date(comment.getCreatedUtc() * 1000))
                        .source(getClass().getSimpleName() + " " + subredditName)
                        .build());
            }

        } catch (Exception e) {
            if (retryCount >= 0) {
                log.error(subredditName + " stopped trying exception", e);
                throw new RuntimeException("Exception", e);
            } else {
                log.warn(subredditName + " exception, will retry times " + --retryCount + " " + e);
                retry(result, red, link, retryCount);
            }
        }
    }
}
