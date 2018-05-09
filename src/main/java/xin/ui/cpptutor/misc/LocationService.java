/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xin.ui.cpptutor.misc;

import java.util.logging.Logger;

/**
 *
 * @author mou1609
 */
public class LocationService {
    static final String patternURL = "C:/Users/jupiter/Documents/NetBeansProjects/CPPTutor/tutor/patterns/";
    static final String assignmentURL = "C:/Users/jupiter/Documents/NetBeansProjects/CPPTutor/tutor/assignments/";
    static final String ontologyURL = "C:/Users/jupiter/Documents/NetBeansProjects/CPPTutor/tutor/ontology/";
//    static final String patternURL = "/home/tutor/patterns/";
//    static final String assignmentURL = "/home/tutor/assignments/";

    static public String getPatternURL() {
        return patternURL;
    }

    public LocationService() {
    }

    public static String getAssignmentURL() {
        return assignmentURL;
    }

    public static String getOntologyURL() {
        return ontologyURL;
    }

    
}
