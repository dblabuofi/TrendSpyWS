/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xin.ui.cpptutor.newpattern;

import java.util.List;

/**
 *
 * @author mou1609
 */
public class NewPattern {
    String name;
    String description;
    String missingfeedback;
    List<NewNode> nodes;
    List<NewEdge> edges;

    public NewPattern(String name, String description, String missingfeedback, List<NewNode> nodes, List<NewEdge> edges) {
        this.name = name;
        this.description = description;
        this.missingfeedback = missingfeedback;
        this.nodes = nodes;
        this.edges = edges;
    }

    public String getMissingfeedback() {
        return missingfeedback;
    }
    
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<NewNode> getNodes() {
        return nodes;
    }

    public List<NewEdge> getEdges() {
        return edges;
    }
    
}
