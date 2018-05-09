/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xin.ui.cpptutor.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import xin.ui.cpptutor.obj.Edge;
import xin.ui.cpptutor.obj.Vertex;
import xin.ui.cpptutor.obj.VertexType;

/**
 *
 * @author mou1609
 */
public class EPDGParser {

    private CPP14BaseListener listener;
    private Map<String, DirectedGraph<Vertex, Edge>> graphs;
    private Map<String, Map<VertexType, List<Vertex>>> verticesByType;

    public EPDGParser() {
        graphs = new HashMap<>();
        verticesByType = new HashMap<>();
    }
    
    public void parseJavaClass(String body, int errorLineOffset) {

        Lexer lexer = new CPP14Lexer(new ANTLRInputStream(body));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CPP14Parser parser = new CPP14Parser(tokens);

        listener = new CPP14BaseListener(errorLineOffset);

        parser.removeErrorListeners();
        parser.addErrorListener(new ErrorListener(listener));

//		ParserRuleContext t = parser.compilationUnit();
        ParserRuleContext t = parser.translationunit();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, t);

        System.out.println("done");

        // Generate the graphs.
        for (String method : listener.getMethods()) {
            DirectedGraph<Vertex, Edge> graph = new DefaultDirectedGraph<>(Edge.class);
            graphs.put(method, graph);
            verticesByType.put(method, new HashMap<>());

            for (Vertex current : listener.getVertices(method)) {
                graph.addVertex(current);

                List<Vertex> list = verticesByType.get(method).get(current.getType());
                if (list == null) {
                    verticesByType.get(method).put(current.getType(), list = new ArrayList<>());
                }
                list.add(current);
            }
            for (Edge current : listener.getEdges(method)) {
                graph.addEdge(current.getFromVertex(), current.getToVertex(), current);
            }
        }
        System.out.println("graphs");
        for (Map.Entry<String, DirectedGraph<Vertex, Edge>> entry : graphs.entrySet()) {
            System.out.println("************Entry***************");
            System.out.println(entry.getKey());
            for (Iterator<Vertex> it = entry.getValue().vertexSet().iterator(); it.hasNext();) {
                System.out.println(it.next());
            }
            for (Iterator<Edge> it = entry.getValue().edgeSet().iterator(); it.hasNext();) {
                System.out.println(it.next());
            }
        }

        System.out.println("types");
        for (Map.Entry<String, Map<VertexType, List<Vertex>>> entry : verticesByType.entrySet()) {
            System.out.println("************Entry***************");
            System.out.println(entry.getKey());
            for (Map.Entry<VertexType, List<Vertex>> en : entry.getValue().entrySet()) {
                System.out.println(en.getKey());
                for (Vertex v : en.getValue()) {
                    System.out.println(v);
                }
            }
        }

    }

    public Set<String> getMethods() {
        return listener.getMethods();
    }

    public Map<String, DirectedGraph<Vertex, Edge>> getGraphs() {
        return graphs;
    }

    public Map<String, Map<VertexType, List<Vertex>>> getVerticesByType() {
        return verticesByType;
    }

}
