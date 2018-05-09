/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xin.ui.cpptutor.assignment;

import com.google.gson.Gson;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import xin.ui.cpptutor.misc.FileReader;
import xin.ui.cpptutor.misc.LocationService;

/**
 *
 * @author mou1609
 */
public class AssignmentService {
    static Gson gson = new Gson();
    
    static public List<Assignment> getAllAssignments() {
        List<Assignment> res = new ArrayList<>();
        try {
            Path dir = Paths.get(LocationService.getAssignmentURL());
            try (Stream<Path> stream = Files.list(dir)) {
                stream.forEach(t -> {
                    try {
                        res.add(gson.fromJson(FileReader.readFile(t.toString()), Assignment.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            } 
        } catch(Exception e) {
            e.printStackTrace();
        }
        return res;
    }

}
