package xin.ui.cpptutor.obj;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.jgrapht.DirectedGraph;
import xin.ui.cpptutor.obj.Feedback.FeedbackType;
import xin.ui.cpptutor.obj.Pattern.PatternType;



public class NodeEqualityConstraint extends Constraint {
//	private PatternType type1, type2;
	private String type1, type2;
	private PatternGraph pattern1, pattern2;
	private Integer node1, node2;
	private String feedback;
	
	public NodeEqualityConstraint(String str) throws Exception {
		java.util.regex.Pattern p = java.util.regex.Pattern.compile("([a-zA-Z0-9\\_]+) ([A-Z0-9\\_]+)\\.([0-9]+)\\<=\\>([A-Z0-9\\_]+)\\.([0-9]+)\\|\\|\\|(.+)");
		Matcher m = p.matcher(str);
		
		if (m.find()) {
			name = m.group(1);
			
			type1 = m.group(2);
			pattern1 = Pattern.getInstance().getPattern(type1);
			node1 = Integer.valueOf(m.group(3));
			
			type2 = m.group(4);
			pattern2 = Pattern.getInstance().getPattern(type2);
			node2 = Integer.valueOf(m.group(5));
			
			feedback = m.group(6);
		} else
			throw new Exception("Something went wrong when parsing the constraints.");
	}
	
	private List<Solution> solutions1, solutions2;
	@Override
	public void setSolutions(Map<String, List<Solution>> solutionsByType) {
		solutions1 = solutionsByType.get(type1);
		solutions2 = solutionsByType.get(type2);
	}
	
	@Override
	public void setFunctionMapping(Map<String, String> functionMapping) { }
	
	@Override
	public void setParameters(List<Vertex> vertices) { }

	@Override
	public List<Feedback> match() {
		List<Feedback> ret = new ArrayList<>();
		List<Solution> copyOfSol2 = new ArrayList<>(solutions2);
		
		// Get the solutions for type 1.
		for (Solution sol1 : solutions1) {
			Vertex v1 = sol1.getNodeMapping().get(pattern1.findNodeById(node1));
			int found = -1;
			
			// Get the solutions for type 2.
			for (int i = 0; found == -1 && i < copyOfSol2.size(); i++) {
				Solution sol2 = copyOfSol2.get(i);
				Vertex v2 = sol2.getNodeMapping().get(pattern2.findNodeById(node2));
				
				if (v1.equals(v2)) {
					found = i;
					
					String feedbackStr = replaceIncorrectFeedback(feedback);
					feedbackStr = Variable.substitute(feedbackStr, sol1, null, "<b>", "</b>");
					feedbackStr = Variable.substitute(feedbackStr, sol2, null, "<b>", "</b>");
					
					ret.add(new ConstraintFeedback(FeedbackType.Correct, feedbackStr + ".", name));
				}
			}
			
			if (found == -1)
				ret.add(feedbackNotFound(feedback));
			else
				copyOfSol2.remove(found);
		}
		
		return ret;
	}

	@Override	public void setGraph(DirectedGraph<Vertex, Edge> graph) { }
	
}
