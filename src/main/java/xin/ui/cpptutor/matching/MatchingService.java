/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package xin.ui.cpptutor.matching;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jgrapht.DirectedGraph;
import xin.ui.cpptutor.assignment.PatternWeight;
import xin.ui.cpptutor.obj.Constraint;
import xin.ui.cpptutor.obj.Edge;
import xin.ui.cpptutor.obj.Feedback;
import xin.ui.cpptutor.obj.Feedback.FeedbackType;
import xin.ui.cpptutor.obj.MatchingResult;
import xin.ui.cpptutor.obj.Pattern;
import xin.ui.cpptutor.obj.Pattern.PatternType;
import xin.ui.cpptutor.obj.PatternGraph;
import xin.ui.cpptutor.obj.PatternVertex;
import xin.ui.cpptutor.obj.Solution;
import xin.ui.cpptutor.obj.Vertex;
import xin.ui.cpptutor.obj.VertexType;
import xin.ui.cpptutor.parser.EPDGParser;

/**
 *
 * @author mou1609
 */
public class MatchingService {

    @Inject
    EPDGParser parser;

    public MatchingService() {
    }

    public Map<String, DirectedGraph<Vertex, Edge>> generatePDG(String submission) {
        parser.getGraphs().clear();
        parser.getVerticesByType().clear();
        try {
            parser.parseJavaClass(submission, 2);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("when parsing the submission");
        }

        return parser.getGraphs();
    }

    public Map<String, List<Solution>> generatePatterns(String submission) {
        EPDGParser parser = new EPDGParser();
        try {
            parser.parseJavaClass(submission, 2);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("when parsing the submission");
        }

        Map<String, String> patternsToApply = new HashMap<>();
        String str = "";
        for (PatternGraph graph : Pattern.getInstance().getPatterns()) {
//            if (
////                    graph.getType().equals("ACCESS_ODD") 
////                    graph.getType().equals("ACCESS_POS_4_OUT_5_FILE") ||
////                    graph.getType().equals("ACCESS_EVEN") 
//            ) {
////                    || graph.getType().equals("ARRAY_COMPUTATION")) {

                    str += graph.getType() + "--1,";
//                }
//            }
        }
        str = str.substring(0, str.length() - 1);
        patternsToApply.put("main", str);
        Map<String, Map<String, Integer>> patterns = new HashMap<>();
        try {
            for (String method : patternsToApply.keySet()) {
                String[] selected = patternsToApply.get(method).split(",");

                Map<String, Integer> typesAndTimes = new HashMap<>();
                patterns.put(method, typesAndTimes);

                for (String currentPattern : selected) {
                    String[] typeAndNumber = currentPattern.split("--");

//                    PatternType type = PatternType.valueOf(typeAndNumber[0]);
                    String type = typeAndNumber[0];
                    int times = Integer.valueOf(typeAndNumber[1]);

                    typesAndTimes.put(type, times);
                }
            }
        } catch (Exception oops) {
        }
        Map<String, List<Constraint>> constraints = new HashMap<>();
        Map<String, String> current = new HashMap<>();
        Set<String> submissionMethods = parser.getMethods();
        Set<String> expectedMethods = patternsToApply.keySet();
        Map<String, List<Solution>> res = new HashMap<>();
        matchSubmissions1(0, new ArrayList<>(expectedMethods), submissionMethods, new HashMap<>(), patterns, constraints, parser, res);
        return res;
//        return new HashMap<>();
    }

    private void matchSubmissions1(int i,
            List<String> expectedMethods,
            Set<String> submissionMethods,
            Map<String, String> current,
            Map<String, Map<String, Integer>> patterns,
            Map<String, List<Constraint>> constraints,
            EPDGParser parser,
            Map<String, List<Solution>> res) {
        if (i == expectedMethods.size()) {
            // Match patterns.
            for (String expected : current.keySet()) {
                String fromSubmission = current.get(expected);
                for (String type : patterns.get(expected).keySet()) {
                    int times = patterns.get(expected).get(type);
                    MatchingResult result = Pattern.getInstance().getPattern(type).match(parser.getGraphs().get(fromSubmission), parser.getVerticesByType().get(fromSubmission), times);
                    if (result.getSolutions().size() > 0) {
                        System.out.println("***** " + type + " " + result.getSolutions().size());
                        List<Solution> val = res.getOrDefault(type.toString(), new ArrayList<>());
                        val.addAll(result.getSolutions());
                        res.put(type.toString(), val);
                    }
                }
            }
        } else {
            String currentExpectedMethod = expectedMethods.get(i);
            // Remove those that are already mapped.
            for (String currentSubmissionMethod : Sets.difference(submissionMethods, new HashSet<>(current.values()))) {
                current.put(currentExpectedMethod, currentSubmissionMethod);
                matchSubmissions1(i + 1, expectedMethods, submissionMethods, current, patterns, constraints, parser, res);
                current.remove(currentExpectedMethod);
            }
        }
    }

    public Map<String, Map<Feedback, List<Feedback>>> match(String submission, Map<String, String> patternsToApply,
            Map<String, String> constraintsToApply, int limit) {

        List<String> errors = new ArrayList<>();
        Map<String, Map<Feedback, List<Feedback>>> feedback = new HashMap<>();
        Map<String, String> submissionMethodsMapping = new HashMap<>();

        try {
            parser.parseJavaClass(submission, 2);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("when parsing the submission");
        }
        // Parse the patterns.
        Map<String, Map<String, Integer>> patterns = new HashMap<>();
        try {
            for (String method : patternsToApply.keySet()) {
                String[] selected = patternsToApply.get(method).split(",");

                Map<String, Integer> typesAndTimes = new HashMap<>();
                patterns.put(method, typesAndTimes);

                for (String currentPattern : selected) {
                    String[] typeAndNumber = currentPattern.split("--");

                    int times = Integer.valueOf(typeAndNumber[1]);

                    typesAndTimes.put(typeAndNumber[0], times);
                }
            }
        } catch (Exception oops) {
            errors.add("Something went wrong when parsing the selected patterns.");
        }

        // Parse the constraints.
        Map<String, List<Constraint>> constraints = new HashMap<>();
        try {
            for (String method : constraintsToApply.keySet()) {
                String[] selected = constraintsToApply.get(method).split("\\|\\|\\|\\|\\|\\|");
                constraints.put(method, new ArrayList<>());

                for (String currentConstraint : selected) {
                    Constraint c = Constraint.parse(currentConstraint);
                    if (c != null) {
                        constraints.get(method).add(c);
                    }
                }
            }
        } catch (Exception oops) {
            errors.add("Something went wrong when parsing the selected constraints.");
        }

        Map<String, DirectedGraph<Vertex, Edge>> graphs = parser.getGraphs();
        Map<String, Map<VertexType, List<Vertex>>> verticesByType = parser.getVerticesByType();

//        printGraph(graphs);
//        printVerticesByType(verticesByType);
        System.out.println("error **************");
        if (errors.isEmpty()) {
            Set<String> submissionMethods = parser.getMethods();
            Set<String> expectedMethods = patternsToApply.keySet();

//            for (String method : submissionMethods) {
//                System.out.println(method);
//            }
//            for (String method : expectedMethods) {
//                System.out.println(method);
//            }
            matchSubmissions(0,
                    new ArrayList<>(expectedMethods),
                    submissionMethods,
                    new HashMap<>(),
                    patterns,
                    constraints,
                    parser,
                    feedback,
                    submissionMethodsMapping);

            // Change expected methods to the matched ones.
            Map<String, Map<Feedback, List<Feedback>>> tempFeedback = new HashMap<>();
            for (String expected : feedback.keySet()) {
                tempFeedback.put(submissionMethodsMapping.get(expected), feedback.get(expected));
            }
            feedback = tempFeedback;
        } else {
            for (String err : errors) {
                System.err.println(err);
            }
        }

        return feedback;
    }

    private void matchSubmissions(int i,
            List<String> expectedMethods,
            Set<String> submissionMethods,
            Map<String, String> current,
            Map<String, Map<String, Integer>> patterns,
            Map<String, List<Constraint>> constraints,
            EPDGParser parser,
            Map<String, Map<Feedback, List<Feedback>>> feedback,
            Map<String, String> mappingResult) {
        if (i == expectedMethods.size()) {
            Map<String, Map<Feedback, List<Feedback>>> currentFeedback = new HashMap<>();
            Map<String, Map<String, List<Solution>>> currentSolutions = new HashMap<>();

            // Match patterns.
            for (String expected : current.keySet()) {
                String fromSubmission = current.get(expected);
                currentFeedback.put(expected, new HashMap<>());
                currentSolutions.put(expected, new HashMap<>());

                for (String type : patterns.get(expected).keySet()) {
                    int times = patterns.get(expected).get(type);

                    MatchingResult result = Pattern.getInstance().getPattern(type).match(parser.getGraphs().get(fromSubmission), parser.getVerticesByType().get(fromSubmission), times);

                    if (type.equals(PatternType.COND_CUMULATIVELY_ADD)
                            || type.equals(PatternType.COND_CUMULATIVELY_MULT)
                            || type.equals(PatternType.ACCESS_EVEN)
                            || type.equals(PatternType.ACCESS_ODD)) {
                        System.out.println(type);
                        System.out.println(result);
                    }
                    Map<Feedback, List<Feedback>> fbs = result.getFeedback();
                    for (Feedback fb : fbs.keySet()) {
                        fb.setPattern(type);
                        List<Feedback> val = fbs.get(fb);
                        for (Feedback v : val) {
                            v.setPattern(type);
                        }
                    }
                    currentFeedback.get(expected).putAll(fbs);
                    currentSolutions.get(expected).put(type, result.getSolutions());
                }
            }

            // Match constraints.
            for (String expected : current.keySet()) {
                if (constraints.get(expected) != null) {
                    for (Constraint c : constraints.get(expected)) {
                        // Get all declarations.
                        c.setParameters(parser.getVerticesByType().get(current.get(expected)).get(VertexType.DECL));
                        c.setSolutions(currentSolutions.get(expected));
                        c.setFunctionMapping(current);
                        c.setGraph(parser.getGraphs().get(current.get(expected)));

                        // f will be null when we are not able to check this constraint.
                        for (Feedback f : c.match()) {
                            currentFeedback.get(expected).put(f, new ArrayList<>());
                        }
                    }
                }
            }

            System.out.println("-------current1Feedback-------");

            for (Map.Entry<String, Map<Feedback, List<Feedback>>> entry : currentFeedback.entrySet()) {
                System.out.println("************Entry***************");
                System.out.println(entry.getKey());
                for (Map.Entry<Feedback, List<Feedback>> ss : entry.getValue().entrySet()) {
                    System.out.println(ss.getKey());
                    for (Feedback v : ss.getValue()) {
                        System.out.println(v);
                    }
                }
            }

            // Compare feedback.
            double feedbackScore = computeScore(feedback);
            double currentFeedbackScore = computeScore(currentFeedback);

            if (feedback.isEmpty() || currentFeedbackScore > feedbackScore) {
                feedback.clear();
                feedback.putAll(currentFeedback);

                mappingResult.clear();
                mappingResult.putAll(current);
            }
        } else {
            String currentExpectedMethod = expectedMethods.get(i);
            // Remove those that are already mapped.
            for (String currentSubmissionMethod : Sets.difference(submissionMethods, new HashSet<>(current.values()))) {
                current.put(currentExpectedMethod, currentSubmissionMethod);
                matchSubmissions(i + 1, expectedMethods, submissionMethods, current, patterns, constraints, parser, feedback, mappingResult);
                current.remove(currentExpectedMethod);
            }
        }
    }

    public double computeScore(Map<String, Map<Feedback, List<Feedback>>> feedback) {
        double ret = 0.0;
        int correct = 0;
        int total = 0;

        for (String f : feedback.keySet()) {
            for (Feedback feed : feedback.get(f).keySet()) {
                if (feed.getType().equals(FeedbackType.Correct)) {
                    ret += 1.0;
                    correct += 2;
                } else if (feed.getType().equals(FeedbackType.Almost)) {
                    ret += 0.5;
                    correct += 1;
                }
                total += 2;
            }
        }
        System.out.println(correct);
        System.out.println(total);

        ret = (correct + 0.0) / total;
        return ret;
    }

    public double computeScore(Map<String, Map<Feedback, List<Feedback>>> feedback, List<PatternWeight> patternWeight) {
        double res = 0.0;

        for (String f : feedback.keySet()) {
            for (Feedback feed : feedback.get(f).keySet()) {
                for (PatternWeight w : patternWeight) {
                    if (w.getPatternName().equals(feed.getPattern()) && feed.getType().equals(FeedbackType.Correct)) {
                        res += w.getWeight();
                    }
                }
            }
        }
        return res;
    }

    private String wrapSubmission(String submission) {
        StringBuilder sb = new StringBuilder("public class Submission {\n");
        sb.append(submission);
        sb.append("}");

        return sb.toString();
    }

    public static void printGraph(Map<String, DirectedGraph<Vertex, Edge>> graphs) {
        System.out.println("graphs");

        for (Map.Entry<String, DirectedGraph<Vertex, Edge>> entry : graphs.entrySet()) {
            System.out.println("************Entry***************");
            System.out.println(entry.getKey());
            for (Iterator<Vertex> it = entry.getValue().vertexSet().iterator(); it.hasNext();) {
                System.out.println(it.next());
            }
            System.out.println("*******edge********");
            for (Iterator<Edge> it = entry.getValue().edgeSet().iterator(); it.hasNext();) {
                System.out.println(it.next());
            }
        }
    }

    public static void printVerticesByType(Map<String, Map<VertexType, List<Vertex>>> types) {
        System.out.println("types");

        for (Map.Entry<String, Map<VertexType, List<Vertex>>> entry : types.entrySet()) {
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

}
