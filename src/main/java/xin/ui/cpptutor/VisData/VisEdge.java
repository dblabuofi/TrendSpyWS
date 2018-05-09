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
public class VisEdge {
    String from;
    String to;
    String arrows;
    boolean dashes;
    
    public VisEdge(String from, String to, String arrows, boolean dashes) {
        this.from = from;
        this.to = to;
        this.arrows = arrows;
        this.dashes = dashes;
    }
}
