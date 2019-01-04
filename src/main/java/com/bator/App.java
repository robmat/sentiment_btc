package com.bator;

import java.net.MalformedURLException;
import java.util.ArrayList;
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

        User user = red.login();

        Subreddit subreddit = red.getSubreddit("Bitcoin");

        List<Link> linkList = new ArrayList<>();
        linkList.addAll(subreddit.getTop(100));
        //linkList.addAll(subreddit.getRising(100));
        //linkList.addAll(subreddit.getNew(100));
        //linkList.addAll(subreddit.getControversial(100));

        for (Link link : linkList) {
            RedditThread redditThread = red.getRedditThread(link.getUrl(), Sorting.NEW);
            List<Comment> comments = redditThread.getFlatComments();
            for (Comment comment : comments) {
                System.out.println(comment);
            }
        }
    }
}
