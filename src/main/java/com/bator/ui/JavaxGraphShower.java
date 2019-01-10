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
import javafx.stage.Stage;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;

import static java.util.Objects.nonNull;

public class JavaxGraphShower extends Application {
    private String chunksDb = "chunks";
    private String chunksTable = "chunks";

    private static final int DAYS_BEFORE = -1;
    private final String REPORT_SQL =
            "select count(  hash) cnt, avg(score) score, avg(magnitude) magnitude, avg(score*magnitude) score_magnitude, "
                    + "strftime('%Y-%m-%d %H:00', (creationDate / 1000), 'unixepoch', 'utc') post_time, "
                    + "strftime('%Y-%m-%d %H:00', (creationDate / 1000), 'unixepoch', 'localtime') post_time_local "
                    + "from " + chunksTable
                    + " where creationDate > XxXxX "
                    + "group by post_time "
                    + "order by creationDate asc";
    private static final Logger log = Logger.getLogger(JavaxGraphShower.class);


    public void start() {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Graph.");
        final ScrollPane scrollPane = new ScrollPane();

        Scene scene = new Scene(scrollPane);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        String finalSql = REPORT_SQL
                .replace("XxXxX", DateUtils.addDays(new Date(), DAYS_BEFORE).getTime() + "");
        log.debug("finalSql " + finalSql);

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + chunksDb + ".db");
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(finalSql)) {

            //defining the axes
            final CategoryAxis xAxis = new CategoryAxis();
            final NumberAxis yAxis = new NumberAxis();
            xAxis.setLabel("Sentiment.");
            //creating the chart
            final LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

            lineChart.setTitle("Sentiment.");

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

            //lineChart.getData().add(seriesCnt);
            lineChart.getData().add(seriesScore);
            lineChart.getData().add(seriesScoreMagnitude);

            scrollPane.setContent(lineChart);
            lineChart.setPrefWidth(1600);
            lineChart.setPrefHeight(800);
        }
    }
}
