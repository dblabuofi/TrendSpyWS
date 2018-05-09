package xin.ui.cpptutor.obj;

import com.google.common.collect.Lists;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import flexjson.JSONSerializer;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import org.jgrapht.DirectedGraph;
import org.jgrapht.EdgeFactory;
import xin.ui.cpptutor.misc.FileReader;
import xin.ui.cpptutor.misc.LocationService;
import xin.ui.cpptutor.newpattern.NewEdge;
import xin.ui.cpptutor.newpattern.NewNode;
import xin.ui.cpptutor.newpattern.NewPattern;
import xin.ui.cpptutor.gson.InterfaceAdapter;

public class Pattern {

    static Gson gson = new GsonBuilder()
            .registerTypeAdapter(DirectedGraph.class, new InterfaceAdapter<DirectedGraph>())
            .registerTypeAdapter(EdgeFactory.class, new InterfaceAdapter<EdgeFactory>())
            .create();
    private static Pattern instance = null;

    private PatternGraph patternAccessingOddPositions
            = new PatternGraph("ACCESS_ODD", "Accessing odd positions in an array", "consider using a loop and a condition, recall that odd is computed by % 2 == 1");
    private PatternGraph patternAccessingEvenPositions
            = new PatternGraph("ACCESS_EVEN", "Accessing even positions in an array", "consider using a loop and a condition, recall that even is computed by % 2 == 0");

    private PatternGraph patternAccessingZeroOutFiveFile
            = new PatternGraph("ACCESS_POS_0_OUT_5_FILE", "Accessing zero out of five positions in a file", "consider using a loop and a condition % 5 == 0");
    private PatternGraph patternAccessingOneOutFiveFile
            = new PatternGraph("ACCESS_POS_1_OUT_5_FILE", "Accessing one out of five positions in a file", "consider using a loop and a condition % 5 == 1");
    private PatternGraph patternAccessingTwoOutFiveFile
            = new PatternGraph("ACCESS_POS_2_OUT_5_FILE", "Accessing two out of five positions in a file", "consider using a loop and a condition % 5 == 2");
    private PatternGraph patternAccessingThreeOutFiveFile
            = new PatternGraph("ACCESS_POS_3_OUT_5_FILE", "Accessing three out of five positions in a file", "consider using a loop and a condition % 5 == 3");
    private PatternGraph patternAccessingFourOutFiveFile
            = new PatternGraph("ACCESS_POS_4_OUT_5_FILE", "Accessing four out of five positions in a file", "consider using a loop and a condition % 5 == 4");

    private PatternGraph patternArrayComputation
            = new PatternGraph("ARRAY_COMPUTATION", "Computation of each position in an array", "consider using a loop and an index variable");

    private PatternGraph patternConditionalCumulativeAdding
            = new PatternGraph("COND_CUMULATIVELY_ADD", "Conditional cumulatively adding", "consider using a loop and a condition, recall to add and assign the same variable");
    private PatternGraph patternConditionalCumulativeMultiplication
            = new PatternGraph("COND_CUMULATIVELY_MULT", "Conditional cumulatively multiplying", "consider using a loop and a condition, recall to multiply and assign the same variable");

    private PatternGraph patternFindingNumberIterativelyInPositiveDynamicInterval
            = new PatternGraph("FIND_NUMBER_POSITIVE_DYNAMIC_INTERVAL", "Finding number iteratively in positive dynamic interval", "consider using a loop incrementing a variable and checking if it is in a given interval using <= and <");
    private PatternGraph patternFindingNumberIterativelyInPositiveStaticInterval
            = new PatternGraph("FIND_NUMBER_POSITIVE_STATIC_INTERVAL", "Finding number iteratively in positive static interval", "consider using a loop incrementing a variable and checking if it is in a given interval using <= and <=");

    private PatternGraph patternCumulativeAdding
            = new PatternGraph("CUMULATIVELY_ADD", "Cumulatively adding", "consider using a loop, recall to add and assign the same variable");
    private PatternGraph patternCumulativeMultiplication
            = new PatternGraph("CUMULATIVELY_MULT", "Cumulatively multiplying", "consider using a loop, recall to multiply and assign the same variable");

    private PatternGraph patternOneToLimitInclusiveLoop
            = new PatternGraph("ONE_LIMIT_INCLUSIVE_LOOP", "Loop from one to limit (inclusive)", "consider using a loop ranging from one to some limit");

    private PatternGraph patternAssignAndPrintingConsole
            = new PatternGraph("PRINT_CONSOLE", "Assign and print to console", "consider working with a variable and printing it to console using System.out.print");
    private PatternGraph patternCopyVariable = new PatternGraph(
            "COPY_VAR", "Copy variable", "consider defining a new variable and initializing to an existing variable");
    private PatternGraph patternDecimalDigitExtract = new PatternGraph(
            "DECIMAL_DIGIT_EXTRACT", "Iteratively extracting decimal digits", "consider defining a loop and extracting the current digit using modulo 10");

    private PatternGraph patternAssignAndReturn = new PatternGraph(
            "RETURN", "Assign and return", "consider working with a variable and returning it using return");

    private PatternGraph patternConditionalPrintConstant = new PatternGraph(
            "CONDITIONAL_PRINT_CONSTANT", "Conditional print to console constant", "consider using a condition to print a constant");

    private PatternGraph patternRepeatedlySubstractFromVar = new PatternGraph(
            "REPEATEDLY_SUBSTRACT_FROM_VARIABLE", "Repeatedly substract from variable", "consider using a variable to substract to other variable using a loop");

    private PatternGraph patternNumberOfDecimalDigits = new PatternGraph(
            "NUMBER_OF_DECIMAL_DIGITS", "Number of decimal digits", "consider using logarithm to compute the number of decimal digits in a given number");

    private PatternGraph patternTwoAssignmentsLoopIndex = new PatternGraph(
            "TWO_ASSIGNMENTS_LOOP_INDEX", "Loop index is only assigned once", "consider using one single updating of the loop index");

    private PatternGraph patternNoTwoAssignedVars = new PatternGraph(
            "NO_TWO_ASSIGNED_VARIABLES_LOOP", "Loop has a variable only assigned once", "consider using one single assignment inside a loop");

    private List<PatternGraph> patterns;

    public List<PatternGraph> getPatterns() {
        return patterns;
    }

    public int numberOfPatterns() {
        return patterns.size();
    }

    public void generatePattern() {
        System.out.println("generate pattern");
        try {
            String url = LocationService.getPatternURL();
            File dir = new File(url);
            if (patterns == null) {
                patterns = new ArrayList<>();
            }
            for (File child : dir.listFiles()) {
                patterns.add((PatternGraph) FileReader.readObject(child.getAbsolutePath()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write() {
        patterns = Lists.newArrayList(
                patternAccessingOddPositions,
                patternAccessingEvenPositions,
                patternConditionalCumulativeAdding,
                patternConditionalCumulativeMultiplication,
                patternFindingNumberIterativelyInPositiveDynamicInterval,
                patternCumulativeAdding,
                patternCumulativeMultiplication,
                patternOneToLimitInclusiveLoop,
                patternAssignAndPrintingConsole,
                patternAssignAndReturn,
                patternRepeatedlySubstractFromVar,
                patternCopyVariable,
                patternDecimalDigitExtract,
                patternNumberOfDecimalDigits,
                patternTwoAssignmentsLoopIndex,
                patternFindingNumberIterativelyInPositiveStaticInterval,
                patternArrayComputation,
                patternConditionalPrintConstant,
                patternAccessingZeroOutFiveFile,
                patternAccessingOneOutFiveFile,
                patternAccessingTwoOutFiveFile,
                patternAccessingThreeOutFiveFile,
                patternAccessingFourOutFiveFile,
                patternNoTwoAssignedVars
        );

        createAccessingOddPositions(patternAccessingOddPositions);
        createAccessingEvenPositions(patternAccessingEvenPositions);

        createAccessingZeroOutFivePositionInFile(patternAccessingZeroOutFiveFile);
        createAccessingOneOutFivePositionInFile(patternAccessingOneOutFiveFile);
        createAccessingTwoOutFivePositionInFile(patternAccessingTwoOutFiveFile);
        createAccessingThreeOutFivePositionInFile(patternAccessingThreeOutFiveFile);
        createAccessingFourOutFivePositionInFile(patternAccessingFourOutFiveFile);

        createArrayComputation(patternArrayComputation);

        createConditionalCumulativeAdding(patternConditionalCumulativeAdding);
        createConditionalCumulativeMultiplication(patternConditionalCumulativeMultiplication);

        createFindingNumberIterativelyInPositiveDynamicInterval(patternFindingNumberIterativelyInPositiveDynamicInterval);
        createFindingNumberIterativelyInPositiveStaticInterval(patternFindingNumberIterativelyInPositiveStaticInterval);

        createCumulativeAdding(patternCumulativeAdding);
        createCumulativeMultiplication(patternCumulativeMultiplication);

        createOneToLimitInclusiveLoop(patternOneToLimitInclusiveLoop);

        createAssignAndPrintToConsole(patternAssignAndPrintingConsole);
        createAssignAndReturn(patternAssignAndReturn);

        createConditionalPrintConstant(patternConditionalPrintConstant);

        createCopyVariable(patternCopyVariable);
        createIterativelyDecimalDigitExtraction(patternDecimalDigitExtract);

        createRepeatedlySubstractFromVar(patternRepeatedlySubstractFromVar);

        createNumberOfDecimalDigits(patternNumberOfDecimalDigits);

        createTwoAssignmentsOfLoopIndex(patternTwoAssignmentsLoopIndex);
        createNoTwoAssignedVars(patternNoTwoAssignedVars);
        JSONSerializer serializer = new JSONSerializer();
        for (PatternGraph pattern : patterns) {
//            FileReader.writeFile(LocationService.getPatternURL() + pattern.getType(), serializer.deepSerialize(pattern));
            FileReader.writeObject(LocationService.getPatternURL() + pattern.getType(), pattern);
        }
    }

    private Pattern() {
        generatePattern();
//        patterns = Lists.newArrayList(
//                patternAccessingOddPositions,
//                patternAccessingEvenPositions,
//                patternConditionalCumulativeAdding,
//                patternConditionalCumulativeMultiplication,
//                patternFindingNumberIterativelyInPositiveDynamicInterval,
//                patternCumulativeAdding,
//                patternCumulativeMultiplication,
//                patternOneToLimitInclusiveLoop,
//                patternAssignAndPrintingConsole,
//                patternAssignAndReturn,
//                patternRepeatedlySubstractFromVar,
//                patternCopyVariable,
//                patternDecimalDigitExtract,
//                patternNumberOfDecimalDigits,
//                patternTwoAssignmentsLoopIndex,
//                patternFindingNumberIterativelyInPositiveStaticInterval,
//                patternArrayComputation,
//                patternConditionalPrintConstant,
//                patternAccessingZeroOutFiveFile,
//                patternAccessingOneOutFiveFile,
//                patternAccessingTwoOutFiveFile,
//                patternAccessingThreeOutFiveFile,
//                patternAccessingFourOutFiveFile,
//                patternNoTwoAssignedVars
//        );
//        
//        createAccessingOddPositions(patternAccessingOddPositions);
//        createAccessingEvenPositions(patternAccessingEvenPositions);
//
//        createAccessingZeroOutFivePositionInFile(patternAccessingZeroOutFiveFile);
//        createAccessingOneOutFivePositionInFile(patternAccessingOneOutFiveFile);
//        createAccessingTwoOutFivePositionInFile(patternAccessingTwoOutFiveFile);
//        createAccessingThreeOutFivePositionInFile(patternAccessingThreeOutFiveFile);
//        createAccessingFourOutFivePositionInFile(patternAccessingFourOutFiveFile);
//
//        createArrayComputation(patternArrayComputation);
//
//        createConditionalCumulativeAdding(patternConditionalCumulativeAdding);
//        createConditionalCumulativeMultiplication(patternConditionalCumulativeMultiplication);
//
//        createFindingNumberIterativelyInPositiveDynamicInterval(patternFindingNumberIterativelyInPositiveDynamicInterval);
//        createFindingNumberIterativelyInPositiveStaticInterval(patternFindingNumberIterativelyInPositiveStaticInterval);
//
//        createCumulativeAdding(patternCumulativeAdding);
//        createCumulativeMultiplication(patternCumulativeMultiplication);
//
//        createOneToLimitInclusiveLoop(patternOneToLimitInclusiveLoop);
//
//        createAssignAndPrintToConsole(patternAssignAndPrintingConsole);
//        createAssignAndReturn(patternAssignAndReturn);
//
//        createConditionalPrintConstant(patternConditionalPrintConstant);
//
//        createCopyVariable(patternCopyVariable);
//        createIterativelyDecimalDigitExtraction(patternDecimalDigitExtract);
//
//        createRepeatedlySubstractFromVar(patternRepeatedlySubstractFromVar);
//
//        createNumberOfDecimalDigits(patternNumberOfDecimalDigits);
//
//        createTwoAssignmentsOfLoopIndex(patternTwoAssignmentsLoopIndex);
//        createNoTwoAssignedVars(patternNoTwoAssignedVars);
//         
//        for (PatternGraph pattern : patterns) {
//            FileReader.writeFile(LocationService.getPatternURL() + pattern.getType(), gson.toJson(pattern));
//        }

    }

    private void createAccessingOddPositions(PatternGraph pattern) {
        PatternVertex one = new PatternVertex(1);
        one.setAssignedVariable(":s");
        one.setType(VertexType.DECL);
        one.setLabel(":s");
//		PatternVertex seven = new PatternVertex(7);
//		one.setAssignedVariable(":s");
//		one.setType(VertexType.DECL);
//		one.setLabel(":s");

        PatternVertex two = new PatternVertex(2);
        two.setAssignedVariable(":x");
        two.setType(VertexType.ASSIGN);
        two.setLabel(":x=0");
        two.setApproxLabel(".*:x.*");
        two.setCorrectFeedback(":x starts in 0");
        two.setIncorrectFeedback(":x should start in 0");

        PatternVertex three = new PatternVertex(3);
        three.setAssignedVariable(":x");
        three.setType(VertexType.ASSIGN);
        three.setLabel(":x\\+\\+");
        three.setApproxLabel(":x.*");
        three.setCorrectFeedback(":x is incremented in 1");
        three.setIncorrectFeedback(":x should be incremented in 1");
        Set<String> s = new HashSet<String>();
        s.add(":x");
        three.getReadingVariables().addAll(s);

        PatternVertex four = new PatternVertex(4);
        four.setType(VertexType.CTRL);
        four.setLabel(":x\\<:s\\.length");
        four.setApproxLabel(":x\\<=:s.length");
        four.setCorrectFeedback(":x does not go beyond :s.length - 1");
        four.setIncorrectFeedback(":x should not go beyond :s.length - 1");
        s.clear();
        s.add(":s");
        s.add(":x");
        four.getReadingVariables().addAll(s);

//        PatternVertex four = new PatternVertex(4);
//		four.setType(VertexType.CTRL);
//		four.setLabel(":x\\<:s\\.length()");
//		four.setApproxLabel(".*");
//		four.setCorrectFeedback(":x does not go beyond :s.length - 1");
//		four.setIncorrectFeedback(":x should not go beyond :s.length - 1");
//		s.clear();
//		s.add(":x");
//		four.getReadingVariables().addAll(s);
        PatternVertex five = new PatternVertex(5);
        five.setType(VertexType.CTRL);
        five.setLabel(":x\\%2==1");
        five.setCorrectFeedback("You are using % 2 == 1 to control :x is odd");
        s.clear();
        s.add(":x");
        five.getReadingVariables().addAll(s);

        PatternVertex six = new PatternVertex(6);
        six.setType(VertexType.ASSIGN);
        six.setLabel(":s\\[:x\\]");
        six.setApproxLabel(".*:s\\[.*:x.*\\].*");
        six.setCorrectFeedback(":x is used exactly to access :s");
        six.setIncorrectFeedback(":x should be used exactly to access :s");
        s.clear();
        s.add(":x");
        s.add(":s");
        six.getReadingVariables().addAll(s);

        pattern.getGraph().addVertex(one);
        pattern.getGraph().addVertex(two);
        pattern.getGraph().addVertex(three);
        pattern.getGraph().addVertex(four);
        pattern.getGraph().addVertex(five);
        pattern.getGraph().addVertex(six);
//		pattern.getGraph().addVertex(seven);

        pattern.getGraph().addEdge(one, four, new Edge(one, four, EdgeType.DATA));
        pattern.getGraph().addEdge(one, six, new Edge(one, six, EdgeType.DATA));
        pattern.getGraph().addEdge(two, three, new Edge(two, three, EdgeType.DATA));
        pattern.getGraph().addEdge(two, four, new Edge(two, four, EdgeType.DATA));
        pattern.getGraph().addEdge(two, five, new Edge(two, five, EdgeType.DATA));
        pattern.getGraph().addEdge(two, six, new Edge(two, six, EdgeType.DATA));
        pattern.getGraph().addEdge(four, three, new Edge(four, three, EdgeType.CTRL));
        pattern.getGraph().addEdge(four, five, new Edge(four, five, EdgeType.CTRL));
        pattern.getGraph().addEdge(five, six, new Edge(five, six, EdgeType.CTRL));
        pattern.getGraph().addEdge(four, four, new Edge(four, four, EdgeType.CTRL));
//		pattern.getGraph().addEdge(seven, four, new Edge(seven, four, EdgeType.DATA));
    }

    private void createAccessingEvenPositions(PatternGraph pattern) {
        createAccessingOddPositions(pattern);

        for (PatternVertex u : pattern.getGraph().vertexSet()) {
            if (u.getLabel().equals(":x\\%2==1")) {
                u.setLabel(":x\\%2==0");
                u.setCorrectFeedback("You are using % 2 == 0 to control :x is even");
            }
        }
    }

    private void createAccessingZeroOutFivePositionInFile(PatternGraph pattern) {
        PatternVertex one = new PatternVertex(1);
        one.setAssignedVariable(":s");
        one.setLabel(":s");

        PatternVertex two = new PatternVertex(2);
        two.setAssignedVariable(":x");
        two.setType(VertexType.ASSIGN);
        two.setLabel(":x=0");
        two.setApproxLabel(".*:x.*");
        two.setCorrectFeedback(":x starts in 0");
        two.setIncorrectFeedback(":x should start in 0");

        PatternVertex three = new PatternVertex(3);
        three.setAssignedVariable(":x");
        three.setType(VertexType.ASSIGN);
        three.setLabel(":x\\+\\+");
        three.setApproxLabel(":x.*");
        three.setCorrectFeedback(":x is incremented in 1");
        three.setIncorrectFeedback(":x should be incremented in 1");
        Set<String> s = new HashSet<String>();
        s.add(":x");
        three.getReadingVariables().addAll(s);

        PatternVertex four = new PatternVertex(4);
        four.setType(VertexType.CTRL);
        four.setLabel(":s\\.hasNext");
        four.setApproxLabel(":s.*");
        four.setCorrectFeedback(":s is controlled by hasNext");
        four.setIncorrectFeedback(":s is not controlled by hasNext");
        s.clear();
        s.add(":s");
        four.getReadingVariables().addAll(s);

        PatternVertex five = new PatternVertex(5);
        five.setType(VertexType.CTRL);
        five.setLabel(":x\\%5==0");
        five.setCorrectFeedback("You are using :x % 5 == 0 to control access to :s");
        s.clear();
        s.add(":x");
        five.getReadingVariables().addAll(s);

        PatternVertex six = new PatternVertex(6);
        six.setAssignedVariable(":y");
        six.setType(VertexType.ASSIGN);
        six.setLabel(":y=:s\\.next.*");
        six.setApproxLabel(".*:s.*");
        six.setCorrectFeedback(":s is accessed using next and assigned to :y");
        six.setIncorrectFeedback(":s is not accessed using next and assigned to :y");
        s.clear();
        s.add(":s");
        six.getReadingVariables().addAll(s);

        PatternVertex seven = new PatternVertex(7);
        seven.setType(VertexType.CALL);
        seven.setLabel(":s\\.close");
        seven.setApproxLabel(".*:s.*");
        seven.setCorrectFeedback(":s is closed");
        seven.setIncorrectFeedback(":s is not closed");
        s.clear();
        s.add(":s");
        seven.getReadingVariables().addAll(s);

        pattern.getGraph().addVertex(one);
        pattern.getGraph().addVertex(two);
        pattern.getGraph().addVertex(three);
        pattern.getGraph().addVertex(four);
        pattern.getGraph().addVertex(five);
        pattern.getGraph().addVertex(six);
        pattern.getGraph().addVertex(seven);

        pattern.getGraph().addEdge(one, four, new Edge(one, four, EdgeType.DATA));
        pattern.getGraph().addEdge(one, six, new Edge(one, six, EdgeType.DATA));
        pattern.getGraph().addEdge(two, three, new Edge(two, three, EdgeType.DATA));
        pattern.getGraph().addEdge(two, five, new Edge(two, five, EdgeType.DATA));
        pattern.getGraph().addEdge(one, seven, new Edge(one, seven, EdgeType.DATA));
        pattern.getGraph().addEdge(four, three, new Edge(four, three, EdgeType.CTRL));
        pattern.getGraph().addEdge(four, five, new Edge(four, five, EdgeType.CTRL));
        pattern.getGraph().addEdge(five, six, new Edge(five, six, EdgeType.CTRL));
        pattern.getGraph().addEdge(four, four, new Edge(four, four, EdgeType.CTRL));
    }

    private void createAccessingOneOutFivePositionInFile(PatternGraph pattern) {
        createAccessingZeroOutFivePositionInFile(pattern);

        for (PatternVertex u : pattern.getGraph().vertexSet()) {
            if (u.getLabel().equals(":x\\%5==0")) {
                u.setLabel(":x\\%5==1");
                u.setCorrectFeedback("You are using :x % 5 == 0 to control access to :s");
            }
        }
    }

    private void createAccessingTwoOutFivePositionInFile(PatternGraph pattern) {
        createAccessingZeroOutFivePositionInFile(pattern);

        for (PatternVertex u : pattern.getGraph().vertexSet()) {
            if (u.getLabel().equals(":x\\%5==0")) {
                u.setLabel(":x\\%5==2");
                u.setCorrectFeedback("You are using :x % 5 == 2 to control access to :s");
            }
        }
    }

    private void createAccessingThreeOutFivePositionInFile(PatternGraph pattern) {
        createAccessingZeroOutFivePositionInFile(pattern);

        for (PatternVertex u : pattern.getGraph().vertexSet()) {
            if (u.getLabel().equals(":x\\%5==0")) {
                u.setLabel(":x\\%5==3");
                u.setCorrectFeedback("You are using :x % 5 == 3 to control access to :s");
            }
        }
    }

    private void createAccessingFourOutFivePositionInFile(PatternGraph pattern) {
        createAccessingZeroOutFivePositionInFile(pattern);

        for (PatternVertex u : pattern.getGraph().vertexSet()) {
            if (u.getLabel().equals(":x\\%5==0")) {
                u.setLabel(":x\\%5==4");
                u.setCorrectFeedback("You are using :x % 5 == 4 to control access to :s");
            }
        }
    }

    private void createArrayComputation(PatternGraph pattern) {
        PatternVertex one = new PatternVertex(1);
        one.setAssignedVariable(":s");
        one.setType(VertexType.DECL);
        one.setLabel(":s");

        PatternVertex two = new PatternVertex(2);
        two.setAssignedVariable(":x");
        two.setType(VertexType.ASSIGN);
        two.setLabel(":x=[0-1]");
        two.setApproxLabel(".*:x.*");
        two.setCorrectFeedback(":x starts in 0 or 1");
        two.setIncorrectFeedback(":x should start in 0 or 1");

        PatternVertex three = new PatternVertex(3);
        three.setAssignedVariable(":x");
        three.setType(VertexType.ASSIGN);
        three.setLabel(":x\\+\\+");
        three.setApproxLabel(":x.*");
        three.setCorrectFeedback(":x is incremented in 1");
        three.setIncorrectFeedback(":x should be incremented in 1");
        Set<String> s = new HashSet<String>();
        s.add(":x");
        three.getReadingVariables().addAll(s);

        PatternVertex four = new PatternVertex(4);
        four.setType(VertexType.CTRL);
        four.setLabel(":x\\<:s\\.length");
        four.setApproxLabel(":x\\<=:s.length");
        four.setCorrectFeedback(":x does not go beyond :s.length - 1");
        four.setIncorrectFeedback(":x should not go beyond :s.length - 1");
        s.clear();
        s.add(":s");
        s.add(":x");
        four.getReadingVariables().addAll(s);

        PatternVertex six = new PatternVertex(6);
        six.setLabel(":s\\[:x\\]");
        six.setApproxLabel(".*:s\\[.*:x.*\\].*");
        six.setCorrectFeedback(":x is used exactly to access :s");
        six.setIncorrectFeedback(":x should be used exactly to access :s");
        s.clear();
        s.add(":x");
        s.add(":s");
        six.getReadingVariables().addAll(s);

        pattern.getGraph().addVertex(one);
        pattern.getGraph().addVertex(two);
        pattern.getGraph().addVertex(three);
        pattern.getGraph().addVertex(four);
        pattern.getGraph().addVertex(six);

        pattern.getGraph().addEdge(one, four, new Edge(one, four, EdgeType.DATA));
        pattern.getGraph().addEdge(one, six, new Edge(one, six, EdgeType.DATA));
        pattern.getGraph().addEdge(two, three, new Edge(two, three, EdgeType.DATA));
        pattern.getGraph().addEdge(two, four, new Edge(two, four, EdgeType.DATA));
        pattern.getGraph().addEdge(two, six, new Edge(two, six, EdgeType.DATA));
        pattern.getGraph().addEdge(four, four, new Edge(four, four, EdgeType.CTRL));
        pattern.getGraph().addEdge(four, three, new Edge(four, three, EdgeType.CTRL));
        pattern.getGraph().addEdge(four, six, new Edge(four, six, EdgeType.CTRL));
    }

    private void createConditionalCumulativeAdding(PatternGraph pattern) {
        PatternVertex one = new PatternVertex(1);
        one.setAssignedVariable(":c");
        one.setType(VertexType.ASSIGN);
        one.setLabel(":c=0");
        one.setApproxLabel(":c=\\d+");
        one.setCorrectFeedback(":c is initialized to 0");
        one.setIncorrectFeedback(":c should be initialized to 0");

        PatternVertex two = new PatternVertex(2);
        two.setAssignedVariable(":c");
        two.setType(VertexType.ASSIGN);
        two.setLabel(":c\\+=");
        two.setCorrectFeedback(":c is cumulatively added");
        Set<String> s = new HashSet<String>();
        s.add(":c");
        two.getReadingVariables().addAll(s);

        PatternVertex three = new PatternVertex(3);
        three.setType(VertexType.CTRL);
        three.setLabel("");
        three.setCorrectFeedback(":c is in a condition");

        PatternVertex four = new PatternVertex(4);
        four.setType(VertexType.CTRL);
        four.setLabel("");
        four.setCorrectFeedback(":c is in a loop");

        pattern.getGraph().addVertex(one);
        pattern.getGraph().addVertex(two);
        pattern.getGraph().addVertex(three);
        pattern.getGraph().addVertex(four);

        pattern.getGraph().addEdge(one, two, new Edge(one, two, EdgeType.DATA));
        pattern.getGraph().addEdge(three, two, new Edge(three, two, EdgeType.CTRL));
        pattern.getGraph().addEdge(four, three, new Edge(four, three, EdgeType.CTRL));
        pattern.getGraph().addEdge(four, four, new Edge(four, four, EdgeType.CTRL));
    }

    private void createConditionalCumulativeMultiplication(PatternGraph pattern) {
        createConditionalCumulativeAdding(pattern);

        for (PatternVertex u : pattern.getGraph().vertexSet()) {
            if (u.getLabel().equals(":c=0")) {
                u.setLabel(":c=1");
                u.setCorrectFeedback(":c is initialized to 1");
                u.setIncorrectFeedback(":c should be initialized to 1");
            } else if (u.getLabel().equals(":c\\+=")) {
                u.setLabel(":c\\*=");
                u.setCorrectFeedback(":c is cumulatively multiplied");
            }
        }
    }

    private void createCumulativeAdding(PatternGraph pattern) {
        PatternVertex one = new PatternVertex(1);
        one.setAssignedVariable(":c");
        one.setType(VertexType.ASSIGN);
        one.setLabel(":c=0");
        one.setApproxLabel(":c=\\d+");
        one.setCorrectFeedback(":c is initialized to 0");
        one.setIncorrectFeedback(":c should be initialized to 0");

        PatternVertex two = new PatternVertex(2);
        two.setAssignedVariable(":c");
        two.setType(VertexType.ASSIGN);
        two.setLabel(":c\\+=");
        two.setCorrectFeedback(":c is cumulatively added");
        Set<String> s = new HashSet<String>();
        s.add(":c");
        two.getReadingVariables().addAll(s);

        PatternVertex four = new PatternVertex(3);
        four.setType(VertexType.CTRL);
        four.setLabel("");
        four.setCorrectFeedback(":c is in a loop");

        pattern.getGraph().addVertex(one);
        pattern.getGraph().addVertex(two);
        pattern.getGraph().addVertex(four);

        pattern.getGraph().addEdge(one, two, new Edge(one, two, EdgeType.DATA));
        pattern.getGraph().addEdge(four, two, new Edge(four, two, EdgeType.CTRL));
        pattern.getGraph().addEdge(four, four, new Edge(four, four, EdgeType.CTRL));
    }

    private void createCumulativeMultiplication(PatternGraph pattern) {
        createCumulativeAdding(pattern);

        for (PatternVertex u : pattern.getGraph().vertexSet()) {
            if (u.getLabel().equals(":c=0")) {
                u.setLabel(":c=1");
                u.setCorrectFeedback(":c is initialized to 1");
                u.setIncorrectFeedback(":c should be initialized to 1");
            } else if (u.getLabel().equals(":c\\+=")) {
                u.setLabel(":c\\*=");
                u.setCorrectFeedback(":c is cumulatively multiplied");
            }
        }
    }

    private void createAssignAndPrintToConsole(PatternGraph pattern) {
        PatternVertex one = new PatternVertex(1);
        one.setAssignedVariable(":x");
        one.setType(VertexType.ASSIGN);
        one.setLabel(":x");
        one.setCorrectFeedback(":x is assigned");
        one.setIncorrectFeedback(":x is not assigned");

        PatternVertex two = new PatternVertex(2);
        two.setType(VertexType.CALL);
//		two.setLabel("System\\.out\\.print(ln)?\\(.*:x.*\\)");
        two.setLabel("cout.*:x.*");
        two.setCorrectFeedback(":x is printed to console");
        two.setIncorrectFeedback(":x is not printed to console");
        Set<String> s = new HashSet<String>();
        s.add(":x");
        two.getReadingVariables().addAll(s);

        pattern.getGraph().addVertex(one);
        pattern.getGraph().addVertex(two);

        pattern.getGraph().addEdge(one, two, new Edge(one, two, EdgeType.DATA));
    }

    private void createConditionalPrintConstant(PatternGraph pattern) {
        PatternVertex one = new PatternVertex(1);
        one.setType(VertexType.CTRL);
        one.setLabel("");
        one.setCorrectFeedback("You are using a condition");

        PatternVertex two = new PatternVertex(2);
        two.setType(VertexType.CALL);
        two.setLabel("System\\.out\\.print(ln)?\\(.*\\)");
        two.setCorrectFeedback("You are printing to console");
        Set<String> s = new HashSet<String>();
        two.getReadingVariables().addAll(s);

        pattern.getGraph().addVertex(one);
        pattern.getGraph().addVertex(two);

        pattern.getGraph().addEdge(one, two, new Edge(one, two, EdgeType.CTRL));

    }

    private void createFindingNumberIterativelyInPositiveDynamicInterval(PatternGraph pattern) {
        PatternVertex two = new PatternVertex(2);
        two.setAssignedVariable(":x");
        two.setType(VertexType.ASSIGN);
        two.setLabel(":x=[0-1]");
        two.setApproxLabel(".*:x.*");
        two.setCorrectFeedback(":x starts in 0 or 1");
        two.setIncorrectFeedback(":x should start in 0 or 1");

        PatternVertex three = new PatternVertex(3);
        three.setAssignedVariable(":x");
        three.setType(VertexType.ASSIGN);
        three.setLabel(":x\\+\\+");
        three.setApproxLabel(":x.*");
        three.setCorrectFeedback(":x is incremented in 1");
        three.setIncorrectFeedback(":x should be incremented in 1");
        Set<String> s = new HashSet<String>();
        s.add(":x");
        three.getReadingVariables().addAll(s);

        PatternVertex four = new PatternVertex(4);
        four.setType(VertexType.CTRL);
        four.setLabel(":x\\<=.*");
        four.setApproxLabel(":x\\<.*");
        four.setCorrectFeedback(":x does not go beyond the limit");
        four.setIncorrectFeedback(":x should not go beyond the limit");
        s.clear();
        s.add(":x");
        four.getReadingVariables().addAll(s);

        PatternVertex five = new PatternVertex(5);
        five.setAssignedVariable(":low");
        five.setType(VertexType.ASSIGN);
        five.setLabel(":low=.*");
        five.setCorrectFeedback(":low stores the initial number of the interval");
        five.setIncorrectFeedback(":low should store the initial number of the interval");

        PatternVertex six = new PatternVertex(6);
        six.setAssignedVariable(":high");
        six.setType(VertexType.ASSIGN);
        six.setLabel(":high=.*");
        six.setCorrectFeedback(":high stores the initial number of the interval");
        six.setIncorrectFeedback(":high should store the initial number of the interval");

        PatternVertex seven = new PatternVertex(7);
        seven.setType(VertexType.CTRL);
        seven.setLabel(":low\\<=:y\\&\\&:y\\<:high");
        seven.setApproxLabel(":low(\\<|\\>)=?:y\\&\\&:y(\\<|\\>)=?:high");
        seven.setCorrectFeedback(":y is in the interval [:low, :high)");
        seven.setIncorrectFeedback(":y must be in the interval [:low, :high)");
        s.clear();
        s.add(":y");
        s.add(":low");
        s.add(":high");
        seven.getReadingVariables().addAll(s);

        PatternVertex eight = new PatternVertex(8);
        eight.setLabel(".*");

        PatternVertex nine = new PatternVertex(9);
        nine.setType(VertexType.BREAK);
        nine.setLabel("");
        nine.setCorrectFeedback("You are stopping the loop");
        nine.setIncorrectFeedback("You are not stopping the loop");

        pattern.getGraph().addVertex(two);
        pattern.getGraph().addVertex(three);
        pattern.getGraph().addVertex(four);
        pattern.getGraph().addVertex(five);
        pattern.getGraph().addVertex(six);
        pattern.getGraph().addVertex(seven);
        pattern.getGraph().addVertex(eight);
        pattern.getGraph().addVertex(nine);

        pattern.getGraph().addEdge(two, three, new Edge(two, three, EdgeType.DATA));
        pattern.getGraph().addEdge(two, four, new Edge(two, four, EdgeType.DATA));
        pattern.getGraph().addEdge(four, four, new Edge(four, four, EdgeType.CTRL));
        pattern.getGraph().addEdge(four, five, new Edge(four, five, EdgeType.CTRL));
        pattern.getGraph().addEdge(four, six, new Edge(four, six, EdgeType.CTRL));
        pattern.getGraph().addEdge(four, seven, new Edge(four, seven, EdgeType.CTRL));
        pattern.getGraph().addEdge(seven, eight, new Edge(seven, eight, EdgeType.CTRL));
        pattern.getGraph().addEdge(seven, nine, new Edge(seven, nine, EdgeType.CTRL));
    }

    private void createOneToLimitInclusiveLoop(PatternGraph pattern) {
        PatternVertex two = new PatternVertex(1);
        two.setAssignedVariable(":x");
        two.setType(VertexType.ASSIGN);
        two.setLabel(":x=1");
        two.setApproxLabel(".*:x.*");
        two.setCorrectFeedback(":x starts in 1");
        two.setIncorrectFeedback(":x should start in 1");

        PatternVertex three = new PatternVertex(2);
        three.setAssignedVariable(":x");
        three.setType(VertexType.ASSIGN);
        three.setLabel(":x\\+\\+");
        three.setCorrectFeedback(":x is incremented in 1");
        three.setIncorrectFeedback(":x should be incremented in 1");
        Set<String> s = new HashSet<String>();
        s.add(":x");
        three.getReadingVariables().addAll(s);

        PatternVertex four = new PatternVertex(3);
        four.setType(VertexType.CTRL);
        four.setLabel(":x\\<=.*");
        four.setApproxLabel(":x\\<.*");
        four.setCorrectFeedback(":x includes the limit");
        four.setIncorrectFeedback(":x does not include the limit");
        s.clear();
        s.add(":x");
        four.getReadingVariables().addAll(s);

        pattern.getGraph().addVertex(two);
        pattern.getGraph().addVertex(three);
        pattern.getGraph().addVertex(four);

        pattern.getGraph().addEdge(two, three, new Edge(two, three, EdgeType.DATA));
        pattern.getGraph().addEdge(two, four, new Edge(two, four, EdgeType.DATA));
        pattern.getGraph().addEdge(four, three, new Edge(four, three, EdgeType.CTRL));
        pattern.getGraph().addEdge(four, four, new Edge(four, four, EdgeType.CTRL));
    }

    private void createAssignAndReturn(PatternGraph pattern) {
        PatternVertex one = new PatternVertex(1);
        one.setAssignedVariable(":x");
        one.setType(VertexType.ASSIGN);
        one.setLabel(":x");
        one.setCorrectFeedback(":x is assigned");
        one.setIncorrectFeedback(":x is not assigned");

        PatternVertex two = new PatternVertex(2);
        two.setType(VertexType.RETURN);
        two.setLabel(".*:x.*");
        two.setCorrectFeedback(":x is returned");
        two.setIncorrectFeedback(":x is not returned");
        Set<String> s = new HashSet<String>();
        s.add(":x");
        two.getReadingVariables().addAll(s);

        pattern.getGraph().addVertex(one);
        pattern.getGraph().addVertex(two);

        pattern.getGraph().addEdge(one, two, new Edge(one, two, EdgeType.DATA));
    }

    private void createRepeatedlySubstractFromVar(PatternGraph pattern) {
        PatternVertex one = new PatternVertex(1);
        one.setAssignedVariable(":x");
        one.setType(VertexType.ASSIGN);
        one.setLabel(":x\\=");
        one.setCorrectFeedback(":x is assigned");
        one.setIncorrectFeedback(":x is not assigned");

        PatternVertex two = new PatternVertex(2);
        two.setLabel(":y-:x");
        two.setCorrectFeedback(":x is substracted from :y");
        two.setIncorrectFeedback(":x is not substracted from :y");
        Set<String> s = new HashSet<String>();
        s.add(":x");
        s.add(":y");
        two.getReadingVariables().addAll(s);

        PatternVertex three = new PatternVertex(3);
        three.setType(VertexType.CTRL);
        three.setLabel("");
        three.setCorrectFeedback(":x and :y are in a loop");
        three.setIncorrectFeedback(":x and :y are not in a loop");

        pattern.getGraph().addVertex(one);
        pattern.getGraph().addVertex(two);
        pattern.getGraph().addVertex(three);

        pattern.getGraph().addEdge(one, two, new Edge(one, two, EdgeType.DATA));
        pattern.getGraph().addEdge(three, two, new Edge(three, two, EdgeType.CTRL));
        pattern.getGraph().addEdge(three, three, new Edge(three, three, EdgeType.CTRL));
    }

    private void createCopyVariable(PatternGraph pattern) {
        PatternVertex one = new PatternVertex(1);
        one.setAssignedVariable(":x");
        one.setType(VertexType.ASSIGN);
        one.setLabel("^:x=:y$");
        one.setCorrectFeedback(":x is exclusively assigned to :y");
        one.setIncorrectFeedback(":x is not exclusively assigned to :y");
        Set<String> s = new HashSet<String>();
        s.clear();
        s.add(":y");
        one.getReadingVariables().addAll(s);

        pattern.getGraph().addVertex(one);
    }

    private void createIterativelyDecimalDigitExtraction(PatternGraph pattern) {
        PatternVertex one = new PatternVertex(1);
        one.setAssignedVariable(":n");
        one.setLabel(":n");
        one.setCorrectFeedback(":n is initialized");
        one.setIncorrectFeedback(":n is not initialized");

        PatternVertex two = new PatternVertex(2);
        two.setAssignedVariable(":d");
        two.setType(VertexType.ASSIGN);
        two.setLabel(":d=:n\\%10");
        two.setApproxLabel(":d=:n(.*)");
        two.setCorrectFeedback(":d is calculated using modulo 10");
        two.setIncorrectFeedback(":d is calculated not using modulo 10");
        Set<String> s = new HashSet<String>();
        s.add(":n");
        two.getReadingVariables().addAll(s);

        PatternVertex four = new PatternVertex(3);
        four.setType(VertexType.CTRL);
        four.setLabel(":n>0");
        four.setApproxLabel(".*");
        four.setCorrectFeedback(":n is in a loop strictly greater than zero");
        four.setIncorrectFeedback(":n must be in a loop strictly greater than zero");
        s.clear();
        s.add(":n");
        four.getReadingVariables().addAll(s);

        PatternVertex five = new PatternVertex(4);
        five.setAssignedVariable(":n");
        five.setType(VertexType.ASSIGN);
        five.setLabel(":n\\/=10");
        five.setApproxLabel("(.*):n(.*)");
        five.setCorrectFeedback(":n is updated divided by 10");
        five.setIncorrectFeedback(":n is not updated divided by 10");
        s.clear();
        s.add(":n");
        five.getReadingVariables().addAll(s);

        pattern.getGraph().addVertex(one);
        pattern.getGraph().addVertex(two);
        pattern.getGraph().addVertex(four);
        pattern.getGraph().addVertex(five);

        pattern.getGraph().addEdge(one, two, new Edge(one, two, EdgeType.DATA));
        pattern.getGraph().addEdge(one, four, new Edge(one, four, EdgeType.DATA));
        pattern.getGraph().addEdge(one, five, new Edge(one, five, EdgeType.DATA));
        pattern.getGraph().addEdge(four, two, new Edge(four, two, EdgeType.CTRL));
        pattern.getGraph().addEdge(four, four, new Edge(four, four, EdgeType.CTRL));
        pattern.getGraph().addEdge(four, five, new Edge(four, five, EdgeType.CTRL));
    }

    private void createNumberOfDecimalDigits(PatternGraph pattern) {
        PatternVertex one = new PatternVertex(1);
        one.setAssignedVariable(":x");
        one.setType(VertexType.ASSIGN);
        one.setLabel("^:x=Math\\.floor\\(Math\\.log10\\((.*)\\)\\)\\+1$");
        one.setCorrectFeedback(":x is computed using the floor of the logarithm of the number");
        one.setIncorrectFeedback(":x is not computed using the floor of the logarithm of the number");

        pattern.getGraph().addVertex(one);
    }

    private void createTwoAssignmentsOfLoopIndex(PatternGraph pattern) {
        PatternVertex one = new PatternVertex(1);
        one.setAssignedVariable(":x");
        one.setType(VertexType.ASSIGN);
        one.setLabel(":x=.*");

        PatternVertex two = new PatternVertex(2);
        two.setAssignedVariable(":x");
        two.setType(VertexType.ASSIGN);
        two.setLabel(":x\\+\\+");
        Set<String> s = new HashSet<String>();
        s.add(":x");
        two.getReadingVariables().addAll(s);

        PatternVertex three = new PatternVertex(3);
        three.setAssignedVariable(":x");
        three.setType(VertexType.ASSIGN);
        three.setLabel(":x\\+\\+");
        s.clear();
        s.add(":x");
        three.getReadingVariables().addAll(s);

        PatternVertex four = new PatternVertex(4);
        four.setType(VertexType.CTRL);
        four.setLabel(".*:x.*");
        s.clear();
        s.add(":x");
        four.getReadingVariables().addAll(s);

        pattern.getGraph().addVertex(one);
        pattern.getGraph().addVertex(two);
        pattern.getGraph().addVertex(three);
        pattern.getGraph().addVertex(four);

        pattern.getGraph().addEdge(one, two, new Edge(one, two, EdgeType.DATA));
        pattern.getGraph().addEdge(one, four, new Edge(one, four, EdgeType.DATA));
        pattern.getGraph().addEdge(two, three, new Edge(two, three, EdgeType.DATA));
        pattern.getGraph().addEdge(four, three, new Edge(four, three, EdgeType.CTRL));
        pattern.getGraph().addEdge(four, two, new Edge(four, two, EdgeType.CTRL));
    }

    private void createNoTwoAssignedVars(PatternGraph pattern) {
        PatternVertex one = new PatternVertex(1);
        one.setType(VertexType.CTRL);
        one.setLabel("");

        PatternVertex two = new PatternVertex(2);
        two.setType(VertexType.CTRL);
        two.setLabel("");

        PatternVertex three = new PatternVertex(3);
        three.setType(VertexType.CTRL);
        three.setLabel("");

        PatternVertex four = new PatternVertex(4);
        four.setAssignedVariable(":x");
        four.setType(VertexType.ASSIGN);
        four.setLabel("");

        PatternVertex five = new PatternVertex(5);
        five.setAssignedVariable(":x");
        five.setType(VertexType.ASSIGN);
        five.setLabel("");

        pattern.getGraph().addVertex(one);
        pattern.getGraph().addVertex(two);
        pattern.getGraph().addVertex(three);
        pattern.getGraph().addVertex(four);
        pattern.getGraph().addVertex(five);

        pattern.getGraph().addEdge(one, one, new Edge(one, one, EdgeType.CTRL));
        pattern.getGraph().addEdge(one, two, new Edge(one, two, EdgeType.CTRL));
        pattern.getGraph().addEdge(one, three, new Edge(one, three, EdgeType.CTRL));
        pattern.getGraph().addEdge(two, four, new Edge(two, four, EdgeType.CTRL));
        pattern.getGraph().addEdge(three, five, new Edge(three, five, EdgeType.CTRL));
    }

    private void createFindingNumberIterativelyInPositiveStaticInterval(PatternGraph pattern) {
        PatternVertex two = new PatternVertex(2);
        two.setAssignedVariable(":x");
        two.setType(VertexType.ASSIGN);
        two.setLabel(":x=[0-1]");
        two.setApproxLabel(".*:x.*");
        two.setCorrectFeedback(":x starts in 0 or 1");
        two.setIncorrectFeedback(":x should start in 0 or 1");

        PatternVertex three = new PatternVertex(3);
        three.setAssignedVariable(":x");
        three.setType(VertexType.ASSIGN);
        three.setLabel(":x\\+\\+");
        three.setApproxLabel(":x.*");
        three.setCorrectFeedback(":x is incremented in 1");
        three.setIncorrectFeedback(":x should be incremented in 1");
        Set<String> s = new HashSet<String>();
        s.add(":x");
        three.getReadingVariables().addAll(s);

        PatternVertex ten = new PatternVertex(10);
        ten.setAssignedVariable(":y");
        ten.setType(VertexType.ASSIGN);
        ten.setCorrectFeedback(":y is assigned");
        ten.setLabel(".*");

        PatternVertex four = new PatternVertex(4);
        four.setType(VertexType.CTRL);
        four.setLabel(":x\\<=.*");
        four.setApproxLabel(":x\\<.*");
        four.setCorrectFeedback(":x does not go beyond the limit");
        four.setIncorrectFeedback(":x should not go beyond the limit");
        s.clear();
        s.add(":x");
        four.getReadingVariables().addAll(s);

        PatternVertex five = new PatternVertex(5);
        five.setAssignedVariable(":low");
        five.setLabel(":low");
        five.setCorrectFeedback(":low stores the initial number of the interval");
        five.setIncorrectFeedback(":low should store the initial number of the interval");

        PatternVertex six = new PatternVertex(6);
        six.setAssignedVariable(":high");
        six.setLabel(":high");
        six.setCorrectFeedback(":high stores the initial number of the interval");
        six.setIncorrectFeedback(":high should store the initial number of the interval");

        PatternVertex seven = new PatternVertex(7);
        seven.setType(VertexType.CTRL);
        seven.setLabel(":low\\<=:y\\&\\&:y\\<=:high");
        seven.setApproxLabel(":low(\\<|\\>)=?:y\\&\\&:y(\\<|\\>)=?:high");
        seven.setCorrectFeedback(":y is in the interval [:low, :high]");
        seven.setIncorrectFeedback(":y must be in the interval [:low, :high]");
        s.clear();
        s.add(":y");
        s.add(":low");
        s.add(":high");
        seven.getReadingVariables().addAll(s);

        PatternVertex eight = new PatternVertex(8);
        eight.setLabel(".*");

        pattern.getGraph().addVertex(two);
        pattern.getGraph().addVertex(three);
        pattern.getGraph().addVertex(four);
        pattern.getGraph().addVertex(five);
        pattern.getGraph().addVertex(six);
        pattern.getGraph().addVertex(seven);
        pattern.getGraph().addVertex(eight);
        pattern.getGraph().addVertex(ten);

        pattern.getGraph().addEdge(two, three, new Edge(two, three, EdgeType.DATA));
        pattern.getGraph().addEdge(two, four, new Edge(two, four, EdgeType.DATA));
        pattern.getGraph().addEdge(six, four, new Edge(six, four, EdgeType.DATA));
        pattern.getGraph().addEdge(five, seven, new Edge(five, seven, EdgeType.DATA));
        pattern.getGraph().addEdge(six, seven, new Edge(six, seven, EdgeType.DATA));
        pattern.getGraph().addEdge(ten, seven, new Edge(ten, seven, EdgeType.DATA));

        pattern.getGraph().addEdge(four, four, new Edge(four, four, EdgeType.CTRL));
        pattern.getGraph().addEdge(four, three, new Edge(four, three, EdgeType.CTRL));
        pattern.getGraph().addEdge(four, seven, new Edge(four, seven, EdgeType.CTRL));
        pattern.getGraph().addEdge(four, ten, new Edge(four, ten, EdgeType.CTRL));
        pattern.getGraph().addEdge(seven, eight, new Edge(seven, eight, EdgeType.CTRL));
    }

    public static Pattern getInstance() {
        if (instance == null) {
            // Thread Safe. Might be costly operation in some case
            synchronized (Pattern.class) {
                if (instance == null) {
                    instance = new Pattern();
                }
            }
        }
        return instance;
    }
    String[] template = {":x", ":y", ":z", ":q", ":w", ":e", ":f", ":g", ":h", ":i"};
    int index;

    public void addPattern(NewPattern newPattern) {
        Iterator<PatternGraph> it = patterns.iterator();
        while (it.hasNext()) {
            PatternGraph graph = it.next();
            if (graph.getType().equals(newPattern.getName())) {
                it.remove();
            }
        }

        PatternGraph pattern
                = new PatternGraph(newPattern.getName(), newPattern.getDescription(), newPattern.getMissingfeedback());

        Map<String, PatternVertex> map = new HashMap<>();

        java.util.regex.Pattern mypattern = java.util.regex.Pattern.compile("[a-zA-Z_$][a-zA-Z_$0-9]*");

        for (NewNode node : newPattern.getNodes()) {
            index = 0;
            PatternVertex tmp = new PatternVertex(Integer.valueOf(node.getId()));
            String label = node.getLabel();
            if (label.contains("==") || label.contains("<=") || label.contains(">=") || label.contains("<") || label.contains(">") || label.contains("!=")) {
                tmp.setType(VertexType.CTRL);
            } else {
                tmp.setType(VertexType.ASSIGN);
            }
            System.out.println("id: " + node.getId());
            System.out.println(node.getLabel());
            Map<String, String> vars = new HashMap<>();
            Matcher mymatcher = mypattern.matcher(node.getLabel());
            while (mymatcher.find()) {
                String find = mymatcher.group(0);
                if (!vars.containsKey(find)) {
                    vars.put(find, template[index++]);
                }
            }
            String replacedlabel = node.getLabel();
            for (Map.Entry<String, String> entry : vars.entrySet()) {
                replacedlabel = replacedlabel.replace(entry.getKey(), entry.getValue());
            }
            tmp.setLabel(replacedlabel);
//            tmp.setApproxLabel(":low(\\<|\\>)=?:y\\&\\&:y(\\<|\\>)=?:high");
            tmp.setCorrectFeedback(node.getCorrect());
            tmp.setIncorrectFeedback(node.getInCorrect());
            pattern.getGraph().addVertex(tmp);
            map.put(node.getId(), tmp);
        }
        for (NewEdge edge : newPattern.getEdges()) {
            if (edge.getDashes().equals("true")) {
                pattern.getGraph().addEdge(map.get(edge.getFrom()), map.get(edge.getTo()), new Edge(map.get(edge.getFrom()), map.get(edge.getTo()), EdgeType.CTRL));
            } else {
                pattern.getGraph().addEdge(map.get(edge.getFrom()), map.get(edge.getTo()), new Edge(map.get(edge.getFrom()), map.get(edge.getTo()), EdgeType.DATA));
            }
        }
        patterns.add(pattern);
        System.out.println(LocationService.getPatternURL() + pattern.getType());
        FileReader.writeObject(LocationService.getPatternURL() + pattern.getType(), pattern);
    }

    public PatternGraph getPattern(String type) {
        PatternGraph pattern = null;

        for (Iterator<PatternGraph> it = patterns.iterator(); pattern == null && it.hasNext();) {
            PatternGraph current = it.next();
            if (current.getType().equals(type)) {
                pattern = current;
            }
        }

        return pattern;
    }

    public enum PatternType {
        ACCESS_ODD,
        ACCESS_EVEN,
        COND_CUMULATIVELY_ADD,
        COND_CUMULATIVELY_MULT,
        PRINT_CONSOLE,
        CUMULATIVELY_ADD,
        CUMULATIVELY_MULT,
        FIND_NUMBER_POSITIVE_DYNAMIC_INTERVAL,
        RETURN,
        ONE_LIMIT_INCLUSIVE_LOOP,
        REPEATEDLY_SUBSTRACT_FROM_VARIABLE,
        COPY_VAR,
        DECIMAL_DIGIT_EXTRACT,
        NUMBER_OF_DECIMAL_DIGITS,
        TWO_ASSIGNMENTS_LOOP_INDEX,
        FIND_NUMBER_POSITIVE_STATIC_INTERVAL,
        ARRAY_COMPUTATION,
        CONDITIONAL_PRINT_CONSTANT,
        ACCESS_POS_0_OUT_5_FILE,
        ACCESS_POS_1_OUT_5_FILE,
        ACCESS_POS_2_OUT_5_FILE,
        ACCESS_POS_3_OUT_5_FILE,
        ACCESS_POS_4_OUT_5_FILE,
        NO_TWO_ASSIGNED_VARIABLES_LOOP
    }

}
