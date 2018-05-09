/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xin.ui.cpptutor.dao;

/**
 *
 * @author mou1609
 */
public class MyClass {
    String name;
    String semester;
    int id;

    public MyClass(String name, String semester, int id) {
        this.name = name;
        this.semester = semester;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSemester() {
        return semester;
    }
    
}
