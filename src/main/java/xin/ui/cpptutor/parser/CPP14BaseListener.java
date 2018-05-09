// Generated from CPP14.g4 by ANTLR 4.5.1
package xin.ui.cpptutor.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import xin.ui.cpptutor.obj.Edge;
import xin.ui.cpptutor.obj.EdgeType;
import xin.ui.cpptutor.obj.Vertex;
import xin.ui.cpptutor.obj.VertexType;

/**
 * This class provides an empty implementation of {@link CPP14Listener}, which
 * can be extended to create a listener which only needs to handle a subset of
 * the available methods.
 */
public class CPP14BaseListener implements CPP14Listener {

    private String currentMethod = "";

    private Map<String, List<Vertex>> vertices = new HashMap<>();
    private Map<String, List<Edge>> edges = new HashMap<>();

    private Map<String, Vertex> lastAppearanceOfVariables = new HashMap<>();
    private int vertexCounter;
    private Stack<Vertex> controlStack = new Stack<>();

    private List<String> errorMessages = new ArrayList<>();

    private int errorLineOffset;

    private boolean collectExpressionVars;

    // VARIABLES
    private String varDecl;
    private boolean declaredAndNotInitVar;

    //Deal with array
    private boolean isLeftHandSide;

    public CPP14BaseListener(int errorLineOffset) {
        this.errorLineOffset = errorLineOffset;
        vertices = new HashMap<>();
        edges = new HashMap<>();
        lastAppearanceOfVariables = new HashMap<>(); 

        controlStack = new Stack<>();
        errorMessages = new ArrayList<>();

    }

    @Override
    public void enterFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx) {
//        System.out.println("***************FUNCTION DEF*********************");
//        System.out.println(ctx.getText());
//        System.out.println(ctx.getChildCount());
//        System.out.println(ctx.getChild(0).getText());
//        System.out.println(ctx.getChild(1).getText());
//        System.out.println(ctx.getChild(2).getText());
//        System.out.println(ctx.getChild(1).getChild(0).getChild(0).getText());
//        System.out.println(ctx.getChild(1).getChild(0).getChild(0).getChild(0).getText());
        currentMethod = ctx.getChild(1).getChild(0).getChild(0).getChild(0).getText();
        vertices.put(currentMethod, new ArrayList<>());
        edges.put(currentMethod, new ArrayList<>());
        lastAppearanceOfVariables.clear();
        vertexCounter = 0;
        controlStack.clear();
    }

    @Override
    public void exitFunctiondefinition(CPP14Parser.FunctiondefinitionContext ctx) {
    }

    // SYNTAX ERROR
    public void addSyntaxErrorMsg(int line, int charPositionInLine, String msg) {
        errorMessages.add("Syntax error in line " + (line - errorLineOffset) + " and column " + charPositionInLine + ": " + msg);
    }

    // AUX
    private Vertex createVertex(VertexType type, String lbl, String assignedVar, Set<String> refVars, String parent, int lineNum) {
        Vertex v = new Vertex(vertexCounter++);
        v.setAssignedVariable(assignedVar);
        v.setType(type);
        v.setLabel(lbl);
        v.getReadingVariables().addAll(refVars);
        v.setParent(parent);
        v.setLineNum("" + lineNum);
        vertices.get(currentMethod).add(v);
        if (!controlStack.isEmpty()) {
            createEdge(controlStack.peek(), v, EdgeType.CTRL);
        }
        return v;
    }

    private Edge createEdge(Vertex from, Vertex to, EdgeType type) {
        Edge e = new Edge(from, to, type);
        edges.get(currentMethod).add(e);
        return e;
    }

    private void createDataEdges(Vertex v) {

//            System.out.println("******");
//            System.out.println(v.getLabel());
//            System.out.println(expressionVariables);
        for (String var : expressionVariables) {
            createEdge(lastAppearanceOfVariables.get(var), v, EdgeType.DATA);
        }

    }

    //DEAL functions
    private void dealWithDeclaratorid(ParserRuleContext ctx) {
        String var = ctx.getText();
        Vertex v = createVertex(VertexType.DECL, var, var, new HashSet<>(), ctx.getParent().getText(), ctx.getStart().getLine());
        expressionVariables.add(var);
        lastAppearanceOfVariables.put(var, v);
    }

    private void dealWithExitInitdeclarator(ParserRuleContext ctx) {

        String stat = ctx.getText();
        if (!stat.contains("=")) {
            return;
        }
        Vertex v = null;
        if (expressionVariables.isEmpty()) {
            varAssigned = null;
            v = createVertex(VertexType.ASSIGN, ctx.getText(), varAssigned, new HashSet<>(), ctx.getParent().getText(), ctx.getStart().getLine());
        } else if (stat.contains("=")) {
            varAssigned = stat.substring(0, stat.indexOf("="));
            expressionVariables.remove(varAssigned);
            v = createVertex(VertexType.ASSIGN, ctx.getText(), varAssigned, expressionVariables, ctx.getParent().getText(), ctx.getStart().getLine());
        } else {
            varAssigned = (String) expressionVariables.toArray()[expressionVariables.size() - 1];
            v = createVertex(VertexType.ASSIGN, ctx.getText(), varAssigned, new HashSet<>(), ctx.getParent().getText(), ctx.getStart().getLine());
        }
        createDataEdges(v);
        lastAppearanceOfVariables.put(varAssigned, v);
        expressionVariables.clear();
    }

    private void dealWitExitAssignment(ParserRuleContext ctx) {
        String stat = ctx.getText();
        Vertex v = null;
        if (stat.contains("cout") || stat.contains("cin")) {
            v = createVertex(VertexType.CALL, ctx.getText(), null, expressionVariables, ctx.getParent().getText(), ctx.getStart().getLine());
            createDataEdges(v);
        } else if (stat.contains("==") || stat.contains("<=") || stat.contains(">=")
                || stat.contains("<") || stat.contains(">") || stat.contains("!=")
                || stat.contains("==") || stat.contains("<=") || stat.contains(">=")) {
            v = createVertex(VertexType.CTRL, ctx.getText(), null, expressionVariables, ctx.getParent().getText(), ctx.getStart().getLine());
            createDataEdges(v);
        } else if (ctx.getText().contains("=") || ctx.getText().contains("+=") || ctx.getText().contains("-=")
                || ctx.getText().contains("*=") || ctx.getText().contains("/=") || ctx.getText().contains("&=")
                || ctx.getText().contains("|=") || ctx.getText().contains("^=") || ctx.getText().contains("<<=")
                || ctx.getText().contains(">>=") || ctx.getText().contains("++") || ctx.getText().contains("--")) {
//            System.out.println("******");
//            System.out.println(expressionVariables.toArray()[ expressionVariables.size()-1 ]);
            if (stat.contains("++") || stat.contains("--")) {
                varAssigned = stat.replace("++", "");
                varAssigned = varAssigned.replace("--", "");
            } else {
                int index = stat.indexOf("=");
                String b = stat.substring(0, index);
                java.util.regex.Pattern mypattern = java.util.regex.Pattern.compile("[a-zA-Z_$][a-zA-Z_$0-9]*");
                Matcher mymatcher = mypattern.matcher(b);
                while (mymatcher.find()) {
                    varAssigned = mymatcher.group(0);
                }
                expressionVariables.clear();
                String d = stat.substring(index + 1);
                mymatcher = mypattern.matcher(d);
                while (mymatcher.find()) {
                    expressionVariables.add(mymatcher.group(0));
                }
                if (ctx.getText().contains("+=") || ctx.getText().contains("-=")
                        || ctx.getText().contains("*=") || ctx.getText().contains("/=") || ctx.getText().contains("&=")
                        || ctx.getText().contains("|=") || ctx.getText().contains("^=") || ctx.getText().contains("<<=")
                        || ctx.getText().contains(">>=")) {
                    expressionVariables.add(varAssigned);
                }
            }
            v = createVertex(VertexType.ASSIGN, ctx.getText(), varAssigned, expressionVariables, ctx.getParent().getText(), ctx.getStart().getLine());
            createDataEdges(v);
        }
        expressionVariables.clear();
    }

    private Vertex dealWithExitCondition(ParserRuleContext ctx) {
        Vertex v = null;
        if (vertices.get(currentMethod).stream().anyMatch(t -> t.getLabel().equals(ctx.getText()))) {
            v = vertices.get(currentMethod).stream()
                    .filter(t -> t.getLabel().equals(ctx.getText()))
                    .reduce((first, second) -> second)
                    .get();
        } else {//multi conditions 
            v = createVertex(VertexType.CTRL, ctx.getText(), null, expressionVariables, ctx.getParent().getText(), ctx.getStart().getLine());
        }
        controlStack.push(v);
//		expressionVariables.clear();
        return v;
    }

    //Declarations
    @Override
    public void enterDeclaratorid(CPP14Parser.DeclaratoridContext ctx) {
//        System.out.println("enterDeclaratorid");
//        System.out.println(ctx.getText());
        String name = ctx.getText();
        if (name.equals(currentMethod)) {
            return;
        }
        dealWithDeclaratorid(ctx);
    }

    @Override
    public void exitDeclaratorid(CPP14Parser.DeclaratoridContext ctx) {
    }

    @Override
    public void enterBraceorequalinitializer(CPP14Parser.BraceorequalinitializerContext ctx) {
//        System.out.println("enterBraceorequalinitializer");
//        System.out.println(ctx.getText());
    }

    @Override
    public void exitBraceorequalinitializer(CPP14Parser.BraceorequalinitializerContext ctx) {
    }

    //Initializers int odd = 0, event = 0;
    /*
    initdeclarator
    :
	declarator initializer?
    ;
     */
    @Override
    public void enterInitdeclarator(CPP14Parser.InitdeclaratorContext ctx) {
//        System.out.println("enter InitDeclarator");
        expressionVariables.clear();
    }

    @Override
    public void exitInitdeclarator(CPP14Parser.InitdeclaratorContext ctx) {
//        System.out.println("exitInitdeclarator");
//        System.out.println(ctx.getText());
//        System.out.println(expressionVariables);
        dealWithExitInitdeclarator(ctx);
    }

    @Override
    public void enterInitializerclause(CPP14Parser.InitializerclauseContext ctx) {
    }

    @Override
    public void exitInitializerclause(CPP14Parser.InitializerclauseContext ctx) {
    }

    // EXPRESSIONS
    private String varAssigned;
    private Set<String> expressionVariables = new LinkedHashSet<>();

    @Override
    public void enterExpression(CPP14Parser.ExpressionContext ctx) {
//        System.out.println("enterExpression");
//        System.out.println(ctx.getText());
        expressionVariables.clear();

    }

    @Override
    public void exitExpression(CPP14Parser.ExpressionContext ctx) {
//        System.out.println("exitExpression");
////        System.out.println(ctx.getParent().getText());
//        System.out.println(ctx.getText());
        String par = ctx.getParent().getText();//even = a[i]
        int index = par.indexOf(ctx.getText());
        if (index == -1) {
            return;
        }
        if (index == 0 || (index > 0 && par.charAt(index - 1) != '[')) {
            dealWitExitAssignment(ctx);
        }
    }

    @Override
    public void enterSimpledeclaration(CPP14Parser.SimpledeclarationContext ctx) {
    }

    @Override
    public void exitSimpledeclaration(CPP14Parser.SimpledeclarationContext ctx) {
    }

    @Override
    public void enterDeclarator(CPP14Parser.DeclaratorContext ctx) {
    }

    @Override
    public void exitDeclarator(CPP14Parser.DeclaratorContext ctx) {
    }

    @Override
    public void enterNoptrdeclarator(CPP14Parser.NoptrdeclaratorContext ctx) {
    }

    @Override
    public void exitNoptrdeclarator(CPP14Parser.NoptrdeclaratorContext ctx) {
    }
    //Assignments 
    /*
    assignmentexpression
    :
	conditionalexpression
	| logicalorexpression assignmentoperator initializerclause
	| throwexpression
    ;
    logicalorexpression
    :
	logicalandexpression
	| logicalorexpression '||' logicalandexpression
    ;
    
     */
    private boolean isReadingAndWriting;

    @Override
    public void enterInitializer(CPP14Parser.InitializerContext ctx) {
    }

    @Override
    public void exitInitializer(CPP14Parser.InitializerContext ctx) {
    }

    @Override
    public void enterAssignmentoperator(CPP14Parser.AssignmentoperatorContext ctx) {
        isReadingAndWriting = ctx.getText().equals("=") || ctx.getText().equals("+=") || ctx.getText().equals("-=")
                || ctx.getText().equals("*=") || ctx.getText().equals("/=") || ctx.getText().equals("&=")
                || ctx.getText().equals("|=") || ctx.getText().equals("^=") || ctx.getText().equals("<<=")
                || ctx.getText().equals(">>=");
//        System.out.println("Assignmentoperator");
//        System.out.println(isReadingAndWriting);
    }

    @Override
    public void exitAssignmentoperator(CPP14Parser.AssignmentoperatorContext ctx) {
    }

    @Override
    public void enterLiteral(CPP14Parser.LiteralContext ctx) {
//        if (isReadingAndWriting) {
//            varAssigned = ctx.getText();
//        }
    }

    @Override
    public void exitLiteral(CPP14Parser.LiteralContext ctx) {
    }

    @Override
    public void enterAssignmentexpression(CPP14Parser.AssignmentexpressionContext ctx) {
//        System.out.println("enterAssignmentexpression");
//        System.out.println(ctx.getText());
    }

    @Override
    public void exitAssignmentexpression(CPP14Parser.AssignmentexpressionContext ctx) {
//        System.out.println("exitAssignmentexpression");
//        System.out.println(ctx.getText());
//        if (isReadingAndWriting) {
//            varAssigned = ctx.getText();
//            System.out.println("***" + varAssigned);
//        }

    }

    @Override
    public void enterPrimaryexpression(CPP14Parser.PrimaryexpressionContext ctx) {
//        System.out.println("enterPrimaryexpression");
//        System.out.println(ctx.getText());
        String val = ctx.getText();
        if (lastAppearanceOfVariables.containsKey(val)) {
            expressionVariables.add(val);
        }
    }

    @Override
    public void exitPrimaryexpression(CPP14Parser.PrimaryexpressionContext ctx) {

    }

    @Override
    public void enterIterationstatement(CPP14Parser.IterationstatementContext ctx) {
    }

    @Override
    public void exitIterationstatement(CPP14Parser.IterationstatementContext ctx) {
    }

    // FOR
    private Vertex delayedNode;
    private boolean forUpdate;

    @Override
    public void enterCondition(CPP14Parser.ConditionContext ctx) {
    }

    @Override
    public void exitCondition(CPP14Parser.ConditionContext ctx) {
//        System.out.println("exitCondition");
//        System.out.println(ctx.getParent().getText());
//        System.out.println(ctx.getText());

        Vertex v = dealWithExitCondition(ctx);
        if (ctx.getParent().getText().contains("for")
                || ctx.getParent().getText().contains("while")) {
            createEdge(v, v, EdgeType.CTRL);
        }

    }

    @Override
    public void enterSelectionstatement(CPP14Parser.SelectionstatementContext ctx) {

    }

    @Override
    public void exitSelectionstatement(CPP14Parser.SelectionstatementContext ctx) {
//        System.out.println("exitSelectionstatement");

        if (!controlStack.isEmpty()) {
            controlStack.pop();
        }

    }

    //gets
    public Set<String> getMethods() {
        return vertices.keySet();
    }

    public List<Vertex> getVertices(String method) {
        return vertices.get(method);
    }

    public List<Edge> getEdges(String method) {
        return edges.get(method);
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }

    public Map<String, List<Vertex>> getVertices() {
        return vertices;
    }

    public Map<String, List<Edge>> getEdges() {
        return edges;
    }

    /**
     * ***************************************************not not mine
     *
     *
     *
     *
     *
     *
     *
     *
     */
    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterTranslationunit(CPP14Parser.TranslationunitContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitTranslationunit(CPP14Parser.TranslationunitContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterIdexpression(CPP14Parser.IdexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitIdexpression(CPP14Parser.IdexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterUnqualifiedid(CPP14Parser.UnqualifiedidContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitUnqualifiedid(CPP14Parser.UnqualifiedidContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterQualifiedid(CPP14Parser.QualifiedidContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitQualifiedid(CPP14Parser.QualifiedidContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterNestednamespecifier(CPP14Parser.NestednamespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitNestednamespecifier(CPP14Parser.NestednamespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterLambdaexpression(CPP14Parser.LambdaexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitLambdaexpression(CPP14Parser.LambdaexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterLambdaintroducer(CPP14Parser.LambdaintroducerContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitLambdaintroducer(CPP14Parser.LambdaintroducerContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterLambdacapture(CPP14Parser.LambdacaptureContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitLambdacapture(CPP14Parser.LambdacaptureContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterCapturedefault(CPP14Parser.CapturedefaultContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitCapturedefault(CPP14Parser.CapturedefaultContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterCapturelist(CPP14Parser.CapturelistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitCapturelist(CPP14Parser.CapturelistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterCapture(CPP14Parser.CaptureContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitCapture(CPP14Parser.CaptureContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterSimplecapture(CPP14Parser.SimplecaptureContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitSimplecapture(CPP14Parser.SimplecaptureContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterInitcapture(CPP14Parser.InitcaptureContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitInitcapture(CPP14Parser.InitcaptureContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterLambdadeclarator(CPP14Parser.LambdadeclaratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitLambdadeclarator(CPP14Parser.LambdadeclaratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterPostfixexpression(CPP14Parser.PostfixexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitPostfixexpression(CPP14Parser.PostfixexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterExpressionlist(CPP14Parser.ExpressionlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitExpressionlist(CPP14Parser.ExpressionlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterPseudodestructorname(CPP14Parser.PseudodestructornameContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitPseudodestructorname(CPP14Parser.PseudodestructornameContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterUnaryexpression(CPP14Parser.UnaryexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitUnaryexpression(CPP14Parser.UnaryexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterUnaryoperator(CPP14Parser.UnaryoperatorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitUnaryoperator(CPP14Parser.UnaryoperatorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterNewexpression(CPP14Parser.NewexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitNewexpression(CPP14Parser.NewexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterNewplacement(CPP14Parser.NewplacementContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitNewplacement(CPP14Parser.NewplacementContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterNewtypeid(CPP14Parser.NewtypeidContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitNewtypeid(CPP14Parser.NewtypeidContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterNewdeclarator(CPP14Parser.NewdeclaratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitNewdeclarator(CPP14Parser.NewdeclaratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterNoptrnewdeclarator(CPP14Parser.NoptrnewdeclaratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitNoptrnewdeclarator(CPP14Parser.NoptrnewdeclaratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterNewinitializer(CPP14Parser.NewinitializerContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitNewinitializer(CPP14Parser.NewinitializerContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterDeleteexpression(CPP14Parser.DeleteexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitDeleteexpression(CPP14Parser.DeleteexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterNoexceptexpression(CPP14Parser.NoexceptexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitNoexceptexpression(CPP14Parser.NoexceptexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterCastexpression(CPP14Parser.CastexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitCastexpression(CPP14Parser.CastexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterPmexpression(CPP14Parser.PmexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitPmexpression(CPP14Parser.PmexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterMultiplicativeexpression(CPP14Parser.MultiplicativeexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitMultiplicativeexpression(CPP14Parser.MultiplicativeexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterAdditiveexpression(CPP14Parser.AdditiveexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitAdditiveexpression(CPP14Parser.AdditiveexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterShiftexpression(CPP14Parser.ShiftexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitShiftexpression(CPP14Parser.ShiftexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterRelationalexpression(CPP14Parser.RelationalexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitRelationalexpression(CPP14Parser.RelationalexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterEqualityexpression(CPP14Parser.EqualityexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitEqualityexpression(CPP14Parser.EqualityexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterAndexpression(CPP14Parser.AndexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitAndexpression(CPP14Parser.AndexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterExclusiveorexpression(CPP14Parser.ExclusiveorexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitExclusiveorexpression(CPP14Parser.ExclusiveorexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterInclusiveorexpression(CPP14Parser.InclusiveorexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitInclusiveorexpression(CPP14Parser.InclusiveorexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterLogicalandexpression(CPP14Parser.LogicalandexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitLogicalandexpression(CPP14Parser.LogicalandexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterLogicalorexpression(CPP14Parser.LogicalorexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitLogicalorexpression(CPP14Parser.LogicalorexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterConditionalexpression(CPP14Parser.ConditionalexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitConditionalexpression(CPP14Parser.ConditionalexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterConstantexpression(CPP14Parser.ConstantexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitConstantexpression(CPP14Parser.ConstantexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterStatement(CPP14Parser.StatementContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitStatement(CPP14Parser.StatementContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterLabeledstatement(CPP14Parser.LabeledstatementContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitLabeledstatement(CPP14Parser.LabeledstatementContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterExpressionstatement(CPP14Parser.ExpressionstatementContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitExpressionstatement(CPP14Parser.ExpressionstatementContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterCompoundstatement(CPP14Parser.CompoundstatementContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitCompoundstatement(CPP14Parser.CompoundstatementContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterStatementseq(CPP14Parser.StatementseqContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitStatementseq(CPP14Parser.StatementseqContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterForinitstatement(CPP14Parser.ForinitstatementContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitForinitstatement(CPP14Parser.ForinitstatementContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterForrangedeclaration(CPP14Parser.ForrangedeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitForrangedeclaration(CPP14Parser.ForrangedeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterForrangeinitializer(CPP14Parser.ForrangeinitializerContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitForrangeinitializer(CPP14Parser.ForrangeinitializerContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterJumpstatement(CPP14Parser.JumpstatementContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitJumpstatement(CPP14Parser.JumpstatementContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterDeclarationstatement(CPP14Parser.DeclarationstatementContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitDeclarationstatement(CPP14Parser.DeclarationstatementContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterDeclarationseq(CPP14Parser.DeclarationseqContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitDeclarationseq(CPP14Parser.DeclarationseqContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterDeclaration(CPP14Parser.DeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitDeclaration(CPP14Parser.DeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterBlockdeclaration(CPP14Parser.BlockdeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitBlockdeclaration(CPP14Parser.BlockdeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterAliasdeclaration(CPP14Parser.AliasdeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitAliasdeclaration(CPP14Parser.AliasdeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterStatic_assertdeclaration(CPP14Parser.Static_assertdeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitStatic_assertdeclaration(CPP14Parser.Static_assertdeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterEmptydeclaration(CPP14Parser.EmptydeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitEmptydeclaration(CPP14Parser.EmptydeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterAttributedeclaration(CPP14Parser.AttributedeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitAttributedeclaration(CPP14Parser.AttributedeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterDeclspecifier(CPP14Parser.DeclspecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitDeclspecifier(CPP14Parser.DeclspecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterDeclspecifierseq(CPP14Parser.DeclspecifierseqContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitDeclspecifierseq(CPP14Parser.DeclspecifierseqContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterStorageclassspecifier(CPP14Parser.StorageclassspecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitStorageclassspecifier(CPP14Parser.StorageclassspecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterFunctionspecifier(CPP14Parser.FunctionspecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitFunctionspecifier(CPP14Parser.FunctionspecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterTypedefname(CPP14Parser.TypedefnameContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitTypedefname(CPP14Parser.TypedefnameContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterTypespecifier(CPP14Parser.TypespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitTypespecifier(CPP14Parser.TypespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterTrailingtypespecifier(CPP14Parser.TrailingtypespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitTrailingtypespecifier(CPP14Parser.TrailingtypespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterTypespecifierseq(CPP14Parser.TypespecifierseqContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitTypespecifierseq(CPP14Parser.TypespecifierseqContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterTrailingtypespecifierseq(CPP14Parser.TrailingtypespecifierseqContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitTrailingtypespecifierseq(CPP14Parser.TrailingtypespecifierseqContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterSimpletypespecifier(CPP14Parser.SimpletypespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitSimpletypespecifier(CPP14Parser.SimpletypespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterThetypename(CPP14Parser.ThetypenameContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitThetypename(CPP14Parser.ThetypenameContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterDecltypespecifier(CPP14Parser.DecltypespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitDecltypespecifier(CPP14Parser.DecltypespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterElaboratedtypespecifier(CPP14Parser.ElaboratedtypespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitElaboratedtypespecifier(CPP14Parser.ElaboratedtypespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterEnumname(CPP14Parser.EnumnameContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitEnumname(CPP14Parser.EnumnameContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterEnumspecifier(CPP14Parser.EnumspecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitEnumspecifier(CPP14Parser.EnumspecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterEnumhead(CPP14Parser.EnumheadContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitEnumhead(CPP14Parser.EnumheadContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterOpaqueenumdeclaration(CPP14Parser.OpaqueenumdeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitOpaqueenumdeclaration(CPP14Parser.OpaqueenumdeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterEnumkey(CPP14Parser.EnumkeyContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitEnumkey(CPP14Parser.EnumkeyContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterEnumbase(CPP14Parser.EnumbaseContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitEnumbase(CPP14Parser.EnumbaseContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterEnumeratorlist(CPP14Parser.EnumeratorlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitEnumeratorlist(CPP14Parser.EnumeratorlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterEnumeratordefinition(CPP14Parser.EnumeratordefinitionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitEnumeratordefinition(CPP14Parser.EnumeratordefinitionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterEnumerator(CPP14Parser.EnumeratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitEnumerator(CPP14Parser.EnumeratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterNamespacename(CPP14Parser.NamespacenameContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitNamespacename(CPP14Parser.NamespacenameContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterOriginalnamespacename(CPP14Parser.OriginalnamespacenameContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitOriginalnamespacename(CPP14Parser.OriginalnamespacenameContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterNamespacedefinition(CPP14Parser.NamespacedefinitionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitNamespacedefinition(CPP14Parser.NamespacedefinitionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterNamednamespacedefinition(CPP14Parser.NamednamespacedefinitionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitNamednamespacedefinition(CPP14Parser.NamednamespacedefinitionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterOriginalnamespacedefinition(CPP14Parser.OriginalnamespacedefinitionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitOriginalnamespacedefinition(CPP14Parser.OriginalnamespacedefinitionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterExtensionnamespacedefinition(CPP14Parser.ExtensionnamespacedefinitionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitExtensionnamespacedefinition(CPP14Parser.ExtensionnamespacedefinitionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterUnnamednamespacedefinition(CPP14Parser.UnnamednamespacedefinitionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitUnnamednamespacedefinition(CPP14Parser.UnnamednamespacedefinitionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterNamespacebody(CPP14Parser.NamespacebodyContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitNamespacebody(CPP14Parser.NamespacebodyContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterNamespacealias(CPP14Parser.NamespacealiasContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitNamespacealias(CPP14Parser.NamespacealiasContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterNamespacealiasdefinition(CPP14Parser.NamespacealiasdefinitionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitNamespacealiasdefinition(CPP14Parser.NamespacealiasdefinitionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterQualifiednamespacespecifier(CPP14Parser.QualifiednamespacespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitQualifiednamespacespecifier(CPP14Parser.QualifiednamespacespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterUsingdeclaration(CPP14Parser.UsingdeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitUsingdeclaration(CPP14Parser.UsingdeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterUsingdirective(CPP14Parser.UsingdirectiveContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitUsingdirective(CPP14Parser.UsingdirectiveContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterAsmdefinition(CPP14Parser.AsmdefinitionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitAsmdefinition(CPP14Parser.AsmdefinitionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterLinkagespecification(CPP14Parser.LinkagespecificationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitLinkagespecification(CPP14Parser.LinkagespecificationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterAttributespecifierseq(CPP14Parser.AttributespecifierseqContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitAttributespecifierseq(CPP14Parser.AttributespecifierseqContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterAttributespecifier(CPP14Parser.AttributespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitAttributespecifier(CPP14Parser.AttributespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterAlignmentspecifier(CPP14Parser.AlignmentspecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitAlignmentspecifier(CPP14Parser.AlignmentspecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterAttributelist(CPP14Parser.AttributelistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitAttributelist(CPP14Parser.AttributelistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterAttribute(CPP14Parser.AttributeContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitAttribute(CPP14Parser.AttributeContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterAttributetoken(CPP14Parser.AttributetokenContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitAttributetoken(CPP14Parser.AttributetokenContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterAttributescopedtoken(CPP14Parser.AttributescopedtokenContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitAttributescopedtoken(CPP14Parser.AttributescopedtokenContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterAttributenamespace(CPP14Parser.AttributenamespaceContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitAttributenamespace(CPP14Parser.AttributenamespaceContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterAttributeargumentclause(CPP14Parser.AttributeargumentclauseContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitAttributeargumentclause(CPP14Parser.AttributeargumentclauseContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterBalancedtokenseq(CPP14Parser.BalancedtokenseqContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitBalancedtokenseq(CPP14Parser.BalancedtokenseqContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterBalancedtoken(CPP14Parser.BalancedtokenContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitBalancedtoken(CPP14Parser.BalancedtokenContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterInitdeclaratorlist(CPP14Parser.InitdeclaratorlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitInitdeclaratorlist(CPP14Parser.InitdeclaratorlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterPtrdeclarator(CPP14Parser.PtrdeclaratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitPtrdeclarator(CPP14Parser.PtrdeclaratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterParametersandqualifiers(CPP14Parser.ParametersandqualifiersContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitParametersandqualifiers(CPP14Parser.ParametersandqualifiersContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterTrailingreturntype(CPP14Parser.TrailingreturntypeContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitTrailingreturntype(CPP14Parser.TrailingreturntypeContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterPtroperator(CPP14Parser.PtroperatorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitPtroperator(CPP14Parser.PtroperatorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterCvqualifierseq(CPP14Parser.CvqualifierseqContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitCvqualifierseq(CPP14Parser.CvqualifierseqContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterCvqualifier(CPP14Parser.CvqualifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitCvqualifier(CPP14Parser.CvqualifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterRefqualifier(CPP14Parser.RefqualifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitRefqualifier(CPP14Parser.RefqualifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterThetypeid(CPP14Parser.ThetypeidContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitThetypeid(CPP14Parser.ThetypeidContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterAbstractdeclarator(CPP14Parser.AbstractdeclaratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitAbstractdeclarator(CPP14Parser.AbstractdeclaratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterPtrabstractdeclarator(CPP14Parser.PtrabstractdeclaratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitPtrabstractdeclarator(CPP14Parser.PtrabstractdeclaratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterNoptrabstractdeclarator(CPP14Parser.NoptrabstractdeclaratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitNoptrabstractdeclarator(CPP14Parser.NoptrabstractdeclaratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterAbstractpackdeclarator(CPP14Parser.AbstractpackdeclaratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitAbstractpackdeclarator(CPP14Parser.AbstractpackdeclaratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterNoptrabstractpackdeclarator(CPP14Parser.NoptrabstractpackdeclaratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitNoptrabstractpackdeclarator(CPP14Parser.NoptrabstractpackdeclaratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterParameterdeclarationclause(CPP14Parser.ParameterdeclarationclauseContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitParameterdeclarationclause(CPP14Parser.ParameterdeclarationclauseContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterParameterdeclarationlist(CPP14Parser.ParameterdeclarationlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitParameterdeclarationlist(CPP14Parser.ParameterdeclarationlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterParameterdeclaration(CPP14Parser.ParameterdeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitParameterdeclaration(CPP14Parser.ParameterdeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterFunctionbody(CPP14Parser.FunctionbodyContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitFunctionbody(CPP14Parser.FunctionbodyContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterInitializerlist(CPP14Parser.InitializerlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitInitializerlist(CPP14Parser.InitializerlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterBracedinitlist(CPP14Parser.BracedinitlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitBracedinitlist(CPP14Parser.BracedinitlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterClassname(CPP14Parser.ClassnameContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitClassname(CPP14Parser.ClassnameContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterClassspecifier(CPP14Parser.ClassspecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitClassspecifier(CPP14Parser.ClassspecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterClasshead(CPP14Parser.ClassheadContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitClasshead(CPP14Parser.ClassheadContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterClassheadname(CPP14Parser.ClassheadnameContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitClassheadname(CPP14Parser.ClassheadnameContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterClassvirtspecifier(CPP14Parser.ClassvirtspecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitClassvirtspecifier(CPP14Parser.ClassvirtspecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterClasskey(CPP14Parser.ClasskeyContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitClasskey(CPP14Parser.ClasskeyContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterMemberspecification(CPP14Parser.MemberspecificationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitMemberspecification(CPP14Parser.MemberspecificationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterMemberdeclaration(CPP14Parser.MemberdeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitMemberdeclaration(CPP14Parser.MemberdeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterMemberdeclaratorlist(CPP14Parser.MemberdeclaratorlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitMemberdeclaratorlist(CPP14Parser.MemberdeclaratorlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterMemberdeclarator(CPP14Parser.MemberdeclaratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitMemberdeclarator(CPP14Parser.MemberdeclaratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterVirtspecifierseq(CPP14Parser.VirtspecifierseqContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitVirtspecifierseq(CPP14Parser.VirtspecifierseqContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterVirtspecifier(CPP14Parser.VirtspecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitVirtspecifier(CPP14Parser.VirtspecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterPurespecifier(CPP14Parser.PurespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitPurespecifier(CPP14Parser.PurespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterBaseclause(CPP14Parser.BaseclauseContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitBaseclause(CPP14Parser.BaseclauseContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterBasespecifierlist(CPP14Parser.BasespecifierlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitBasespecifierlist(CPP14Parser.BasespecifierlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterBasespecifier(CPP14Parser.BasespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitBasespecifier(CPP14Parser.BasespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterClassordecltype(CPP14Parser.ClassordecltypeContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitClassordecltype(CPP14Parser.ClassordecltypeContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterBasetypespecifier(CPP14Parser.BasetypespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitBasetypespecifier(CPP14Parser.BasetypespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterAccessspecifier(CPP14Parser.AccessspecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitAccessspecifier(CPP14Parser.AccessspecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterConversionfunctionid(CPP14Parser.ConversionfunctionidContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitConversionfunctionid(CPP14Parser.ConversionfunctionidContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterConversiontypeid(CPP14Parser.ConversiontypeidContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitConversiontypeid(CPP14Parser.ConversiontypeidContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterConversiondeclarator(CPP14Parser.ConversiondeclaratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitConversiondeclarator(CPP14Parser.ConversiondeclaratorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterCtorinitializer(CPP14Parser.CtorinitializerContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitCtorinitializer(CPP14Parser.CtorinitializerContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterMeminitializerlist(CPP14Parser.MeminitializerlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitMeminitializerlist(CPP14Parser.MeminitializerlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterMeminitializer(CPP14Parser.MeminitializerContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitMeminitializer(CPP14Parser.MeminitializerContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterMeminitializerid(CPP14Parser.MeminitializeridContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitMeminitializerid(CPP14Parser.MeminitializeridContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterOperatorfunctionid(CPP14Parser.OperatorfunctionidContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitOperatorfunctionid(CPP14Parser.OperatorfunctionidContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterLiteraloperatorid(CPP14Parser.LiteraloperatoridContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitLiteraloperatorid(CPP14Parser.LiteraloperatoridContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterTemplatedeclaration(CPP14Parser.TemplatedeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitTemplatedeclaration(CPP14Parser.TemplatedeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterTemplateparameterlist(CPP14Parser.TemplateparameterlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitTemplateparameterlist(CPP14Parser.TemplateparameterlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterTemplateparameter(CPP14Parser.TemplateparameterContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitTemplateparameter(CPP14Parser.TemplateparameterContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterTypeparameter(CPP14Parser.TypeparameterContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitTypeparameter(CPP14Parser.TypeparameterContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterSimpletemplateid(CPP14Parser.SimpletemplateidContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitSimpletemplateid(CPP14Parser.SimpletemplateidContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterTemplateid(CPP14Parser.TemplateidContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitTemplateid(CPP14Parser.TemplateidContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterTemplatename(CPP14Parser.TemplatenameContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitTemplatename(CPP14Parser.TemplatenameContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterTemplateargumentlist(CPP14Parser.TemplateargumentlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitTemplateargumentlist(CPP14Parser.TemplateargumentlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterTemplateargument(CPP14Parser.TemplateargumentContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitTemplateargument(CPP14Parser.TemplateargumentContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterTypenamespecifier(CPP14Parser.TypenamespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitTypenamespecifier(CPP14Parser.TypenamespecifierContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterExplicitinstantiation(CPP14Parser.ExplicitinstantiationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitExplicitinstantiation(CPP14Parser.ExplicitinstantiationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterExplicitspecialization(CPP14Parser.ExplicitspecializationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitExplicitspecialization(CPP14Parser.ExplicitspecializationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterTryblock(CPP14Parser.TryblockContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitTryblock(CPP14Parser.TryblockContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterFunctiontryblock(CPP14Parser.FunctiontryblockContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitFunctiontryblock(CPP14Parser.FunctiontryblockContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterHandlerseq(CPP14Parser.HandlerseqContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitHandlerseq(CPP14Parser.HandlerseqContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterHandler(CPP14Parser.HandlerContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitHandler(CPP14Parser.HandlerContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterExceptiondeclaration(CPP14Parser.ExceptiondeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitExceptiondeclaration(CPP14Parser.ExceptiondeclarationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterThrowexpression(CPP14Parser.ThrowexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitThrowexpression(CPP14Parser.ThrowexpressionContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterExceptionspecification(CPP14Parser.ExceptionspecificationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitExceptionspecification(CPP14Parser.ExceptionspecificationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterDynamicexceptionspecification(CPP14Parser.DynamicexceptionspecificationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitDynamicexceptionspecification(CPP14Parser.DynamicexceptionspecificationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterTypeidlist(CPP14Parser.TypeidlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitTypeidlist(CPP14Parser.TypeidlistContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterNoexceptspecification(CPP14Parser.NoexceptspecificationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitNoexceptspecification(CPP14Parser.NoexceptspecificationContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterRightShift(CPP14Parser.RightShiftContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitRightShift(CPP14Parser.RightShiftContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterRightShiftAssign(CPP14Parser.RightShiftAssignContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitRightShiftAssign(CPP14Parser.RightShiftAssignContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterTheoperator(CPP14Parser.TheoperatorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitTheoperator(CPP14Parser.TheoperatorContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterBooleanliteral(CPP14Parser.BooleanliteralContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitBooleanliteral(CPP14Parser.BooleanliteralContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterPointerliteral(CPP14Parser.PointerliteralContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitPointerliteral(CPP14Parser.PointerliteralContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterUserdefinedliteral(CPP14Parser.UserdefinedliteralContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitUserdefinedliteral(CPP14Parser.UserdefinedliteralContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void visitTerminal(TerminalNode node) {
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The default implementation does nothing.</p>
     */
    @Override
    public void visitErrorNode(ErrorNode node) {
    }
}
