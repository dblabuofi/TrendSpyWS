/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xin.ui.cpptutor.ontology;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mou1609
 */
public class OntologyNode {
    String id;
    String value;
    List<OntologyNode> children;
    String type;

    public OntologyNode(String id, String value, String type) {
        this.id = id;
        this.value = value;
        this.type = type;
        children = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public List<OntologyNode> getChildren() {
        return children;
    }

    public String getType() {
        return type;
    }
    
    
}
