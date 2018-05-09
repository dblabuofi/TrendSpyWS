/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.googletrendws.resources;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mycompany.googletrendws.googletrends.data.CanvasData;
import com.mycompany.googletrendws.googletrends.data.LagTimeData;
import com.mycompany.googletrendws.googletrends.regression.RegressionLine;
import com.mycompany.googletrendws.helper.LocationHelper;
import com.mycompany.googletrendws.helper.MyFileReader;
import com.mycompany.googletrendws.helper.MySQLConn;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.HttpHeaders.USER_AGENT;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author jupiter
 */

@Path("result")
public class ResultResources {

        Gson gson;
        MySQLConn mySQLConn;
        @Inject
        public ResultResources(Gson gson, MySQLConn mySQLConn) {
                this.gson = gson;
                this.mySQLConn = mySQLConn;
        }
        
           @GET
        public Response status() {
                
                
                return Response.status(200)
                        .entity("Result Resources good")
                        .build();
        }
        
       DateTimeFormatter fromFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
       DateTimeFormatter toFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
      
        @GET
        @Path("googleTrend")
        public Response getGoogleTrendData(
                @QueryParam("id") String id
        ) {

                String fileURL = LocationHelper.getDataDir() + "t" + id + ".csv";
                List<List<String>> fileContent = MyFileReader.readCSV(fileURL);

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


                for (int i = 3; i < fileContent.size(); ++i) {
                        labels.add(LocalDate.parse(fileContent.get(i).get(0), fromFormatter).format(toFormatter));
                        data.add(Double.valueOf(fileContent.get(i).get(1)));
                }

                finalData.add(data);

                
                series.add("Smoothed " + fileContent.get(2).get(1));
                double[] xData = DoubleStream.iterate(1, n -> n + 1).limit(data.size()).toArray();
                double[] yData = data.stream().mapToDouble(Double::doubleValue).toArray();

                RegressionLine regressionLine = new RegressionLine();
                double[] smoothedData = regressionLine.getRegression(xData, yData);
                
                finalData.add(Arrays.stream(smoothedData)
                        .boxed()
                        .collect(Collectors.toList()));;
                
                
                CanvasData canvasData = new CanvasData(labels, series, finalData);
                String res = gson.toJson(canvasData, CanvasData.class);

                return Response.status(200)
                        .entity(res)
                        .build();
        }
        
        @GET
        @Path("twitterCrawler")
        public Response getTwitterCrawlerData(
                @QueryParam("id") String id,
                @QueryParam("clientTimezone") String clientTimezone
        ) {

                String fileURL = LocationHelper.getDataDir() + "t" + id + ".csv";
                
//                File csvFile = new File(fileURL);
//                if (!csvFile.exists()) {//twitter crawler 
//                        System.out.println("twitterCrawler file not exists");
//                        //not exist need call facebookws to get the file ready
//                        try {
//                                HttpClient client = HttpClientBuilder.create().build();
//
//                                String url = LocationHelper.getFacebookCrawlerURL() + "/download/generateTwitterCSV?";
//
//                                List<NameValuePair> params = new LinkedList<NameValuePair>();
//                                params.add(new BasicNameValuePair("id", id));
//                                params.add(new BasicNameValuePair("clientTimezone", clientTimezone));
//                                params.add(new BasicNameValuePair("dataURL", LocationHelper.getFacebookCrawlerDir()));
//                                
//                                url += URLEncodedUtils.format(params, "utf-8");
//                                System.out.println(url);
//                                HttpGet request = new HttpGet(url);
//                                // add request header
//                                request.addHeader("User-Agent", USER_AGENT);
//                                HttpResponse response = client.execute(request);
//                                if (response.getStatusLine().getStatusCode() == 200) {
//                                        InputStream in = response.getEntity().getContent();
//                                        String body = IOUtils.toString(in);
//                                        System.out.println(body);
//                                }
//                                System.out.println(response.getEntity().getContent());
//                        } catch (Exception e) {
//                                e.printStackTrace();
//                        }
//
//                }
                
                   List<List<String>> fileContent = MyFileReader.readCSV(fileURL);

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

                        for (int i = 3; i < fileContent.size(); ++i) {
                                labels.add(fileContent.get(i).get(0));
                                data.add(Double.valueOf(fileContent.get(i).get(1)));
                        }
                        finalData.add(data);
                        CanvasData canvasData = new CanvasData(labels, series, finalData);
                        
                        String res = gson.toJson(canvasData, CanvasData.class);

                        return Response.status(200)
                                .entity(res)
                                .build();
                
                
                
        }
        
        
        List<Double> getFirstDerivation(List<Double> data) {
                List<Double> firstDerivation = new ArrayList<>();
                
                for (int i = 1; i < data.size(); ++i) {
                        firstDerivation.add(data.get(i) - data.get(i - 1));
                }
                return firstDerivation;
        }
        
        @GET
        @Path("lagtime")
        public Response getLagTime(
                @QueryParam("id") String id,
                @QueryParam("eventStartDate") String eventStartDate
        ) {

                LocalDate eventDate = LocalDate.parse(eventStartDate, toFormatter);
                
                String csvFileURL = LocationHelper.getFacebookCrawlerDir() + "/data/t" + id + ".csv";
                //read arries from t.csv file
                List<Double> data = MyFileReader.readCSVData(csvFileURL);
                List<String> labels = MyFileReader.readCSVLabel(csvFileURL);
                
                List<Double> firstDerivation = getFirstDerivation(data);
                
                //we use a simple one to identify the 
//                Double threshold = 30.0;
//                Integer responseDateIndex = -1;
//                
//                for (Integer i = 0; i < firstDerivation.size(); ++i) {
//                        if (firstDerivation.get(i) > threshold) {
//                                responseDateIndex = i;
//                                break;
//                        }
//                }

                Integer responseDateIndex = -1;
                for ( Integer i = 0; i < data.size(); ++i) {
                        if (data.get(i) == 100) {
                                  responseDateIndex = i;
                                break;
                        }
                }

                
                long daysBetween = 0;
                LocalDate responseDate = null;
                
                if (responseDateIndex == -1) {
                        return Response.status(200)
                                .entity("No Event found")
                                .build();
                } else {
                        responseDate = LocalDate.parse(labels.get(responseDateIndex), fromFormatter);
                        System.out.println(fromFormatter.format(responseDate));
                        daysBetween = ChronoUnit.DAYS.between(eventDate, responseDate);
                }
                
                LagTimeData lagTimeData = new LagTimeData(daysBetween, toFormatter.format(responseDate));
                
                String res = gson.toJson(lagTimeData); 
                System.out.println(res);
                
                return Response.status(200)
                        .entity(res)
                        .build();
        }
        
         @GET
        @Path("lagtimeWithoutId")
        public Response getLagtimeWithoutId(
                @QueryParam("eventStartDate") String eventStartDate
        ) {

                LocalDate eventDate = LocalDate.parse(eventStartDate, toFormatter);
                Integer id = mySQLConn.getTwitterMaxHistoryId();
                
                String csvFileURL = LocationHelper.getFacebookCrawlerDir() + "/data/t" + id + ".csv";
                //read arries from t.csv file
                List<Double> data = MyFileReader.readCSVData(csvFileURL);
                List<String> labels = MyFileReader.readCSVLabel(csvFileURL);
                
                List<Double> firstDerivation = getFirstDerivation(data);
                
                //we use a simple one to identify the 
//                Double threshold = 30.0;
//                Integer responseDateIndex = -1;
//                
//                for (Integer i = 0; i < firstDerivation.size(); ++i) {
//                        if (firstDerivation.get(i) > threshold) {
//                                responseDateIndex = i;
//                                break;
//                        }
//                }

                Integer responseDateIndex = -1;
                for ( Integer i = 0; i < data.size(); ++i) {
                        if (data.get(i) == 100) {
                                  responseDateIndex = i;
                                break;
                        }
                }

                
                long daysBetween = 0;
                LocalDate responseDate = null;
                
                if (responseDateIndex == -1) {
                        return Response.status(200)
                                .entity("No Event found")
                                .build();
                } else {
                        responseDate = LocalDate.parse(labels.get(responseDateIndex), fromFormatter);
                        System.out.println(fromFormatter.format(responseDate));
                        daysBetween = ChronoUnit.DAYS.between(eventDate, responseDate);
                }
                
                LagTimeData lagTimeData = new LagTimeData(daysBetween, toFormatter.format(responseDate));
                
                String res = gson.toJson(lagTimeData); 
                System.out.println(res);
                
                return Response.status(200)
                        .entity(res)
                        .build();
        }
        
        
        
        @GET
        @Path("CompareSimilarEvent")
        public Response getCompareSimilarEvent (
                @QueryParam("idOne") String idOne,
                @QueryParam("idTwo") String idTwo
        ) {
        
                System.out.println(idOne);
                System.out.println(idTwo);
        
                //first
                List<String> series = new ArrayList<>();
                List<String> labelsOne = new ArrayList<>();
                List<Double> data = new ArrayList<>();
                List<List<Double>> finalData = new ArrayList<>();
                
                String fileURLOne = LocationHelper.getDataDir() + "t" + idOne + ".csv";
                List<List<String>> fileContentOne = MyFileReader.readCSV(fileURLOne);
                series.add(fileContentOne.get(2).get(1));
                
                System.out.println("file");
                System.out.println(fileContentOne.size());
                System.out.println(fileContentOne.get(2).get(0));
                System.out.println(fileContentOne.get(2).get(1));
                
                Map<String, Double> fileMapOne = new HashMap<>();
                Map<String, Double> fileMapSmoothOne = new HashMap<>();
                for (int i = 3; i < fileContentOne.size(); ++i) {
                        String date = LocalDate.parse(fileContentOne.get(i).get(0), fromFormatter).format(toFormatter);
                        Double value = Double.valueOf(fileContentOne.get(i).get(1)); 
                        labelsOne.add(date);
                        data.add(value);
                        fileMapOne.put(date, value);
                }
//                finalData.add(data);
                series.add("Smoothed " + fileContentOne.get(2).get(1));
                double[] xData = DoubleStream.iterate(1, n -> n + 1).limit(data.size()).toArray();
                double[] yData = data.stream().mapToDouble(Double::doubleValue).toArray();

                RegressionLine regressionLine = new RegressionLine();
                double[] smoothedData = regressionLine.getRegression(xData, yData);
                for (int i = 0; i < smoothedData.length; ++i) {
                        fileMapSmoothOne.put(labelsOne.get(i), smoothedData[i]);
                }


                //second
                String fileURLTwo = LocationHelper.getDataDir() + "t" + idTwo + ".csv";
                List<List<String>> fileContentTwo = MyFileReader.readCSV(fileURLTwo);
                List<Double> dataTwo = new ArrayList<>();
                List<String> labelsTwo = new ArrayList<>();
                Map<String, Double> fileMapTwo = new HashMap<>();
                Map<String, Double> fileMapSmoothTwo = new HashMap<>();
                series.add(fileContentTwo.get(2).get(1));
                for (int i = 3; i < fileContentTwo.size(); ++i) {
                        String date = LocalDate.parse(fileContentTwo.get(i).get(0), fromFormatter).format(toFormatter);
                        Double value = Double.valueOf(fileContentTwo.get(i).get(1)); 
                        labelsTwo.add(date);
                        dataTwo.add(value);
                        fileMapTwo.put(date, value);
                }
//                finalData.add(dataTwo);
                 series.add("Smoothed " + fileContentTwo.get(2).get(1));
                xData = DoubleStream.iterate(1, n -> n + 1).limit(dataTwo.size()).toArray();
                yData = dataTwo.stream().mapToDouble(Double::doubleValue).toArray();

                RegressionLine regressionLineTwo = new RegressionLine();
                smoothedData = regressionLineTwo.getRegression(xData, yData);
                for (int i = 0; i < smoothedData.length; ++i) {
                        fileMapSmoothTwo.put(labelsTwo.get(i), smoothedData[i]);
                }

                //intercetion data
                LocalDate fileOne = LocalDate.parse(labelsOne.get(0), toFormatter);
                LocalDate fileTwo = LocalDate.parse(labelsTwo.get(0), toFormatter);
                Set<String> calender = new LinkedHashSet<>();
                
                if (fileOne.isBefore(fileTwo)) {
                        calender.addAll(labelsOne);
                        calender.addAll(labelsTwo);
                } else {
                        calender.addAll(labelsTwo);
                        calender.addAll(labelsOne);                        
                }
                System.out.println("********************");
                System.out.println(labelsOne.size());
                System.out.println(labelsTwo.size());
                System.out.println(calender.size());
                
                
                List<Double> finalDataOne = new ArrayList<>();
                List<Double> finalDataSmoothOne = new ArrayList<>();
                List<Double> finalDataTwo = new ArrayList<>();
                List<Double> finalDataSmoothTwo = new ArrayList<>();
                
                for (String date : calender) {
                        if (fileMapOne.containsKey(date)) {
                                finalDataOne.add(fileMapOne.get(date));
                        } else {
                                finalDataOne.add(0.0);
                        }
                }
                for (String date : calender) {
                        if (fileMapSmoothOne.containsKey(date)) {
                                finalDataSmoothOne.add(fileMapSmoothOne.get(date));
                        } else {
                                finalDataSmoothOne.add(0.0);
                        }
                }
                
                for (String date : calender) {
                        if (fileMapTwo.containsKey(date)) {
                                finalDataTwo.add(fileMapTwo.get(date));
                        } else {
                                finalDataTwo.add(0.0);
                        }
                }
                for (String date : calender) {
                        if (fileMapSmoothTwo.containsKey(date)) {
                                finalDataSmoothTwo.add(fileMapSmoothTwo.get(date));
                        } else {
                                finalDataSmoothTwo.add(0.0);
                        }
                }
                
                List<String> labels = new ArrayList<>(calender);
                finalData.add(finalDataOne);
                finalData.add(finalDataSmoothOne);
                finalData.add(finalDataTwo);
                finalData.add(finalDataSmoothTwo);

                CanvasData canvasData = new CanvasData(labels, series, finalData);
                String res = gson.toJson(canvasData, CanvasData.class);
                return Response.status(200)
                        .entity(res)
                        .build();
        }
        
        Type listOfListType = new TypeToken<ArrayList<ArrayList<Double>>>(){}.getType();
        @POST
        @Path("regressionLines")
        public Response getregressionLines (
                String dataString
        ) {
            System.out.println(dataString);
            List<List<Double>> data = gson.fromJson(dataString, listOfListType);
            
            if (data.size() == 0) {
                return Response.status(200)
                        .entity("[]")
                        .build();
            }
            
            List<List<Double>> returnData = new ArrayList<>();
            
            for (int i = 0; i < data.size(); ++i) {
                returnData.add(data.get(i));
                List<Double> y = data.get(i);
                double[] xData = DoubleStream.iterate(1, n -> n + 1).limit(y.size()).toArray();
                double[] yData = y.stream().mapToDouble(Double::doubleValue).toArray();
                RegressionLine regressionLine = new RegressionLine();
                double[] smoothedData = regressionLine.getRegression(xData, yData);
                returnData.add(Arrays.stream(smoothedData)
                        .boxed()
                        .collect(Collectors.toList()));
            }
            
            String res = gson.toJson(returnData, listOfListType);
            
                return Response.status(200)
                        .entity(res)
                        .build();
        }
        
        
        
        
        
        
        
}
