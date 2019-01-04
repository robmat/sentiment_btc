package com.bator;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ga.dryco.redditjerk.api.Reddit;
import ga.dryco.redditjerk.api.enums.Sorting;
import ga.dryco.redditjerk.implementation.RedditApi;
import ga.dryco.redditjerk.wrappers.Comment;
import ga.dryco.redditjerk.wrappers.Link;
import ga.dryco.redditjerk.wrappers.Post;
import ga.dryco.redditjerk.wrappers.RedditThread;
import ga.dryco.redditjerk.wrappers.Subreddit;
import ga.dryco.redditjerk.wrappers.User;

public class App {
    public static void main(String[] args) throws MalformedURLException {
        Reddit red = RedditApi.getRedditInstance("sentiment_btc /u/robthebobr");

        Subreddit subreddit = red.getSubreddit("Bitcoin");

        List<Link> linkList = new ArrayList<>();
        linkList.addAll(subreddit.getTop(Integer.MAX_VALUE));
        linkList.addAll(subreddit.getRising(Integer.MAX_VALUE));
        linkList.addAll(subreddit.getNew(Integer.MAX_VALUE));
        linkList.addAll(subreddit.getControversial(Integer.MAX_VALUE));

        for (Link link : linkList) {
            System.out.println(link);
            RedditThread redditThread = red.getRedditThread("https://www.reddit.com" + link.getPermalink(), Sorting.NEW);
            redditThread.fetchMoreComments(true);
            List<Comment> comments = redditThread.getFlatComments();
            for (Comment comment : comments) {
                System.out.println(comment.getBody());
                System.out.println(new Date(comment.getCreatedUtc() * 1000));
            }
        }
    }
}
