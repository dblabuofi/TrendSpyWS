/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.googletrendws.helper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 *
 * @author jupiter
 */
public class LocationFactory {
//        final static String destDir = "C:\\Users\\jupiter\\Documents\\NetBeansProjects\\FacebookCrawlerWS\\data\\";

//        final static String fileURL = "C:\\Users\\jupiter\\Downloads\\multiTimeline.csv";
//        final static String dirURL = "C:\\Users\\jupiter\\Downloads";
//      final static String fileURL = "C:\\Users\\Xin\\Downloads\\multiTimeline.csv";
//      final static String dirURL = "C:\\Users\\Xin\\Downloads";

//        static public String getFileURL() {
//                return fileURL;
//        }
//
//        static public String getDirURL() {
//                return dirURL;
//        }
//
//        public static String getDestDir() {
//                return destDir;
//        }
//
//        static public void copyFileToDataDirectory(String fileName) {
//                File oldFile = new File(fileURL);
//                File newFile = new File(destDir + fileName);
//
//                try {
////                      FileUtils.copyDirectory(oldFile, newFile);
////                      oldFile.renameTo(newFile);
//                        Files.copy(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                } catch (Exception e) {
//                        e.printStackTrace();
//                }
//
//        }
        static public void copyFileToDataDirectory(String fromDir, String toDir, String fromFileName, String toFileName) {
                File oldFile = new File(fromDir + fromFileName);
                File newFile = new File(toDir + toFileName);

                
                //remove         
                try {
                        Files.copy(oldFile.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
//                        List<List<String>> fileContent = MyFileReader.readCSV(fromDir + fromFileName);
//                        for (int i = 2; i < fileContent.size(); ++i) {
//                                fileContent.get(i).remove(1);
//                        }
//                        MyFileReader.writeFile(toDir + toFileName, fileContent, ",");
                        
                } catch (Exception e) {
                        e.printStackTrace();
                }

        }
}
