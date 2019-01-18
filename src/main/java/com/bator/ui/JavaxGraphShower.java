package com.bator.ui;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

import static java.util.Objects.nonNull;

public class JavaxGraphShower extends Application {
    private static final Logger log = Logger.getLogger(JavaxGraphShower.class);

    private String chunksDb = "chunks";
    private String chunksTable = "chunks";

    static final int DAYS_BEFORE = -7;
    static final String REPORT_SQL =
            "select count(  hash) cnt, avg(score) score, avg(magnitude) magnitude, avg(score*magnitude) score_magnitude, "
                    + "strftime('%Y-%m-%d %H:00', (creationDate / 1000), 'unixepoch', 'utc') post_time, "
                    + "strftime('%Y-%m-%d %H:00', (creationDate / 1000), 'unixepoch', 'localtime') post_time_local "
                    + "from YyYyY "
                    + " where creationDate > XxXxX "
                    + "group by post_time "
                    + "order by creationDate asc";



    public void start() {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        final ScrollPane scrollPane = getScrollPane(stage);

        String finalSql = finalSql(chunksTable);

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + chunksDb + ".db");
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(finalSql)) {

            final LineChart<String, Number> sentimentChart = createChart("Score.");
            final LineChart<String, Number> countChart = createChart("Text count.");
            final LineChart<String, Number> magnitudeChart = createChart("Score * magnitude.");

            XYChart.Series<String, Number> seriesScore = new XYChart.Series<>();
            seriesScore.setName("Score.");

            XYChart.Series<String, Number> seriesCnt = new XYChart.Series<>();
            seriesCnt.setName("Count.");

            XYChart.Series<String, Number> seriesScoreMagnitude = new XYChart.Series<>();
            seriesScoreMagnitude.setName("Score * magnitude.");

            //populating the series with data
            while (rs.next()) {
                BigDecimal number = rs.getBigDecimal(2);
                String date = /*rs.getString(5) + " " + */rs.getString(6);
                if (nonNull(number)) {
                    seriesScore.getData().add(new Data<>(date, number));
                }

                number = rs.getBigDecimal(1);
                if (nonNull(number)) {
                    seriesCnt.getData().add(new Data<>(date, number));
                }

                number = rs.getBigDecimal(4);
                if (nonNull(number)) {
                    seriesScoreMagnitude.getData().add(new Data<>(date, number));
                }
            }

            countChart.getData().add(seriesCnt);
            sentimentChart.getData().add(seriesScore);
            magnitudeChart.getData().add(seriesScoreMagnitude);

            GridPane graphs = new GridPane();
            graphs.add(sentimentChart, 0, 0);
            graphs.add(countChart, 0, 1);
            graphs.add(magnitudeChart, 0, 2);
            scrollPane.setContent(graphs);
            sentimentChart.setPrefWidth(1600);
            sentimentChart.setPrefHeight(800);
        }
    }

    private LineChart<String, Number> createChart(String title) {
        //defining the axes
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(title);
        //creating the chart
        final LineChart<String, Number> sentimentChart = new LineChart<>(xAxis, yAxis);

        sentimentChart.setTitle(title);
        return sentimentChart;
    }

    static String finalSql(String chunksTable) {
        String finalSql = REPORT_SQL
                .replace("XxXxX", DateUtils.addDays(new Date(), DAYS_BEFORE).getTime() + "")
                .replace("YyYyY", chunksTable);
        log.debug("finalSql " + finalSql);
        return finalSql;
    }

    private static ScrollPane getScrollPane(Stage stage) {
        stage.setTitle("Graph.");
        final ScrollPane scrollPane = new ScrollPane();

        Scene scene = new Scene(scrollPane);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        return scrollPane;
    }
}
