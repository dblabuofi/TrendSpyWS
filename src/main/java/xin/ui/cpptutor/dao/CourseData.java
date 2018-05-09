/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xin.ui.cpptutor.dao;

import java.util.List;

/**
 *
 * @author mou1609
 */
public class CourseData {
    String name;
    int id;
    List<String> teachers;
    List<String> tutors;
    List<String> students;

    public CourseData(String name, int id, List<String> teachers, List<String> tutors, List<String> students) {
        this.name = name;
        this.id = id;
        this.teachers = teachers;
        this.tutors = tutors;
        this.students = students;
    }
    
}
