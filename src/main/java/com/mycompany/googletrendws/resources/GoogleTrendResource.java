/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.googletrendws.resources;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mycompany.googletrendws.googletrends.GoogleTrends;
import com.mycompany.googletrendws.helper.LocationFactory;
import com.mycompany.googletrendws.googletrends.data.CanvasData;
import com.mycompany.googletrendws.googletrends.regression.RegressionLine;
import com.mycompany.googletrendws.helper.LocationHelper;
import com.mycompany.googletrendws.helper.MyFileReader;
import com.mycompany.googletrendws.helper.MySQLConn;
import flanagan.analysis.CurveSmooth;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 *
 * @author jupiter
 */
@Path("google")
public class GoogleTrendResource {

    MySQLConn mySQLCon;
    Type listType = new TypeToken<List<List<Double>>>() {
    }.getType();

    GoogleTrends googleTrends;
    Gson gson;

    @Inject
    public GoogleTrendResource(GoogleTrends googleTrends, Gson gson, MySQLConn mySQLCon) {
        this.googleTrends = googleTrends;
        this.gson = gson;
        this.mySQLCon = mySQLCon;
    }

    @GET
    @Path("csv")
    public Response getCSV() {
//            String fileURL = "C:\\Users\\jupiter\\Downloads\\multiTimeline.csv";
        String fileURL = LocationHelper.getDownloadDir() + "multiTimeline.csv";
//                String fileURL = LocationFactory.getFileURL();
        List<List<String>> fileContent = MyFileReader.readCSV(fileURL);
        fileContent.forEach(t -> {
            t.forEach(g -> {
                System.out.print(g + " ");
            });
            System.out.print("\n");
        });
        List<String> series = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        series.add(fileContent.get(2).get(1));

        for (int i = 3; i < fileContent.size(); ++i) {
            labels.add(fileContent.get(i).get(0));
            data.add(Double.valueOf(fileContent.get(i).get(1)));
        }
        List<List<Double>> finalData = new ArrayList<>();
        finalData.add(data);
        CanvasData canvasData = new CanvasData(labels, series, finalData);
        String res = gson.toJson(canvasData, CanvasData.class);
        return Response.status(200)
                .entity(res)
                .build();
    }

    DateTimeFormatter fromFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter toFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    @GET
    public Response submit(
            @QueryParam("q") String keywords,
            @QueryParam("position") String position,
            @QueryParam("date") String date,
            @QueryParam("grop") String trendResources,
            @QueryParam("location") String location,
            @QueryParam("queryCatlog") String queryCatlog,
            @QueryParam("eventFrom") String eventFrom,
            @QueryParam("eventTo") String eventTo
            ) throws Exception {
//      @GET
//      public Response submit(
//            @QueryParam("url") String url
//      ) {
        System.out.println("google trends");
//            try {
//                  System.out.println(url);
//                  url = URLEncoder.encode(url, "UTF-8");
//                  
//            } catch (Exception e) {
//                  
//            }
//                System.out.println(date);
        String[] dates = date.split("%20");
        String fromDate = dates[0];
        String toDate = dates[1];
        String decodeKeywords = URLDecoder.decode(keywords, "UTF-8");
        decodeKeywords = URLDecoder.decode(decodeKeywords, "UTF-8");
        decodeKeywords = decodeKeywords.replaceAll("\\s+", "%2B");
        mySQLCon.saveTwitterKeyword(decodeKeywords, fromDate, toDate, eventFrom, eventTo, location, trendResources, "GoogleTrend", queryCatlog);

        System.out.println(decodeKeywords);

        String query = "https://www.google.com/trends/explore?";
        query += "date=" + date;
        query += "&geo=" + position;
        query += "&q=" + decodeKeywords;
//                query += "&gprop=" + trendResources;
        System.out.println(query);
//            System.out.println(query + url);

        googleTrends.run2(query);

        //
        Integer historyId = mySQLCon.getTwitterMaxHistoryId();
        //copy file to new directory
        LocationFactory.copyFileToDataDirectory(LocationHelper.getDownloadDir(), LocationHelper.getDataDir(), "multiTimeline.csv", "t" + historyId + ".csv");

//            String fileURL = "C:\\Users\\jupiter\\Downloads\\multiTimeline.csv";
        String fileURL = LocationHelper.getDownloadDir() + "multiTimeline.csv";
//                String fileURL = LocationFactory.getFileURL();
        List<List<String>> fileContent = MyFileReader.readCSV(fileURL);

//                for (int i = 2; i < fileContent.size(); ++i) {
//                        fileContent.get(i).remove(1);
//                }
//            fileContent.forEach(t -> {
//                  t.forEach(g -> {
//                        System.out.print(g + " ");
//                  });
//                  System.out.print("\n");
//            });
        List<String> series = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        List<List<Double>> finalData = new ArrayList<>();

        if (fileContent.isEmpty()) {
            CanvasData canData = new CanvasData(labels, series, finalData);
            String r = gson.toJson(canData, CanvasData.class);
            return Response.status(200)
                    .entity(r)
                    .build();
        }

        series.add(fileContent.get(2).get(1));

        series.add("smoothed");

        for (int i = 3; i < fileContent.size(); ++i) {
//                      labels.add(fileContent.get(i).get(0));
            labels.add(LocalDate.parse(fileContent.get(i).get(0), fromFormatter).format(toFormatter));
            data.add(Double.valueOf(fileContent.get(i).get(1)));
        }

        finalData.add(data);

        double[] xData = DoubleStream.iterate(1, n -> n + 1).limit(data.size()).toArray();
        double[] yData = data.stream().mapToDouble(Double::doubleValue).toArray();

//              CurveSmooth csm = new CurveSmooth(xData, yData);
//              double[] smoothedData = csm.movingAverage(10);
        RegressionLine regressionLine = new RegressionLine();
        double[] smoothedData = regressionLine.getRegression(xData, yData);

        finalData.add(Arrays.stream(smoothedData)
                .boxed()
                .collect(Collectors.toList()));

        CanvasData canvasData = new CanvasData(labels, series, finalData);
        String res = gson.toJson(canvasData, CanvasData.class);
        return Response.status(200)
                .entity(res)
                .build();

    }

    @GET
    @Path("updateSmoothData")
    public Response getSmoothedData(
            @QueryParam("data") String dataString,
            @QueryParam("scale") Integer scaleValue
    ) {
        System.out.println(dataString);
        List<List<Double>> data = gson.fromJson(dataString, listType);
        double[] xData = DoubleStream.iterate(1, n -> n + 1).limit(data.get(0).size()).toArray();
        double[] yData = data.get(0).stream().mapToDouble(Double::doubleValue).toArray();
        CurveSmooth csm = new CurveSmooth(xData, yData);

        double[] smoothedData = csm.movingAverage(scaleValue);
        data.set(1, Arrays.stream(smoothedData)
                .boxed()
                .collect(Collectors.toList()));

        String res = gson.toJson(data, listType);
        System.out.println(res);
        return Response.status(200)
                .entity(res)
                .build();
    }

    @GET
    @Path("contrast")
    public Response getContrast(
            @QueryParam("q") String keywords,
            @QueryParam("position") String position,
            @QueryParam("date") String date,
            @QueryParam("locationOne") String locationOne,
            @QueryParam("locationOneID") String locationOneID,
            @QueryParam("locationTwo") String locationTwo,
            @QueryParam("locationTwoID") String locationTwoID,
            @QueryParam("resource") String resource,
            @QueryParam("queryCatlog") String queryCatlog,
             @QueryParam("eventFrom") String eventFrom,
            @QueryParam("eventTo") String eventTo
    ) throws Exception {
        //submit one
        System.out.println("submit one");
        System.out.println(locationOne);
        System.out.println(keywords);
        String[] dates = date.split("%20");
        String fromDate = dates[0];
        String toDate = dates[1];
        String decodeKeywords = URLDecoder.decode(keywords, "UTF-8");
        decodeKeywords = decodeKeywords.replaceAll("\\+", "%2B");
        mySQLCon.saveTwitterKeyword(decodeKeywords, fromDate, toDate, eventFrom, eventTo, locationOne, resource, "GoogleTrend", queryCatlog);

        //"slythytovery 篳 السموط  papa 囖" 
//                String header = "\"slythytovery 篳السموط papa 囖\",";
//                keywords = header + keywords;
//                System.out.println(keywords);
        String query = "https://www.google.com/trends/explore?";
        query += "date=" + date;
        query += "&geo=" + locationOneID;
        query += "&gprop=" + resource;
        query += "&q=" + decodeKeywords;
        System.out.println(query);
        googleTrends.run2(query);

        Integer historyIdOne = mySQLCon.getTwitterMaxHistoryId();
        LocationFactory.copyFileToDataDirectory(LocationHelper.getDownloadDir(), LocationHelper.getDataDir(), "multiTimeline.csv", "t" + historyIdOne + ".csv");

        //submit two
        mySQLCon.saveTwitterKeyword(decodeKeywords, fromDate, toDate, eventFrom, eventTo, locationTwo, resource, "GoogleTrend", queryCatlog);

        query = "https://www.google.com/trends/explore?";
        query += "date=" + date;
        query += "&geo=" + locationTwoID;
        query += "&gprop=" + resource;
        query += "&q=" + decodeKeywords;
        System.out.println(query);
        googleTrends.run2(query);
        Integer historyIdTwo = mySQLCon.getTwitterMaxHistoryId();
        LocationFactory.copyFileToDataDirectory(LocationHelper.getDownloadDir(), LocationHelper.getDataDir(), "multiTimeline.csv", "t" + historyIdTwo + ".csv");

        //group two together
        List<String> series = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        List<List<Double>> finalData = new ArrayList<>();

        String fileURLOne = LocationHelper.getDataDir() + "t" + historyIdOne + ".csv";
        List<List<String>> fileContentOne = MyFileReader.readCSV(fileURLOne);

        series.add(fileContentOne.get(2).get(1));
        for (int i = 3; i < fileContentOne.size(); ++i) {
            labels.add(LocalDate.parse(fileContentOne.get(i).get(0), fromFormatter).format(toFormatter));
            data.add(Double.valueOf(fileContentOne.get(i).get(1)));
        }
        finalData.add(data);
        series.add("Smoothed " + fileContentOne.get(2).get(1));
        double[] xData = DoubleStream.iterate(1, n -> n + 1).limit(data.size()).toArray();
        double[] yData = data.stream().mapToDouble(Double::doubleValue).toArray();

        RegressionLine regressionLine = new RegressionLine();
        double[] smoothedData = regressionLine.getRegression(xData, yData);

        finalData.add(Arrays.stream(smoothedData)
                .boxed()
                .collect(Collectors.toList()));

        //Two
        String fileURLTwo = LocationHelper.getDataDir() + "t" + historyIdTwo + ".csv";
        List<List<String>> fileContentTwo = MyFileReader.readCSV(fileURLTwo);
        List<Double> dataTwo = new ArrayList<>();
        series.add(fileContentTwo.get(2).get(1));

        for (int i = 3; i < fileContentTwo.size(); ++i) {
            dataTwo.add(Double.valueOf(fileContentTwo.get(i).get(1)));
        }
        finalData.add(dataTwo);
        series.add("Smoothed " + fileContentTwo.get(2).get(1));
        yData = dataTwo.stream().mapToDouble(Double::doubleValue).toArray();

        RegressionLine regressionLineTwo = new RegressionLine();
        double[] smoothedDataTwo = regressionLineTwo.getRegression(xData, yData);

        finalData.add(Arrays.stream(smoothedDataTwo)
                .boxed()
                .collect(Collectors.toList()));

        CanvasData canvasData = new CanvasData(labels, series, finalData);
        String res = gson.toJson(canvasData, CanvasData.class);
        return Response.status(200)
                .entity(res)
                .build();
    }

}
