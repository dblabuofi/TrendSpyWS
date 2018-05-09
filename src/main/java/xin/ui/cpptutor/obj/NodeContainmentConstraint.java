package xin.ui.cpptutor.obj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;
import org.jgrapht.DirectedGraph;

import com.google.common.collect.Sets;
import xin.ui.cpptutor.obj.Feedback.FeedbackType;
import xin.ui.cpptutor.obj.Pattern.PatternType;



public class NodeContainmentConstraint extends Constraint {
	private PatternGraph mainPattern;
	private Integer node;
	private List<PatternGraph> otherPatterns = new ArrayList<>();
	
	private String expressionToEnforce, feedback;
	
	public NodeContainmentConstraint(String str) throws Exception {
		java.util.regex.Pattern p = java.util.regex.Pattern.compile("([a-zA-Z0-9\\_]+) ([A-Z0-9\\_]+)\\.([0-9]+)\\>\\>\\>(.+)\\|\\|\\|(.+)");
		Matcher m = p.matcher(str);
		
		if (m.find()) {
			name = m.group(1);
			
			mainPattern = Pattern.getInstance().getPattern(m.group(2));
			node = Integer.valueOf(m.group(3));
			
			expressionToEnforce = m.group(4);
			feedback = m.group(5);
			
			p = java.util.regex.Pattern.compile("([A-Z0-9\\_]+)\\.\\:([a-zA-Z]+)");
			m = p.matcher(str);
			
			while (m.find())
				otherPatterns.add(Pattern.getInstance().getPattern(m.group(1)));
		} else
			throw new Exception("Something went wrong when parsing the constraints.");
	}

	private List<Solution> mainSolutions;
	private Map<String, List<Solution>> otherSolutions = new HashMap<>();
	@Override
	public void setSolutions(Map<String, List<Solution>> solutionsByType) {
		mainSolutions = solutionsByType.get(mainPattern.getType());
		
		for (PatternGraph p : otherPatterns)
			otherSolutions.put(p.getType(), solutionsByType.get(p.getType()));
	}
	
	private Map<String, String> functionMapping;
	@Override
	public void setFunctionMapping(Map<String, String> functionMapping) {
		this.functionMapping = functionMapping;
	}

	private List<String> parameters;
	@Override
	public void setParameters(List<Vertex> vertices) {
		parameters = new ArrayList<>();
		for (Vertex v : vertices)
			parameters.add(v.getAssignedVariable());
	}

	@Override
	public List<Feedback> match() {
		List<Feedback> ret = new ArrayList<>();
		
		// Get the solutions.
		for (Solution sol : mainSolutions) {
			boolean found = false;
			Vertex v = sol.getNodeMapping().get(mainPattern.findNodeById(node));
			
			String subsExpression = expressionToEnforce;
			// We are assuming one single solution for the other solutions.
			for (PatternGraph o : otherPatterns)
				if (!otherSolutions.get(o.getType()).isEmpty())
					subsExpression = Variable.substitute(subsExpression, otherSolutions.get(o.getType()).get(0), o.getType(), "", "");
			
			// Substitute expression with current variables.
			subsExpression = Variable.substitute(subsExpression, sol, null, "", "");
			
			// Substitute function names.
			for (String expected : functionMapping.keySet())
				subsExpression = subsExpression.replace(expected, functionMapping.get(expected));
			String originalSubsExpr = subsExpression;
			
			List<Set<String>> allPermutations = new ArrayList<>();
			int matches = StringUtils.countMatches(subsExpression, ":methodParameter");
			for (int i = 0; i < matches; i++)
				allPermutations.add(new HashSet<>(parameters));
			
			for (Iterator<List<String>> paramIt = Sets.cartesianProduct(allPermutations).iterator(); !found && paramIt.hasNext(); ) {
				List<String> params = paramIt.next();
				subsExpression = originalSubsExpr;
				
				for (String param : params)
					subsExpression = StringUtils.replaceOnce(subsExpression, ":methodParameter", param);
				
				// Check expression found.
				java.util.regex.Pattern p = java.util.regex.Pattern.compile(subsExpression);
				Matcher m = p.matcher(v.getLabel());
				boolean matching = m.find();
				
				if (matching) {
					found = true;
					
					String feedbackStr = replaceIncorrectFeedback(feedback);
					for (PatternGraph o : otherPatterns)
						if (!otherSolutions.get(o.getType()).isEmpty())
							feedbackStr = Variable.substitute(feedbackStr, otherSolutions.get(o.getType()).get(0), o.getType(), "<b>", "</b>");
					feedbackStr = Variable.substitute(feedbackStr, sol, null, "<b>", "</b>");
					
					for (String param : params)
						feedbackStr = StringUtils.replaceOnce(feedbackStr, ":methodParameter", "<b>" + param + "</b>");
					
					for (String expected : functionMapping.keySet())
						feedbackStr = feedbackStr.replace(expected, functionMapping.get(expected));
					
					ret.add(new ConstraintFeedback(FeedbackType.Correct, feedbackStr + ".", name));
				}	
			}
			
			if (!found)
				ret.add(feedbackNotFound(feedback));
		}
		
		return ret;
	}

	@Override	public void setGraph(DirectedGraph<Vertex, Edge> graph) { }

}
