package com.bator.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.PeriodAxis;
import org.jfree.chart.axis.StandardTickUnitSource;
import org.jfree.chart.axis.TickUnit;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import static com.bator.ui.JavaxGraphShower.finalSql;

public class JFreeChartGraphShower  {

    private static final Logger log = Logger.getLogger(JavaxGraphShower.class);

    private static String chunksDb = "chunks";
    private static String chunksTable = "chunks";

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(() -> {
            try {
                start();
            } catch (Exception e) {
                log.error(e, e);
            }
        });
    }

    public static void start() throws Exception {

        String finalSql = finalSql(chunksTable);

        TimeSeries countSeries = new TimeSeries("Count.");
        TimeSeries scoreSeries = new TimeSeries("Score.");
        TimeSeries magnitudeSeries = new TimeSeries("Score * magnitude.");

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + chunksDb + ".db");
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(finalSql)) {

            while (rs.next()) {
                Hour hour = toHour(rs.getString(6));
                countSeries.add(hour, rs.getBigDecimal(1));
                scoreSeries.add(hour, rs.getBigDecimal(2));
                magnitudeSeries.add(hour, rs.getBigDecimal(4));
            }
        }

        TimeSeriesCollection countSeriesCollection = new TimeSeriesCollection(countSeries);
        TimeSeriesCollection scoreSeriesCollection = new TimeSeriesCollection(scoreSeries);
        scoreSeriesCollection.addSeries(magnitudeSeries);

        XYPlot plot = new XYPlot();
        plot.setDataset(0, countSeriesCollection);
        plot.setDataset(1, scoreSeriesCollection);

        plot.setRenderer(0, new XYSplineRenderer());

        XYSplineRenderer splinerenderer = new XYSplineRenderer();
        splinerenderer.setSeriesFillPaint(0, Color.BLUE);
        plot.setRenderer(1, splinerenderer);

        plot.setRangeAxis(0, new NumberAxis("Count"));
        plot.setRangeAxis(1, new NumberAxis("Score"));
        PeriodAxis periodAxis = new PeriodAxis("Time");
        // periodAxis.setStandardTickUnits(new HourlyTickUnitSource());
        periodAxis.setVerticalTickLabels(true);
        periodAxis.setTickLabelsVisible(true);
        plot.setDomainAxis(periodAxis);

        //Map the data to the appropriate axis
        plot.mapDatasetToRangeAxis(0, 0);
        plot.mapDatasetToRangeAxis(1, 1);

        //generate the chart
        JFreeChart chart = new JFreeChart("Graph", Font.getFont("Arial"), plot, true);
        chart.setBackgroundPaint(Color.WHITE);
        JPanel chartPanel = new ChartPanel(chart);

        //Create and set up the window.
        JFrame frame = new JFrame("Graph.");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(chartPanel);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    private static Hour toHour(String dateString) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = simpleDateFormat.parse(dateString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return new Hour(
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR)
        );
    }

    static class HourlyTickUnitSource implements  TickUnitSource {
        @Override
        public TickUnit getLargerTickUnit(TickUnit unit) {
            return new DateTickUnit(DateTickUnitType.HOUR, 1);
        }

        @Override
        public TickUnit getCeilingTickUnit(TickUnit unit) {
            return new DateTickUnit(DateTickUnitType.HOUR, 1);
        }

        @Override
        public TickUnit getCeilingTickUnit(double size) {
            return new DateTickUnit(DateTickUnitType.HOUR, 1);
        }
    }
}
