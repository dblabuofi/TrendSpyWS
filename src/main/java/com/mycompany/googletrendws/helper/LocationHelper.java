/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.googletrendws.helper;

/**
 *
 * @author mou1609
 */
public class LocationHelper {
        static final String FacebookCrawlerURL = "http://localhost:8084/FacebookCrawlerWS";
//        static final String FacebookCrawlerURL = "http://xin.nkn.uidaho.edu:8084/FacebookCrawlerWS";

        //Need change
        static final String DownloadDir = "C:\\Users\\jupiter\\Downloads\\";
//        static final String DownloadDir = "C:\\Users\\mou1609\\Downloads\\";
//        static final String DownloadDir = "C:\\Users\\Xin\\Downloads\\";
        
         final static String DataDir = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\FacebookCrawlerWS\\data\\";
        static final String FacebookCrawlerDir = "C:/Users/jupiter/Documents/NetBeansProjects/FacebookCrawlerWS";

        
        public static String getFacebookCrawlerURL() {
                return FacebookCrawlerURL;
        }

        public static String getFacebookCrawlerDir() {
                return FacebookCrawlerDir;
        }

        public static String getDownloadDir() {
                return DownloadDir;
        }

        public static String getDataDir() {
                return DataDir;
        }
        
        
        
        
}
