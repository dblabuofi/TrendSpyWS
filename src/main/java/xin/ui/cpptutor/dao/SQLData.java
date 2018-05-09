/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xin.ui.cpptutor.dao;

import java.util.List;

/**
 *
 * @author jupiter
 */
public class SQLData {
    int id;
    String username;
    Boolean admin;
    Boolean teacher;
    Boolean tutor;
    Boolean student;

    public SQLData(int id, String username, Boolean admin, Boolean teacher, Boolean tutor, Boolean studuent) {
        this.id = id;
        this.username = username;
        this.admin = admin;
        this.teacher = teacher;
        this.tutor = tutor;
        this.student = studuent;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public Boolean getTeacher() {
        return teacher;
    }

    public Boolean getTutor() {
        return tutor;
    }

    public Boolean getStuduent() {
        return student;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public void setTeacher(Boolean teacher) {
        this.teacher = teacher;
    }

    public void setTutor(Boolean tutor) {
        this.tutor = tutor;
    }

    public void setStuduent(Boolean studuent) {
        this.student = studuent;
    }


   
    
}
