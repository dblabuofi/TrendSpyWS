/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.googletrendws.googletrends.data;

import java.util.List;

/**
 *
 * @author jupiter
 */
public class CanvasData {
      List<String> labels;
      List<String> series;
      List<List<Double>> data;
      String lagTime;
      public CanvasData(List<String> labels, List<String> series, List<List<Double>> data) {
            this.labels = labels;
            this.series = series;
            this.data = data;
      }
      public CanvasData(List<String> labels, List<String> series, List<List<Double>> data, String lagTime) {
            this.labels = labels;
            this.series = series;
            this.data = data;
            this.lagTime = lagTime;
      }
      
}
