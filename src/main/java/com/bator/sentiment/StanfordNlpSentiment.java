package com.bator.sentiment;

import java.math.BigDecimal;
import java.util.Properties;

import com.bator.input.InputChunk;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class StanfordNlpSentiment {

    StanfordCoreNLP pipeline;

    public StanfordNlpSentiment() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
        pipeline = new StanfordCoreNLP(props);
    }

    public InputChunk findSentiment(InputChunk inputChunk) {
        int mainSentiment = 0;
        if (inputChunk != null && inputChunk.getText().length() > 0) {
            int longest = 0;
            Annotation annotation = pipeline.process(inputChunk.getText());
            for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
                Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
                int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
                String partText = sentence.toString();
                if (partText.length() > longest) {
                    mainSentiment = sentiment;
                    longest = partText.length();
                }

            }
            inputChunk.setScore(BigDecimal.valueOf(mainSentiment));
            inputChunk.setMagnitude(BigDecimal.ONE);
        }
        return inputChunk;

    }
}
