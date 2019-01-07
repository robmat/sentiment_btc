package com.bator.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GraphShower extends Application {

    public void start() {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Graph.");
        final Label label = new Label("asc");
        final ScrollPane scrollPane = new ScrollPane();

        scrollPane.setContent(label);

        Scene scene  = new Scene(scrollPane);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
/*
select count(hash) cnt, avg(score) score, avg(magnitude) magnitude, avg(score*magnitude) score_magnitude,
    strftime('%Y-%m-%d %H:00', creationDate / 1000, 'unixepoch', 'utc') post_time from chunks
group by post_time
order by creationDate asc limit 100;
* */
    }
}
