package com.bator.input;

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

@Data
public class RedditInput implements Input {

    int itemCount = Integer.MAX_VALUE;

    @Override
    public List<InputChunk> gather() {
        ArrayList<InputChunk> result = new ArrayList<>();
        Reddit red = RedditApi.getRedditInstance("sentiment_btc /u/robthebobr");

        Subreddit subreddit = red.getSubreddit("Bitcoin");

        List<Link> linkList = new ArrayList<>();

        linkList.addAll(subreddit.getTop(itemCount));
        linkList.addAll(subreddit.getRising(itemCount));
        linkList.addAll(subreddit.getNew(itemCount));
        linkList.addAll(subreddit.getControversial(itemCount));

        for (Link link : linkList) {
            try {
                RedditThread redditThread = red.getRedditThread("https://www.reddit.com" + link.getPermalink(), Sorting.NEW);
                redditThread.fetchMoreComments(true);
                List<Comment> comments = redditThread.getFlatComments();
                for (Comment comment : comments) {
                    result.add(InputChunk.builder()
                            .text(comment.getBody())
                            .utcPostDate(new Date(comment.getCreatedUtc() * 1000))
                            .source(getClass().getSimpleName())
                            .build());
                }
            } catch (MalformedURLException e) {
                throw new RuntimeException("MalformedURLException", e);
            }
        }
        return result;
    }
}
