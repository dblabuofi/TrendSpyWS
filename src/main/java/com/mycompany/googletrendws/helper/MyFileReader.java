/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.googletrendws.helper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author jupiter
 */
public class MyFileReader {
      static public List<List<String>> readCSV(String csvFile) {
                BufferedReader br = null;
                String line = "";
                String cvsSplitBy = ",";
                List<List<String>> res = null;
                res = new ArrayList<>();
                try {
                        br = new BufferedReader(new FileReader(csvFile));
                        while ((line = br.readLine()) != null) {
                                // use comma as separator
                                String[] cells = line.split(cvsSplitBy);
                                List<String> row = new ArrayList<>(Arrays.asList(cells));
                                res.add(row);
                        }

                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                } finally {
                        if (br != null) {
                                try {
                                        br.close();
                                } catch (IOException e) {
                                        e.printStackTrace();
                                }
                        }
                }
                return res;
        }
      
      
      public static <T> List<List<T>> transpose(List<List<T>> table) {
            List<List<T>> ret = new ArrayList<List<T>>();
            final int N = table.get(0).size();
            for (int i = 0; i < N; i++) {
                  List<T> col = new ArrayList<T>();
                  for (List<T> row : table) {
                        col.add(row.get(i));
                  }
                  ret.add(col);
            }
            return ret;
      }
      
        static public List<Double> readCSVData(String csvFile) {
                BufferedReader br = null;
                String line = "";
                String cvsSplitBy = ",";
                List<List<String>> fileContent = null;
                fileContent = new ArrayList<>();
                try {
                        br = new BufferedReader(new FileReader(csvFile));
                        while ((line = br.readLine()) != null) {
                                // use comma as separator
                                String[] cells = line.split(cvsSplitBy);
                                List<String> row = new ArrayList<>(Arrays.asList(cells));
                                fileContent.add(row);
                        }

                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                } finally {
                        if (br != null) {
                                try {
                                        br.close();
                                } catch (IOException e) {
                                        e.printStackTrace();
                                }
                        }
                }
                
                List<Double> data = new ArrayList<>();
                
                for (int i = 3; i < fileContent.size(); ++i) {
                        data.add(Double.valueOf(fileContent.get(i).get(1)));
                }
                
                return data;
        }
        static public List<String> readCSVLabel(String csvFile) {
                BufferedReader br = null;
                String line = "";
                String cvsSplitBy = ",";
                List<List<String>> fileContent = null;
                fileContent = new ArrayList<>();
                try {
                        br = new BufferedReader(new FileReader(csvFile));
                        while ((line = br.readLine()) != null) {
                                // use comma as separator
                                String[] cells = line.split(cvsSplitBy);
                                List<String> row = new ArrayList<>(Arrays.asList(cells));
                                fileContent.add(row);
                        }

                } catch (FileNotFoundException e) {
                        e.printStackTrace();
                } catch (IOException e) {
                        e.printStackTrace();
                } finally {
                        if (br != null) {
                                try {
                                        br.close();
                                } catch (IOException e) {
                                        e.printStackTrace();
                                }
                        }
                }
                
                List<String> data = new ArrayList<>();
                
                for (int i = 3; i < fileContent.size(); ++i) {
                        data.add(fileContent.get(i).get(0).split(" to ")[0]);
                }
                
                return data;
        }
        
        static public String writeFile(String fileUrl, List<List<String>> words, String separator) {
                try (
                        PrintWriter out = new PrintWriter(fileUrl) {
                        }) {
                        System.out.println(fileUrl);
                        for (List<String> wordsLine : words) {
                                String line = StringUtils.join(wordsLine, separator);
                                out.println(line);
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return fileUrl;
        }
}
