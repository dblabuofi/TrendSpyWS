/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xin.ui.cpptutor.VisData;

import java.util.List;

/**
 *
 * @author mou1609
 */
public class VisDataSet {
    List<VisNode> nodes;
    List<VisEdge> edges;

    public VisDataSet(List<VisNode> nodes, List<VisEdge> edges) {
        this.nodes = nodes;
        this.edges = edges;
    }
    
}
