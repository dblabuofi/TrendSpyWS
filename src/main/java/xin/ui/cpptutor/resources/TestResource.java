/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xin.ui.cpptutor.resources;

import xin.ui.cpptutor.ontology.OntologyNode;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import xin.ui.cpptutor.newpattern.NewPattern;
import org.jgrapht.DirectedGraph;
import xin.ui.cpptutor.VisData.VisDataSet;
import xin.ui.cpptutor.VisData.VisEdge;
import xin.ui.cpptutor.VisData.VisNode;
import xin.ui.cpptutor.assignment.Assignment;
import xin.ui.cpptutor.assignment.AssignmentService;
import xin.ui.cpptutor.matching.MatchingService;
import xin.ui.cpptutor.misc.FileReader;
import xin.ui.cpptutor.obj.ConstraintFeedback;
import xin.ui.cpptutor.obj.Edge;
import xin.ui.cpptutor.obj.EdgeType;
import xin.ui.cpptutor.obj.Feedback;
import xin.ui.cpptutor.obj.Feedback.FeedbackType;
import xin.ui.cpptutor.obj.FeedbackObj;
import xin.ui.cpptutor.obj.Pattern;
import xin.ui.cpptutor.obj.PatternFeedback;
import xin.ui.cpptutor.obj.PatternGraph;
import xin.ui.cpptutor.obj.Solution;
import xin.ui.cpptutor.obj.Vertex;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import xin.ui.cpptutor.assignment.PatternWeight;
import xin.ui.cpptutor.misc.LocationService;
import xin.ui.cpptutor.obj.PatternVertex;
import xin.ui.cpptutor.obj.VertexType;

/**
 *
 * @author jupiter
 */
@Path("r")
public class TestResource {

    Gson gson;
    MatchingService service;

    @Inject
    public TestResource(Gson gson, MatchingService service) {
        this.gson = gson;
        this.service = service;
    }

    @GET
    public Response status() {
        return Response.status(200)
                .entity("I am good")
                .build();
    }

    @GET
    @Path("/getOntology")
    public Response getOntology() {
        String location = LocationService.getOntologyURL();
        String nodeTypeFile = location + "nodeType.txt";
        String edgesFile = location + "graph.txt";
        List<List<String>> nodeTypeContent = FileReader.readCSVContent(nodeTypeFile);
        List<List<String>> edgesContent = FileReader.readCSVContent(edgesFile);
        Map<String, String> nodeType = new HashMap<>();
        Map<String, OntologyNode> nodeMap = new HashMap<>();
        Map<String, Set<String>> graph = new HashMap<>();

        for (List<String> types : nodeTypeContent) {
            nodeType.put(types.get(0), types.get(1));
        }
        for (List<String> e : edgesContent) {
            Set<String> val = graph.getOrDefault(e.get(0), new TreeSet<>((a, b) -> {
                return a.toLowerCase().compareTo(b.toLowerCase());
            }));
            val.add(e.get(1));
            graph.put(e.get(0), val);
        }
        int id = 0;
        for (Map.Entry<String, String> entry : nodeType.entrySet()) {
            if (entry.getValue().equals("term")) {
                nodeMap.put(entry.getKey(), new OntologyNode("" + id++, entry.getKey(), "term"));
            } else if (entry.getValue().equals("pattern")) {
                nodeMap.put(entry.getKey(), new OntologyNode("" + id++, entry.getKey(), "pattern"));
            }
        }
        for (Map.Entry<String, Set<String>> entry : graph.entrySet()) {
            for (String v : entry.getValue()) {
                System.out.println(entry.getKey() + " " + entry.getValue());
                nodeMap.get(entry.getKey()).getChildren().add(nodeMap.get(v));
            }
        }
        String res = gson.toJson(nodeMap.get("root"));

        return Response.status(200)
                .entity(res)
                .build();
    }

    @GET
    @Path("/updateOnotology")
    public Response updateOnotology(@QueryParam("edges") String edgestr) {
        System.out.println(edgestr);
        String location = LocationService.getOntologyURL();
        String nodeTypeFile = location + "nodeType.txt";
        String edgesFile = location + "graph.txt";
        List<List<String>> nodeTypeContent = FileReader.readCSVContent(nodeTypeFile);
        List<List<String>> edgesContent = FileReader.readCSVContent(edgesFile);
        Map<String, String> nodeType = new HashMap<>();
        String[] edges = edgestr.split(",");
        Set<String> nodes = new HashSet<>();
        Set<String> toNodes = new HashSet<>();

        List<String> patterns = new ArrayList<>();
        String url = LocationService.getPatternURL();
        File dir = new File(url);

        for (File child : dir.listFiles()) {
            patterns.add(child.getName());
        }
        List<List<String>> newEdges = new ArrayList<>();
        for (String e : edges) {
            String[] val = e.split(":");
            toNodes.add(val[1]);
            nodes.addAll(Arrays.asList(val));
            List<String> row = new ArrayList<>();
            row.add(val[0]);
            row.add(val[1]);
            newEdges.add(row);
        }

        for (List<String> types : nodeTypeContent) {
            nodeType.put(types.get(0), types.get(1));
        }

        for (String node : nodes) {
            if (!nodeType.containsKey(node)) {
                List<String> row = new ArrayList<>();
                row.add(node);
                if (patterns.contains(node)) {
                    row.add("pattern");
                } else {
                    row.add("term");
                }
                nodeTypeContent.add(row);
            }
        }
        Iterator<List<String>> it = edgesContent.iterator();
        while (it.hasNext()) {
            List<String> cur = it.next();
            if (toNodes.contains(cur.get(1))) {
                it.remove();
            }
        }
        Collections.sort(nodeTypeContent, (a, b) -> {
            return a.get(0).toLowerCase().compareTo(b.get(0).toLowerCase());
        });
        edgesContent.addAll(newEdges);
        Collections.sort(edgesContent, (a, b) -> {
            return a.get(0).toLowerCase().compareTo(b.get(0).toLowerCase());
        });

        List<String> typeHeader = new ArrayList<>();
        typeHeader.add("node");
        typeHeader.add("type");
        nodeTypeContent.add(0, typeHeader);
        List<String> edgeHeader = new ArrayList<>();
        edgeHeader.add("from");
        edgeHeader.add("to");
        edgesContent.add(0, edgeHeader);
        FileReader.writeFile(nodeTypeFile, nodeTypeContent);
        FileReader.writeFile(edgesFile, edgesContent);

        return Response.status(200)
                .entity("good")
                .build();
    }

    @GET
    @Path("/getPatternPDG")
    public Response getPatternPDG(@QueryParam("pattern") String pattern) {
        String res = "{}";
        System.out.println(pattern);
        try {
            String url = LocationService.getPatternURL();
            File dir = new File(url);
            for (File child : dir.listFiles()) {
                if (child.getName().equals(pattern)) {
                    PatternGraph graph = (PatternGraph) FileReader.readObject(child.getAbsolutePath());
                    DirectedGraph<PatternVertex, Edge> g = graph.getGraph();
                    List<VisNode> nodes = new ArrayList<>();
                    for (Iterator<PatternVertex> it = g.vertexSet().iterator(); it.hasNext();) {
                        Vertex v = it.next();
                        VisNode cur = new VisNode("" + v.getId(), v.getLabel(), v.getLineNum());
                        nodes.add(cur);
                    }
                    List<VisEdge> edges = new ArrayList<>();
                    for (Iterator<Edge> it = g.edgeSet().iterator(); it.hasNext();) {
                        Edge e = it.next();
                        VisEdge cur = null;
                        if (e.getType() == EdgeType.DATA) {
                            cur = new VisEdge("" + e.getFromVertex().getId(),
                                    "" + e.getToVertex().getId(),
                                    "to",
                                    false
                            );
                        } else {
                            cur = new VisEdge("" + e.getFromVertex().getId(),
                                    "" + e.getToVertex().getId(),
                                    "to",
                                    true
                            );
                        }
                        edges.add(cur);
                    }
                    VisDataSet dataSet = new VisDataSet(nodes, edges);
                    res = gson.toJson(dataSet);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Response.status(200)
                .entity(res)
                .build();
    }

    @POST
    @Path("/addNewPattern")
    @Consumes("application/x-www-form-urlencoded")
    public Response addNewPattern(MultivaluedMap<String, String> formParams) throws Exception {
        String patternStr = formParams.get("pattern").get(0);
        NewPattern newPattern = gson.fromJson(patternStr, NewPattern.class);
        Pattern.getInstance().addPattern(newPattern);

        for (PatternGraph graph : Pattern.getInstance().getPatterns()) {
            System.out.println(graph.getType());
        }
        String name = newPattern.getName();

        String location = LocationService.getOntologyURL();
        String nodeTypeFile = location + "nodeType.txt";
        String edgesFile = location + "graph.txt";
        List<List<String>> nodeTypes = FileReader.readCSV(nodeTypeFile);
        List<List<String>> edges = FileReader.readCSV(edgesFile);

        List<String> newType = new ArrayList<>();
        newType.add(name);
        newType.add("pattern");
        nodeTypes.add(newType);
        List<String> newEdge = new ArrayList<>();
        newEdge.add("root");
        newEdge.add(name);
        edges.add(newEdge);

        FileReader.writeFile(nodeTypeFile, nodeTypes);
        FileReader.writeFile(edgesFile, edges);
        return Response.status(200)
                .entity("Good")
                .build();
    }

    @POST
    @Path("/addNewAssignment")
    @Consumes("application/x-www-form-urlencoded")
    public Response getNewAssignment(MultivaluedMap<String, String> formParams) throws Exception {
        String assignStr = formParams.get("assignment").get(0);
        Assignment assignment = gson.fromJson(assignStr, Assignment.class);
        FileReader.writeFile(LocationService.getAssignmentURL() + assignment.getName().replaceAll("[^a-zA-Z ]", ""), assignStr);

        return Response.status(200)
                .entity("Good")
                .build();
    }

    DirectedGraph<Vertex, Edge> generateNewGraph(DirectedGraph<Vertex, Edge> graph) {
        Map<String, Vertex> mapping = new HashMap<>();
        List<Vertex> reused = new ArrayList<>();
        //entry
        for (Iterator<Vertex> it = graph.vertexSet().iterator(); it.hasNext();) {
            Vertex cur = it.next();
            if (cur.getType() == VertexType.DECL) {
                if (mapping.containsKey(cur.getLabel())) {
                    reused.add(cur);
                } else {
                    mapping.put(cur.getLabel(), cur);
                }
            }
        }

        graph.removeAllVertices(reused);
        
        for (Iterator<Vertex> it = graph.vertexSet().iterator(); it.hasNext();) {
            Vertex cur = it.next();
            if (cur.getType() != VertexType.DECL) {
                String label = cur.getLabel();
                int from = cur.getId();
                for (Map.Entry<String, Vertex> entry : mapping.entrySet()) {
                    String var = entry.getKey();
                    if (label.indexOf(var) != -1) {
                        boolean flag = false;
                        int to = entry.getValue().getId();
                        //edges
                        for (Iterator<Edge> it1 = graph.edgeSet().iterator(); it1.hasNext();) {
                            Edge e = it1.next();
                            int eFrom = e.getFromVertex().getId();
                            int eTo = e.getToVertex().getId();
                            if (from == eFrom && to == eTo) {
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            graph.addEdge(entry.getValue(), cur, new Edge(entry.getValue(), cur, EdgeType.DATA));
                        }
                    }
                }
            }
        }
        
        //remove assign to assign
        List<Edge> removes = new ArrayList<>();
        for (Iterator<Edge> it = graph.edgeSet().iterator(); it.hasNext();) {
             Edge e = it.next();
             if (e.getFromVertex().getType() == VertexType.ASSIGN && e.getToVertex().getType() == VertexType.ASSIGN) {
                 removes.add(e);
             }
        }
        graph.removeAllEdges(removes);
        return graph;
    }

    @POST
    @Path("/getPDG")
    @Consumes("application/x-www-form-urlencoded")
    public Response getPDG(MultivaluedMap<String, String> formParams) throws Exception {
        String code = formParams.get("code").get(0);
        System.out.println("getPDG code: " + code);

        Map<String, DirectedGraph<Vertex, Edge>> graphs = service.generatePDG(code);

        //we update the edges now
//        Map
        for (Map.Entry<String, DirectedGraph<Vertex, Edge>> entry : graphs.entrySet()) {
            String key = entry.getKey();
            DirectedGraph<Vertex, Edge> newValue = generateNewGraph(entry.getValue());
            entry.setValue(newValue);
        }

        MatchingService.printGraph(graphs);
        String res = "";
        for (Map.Entry<String, DirectedGraph<Vertex, Edge>> entry : graphs.entrySet()) {
            List<VisNode> nodes = new ArrayList<>();
            for (Iterator<Vertex> it = entry.getValue().vertexSet().iterator(); it.hasNext();) {
                Vertex v = it.next();
                VisNode cur = new VisNode("" + v.getId(), v.getLabel(), v.getLineNum());
                nodes.add(cur);
            }
            List<VisEdge> edges = new ArrayList<>();
            for (Iterator<Edge> it = entry.getValue().edgeSet().iterator(); it.hasNext();) {
                Edge e = it.next();
                VisEdge cur = null;
                if (e.getType() == EdgeType.DATA) {
                    cur = new VisEdge("" + e.getFromVertex().getId(),
                            "" + e.getToVertex().getId(),
                            "to",
                            false
                    );
                } else {
                    cur = new VisEdge("" + e.getFromVertex().getId(),
                            "" + e.getToVertex().getId(),
                            "to",
                            true
                    );
                }
                edges.add(cur);
            }
            VisDataSet dataSet = new VisDataSet(nodes, edges);
            res = gson.toJson(dataSet);
//            System.out.println(res);
            break;
        }

        return Response.status(200)
                .entity(res)
                .build();
    }

    @POST
    @Path("/getPatterns")
    @Consumes("application/x-www-form-urlencoded")
    public Response getPatterns(MultivaluedMap<String, String> formParams) throws Exception {
        String code = formParams.get("code").get(0);
        System.out.println("code: " + code);

        Map<String, List<Solution>> map = service.generatePatterns(code);
        TreeMap<String, List<Solution>> sortedMap = new TreeMap<>(map);

        String res = gson.toJson(sortedMap);

        return Response.status(200)
                .entity(res)
                .build();
    }

    @GET
    @Path("/writePattern")
    public Response writePattern() throws Exception {

        Pattern.getInstance().write();
        return Response.status(200)
                .entity("good")
                .build();
    }

    @POST
    @Path("/getFeedback")
    @Consumes("application/x-www-form-urlencoded")
    public Response getCPPpost(MultivaluedMap<String, String> formParams) throws Exception {
        String code = formParams.get("code").get(0);
        System.out.println(formParams.get("question"));
        Assignment assignment = gson.fromJson(formParams.get("question").get(0), Assignment.class);
//        System.out.println("getCPPPost code:" + code);
        System.out.println("assignment: " + gson.toJson(assignment));

        NumberFormat nf = NumberFormat.getNumberInstance();
        ((DecimalFormat) nf).applyPattern("00");
//        Map<String, String> patternsToApply = getAssignmentPatterns(assignment), constraintsToApply = getAssignmentConstraints(assignment);
        Map<String, String> patternsToApply = new HashMap<>(), constraintsToApply = new HashMap<>();
        patternsToApply.put(assignment.getFunctionName(), assignment.getPattern());
        constraintsToApply.put(assignment.getFunctionName(), assignment.getConstrains());

        Map<String, Map<Feedback, List<Feedback>>> result = service.match(code, patternsToApply, constraintsToApply, Integer.MAX_VALUE);

        for (PatternWeight pw : assignment.getPatternWeight()) {
            System.out.println(pw.getPatternName());
        }

        int value = (int) (service.computeScore(result, assignment.getPatternWeight()));
        String res = "Process: " + value + "%\n";
        for (String method : result.keySet()) {
            System.out.println("Method: " + method);
            res += "Method: " + method + "\n";
            for (Feedback major : result.get(method).keySet()) {
                System.out.println("\t" + major.getType() + "--" + major.getText());
                res += "\t" + major.getType() + "--" + major.getText() + "\n";
                for (Feedback minor : result.get(method).get(major)) {
                    System.out.println("\t\t" + minor.getType() + "--" + minor.getText());
                    res += "\t\t" + minor.getType() + "--" + minor.getText() + "\n";
                }
            }
        }
        FeedbackObj f = new FeedbackObj(res);
        String r = gson.toJson(f);
        System.out.println(r);
        return Response.status(200)
                .entity(r)
                //                .entity("good")
                .build();

    }

    @GET
    @Path("/getFeedback")
    public Response getCPP(@QueryParam("assignment") String code,
            @QueryParam("assignment") int assignment,
            @QueryParam("solNumber") int solNumber) throws Exception {

        NumberFormat nf = NumberFormat.getNumberInstance();
        ((DecimalFormat) nf).applyPattern("00");
        Map<String, String> patternsToApply = getAssignmentPatterns(assignment), constraintsToApply = getAssignmentConstraints(assignment);

        Map<String, Map<Feedback, List<Feedback>>> result = service.match(code, patternsToApply, constraintsToApply, Integer.MAX_VALUE);

        for (String method : result.keySet()) {
            System.out.println("Method: " + method);

            for (Feedback major : result.get(method).keySet()) {
                System.out.println("\t" + major.getType() + "--" + major.getText());

                for (Feedback minor : result.get(method).get(major)) {
                    System.out.println("\t\t" + minor.getType() + "--" + minor.getText());
                }
            }
        }

        return Response.status(200)
                .entity("I am good")
                .build();

    }

    @GET
    @Path("/getAssignments")
    public Response getAssignments() throws Exception {
        Type listType = new TypeToken<ArrayList<Assignment>>() {
        }.getType();

        List<Assignment> res = AssignmentService.getAllAssignments();
        String str = gson.toJson(res, listType);

        return Response.status(200)
                .entity(str)
                .build();
    }

    @GET
    @Path("/getCPP")
    public Response getCPP(@QueryParam("assignment") int assignment, @QueryParam("solNumber") int solNumber) throws Exception {

        NumberFormat nf = NumberFormat.getNumberInstance();
        ((DecimalFormat) nf).applyPattern("00");
//        int assignment = 1, solNumber = 1;
        String location = "C:/Users/jupiter/Documents/NetBeansProjects/CPPTutor";
        String submissionFile = location + "/assignments" + "/" + getAssignmentFolder(assignment) + "/old/java/" + "sol" + nf.format(solNumber) + ".cpp";
        //read files 
        String code = FileReader.readFile(submissionFile);

        Map<String, String> patternsToApply = getAssignmentPatterns(assignment), constraintsToApply = getAssignmentConstraints(assignment);

        Map<String, Map<Feedback, List<Feedback>>> result = service.match(code, patternsToApply, constraintsToApply, Integer.MAX_VALUE);

        for (String method : result.keySet()) {
            System.out.println("Method: " + method);

            for (Feedback major : result.get(method).keySet()) {
                System.out.println("\t" + major.getType() + "--" + major.getText());

                for (Feedback minor : result.get(method).get(major)) {
                    System.out.println("\t\t" + minor.getType() + "--" + minor.getText());
                }
            }
        }

        return Response.status(200)
                .entity("I am good")
                .build();

    }

    @GET
    @Path("/check")
    public Response checkCPP() throws Exception {

        check(1, 1, "assignment1--TWO_ASSIGNMENTS_LOOP_INDEX,COND_CUMULATIVELY_ADD,~COND_CUMULATIVELY_MULT,C2,C3");

        return Response.status(200)
                .entity("I am good")
                .build();

    }

    private void check(int assignment, int solution, String correct) throws Exception {
        NumberFormat nf = NumberFormat.getNumberInstance();
        ((DecimalFormat) nf).applyPattern("00");
        String location = "C:/Users/jupiter/Documents/NetBeansProjects/CPPTutor";
        String submissionFile = location + "/assignments" + "/" + getAssignmentFolder(assignment) + "/old/java/" + "sol" + nf.format(solution) + ".cpp";
        //read files 
        String code = FileReader.readFile(submissionFile);

        Map<String, String> patternsToApply = getAssignmentPatterns(assignment), constraintsToApply = getAssignmentConstraints(assignment);

        Map<String, Map<Feedback, List<Feedback>>> result = service.match(code, patternsToApply, constraintsToApply, Integer.MAX_VALUE);

        double value = service.computeScore(result);
        System.out.println("score: " + value);

        String[] arrayOfMethods = correct.split("\\|");

        for (String method : arrayOfMethods) {
            String[] array = method.split("--");
            String m = array[0];

            for (String typeOrName : array[1].split(",")) {
                boolean isAlmost = typeOrName.startsWith("~");
                typeOrName = typeOrName.replace("~", "");

                Feedback found = null;

                for (Iterator<Feedback> it = result.get(m).keySet().iterator(); found == null && it.hasNext();) {
                    Feedback f = it.next();

                    PatternFeedback p = null;
                    ConstraintFeedback c = null;

                    if (f instanceof PatternFeedback) {
                        p = (PatternFeedback) f;
                    } else if (f instanceof ConstraintFeedback) {
                        c = (ConstraintFeedback) f;
                    }

                    if (p != null && p.getPatternType().toString().equals(typeOrName) && (isAlmost ? f.getType().equals(FeedbackType.Almost) : f.getType().equals(FeedbackType.Correct))) {
                        found = f;
                    } else if (c != null && c.getConstraint().toString().equals(typeOrName) && (isAlmost ? f.getType().equals(FeedbackType.Almost) : f.getType().equals(FeedbackType.Correct))) {
                        found = f;
                    }
                }

                if (found == null) {
                    System.err.println("Assignment: " + assignment + "; Solution: " + solution + "; Method: " + m + "; Error: " + typeOrName);
                } else {
                    result.get(m).remove(found);
                }
            }

            // The rest must be incorrect.
            for (Feedback f : result.get(m).keySet()) {
                if (!f.getType().equals(FeedbackType.Incorrect)) {
                    PatternFeedback p = null;
                    ConstraintFeedback c = null;
                    String type = null;

                    if (f instanceof PatternFeedback) {
                        p = (PatternFeedback) f;
                        type = p.getPatternType().toString();
                    } else if (f instanceof ConstraintFeedback) {
                        c = (ConstraintFeedback) f;
                        type = c.getConstraint();
                    }

                    System.err.println("Assignment: " + assignment + "; Solution: " + solution + "; Method: " + m + "; Error: " + type + " is correct!");
                }
            }
        }

    }

    public static String getAssignmentFolder(int a) {
        String folder = "";

        if (a == 1) {
            folder = "running";
        }
        if (a == 2) {
            folder = "esc-LAB-3-P1-V1";
        }
        if (a == 3) {
            folder = "esc-LAB-3-P2-V1";
        }
        if (a == 4) {
            folder = "esc-LAB-3-P2-V2";
        }
        if (a == 5) {
            folder = "esc-LAB-3-P3-V1";
        }
        if (a == 6) {
            folder = "esc-LAB-3-P3-V2";
        }
        if (a == 7) {
            folder = "esc-LAB-3-P4-V1";
        }
        if (a == 8) {
            folder = "esc-LAB-3-P4-V2";
        }
        if (a == 9) {
            folder = "mitx-derivatives";
        }
        if (a == 10) {
            folder = "mitx-polynomials";
        }
        if (a == 11) {
            folder = "rit-all-g-medals";
        }
        if (a == 12) {
            folder = "rit-medals-by-ath";
        }

        return folder;
    }

    public static Map<String, String> getAssignmentPatterns(int a) {
        Map<String, String> patternsToApply = new HashMap<>();

        if (a == 1) {
//            patternsToApply.put("main",
//                    "ACCESS_EVEN--" + 1 + ","
//                    + "ACCESS_ODD--" + 1 + ","
//                    + "COND_CUMULATIVELY_ADD--" + 1 + ","
//                    + "COND_CUMULATIVELY_MULT--" + 1 + ","
//                    + "TWO_ASSIGNMENTS_LOOP_INDEX--" + 0 + ","
//                    + "PRINT_CONSOLE--" + 2);
            patternsToApply.put("main",
                    "ACCESS_EVEN--" + 1 + ","
                    + "ACCESS_ODD--" + 1 + ","
                    + "COND_CUMULATIVELY_ADD--" + 1 + ","
                    + "COND_CUMULATIVELY_MULT--" + 1 + ","
                    + "TWO_ASSIGNMENTS_LOOP_INDEX--" + 0);
//                    + "PRINT_CONSOLE--" + 2);
        }

        if (a == 2) {
//			patternsToApply.put("main", 
//				"FIND_NUMBER_POSITIVE_DYNAMIC_INTERVAL--" + 1 + "," +
//				"TWO_ASSIGNMENTS_LOOP_INDEX--" + 0 + "," +
//				"PRINT_CONSOLE--" + 1);
            patternsToApply.put("main", "PRINT_CONSOLE--" + 1);

            patternsToApply.put("factorial",
                    "ONE_LIMIT_INCLUSIVE_LOOP--" + 1 + ","
                    + "CUMULATIVELY_MULT--" + 1 + ","
                    + "TWO_ASSIGNMENTS_LOOP_INDEX--" + 0 + ","
                    + "RETURN--" + 1);
        }

        if (a == 3) {
            patternsToApply.put("main",
                    "FIND_NUMBER_POSITIVE_DYNAMIC_INTERVAL--" + 1 + ","
                    + "TWO_ASSIGNMENTS_LOOP_INDEX--" + 0 + ","
                    + "PRINT_CONSOLE--" + 1);

            patternsToApply.put("fibonacci",
                    "ONE_LIMIT_INCLUSIVE_LOOP--" + 1 + ","
                    + "CUMULATIVELY_ADD--" + 1 + ","
                    + "RETURN--" + 1 + ","
                    + "TWO_ASSIGNMENTS_LOOP_INDEX--" + 0 + ","
                    + "REPEATEDLY_SUBSTRACT_FROM_VARIABLE--" + 1);
        }

        if (a == 4) {
            patternsToApply.put("main",
                    "CUMULATIVELY_ADD--" + 1 + ","
                    + "RETURN--" + 1 + ","
                    + "DECIMAL_DIGIT_EXTRACT--" + 1 + ","
                    + "COPY_VAR--" + 1);
        }

        if (a == 5) {
            patternsToApply.put("main",
                    "CUMULATIVELY_ADD--" + 1 + ","
                    + "RETURN--" + 1 + ","
                    + "DECIMAL_DIGIT_EXTRACT--" + 1 + ","
                    + "COPY_VAR--" + 1 + ","
                    + "ONE_LIMIT_INCLUSIVE_LOOP--" + 1 + ","
                    + "TWO_ASSIGNMENTS_LOOP_INDEX--" + 0 + ","
                    + "NUMBER_OF_DECIMAL_DIGITS--" + 1);
        }

        if (a == 6) {
            patternsToApply.put("main",
                    "FIND_NUMBER_POSITIVE_STATIC_INTERVAL--" + 1 + ","
                    + "TWO_ASSIGNMENTS_LOOP_INDEX--" + 0 + ","
                    + "COND_CUMULATIVELY_ADD--" + 1 + ","
                    + "PRINT_CONSOLE--" + 1);

            patternsToApply.put("factorial",
                    "ONE_LIMIT_INCLUSIVE_LOOP--" + 1 + ","
                    + "CUMULATIVELY_MULT--" + 1 + ","
                    + "TWO_ASSIGNMENTS_LOOP_INDEX--" + 0 + ","
                    + "RETURN--" + 1);
        }

        if (a == 7) {
            patternsToApply.put("main",
                    "CUMULATIVELY_ADD--" + 1 + ","
                    + "RETURN--" + 1 + ","
                    + "DECIMAL_DIGIT_EXTRACT--" + 1 + ","
                    + "COPY_VAR--" + 1 + ","
                    + "ONE_LIMIT_INCLUSIVE_LOOP--" + 1 + ","
                    + "TWO_ASSIGNMENTS_LOOP_INDEX--" + 0 + ","
                    + "NUMBER_OF_DECIMAL_DIGITS--" + 1);
        }

        if (a == 8) {
            patternsToApply.put("main",
                    "FIND_NUMBER_POSITIVE_STATIC_INTERVAL--" + 1 + ","
                    + "TWO_ASSIGNMENTS_LOOP_INDEX--" + 0 + ","
                    + "COND_CUMULATIVELY_ADD--" + 1 + ","
                    + "PRINT_CONSOLE--" + 1);

            patternsToApply.put("fibonacci",
                    "ONE_LIMIT_INCLUSIVE_LOOP--" + 1 + ","
                    + "CUMULATIVELY_ADD--" + 1 + ","
                    + "RETURN--" + 1 + ","
                    + "TWO_ASSIGNMENTS_LOOP_INDEX--" + 0 + ","
                    + "REPEATEDLY_SUBSTRACT_FROM_VARIABLE--" + 1);
        }

        if (a == 9) {
            patternsToApply.put("main",
                    "ARRAY_COMPUTATION--" + 1 + ","
                    + "CONDITIONAL_PRINT_CONSTANT--" + 2 + ","
                    + "TWO_ASSIGNMENTS_LOOP_INDEX--" + 0 + ",");
        }

        if (a == 10) {
            patternsToApply.put("main",
                    "ARRAY_COMPUTATION--" + 1 + ","
                    + "TWO_ASSIGNMENTS_LOOP_INDEX--" + 0 + ","
                    + "CUMULATIVELY_ADD--" + 1 + ","
                    + "PRINT_CONSOLE--" + 1 + ",");
        }

        if (a == 11) {
            patternsToApply.put("main",
                    "ACCESS_POS_0_OUT_5_FILE--" + 1 + ","
                    + "ACCESS_POS_1_OUT_5_FILE--" + 1 + ","
                    + "ACCESS_POS_2_OUT_5_FILE--" + 1 + ","
                    + "ACCESS_POS_3_OUT_5_FILE--" + 1 + ","
                    + "ACCESS_POS_4_OUT_5_FILE--" + 1 + ","
                    + "TWO_ASSIGNMENTS_LOOP_INDEX--" + 0 + ","
                    + "COND_CUMULATIVELY_ADD--" + 1 + ","
                    + "PRINT_CONSOLE--" + 1 + ","
                    + "NO_TWO_ASSIGNED_VARIABLES_LOOP--" + 0 + ",");
        }

        if (a == 12) {
            patternsToApply.put("main",
                    "ACCESS_POS_0_OUT_5_FILE--" + 1 + ","
                    + "ACCESS_POS_1_OUT_5_FILE--" + 1 + ","
                    + "ACCESS_POS_2_OUT_5_FILE--" + 1 + ","
                    + "ACCESS_POS_3_OUT_5_FILE--" + 1 + ","
                    + "ACCESS_POS_4_OUT_5_FILE--" + 1 + ","
                    + "TWO_ASSIGNMENTS_LOOP_INDEX--" + 0 + ","
                    + "COND_CUMULATIVELY_ADD--" + 3 + ","
                    + "PRINT_CONSOLE--" + 3 + ","
                    + "NO_TWO_ASSIGNED_VARIABLES_LOOP--" + 0 + ",");
        }

        return patternsToApply;
    }

    public static Map<String, String> getAssignmentConstraints(int a) {
        Map<String, String> constraintsToApply = new HashMap<>();

        if (a == 1) {
            //c1 node equal <==>, c2 node contain >>>  Edge existence ->
//            constraintsToApply.put("main",
//                    "C1 ACCESS_EVEN.6<=>COND_CUMULATIVELY_MULT.2|||Variable :c[] is[ not] accessing even positions in :s[array]||||||"
//                    + "C2 ACCESS_ODD.6<=>COND_CUMULATIVELY_ADD.2|||Variable :c[] is[ not] accessing odd positions in :s[array]||||||"
//                    + "C3 PRINT_CONSOLE.1<=>COND_CUMULATIVELY_ADD.2|||Variable :x[] is[ not] cumulatively added and printed to console||||||"
//                    + "C4 PRINT_CONSOLE.1<=>COND_CUMULATIVELY_MULT.2|||Variable :x[] is[ not] cumulatively multiplied and printed to console||||||");
            constraintsToApply.put("main",
                    "C1 ACCESS_EVEN.6<=>COND_CUMULATIVELY_MULT.2|||Variable :c[] is[ not] accessing even positions in :s[array]||||||"
                    + "C2 ACCESS_ODD.6<=>COND_CUMULATIVELY_ADD.2|||Variable :c[] is[ not] accessing odd positions in :s[array]||||||");
        }

        if (a == 2) {
//			constraintsToApply.put("main", 
//				"C1 FIND_NUMBER_POSITIVE_DYNAMIC_INTERVAL.2<=>PRINT_CONSOLE.1|||Variable :x[] is[ not] printed to console||||||" +
//				"C2 FIND_NUMBER_POSITIVE_DYNAMIC_INTERVAL.4>>>.*:methodParameter|||The limit of the loop is[ not] :methodParameter[the parameter]||||||" +
//				"C3 FIND_NUMBER_POSITIVE_DYNAMIC_INTERVAL.5>>>:low=factorial\\(:x\\)|||Variable :low[low limit] is[ not] computed by calling factorial(:x[parameter])||||||" +
//				"C4 FIND_NUMBER_POSITIVE_DYNAMIC_INTERVAL.6>>>:high=factorial\\(:x\\+1\\)|||Variable :high[high limit] is[ not] computed by calling factorial(:x[parameter] + 1)||||||" +
//				"C5 FIND_NUMBER_POSITIVE_DYNAMIC_INTERVAL.7>>>:low\\<=:methodParameter\\&\\&:methodParameter\\<:high|||Parameter :methodParameter[] is[ not] between :low[low limit] and :high[high limit]||||||");

            constraintsToApply.put("main", "C1 FIND_NUMBER_POSITIVE_DYNAMIC_INTERVAL.2<=>PRINT_CONSOLE.1|||Variable :x[] is[ not] printed to console||||||");
            constraintsToApply.put("factorial",
                    "C1 ONE_LIMIT_INCLUSIVE_LOOP.3>>>.*:methodParameter|||The limit of the loop is[ not] :methodParameter[the parameter]||||||"
                    + "C2 CUMULATIVELY_MULT.3<=>ONE_LIMIT_INCLUSIVE_LOOP.3|||Variable :c[] is[ not] cumulatively multiplied from one to :x[limit]||||||"
                    + "C3 CUMULATIVELY_MULT.2>>>:c\\*=ONE_LIMIT_INCLUSIVE_LOOP.:x|||Variable ONE_LIMIT_INCLUSIVE_LOOP.:x[] is[ not] used to cumulatively multiplied :c[another variable]||||||"
                    + "C4 RETURN.1<=>CUMULATIVELY_MULT.2|||Variable :c[] is[ not] cumulatively multiplied and returned||||||");
        }

        if (a == 3) {
            constraintsToApply.put("main",
                    "C1 FIND_NUMBER_POSITIVE_DYNAMIC_INTERVAL.2<=>PRINT_CONSOLE.1|||Variable :x[] is[ not] printed to console||||||"
                    + "C2 FIND_NUMBER_POSITIVE_DYNAMIC_INTERVAL.4>>>.*:methodParameter|||The limit of the loop is[ not] :methodParameter[the parameter]||||||"
                    + "C3 FIND_NUMBER_POSITIVE_DYNAMIC_INTERVAL.5>>>:low=fibonacci\\(:x\\)|||Variable :low[low limit] is[ not] computed by calling fibonacci(:x[variable])||||||"
                    + "C4 FIND_NUMBER_POSITIVE_DYNAMIC_INTERVAL.6>>>:high=fibonacci\\(:x\\+1\\)|||Variable :high[high limit] is[ not] computed by calling fibonacci(:x[variable] + 1)||||||"
                    + "C5 FIND_NUMBER_POSITIVE_DYNAMIC_INTERVAL.7>>>:low\\<=:methodParameter\\&\\&:methodParameter\\<:high|||Parameter :methodParameter[] is[ not] between :low[low limit] and :high[high limit]||||||");

            constraintsToApply.put("fibonacci",
                    "C1 ONE_LIMIT_INCLUSIVE_LOOP.3>>>.*:methodParameter|||The limit of the loop is[ not] :methodParameter[the parameter]||||||"
                    + "C2 CUMULATIVELY_ADD.3<=>ONE_LIMIT_INCLUSIVE_LOOP.3|||Variable :c[] is[ not] cumulatively added from one to :x[limit]||||||"
                    + "C3 REPEATEDLY_SUBSTRACT_FROM_VARIABLE.1>>>:x=1|||Variable :x[] is[ not] starting in one||||||"
                    + "C4 REPEATEDLY_SUBSTRACT_FROM_VARIABLE.2>>>:x=:y-:x|||Variable :x[] is[ not] re-assigned||||||"
                    + "C5 REPEATEDLY_SUBSTRACT_FROM_VARIABLE.2>>>:x=CUMULATIVELY_ADD.:c-:x|||Variable CUMULATIVELY_ADD.:c[] is[ not] substracted from :x[another variable]||||||"
                    + "C6 CUMULATIVELY_ADD.2>>>:c\\+=REPEATEDLY_SUBSTRACT_FROM_VARIABLE.:x|||Variable REPEATEDLY_SUBSTRACT_FROM_VARIABLE.:x[] is[ not] used to cumulatively add :c[another variable]||||||"
                    + "C7 RETURN.1<=>CUMULATIVELY_ADD.2|||Variable :c[] is[ not] cumulatively added and returned||||||"
                    + "C8 CUMULATIVELY_ADD.2-(DATA)->REPEATEDLY_SUBSTRACT_FROM_VARIABLE.2|||Variable REPEATEDLY_SUBSTRACT_FROM_VARIABLE.:y[] is[ not] substracted before cumulatively added||||||");
        }

        if (a == 4) {
            constraintsToApply.put("main",
                    "C1 CUMULATIVELY_ADD.3>>>COPY_VAR.:x>0|||Variable COPY_VAR.:x[] is[ not] used to control the loop when cumulatively adding :c[another variable]||||||"
                    + "C2 COPY_VAR.1>>>:x=:methodParameter|||Variable :x[] is[ not] initialized to :methodParameter[another variable]||||||"
                    + "C3 CUMULATIVELY_ADD.2>>>:c\\+=DECIMAL_DIGIT_EXTRACT.:d\\*DECIMAL_DIGIT_EXTRACT.:d\\*DECIMAL_DIGIT_EXTRACT.:d|||Variable :c[] is[ not] cumulatively added using DECIMAL_DIGIT_EXTRACT.:d[another variable] cubic||||||"
                    + "C4 RETURN.2>>>CUMULATIVELY_ADD.:c==:methodParameter|||You are[ not] returning CUMULATIVELY_ADD.:c[another variable]==:methodParameter[another variable]||||||"
                    + "C5 DECIMAL_DIGIT_EXTRACT.2-(DATA)->CUMULATIVELY_ADD.2|||Variable CUMULATIVELY_ADD.:c[] is[ not] cumulatively added to DECIMAL_DIGIT_EXTRACT.:d[another variable] modulo 10||||||");
        }

        if (a == 5) {
            constraintsToApply.put("main",
                    "C1 NUMBER_OF_DECIMAL_DIGITS.1>>>(.*):methodParameter(.*)|||Variable NUMBER_OF_DECIMAL_DIGITS.:x[] is[ not] computed using :methodParameter[another variable]||||||"
                    + "C2 COPY_VAR.1>>>:x=:methodParameter|||Variable :x[] is[ not] initialized to :methodParameter[another variable]||||||"
                    + "C3 CUMULATIVELY_ADD.2>>>:c\\+=DECIMAL_DIGIT_EXTRACT.:d\\*Math\\.pow\\(10,NUMBER_OF_DECIMAL_DIGITS.:x\\-ONE_LIMIT_INCLUSIVE_LOOP.:x\\)|||Variable :c[] is[ not] cumulatively added using DECIMAL_DIGIT_EXTRACT.:d[another variable] times 10^(NUMBER_OF_DECIMAL_DIGITS.:x[x] - ONE_LIMIT_INCLUSIVE_LOOP.:x[y])||||||"
                    + "C4 RETURN.2>>>:methodParameter-CUMULATIVELY_ADD.:c|||You are[ not] returning :methodParameter[another variable]-CUMULATIVELY_ADD.:c[another variable]||||||"
                    + "C5 DECIMAL_DIGIT_EXTRACT.2-(DATA)->CUMULATIVELY_ADD.2|||Variable CUMULATIVELY_ADD.:c[] is[ not] cumulatively added to DECIMAL_DIGIT_EXTRACT.:d[another variable] modulo 10||||||"
                    + "C6 ONE_LIMIT_INCLUSIVE_LOOP.3>>>(.*)NUMBER_OF_DECIMAL_DIGITS.:x(.*)|||The loop does[ not] go until NUMBER_OF_DECIMAL_DIGITS.:x[another variable]||||||");
        }

        if (a == 6) {
            constraintsToApply.put("main",
                    "C1 FIND_NUMBER_POSITIVE_STATIC_INTERVAL.8<=>COND_CUMULATIVELY_ADD.2|||Variable :c[] is[ not] cumulatively added when :x[] is in the interval||||||"
                    + "C2 FIND_NUMBER_POSITIVE_STATIC_INTERVAL.5>>>:methodParameter|||The limit of the loop is[ not] :methodParameter[a parameter]||||||"
                    + "C3 FIND_NUMBER_POSITIVE_STATIC_INTERVAL.6>>>:methodParameter|||The limit of the loop is[ not] :methodParameter[a parameter]||||||"
                    + "C4 FIND_NUMBER_POSITIVE_STATIC_INTERVAL.10>>>:y=factorial\\(:x\\)|||Variable :y[] is[ not] computed by calling factorial(:x[])||||||"
                    + "C5 COND_CUMULATIVELY_ADD.2>>>:c\\+=1|||Variable :c[] is[ not] cumulatively added + 1||||||"
                    + "C6 PRINT_CONSOLE.1<=>COND_CUMULATIVELY_ADD.2|||Variable :x[] is[ not] cumulatively added and printed to console||||||");

            constraintsToApply.put("factorial",
                    "C1 ONE_LIMIT_INCLUSIVE_LOOP.3>>>.*:methodParameter|||The limit of the loop is[ not] :methodParameter[the parameter]||||||"
                    + "C2 CUMULATIVELY_MULT.3<=>ONE_LIMIT_INCLUSIVE_LOOP.3|||Variable :c[] is[ not] cumulatively multiplied from one to :x[limit]||||||"
                    + "C3 CUMULATIVELY_MULT.2>>>:c\\*=ONE_LIMIT_INCLUSIVE_LOOP.:x|||Variable ONE_LIMIT_INCLUSIVE_LOOP.:x[] is[ not] used to cumulatively multiplied :c[another variable]||||||"
                    + "C4 RETURN.1<=>CUMULATIVELY_MULT.2|||Variable :c[] is[ not] cumulatively multiplied and returned||||||");
        }

        if (a == 7) {
            constraintsToApply.put("main",
                    "C1 NUMBER_OF_DECIMAL_DIGITS.1>>>(.*):methodParameter(.*)|||Variable NUMBER_OF_DECIMAL_DIGITS.:x[] is[ not] computed using :methodParameter[another variable]||||||"
                    + "C2 COPY_VAR.1>>>:x=:methodParameter|||Variable :x[] is[ not] initialized to :methodParameter[another variable]||||||"
                    + "C3 CUMULATIVELY_ADD.2>>>:c\\+=DECIMAL_DIGIT_EXTRACT.:d\\*Math\\.pow\\(10,NUMBER_OF_DECIMAL_DIGITS.:x\\-ONE_LIMIT_INCLUSIVE_LOOP.:x\\)|||Variable :c[] is[ not] cumulatively added using DECIMAL_DIGIT_EXTRACT.:d[another variable] times 10^(NUMBER_OF_DECIMAL_DIGITS.:x[x] - ONE_LIMIT_INCLUSIVE_LOOP.:x[y])||||||"
                    + "C4 RETURN.2>>>:methodParameter==CUMULATIVELY_ADD.:c|||You are[ not] returning :methodParameter[another variable]==CUMULATIVELY_ADD.:c[another variable]||||||"
                    + "C5 DECIMAL_DIGIT_EXTRACT.2-(DATA)->CUMULATIVELY_ADD.2|||Variable CUMULATIVELY_ADD.:c[] is[ not] cumulatively added to DECIMAL_DIGIT_EXTRACT.:d[another variable] modulo 10||||||"
                    + "C6 ONE_LIMIT_INCLUSIVE_LOOP.3>>>(.*)NUMBER_OF_DECIMAL_DIGITS.:x(.*)|||The loop does[ not] go until NUMBER_OF_DECIMAL_DIGITS.:x[another variable]||||||");
        }

        if (a == 8) {
            constraintsToApply.put("main",
                    "C1 FIND_NUMBER_POSITIVE_STATIC_INTERVAL.8<=>COND_CUMULATIVELY_ADD.2|||Variable :c[] is[ not] cumulatively added when :x[] is in the interval||||||"
                    + "C2 FIND_NUMBER_POSITIVE_STATIC_INTERVAL.5>>>:methodParameter|||The limit of the loop is[ not] :methodParameter[a parameter]||||||"
                    + "C3 FIND_NUMBER_POSITIVE_STATIC_INTERVAL.6>>>:methodParameter|||The limit of the loop is[ not] :methodParameter[a parameter]||||||"
                    + "C4 FIND_NUMBER_POSITIVE_STATIC_INTERVAL.10>>>:y=fibonacci\\(:x\\)|||Variable :y[] is[ not] computed by calling fibonacci(:x[])||||||"
                    + "C5 COND_CUMULATIVELY_ADD.2>>>:c\\+=1|||Variable :c[] is[ not] cumulatively added + 1||||||"
                    + "C6 PRINT_CONSOLE.1<=>COND_CUMULATIVELY_ADD.2|||Variable :x[] is[ not] cumulatively added and printed to console||||||");

            constraintsToApply.put("fibonacci",
                    "C1 ONE_LIMIT_INCLUSIVE_LOOP.3>>>.*:methodParameter|||The limit of the loop is[ not] :methodParameter[the parameter]||||||"
                    + "C2 CUMULATIVELY_ADD.3<=>ONE_LIMIT_INCLUSIVE_LOOP.3|||Variable :c[] is[ not] cumulatively added from one to :x[limit]||||||"
                    + "C3 REPEATEDLY_SUBSTRACT_FROM_VARIABLE.1>>>:x=1|||Variable :x[] is[ not] starting in one||||||"
                    + "C4 REPEATEDLY_SUBSTRACT_FROM_VARIABLE.2>>>:x=:y-:x|||Variable :x[] is[ not] re-assigned||||||"
                    + "C5 REPEATEDLY_SUBSTRACT_FROM_VARIABLE.2>>>:x=CUMULATIVELY_ADD.:c-:x|||Variable CUMULATIVELY_ADD.:c[] is[ not] substracted from :x[another variable]||||||"
                    + "C6 CUMULATIVELY_ADD.2>>>:c\\+=REPEATEDLY_SUBSTRACT_FROM_VARIABLE.:x|||Variable REPEATEDLY_SUBSTRACT_FROM_VARIABLE.:x[] is[ not] used to cumulatively add :c[another variable]||||||"
                    + "C7 RETURN.1<=>CUMULATIVELY_ADD.2|||Variable :c[] is[ not] cumulatively added and returned||||||"
                    + "C8 CUMULATIVELY_ADD.2-(DATA)->REPEATEDLY_SUBSTRACT_FROM_VARIABLE.2|||Variable REPEATEDLY_SUBSTRACT_FROM_VARIABLE.:y[] is[ not] substracted before cumulatively added||||||");
        }

        if (a == 9) {
            constraintsToApply.put("main",
                    "C1 ARRAY_COMPUTATION.6>>>System\\.out\\.print(ln)?\\((.*:s\\[:x\\]\\*:x.*)|(.*:x\\*:s\\[:x\\].*)\\)|||You are[ not] using :x[index]*:s[array](:x[index])||||||"
                    + "C2 ARRAY_COMPUTATION.2>>>:x=1|||Variable :x[] is[ not] initialized to 1||||||"
                    + "C3 CONDITIONAL_PRINT_CONSTANT.1>>>ARRAY_COMPUTATION.:s\\.length==1|||The condition are[ not] considering arrays of length one||||||"
                    + "C4 CONDITIONAL_PRINT_CONSTANT.2>>>System\\.out\\.print(ln)?\\(\\\"0.*\\\"\\)|||You are[ not] printing zero||||||");
        }

        if (a == 10) {
            constraintsToApply.put("main",
                    "C1 PRINT_CONSOLE.1<=>CUMULATIVELY_ADD.2|||Variable :x[] is[ not] cumulatively added and printed to console||||||"
                    + "C2 ARRAY_COMPUTATION.6<=>CUMULATIVELY_ADD.2|||Variable CUMULATIVELY_ADD.:x[] is[ not] cumulatively added using ARRAY_COMPUTATION.:s[array]||||||"
                    + "C3 CUMULATIVELY_ADD.2>>>ARRAY_COMPUTATION.:s\\[ARRAY_COMPUTATION.:x\\]\\*Math\\.pow\\(:methodParameter,ARRAY_COMPUTATION.:x\\)|||You are[ not] cumulatively adding ARRAY_COMPUTATION.:s[array] and pow(:methodParameter[parameter],ARRAY_COMPUTATION.:x[index])||||||"
                    + "C4 ARRAY_COMPUTATION.2>>>:x=0|||Variable :x[] is[ not] initialized to zero||||||");
        }

        if (a == 11) {
            constraintsToApply.put("main",
                    "C1 PRINT_CONSOLE.1<=>COND_CUMULATIVELY_ADD.2|||Variable :x[] is[ not] cumulatively added and printed to console||||||"
                    + "C2 ACCESS_POS_0_OUT_5_FILE.1>>>:s=newjava\\.util\\.Scanner\\(newjava\\.io\\.File\\(\"assignments/rit-all-g-medals/summer_olympics.txt\"\\)\\)|||You are[ not] initializing :s[file scanner] with file summer_olympics.txt||||||"
                    + "C3 ACCESS_POS_1_OUT_5_FILE.1>>>:s=newjava\\.util\\.Scanner\\(newjava\\.io\\.File\\(\"assignments/rit-all-g-medals/summer_olympics.txt\"\\)\\)|||You are[ not] initializing :s[file scanner] with file summer_olympics.txt||||||"
                    + "C4 ACCESS_POS_2_OUT_5_FILE.1>>>:s=newjava\\.util\\.Scanner\\(newjava\\.io\\.File\\(\"assignments/rit-all-g-medals/summer_olympics.txt\"\\)\\)|||You are[ not] initializing :s[file scanner] with file summer_olympics.txt||||||"
                    + "C5 ACCESS_POS_3_OUT_5_FILE.1>>>:s=newjava\\.util\\.Scanner\\(newjava\\.io\\.File\\(\"assignments/rit-all-g-medals/summer_olympics.txt\"\\)\\)|||You are[ not] initializing :s[file scanner] with file summer_olympics.txt||||||"
                    + "C6 ACCESS_POS_4_OUT_5_FILE.1>>>:s=newjava\\.util\\.Scanner\\(newjava\\.io\\.File\\(\"assignments/rit-all-g-medals/summer_olympics.txt\"\\)\\)|||You are[ not] initializing :s[file scanner] with file summer_olympics.txt||||||"
                    + "C7 COND_CUMULATIVELY_ADD.3>>>ACCESS_POS_2_OUT_5_FILE.:x\\%5==4\\&\\&ACCESS_POS_2_OUT_5_FILE.:y==:methodParameter\\&\\&ACCESS_POS_3_OUT_5_FILE.:y==1|||You are[ not] enforcing the condition correctly||||||");
        }

        if (a == 12) {
            constraintsToApply.put("main",
                    "C1 PRINT_CONSOLE.1<=>COND_CUMULATIVELY_ADD.2|||Variable :x[] is[ not] cumulatively added and printed to console||||||"
                    + "C2 ACCESS_POS_0_OUT_5_FILE.1>>>:s=newjava\\.util\\.Scanner\\(newjava\\.io\\.File\\(\"assignments/rit-medals-by-ath/summer_olympics.txt\"\\)\\)|||You are[ not] initializing :s[file scanner] with file summer_olympics.txt||||||"
                    + "C3 ACCESS_POS_1_OUT_5_FILE.1>>>:s=newjava\\.util\\.Scanner\\(newjava\\.io\\.File\\(\"assignments/rit-medals-by-ath/summer_olympics.txt\"\\)\\)|||You are[ not] initializing :s[file scanner] with file summer_olympics.txt||||||"
                    + "C4 ACCESS_POS_2_OUT_5_FILE.1>>>:s=newjava\\.util\\.Scanner\\(newjava\\.io\\.File\\(\"assignments/rit-medals-by-ath/summer_olympics.txt\"\\)\\)|||You are[ not] initializing :s[file scanner] with file summer_olympics.txt||||||"
                    + "C5 ACCESS_POS_3_OUT_5_FILE.1>>>:s=newjava\\.util\\.Scanner\\(newjava\\.io\\.File\\(\"assignments/rit-medals-by-ath/summer_olympics.txt\"\\)\\)|||You are[ not] initializing :s[file scanner] with file summer_olympics.txt||||||"
                    + "C6 ACCESS_POS_4_OUT_5_FILE.1>>>:s=newjava\\.util\\.Scanner\\(newjava\\.io\\.File\\(\"assignments/rit-medals-by-ath/summer_olympics.txt\"\\)\\)|||You are[ not] initializing :s[file scanner] with file summer_olympics.txt||||||"
                    + "C7 COND_CUMULATIVELY_ADD.3>>>"
                    + "ACCESS_POS_2_OUT_5_FILE.:x\\%5==4\\&\\&:methodParameter\\.equals\\(ACCESS_POS_0_OUT_5_FILE.:y\\)\\&\\&:methodParameter\\.equals\\(ACCESS_POS_1_OUT_5_FILE.:y\\)\\&\\&ACCESS_POS_3_OUT_5_FILE.:y==(1|2|3)|||You are[ not] enforcing the condition correctly||||||");
        }

        return constraintsToApply;
    }

}
