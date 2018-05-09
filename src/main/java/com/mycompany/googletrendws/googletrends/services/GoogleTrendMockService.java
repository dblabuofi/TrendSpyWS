/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.googletrendws.googletrends.services;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mou1609
 */
public class GoogleTrendMockService {
    
    DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public GoogleTrendMockService() {
    }
    
    public List<String> createDayDataLabel(String fStr, String tStr) {
        List<String> res = new ArrayList<>();
        LocalDate from = LocalDate.parse(fStr, formatter2);
        LocalDate to = LocalDate.parse(tStr, formatter2);
        if (from.isEqual(to)) {
            res.add(from.toString());
            return res;
        }
        Integer day = 0;
        LocalDate before = null;
        LocalDate cur = from;
        while (cur.isBefore(to) || cur.equals(to)) {
            before = from.plusDays(day++);
            cur = from.plusDays(day);
            res.add(before.format(formatter));
        }
        return res;
    }
    
    public List<String> createLagTimeDataLabel(int dateRange) {//5 days
        List<String> res = new ArrayList<>();
        res.add("Event Day");
        for (int i = 1; i < dateRange; ++i) {
            res.add(i + " days after");
        }
        return res;
    }
    
    public void generateFile(String fileURL, List<String> series, List<String> labels, List<Double> data) {
        try (
                        PrintWriter out = new PrintWriter(fileURL) {
//                                @Override
//                                public void println(String x) {
//                                        print(x + "\n");
//                                }
                }) {
                        out.println("Category:All categories");
                        out.println("");
                        out.println("day," + series.get(0));
                        for (int i = 0; i < labels.size(); ++i) {
//                                String line = formatterAfter.format(LocalDate.parse(labels.get(i).split(" to ")[0]))
                                String line = formatter2.format(LocalDate.parse(labels.get(i), formatter))
                                        + "," + data.get(i);
                                out.println(line);
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
        
        
    } 

}
