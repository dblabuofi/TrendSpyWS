/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xin.ui.cpptutor.newpattern;

/**
 *
 * @author mou1609
 */
public class NewNode {
    String id;
    String label;
    String lineNum;
    String maxLen;
    String exp;
    String correct;
    String inCorrect;

    public NewNode(String id, String label, String lineNum, String maxLen, String exp, String correct, String inCorrect) {
        this.id = id;
        this.label = label;
        this.lineNum = lineNum;
        this.maxLen = maxLen;
        this.exp = exp;
        this.correct = correct;
        this.inCorrect = inCorrect;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getLineNum() {
        return lineNum;
    }

    public String getMaxLen() {
        return maxLen;
    }

    public String getExp() {
        return exp;
    }

    public String getCorrect() {
        return correct;
    }

    public String getInCorrect() {
        return inCorrect;
    }
    
    
    
}
