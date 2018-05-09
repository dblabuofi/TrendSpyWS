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
public class NewEdge {
    String from;
    String to;
    String arrows;
    String dashes;
    String id;

    public NewEdge(String from, String to, String arrows, String dashes, String id) {
        this.from = from;
        this.to = to;
        this.arrows = arrows;
        this.dashes = dashes;
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getArrows() {
        return arrows;
    }

    public String getDashes() {
        return dashes;
    }

    public String getId() {
        return id;
    }
    
}
