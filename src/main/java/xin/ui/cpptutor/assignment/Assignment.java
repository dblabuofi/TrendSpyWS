/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xin.ui.cpptutor.assignment;

import java.util.List;

/**
 *
 * @author mou1609
 */
public class Assignment {
    String id;
    String name;
    String pattern;
    List<PatternWeight> patternWeight;
    String constrains;
    String description;
    String hint;
    String code;
    String functionName;

    public Assignment(String id, String name, String pattern, List<PatternWeight> patternWeight, String constrains, String description, String hint, String code, String functionName) {
        this.id = id;
        this.name = name;
        this.pattern = pattern;
        this.patternWeight = patternWeight;
        this.constrains = constrains;
        this.description = description;
        this.hint = hint;
        this.code = code;
        this.functionName = functionName;
    }

    public List<PatternWeight> getPatternWeight() {
        return patternWeight;
    }

   

    public String getDescription() {
        return description;
    }

    public String getFunctionName() {
        return functionName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPattern() {
        return pattern;
    }

    public String getConstrains() {
        return constrains;
    }

    public String getDescrption() {
        return description;
    }

    public String getHint() {
        return hint;
    }

    public String getCode() {
        return code;
    }
    
    
    
}
