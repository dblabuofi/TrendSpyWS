/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xin.ui.cpptutor.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author mou1609
 */
public class FileReader {

    public static String readFile(String submissionFile) throws FileNotFoundException {
        System.out.println(submissionFile);
        Scanner scan = new Scanner(new File(submissionFile));
        StringBuffer submission = new StringBuffer();
        while (scan.hasNext()) {
            submission.append(scan.nextLine());
            submission.append("\n");
        }
        scan.close();
        return submission.toString();
    }
    
    static public List<List<String>> readCSV(String csvFile) {
                BufferedReader br = null;
                String line = "";
                String cvsSplitBy = ",";
                List<List<String>> res = null;
                res = new ArrayList<>();
                try {
                        br = new BufferedReader(new java.io.FileReader(csvFile));
                        while ((line = br.readLine()) != null) {
                                if (line.startsWith("\"")) {//quoates
                                        
                                        
                                        List<String> allMatches = new ArrayList<String>();
                                        Matcher m = Pattern.compile("\"[^\"]*\"")
                                                .matcher(line);
                                        while (m.find()) {
                                                allMatches.add(m.group());
                                        }
                                        
                                        List<String> row = allMatches;
                                        res.add(row);
                                        if (row.size() < res.get(0).size()) {
                                                for (int i = 0; i < res.get(0).size() - row.size(); ++i) {
                                                        row.add("");
                                                }
                                        }
                                } else {
                                        // use comma as separator
                                        String[] cells = line.split(cvsSplitBy);
                                        List<String> row = new ArrayList<>(Arrays.asList(cells));
                                        res.add(row);
                                        if (row.size() < res.get(0).size()) {
                                                for (int i = 0; i < res.get(0).size() - row.size(); ++i) {
                                                        row.add("");
                                                }
                                        }
        //                                System.out.println(line);
                                }
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
        static public List<List<String>> readCSVContent(String csvFile) {
                List<List<String>> res = readCSV(csvFile);
                res.remove(0);
                return res;
        }
        static public List<String> readCSVHead(String csvFile) {
                List<List<String>> res = readCSV(csvFile);
                return res.get(0);
        }
        
    
    
    static public String writeFile(String fileUrl, String content) {
        try {
            PrintWriter out = new PrintWriter(fileUrl);
            out.print(content);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUrl;
    }
    static public String writeFile(String fileUrl, List<List<String>> content) {
        try {
            PrintWriter out = new PrintWriter(fileUrl);
            for (List<String> words : content) {
                String row = StringUtils.join(words, ",");
                out.println(row);
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUrl;
    }
    
    static public void writeObject(String fileUrl, Object obj) {
        try {
            FileOutputStream fout = new FileOutputStream(fileUrl);
            ObjectOutputStream out = new ObjectOutputStream(fout);
            out.writeObject(obj);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    static public Object readObject(String fileUrl) {
        try {
            FileInputStream  fIn = new FileInputStream (fileUrl);
            ObjectInputStream objectIn = new ObjectInputStream(fIn);
            Object obj = objectIn.readObject();
            objectIn.close();
            return obj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    
}
