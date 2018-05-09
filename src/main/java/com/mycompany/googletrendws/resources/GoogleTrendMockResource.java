/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.googletrendws.resources;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mycompany.googletrendws.Jedis.JedisService;
import com.mycompany.googletrendws.SegTree.SegTree;
import com.mycompany.googletrendws.googletrends.GoogleTrends;
import com.mycompany.googletrendws.googletrends.data.CanvasData;
import com.mycompany.googletrendws.googletrends.regression.RegressionLine;
import com.mycompany.googletrendws.googletrends.services.GoogleTrendMockService;
import com.mycompany.googletrendws.helper.LocationHelper;
import com.mycompany.googletrendws.helper.MyFileReader;
import com.mycompany.googletrendws.helper.MySQLConn;
import flanagan.analysis.CurveSmooth;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import redis.clients.jedis.Jedis;

/**
 *
 * @author jupiter
 */
@Path("googleMock")
public class GoogleTrendMockResource {

    MySQLConn mySQLCon;
    Type listType = new TypeToken<List<List<Double>>>() {
    }.getType();

    GoogleTrends googleTrends;
    Gson gson;

    GoogleTrendMockService googleTrendMockService;
    Random random;
    JedisService jedisService;
    Jedis jedis;
    SegTree segTree;

    @Inject
    public GoogleTrendMockResource(GoogleTrends googleTrends, Gson gson, MySQLConn mySQLCon,
            GoogleTrendMockService googleTrendMockService, Random random, JedisService jedisService, Jedis jedis, SegTree segTree) {
        this.googleTrends = googleTrends;
        this.gson = gson;
        this.mySQLCon = mySQLCon;
        this.googleTrendMockService = googleTrendMockService;
        this.random = random;
        this.jedisService = jedisService;
        this.jedis = jedis;
        this.segTree = segTree;
    }

    @GET
    @Path("csv")
    public Response getCSV() {
        String fileURL = LocationHelper.getDownloadDir() + "multiTimeline.csv";
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
            @QueryParam("eventTo") String eventTo,
            @QueryParam("count") int range
    ) throws Exception {
        keywords = URLDecoder.decode(keywords);

        System.out.println("google trends");
        System.out.println(keywords);
        System.out.println(position);
        System.out.println(date);
        System.out.println(eventFrom);
        System.out.println(eventTo);
        System.out.println(trendResources);
        System.out.println(location);
        System.out.println(queryCatlog);
        String[] dates = date.split("%20");
        String fromDate = dates[0];
        String toDate = dates[1];
        String decodeKeywords = URLDecoder.decode(keywords, "UTF-8");
        decodeKeywords = decodeKeywords.replaceAll("\\+", ",");
        mySQLCon.saveTwitterKeyword(decodeKeywords, fromDate, toDate, eventFrom, eventTo, location, trendResources, "GoogleTrend", queryCatlog);

        System.out.println(keywords);

        //
        Integer historyId = mySQLCon.getTwitterMaxHistoryId();

        List<String> series = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        List<List<Double>> finalData = new ArrayList<>();

        series.add(keywords);
        series.add("smoothed");

        List<String> fileLabels = googleTrendMockService.createDayDataLabel(fromDate, toDate);
        double[] dataP = new double[fileLabels.size()];
//        for (String file : fileLabels) {
//            System.out.println(file);
//        }

        for (int i = 0; i < fileLabels.size(); ++i) {
            labels.add(fileLabels.get(i));
//            data.add(Double.valueOf(random.nextInt(range)));
        }

        for (String key : keywords.split("\\+")) {
            List<Double> temp = new ArrayList<>();
            SegTree.query(
                    temp,
                    segTree.getRoot(),
                    LocalDate.parse(fromDate, fromFormatter),
                    LocalDate.parse(toDate, fromFormatter),
                    key + location,
                    jedis,
                    random,
                    range
            );

            for (int i = 0; i < dataP.length; ++i) {
                dataP[i] += temp.get(i);
            }
        }
        data = DoubleStream.of(dataP).boxed().collect(Collectors.toList());

        finalData.add(data);

        googleTrendMockService.generateFile(LocationHelper.getDataDir() + "/t" + historyId + ".csv", series, labels, data);

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
    @Path("oldSubmit")
    public Response submit2(
            @QueryParam("q") String keywords,
            @QueryParam("position") String position,
            @QueryParam("date") String date,
            @QueryParam("grop") String trendResources,
            @QueryParam("location") String location,
            @QueryParam("queryCatlog") String queryCatlog,
            @QueryParam("eventFrom") String eventFrom,
            @QueryParam("eventTo") String eventTo,
            @QueryParam("count") int range
    ) throws Exception {
        keywords = URLDecoder.decode(keywords);

        System.out.println("google trends");
        System.out.println(keywords);
        System.out.println(position);
        System.out.println(date);
        System.out.println(trendResources);
        System.out.println(location);
        System.out.println(queryCatlog);
        String[] dates = date.split("%20");
        String fromDate = dates[0];
        String toDate = dates[1];
        String decodeKeywords = URLDecoder.decode(keywords, "UTF-8");
        decodeKeywords = decodeKeywords.replaceAll("\\+", ",");
        mySQLCon.saveTwitterKeyword(decodeKeywords, fromDate, toDate, eventFrom, eventTo, location, trendResources, "GoogleTrend", queryCatlog);

        System.out.println(keywords);

        //
        Integer historyId = mySQLCon.getTwitterMaxHistoryId();

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

        series.add(keywords);
        series.add("smoothed");

        List<String> fileLabels = googleTrendMockService.createDayDataLabel(fromDate, toDate);
        for (String file : fileLabels) {
            System.out.println(file);
        }

        for (int i = 0; i < fileLabels.size(); ++i) {
//                      labels.add(fileContent.get(i).get(0));
            labels.add(fileLabels.get(i));
            data.add(Double.valueOf(random.nextInt(range)));
        }

        finalData.add(data);

        googleTrendMockService.generateFile(LocationHelper.getDataDir() + "/t" + historyId + ".csv", series, labels, data);

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
            @QueryParam("eventTo") String eventTo,
            @QueryParam("count") int range
    ) throws Exception {
        //submit one
        System.out.println("submit one");
        System.out.println(date);
        String[] dates = date.split("%20");
        String fromDate = dates[0];
        String toDate = dates[1];
        String decodeKeywords = URLDecoder.decode(keywords, "UTF-8");
        decodeKeywords = decodeKeywords.replaceAll("\\+", ",");
        mySQLCon.saveTwitterKeyword(decodeKeywords, fromDate, toDate, eventFrom, eventTo, locationOne, resource, "GoogleTrend", queryCatlog);

//                String query = "https://www.google.com/trends/explore?";
//                query += "date=" + date;
//                query += "&geo=" + locationOneID;
//                query += "&gprop=" + resource;
//                query += "&q=" + keywords;
//                System.out.println(query);
//                googleTrends.run2(query);
        Integer historyIdOne = mySQLCon.getTwitterMaxHistoryId();
//                LocationFactory.copyFileToDataDirectory(LocationHelper.getDownloadDir(), LocationHelper.getDataDir(), "multiTimeline.csv","t" + historyIdOne + ".csv");

        //submit two
        mySQLCon.saveTwitterKeyword(decodeKeywords, fromDate, toDate, eventFrom, eventTo, locationTwo, resource, "GoogleTrend", queryCatlog);

//                query = "https://www.google.com/trends/explore?";
//                query += "date=" + date;
//                query += "&geo=" + locationTwoID;
//                query += "&gprop=" + resource;
//                query += "&q=" + keywords;
//                System.out.println(query);
//                googleTrends.run2(query);
        Integer historyIdTwo = mySQLCon.getTwitterMaxHistoryId();
//                LocationFactory.copyFileToDataDirectory(LocationHelper.getDownloadDir(), LocationHelper.getDataDir(), "multiTimeline.csv","t" + historyIdTwo + ".csv");

        //group two together
        List<String> series = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        List<List<Double>> finalData = new ArrayList<>();

//                String fileURLOne = LocationHelper.getDataDir() + "t" + historyIdOne + ".csv";
//                List<List<String>> fileContentOne = MyFileReader.readCSV(fileURLOne);
        series.add(decodeKeywords);
        List<String> fileLabels = googleTrendMockService.createDayDataLabel(fromDate, toDate);

        for (int i = 0; i < fileLabels.size(); ++i) {
            labels.add(fileLabels.get(i));
            data.add(Double.valueOf(random.nextInt(range)));
        }
        finalData.add(data);
        googleTrendMockService.generateFile(LocationHelper.getDataDir() + "/t" + historyIdOne + ".csv", series, labels, data);

        series.add("Smoothed " + decodeKeywords);
        double[] xData = DoubleStream.iterate(1, n -> n + 1).limit(data.size()).toArray();
        double[] yData = data.stream().mapToDouble(Double::doubleValue).toArray();

        RegressionLine regressionLine = new RegressionLine();
        double[] smoothedData = regressionLine.getRegression(xData, yData);

        finalData.add(Arrays.stream(smoothedData)
                .boxed()
                .collect(Collectors.toList()));

        //Two
//                String fileURLTwo = LocationHelper.getDataDir() + "t" + historyIdTwo + ".csv";
//                List<List<String>> fileContentTwo = MyFileReader.readCSV(fileURLTwo);
        List<Double> dataTwo = new ArrayList<>();

        for (int i = 0; i < fileLabels.size(); ++i) {
            dataTwo.add(Double.valueOf(random.nextInt(range)));
        }
        finalData.add(dataTwo);
        googleTrendMockService.generateFile(LocationHelper.getDataDir() + "/t" + historyIdTwo + ".csv", series, labels, dataTwo);

        series.add("Smoothed " + keywords);
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

    @GET
    @Path("setValue/{key}/{value}")
    public Response set(@PathParam("key") String key,
            @PathParam("value") String value) throws Exception {
        jedisService.set(key, value);
        return Response.status(200)
                .entity("good")
                .build();
    }

    @GET
    @Path("getValue")
    public Response getValue() throws Exception {
        String res = jedisService.getValue("name");
        return Response.status(200)
                .entity(res)
                .build();
    }

    @GET
    @Path("getKeys")
    public Response getKeys() throws Exception {
        Set<String> res = jedisService.getAllKeys();

        return Response.status(200)
                .entity(res.toString())
                .build();
    }

    @GET
    @Path("getKeys/{prefix}")
    public Response getKeys(@PathParam("prefix") String prefix) throws Exception {
        Set<String> res = jedisService.getAllKeys(prefix);

        return Response.status(200)
                .entity(res.toString())
                .build();
    }

    @GET
    @Path("consistentlagtimes")
    public Response getConsistentLagTimes(
            @QueryParam("ids") String idStr,
            @QueryParam("dates") String eventStarts,
            @QueryParam("dateRange") int dateRange,
            @QueryParam("windowRange") int windowRange,
            @QueryParam("threshold") double threshold
    ) throws Exception {
        idStr = URLDecoder.decode(idStr);
        String[] ids = idStr.split(",");
        String[] dates = eventStarts.split("%2C");
        System.out.println("Lag Time");

        threshold /= 100;//done

        Integer historyId = mySQLCon.getTwitterMaxHistoryId();

        List<String> series = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        List<List<Double>> finalData = new ArrayList<>();
        List<String> fileLabels = googleTrendMockService.createLagTimeDataLabel(dateRange);

        for (int i = 0; i < fileLabels.size(); ++i) {
            labels.add(fileLabels.get(i));
        }

        List<Integer> lagTime = new ArrayList<>();

        for (int i = 0; i < ids.length; ++i) {
            String id = ids[i];
            series.add(id);
            String fileURL = LocationHelper.getDataDir() + "t" + id + ".csv";
            List<List<String>> fileContent = MyFileReader.readCSV(fileURL);
            LocalDate start = LocalDate.parse(dates[i], fromFormatter);
            LocalDate end = LocalDate.parse(dates[i], fromFormatter).plusDays(dateRange - 1);

            double[] dataP = new double[fileLabels.size()];
            for (int j = 3; j < fileContent.size(); ++j) {
                LocalDate cur = LocalDate.parse(fileContent.get(j).get(0), fromFormatter);
                if (cur.isEqual(start) || cur.isEqual(end) || (cur.isAfter(start) && cur.isBefore(end))) {
                    int index = (int) start.until(cur, ChronoUnit.DAYS);
                    dataP[index] += Double.valueOf(fileContent.get(j).get(1));
                }
            }
//            double[] norm = StatUtils.normalize(dataP);
            List<Double> data = DoubleStream.of(dataP).boxed().collect(Collectors.toList());
            System.out.println(data);
            finalData.add(data);//xin
            double[][] dataArr = new double[windowRange][2];
            for (int k = 0; k < windowRange; ++k) {
                dataArr[k][0] = k + 1;
            }

            double[] norm = norm(dataP);
            List<Double> normData = DoubleStream.of(norm).boxed().collect(Collectors.toList());
            int k = 0;
            double maxSlop = 0d;
            for (; k <= normData.size() - windowRange; ++k) {
                SimpleRegression regression = new SimpleRegression();
                double[] yData = normData.subList(k, k + windowRange).stream().mapToDouble(t -> t).toArray();
                for (int p = 0; p < windowRange; ++p) {
                    dataArr[p][1] = yData[p];
                }
                regression.addData(dataArr);
                double slope = regression.getSlope();
                System.out.println(slope);

                if (Double.compare(slope, threshold) > 0 && Double.compare(maxSlop, 0d) == 0) {
                    series.add(id + " reg");
                    lagTime.add(k);
                    yData = data.subList(k, k + windowRange).stream().mapToDouble(t -> t).toArray();
                    for (int p = 0; p < windowRange; ++p) {
                        dataArr[p][1] = yData[p];
                    }
                    regression.addData(dataArr);
                    List<Double> reg = new ArrayList<>();
                    for (int p = 0; p < k; ++p) {
                        reg.add(-10d);//not print this in chart!!
                    }
                    for (int p = 1; p <= windowRange; ++p) {
                        reg.add( p * regression.getSlope() + regression.getIntercept());
                    }
                    series.add(id + " regmax");
                    finalData.add(reg);
                    maxSlop = slope;
                    finalData.add(reg);
                } 
                if (Double.compare(slope, threshold) > 0 && Double.compare(maxSlop, 0d) != 0 && Double.compare(maxSlop, slope) < 0) {
                    maxSlop = slope;
                    finalData.remove(finalData.size() - 1);
                    yData = data.subList(k, k + windowRange).stream().mapToDouble(t -> t).toArray();
                    for (int p = 0; p < windowRange; ++p) {
                        dataArr[p][1] = yData[p];
                    }
                    regression.addData(dataArr);
                    List<Double> reg = new ArrayList<>();
                    for (int p = 0; p < k; ++p) {
                        reg.add(-10d);//not print this in chart!!
                    }
                    for (int p = 1; p <= windowRange; ++p) {
                        reg.add( p * regression.getSlope() + regression.getIntercept());
                    }
                    finalData.add(reg);
                }
            }
            if (Double.compare(maxSlop, 0d) == 0) {
                series.add(id + " reg");
                finalData.add(new ArrayList<>());
                series.add(id + " regmax");
                finalData.add(new ArrayList<>());
            }
        }
        System.out.println(lagTime);
        double time = 0d;
        if (!lagTime.isEmpty()) {
            time = lagTime.stream().mapToInt(t -> t).average().getAsDouble();
        }

        System.out.println("time " + time);

        //OLSMultipleLinearRegression
//              CurveSmooth csm = new CurveSmooth(xData, yData);
//              double[] smoothedData = csm.movingAverage(10);
//        RegressionLine regressionLine = new RegressionLine();
//        double[] smoothedData = regressionLine.getRegression(xData, yData);
//
//        finalData.add(Arrays.stream(smoothedData)
//                .boxed()
//                .collect(Collectors.toList()));
        CanvasData canvasData = new CanvasData(labels, series, finalData, (int) time + "");
        String res = gson.toJson(canvasData, CanvasData.class);
        return Response.status(200)
                .entity(res)
                .build();

    }

    double[] norm(double[] data) {
        double[] res = new double[data.length];

        double min = StatUtils.min(data);
        double max = StatUtils.max(data);
        if (max == min) {
            Arrays.fill(res, 1);
            return res;
        }
        for (int i = 0; i < data.length; ++i) {
            res[i] = (data[i] - min) / (max - min);
        }

        return res;
    }
}
