/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xin.ui.cpptutor.Generation;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import xin.ui.cpptutor.misc.FileReader;

/**
 *
 * @author mou1609
 */
public class GenerateCode {
    static String dir = "C:/Users/jupiter/Documents/NetBeansProjects/CPPTutor/data/assign";
    static Random random = new Random();
    
    static public void getAssignmentOne() {
        for (int i = 0; i < 10; ++i) {
            String content = "#include <iostream>\n";
            content += "#include <string>\n";
            content += "using namespace std;\n";
            content += "void assignment1(string a) {\n";
            content += "int ";
            int val = random.nextInt(2);
            if (val == 0) {
                content += "odd=0,";
            } else {
                content += "odd=1,";
            }
            val = random.nextInt(2);
            if (val == 0) {
                content += "even=0;\n";
            } else {
                content += "even=1;\n";
            }
            val = random.nextInt(3);
            if (val == 0) {
                content += "for (int i = 0; i < a.length(); i++) {\n";
            } else if (val == 1) {
                content += "for (int i = 0; i <= a.length(); i++) {\n";
            } else if (val == 2) {
                content += "for (int i = 1; i <= a.length(); i++) {\n";
            }
            val = random.nextInt(2);
            if (val == 0) {
                content += "if (i % 2 == 1)\n";
            } else {
                content += "if (i % 2 == 0)\n";
            }
            val = random.nextInt(2);
            if (val == 0) {
                content += "odd += a[i] - '0';\n";
            } else {
                content += "odd *= a[i] - '0';\n";
            }
            val = random.nextInt(2);
            if (val == 0) {
                content += "if (i % 2 == 1)\n";
            } else {
                content += "if (i % 2 == 0)\n";
            }
            val = random.nextInt(2);
            if (val == 0) {
                content += "even += a[i] - '0';\n";
            } else {
                content += "even *= a[i] - '0';\n";
            }
            content += "}\n";
            content += "}\n";
            //main
            content += "int main(){\n";
            content += "string a = \"123\";\n";
            content += "assignment1(a);\n";
            content += "return 0;\n";
            
            FileReader.writeFile(dir + i + ".cpp", content);
        }
        
    }
}
