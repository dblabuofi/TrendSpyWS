/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xin.ui.cpptutor.VisData;

/**
 *
 * @author mou1609
 */
public class VisNode {
    String id;
    String label;
    String lineNum;
    
    public VisNode(String id, String label, String lineNum) {
        this.id = id;
        this.label = label;
        this.lineNum = lineNum;
    }
}
