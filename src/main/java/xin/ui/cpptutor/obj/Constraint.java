package xin.ui.cpptutor.obj;

import java.util.List;
import java.util.Map;

import org.jgrapht.DirectedGraph;
import xin.ui.cpptutor.obj.Feedback.FeedbackType;
import xin.ui.cpptutor.obj.Pattern.PatternType;


public abstract class Constraint {
	protected static final String NODE_EQ_SEPARATOR = "<=>", NODE_CONT_SEPARATOR = ">>>", 
			FEEDBACK_SEPARATOR = "|||", EDGE_EXISTENCE_SEPARATOR = "->";
	protected String name;
	
	public static Constraint parse(String str) throws Exception {
		Constraint ret = null;
		if (str.contains(NODE_EQ_SEPARATOR))
			ret = new NodeEqualityConstraint(str);
		else if (str.contains(NODE_CONT_SEPARATOR))
			ret = new NodeContainmentConstraint(str);
		else if (str.contains(EDGE_EXISTENCE_SEPARATOR))
			ret = new EdgeExistenceConstraint(str);
		return ret;
	}
	
	public abstract void setSolutions(Map<String, List<Solution>> solutionsByType);
	
	public abstract void setFunctionMapping(Map<String, String> functionMapping);
	
	public abstract void setParameters(List<Vertex> vertices);
	
	public abstract void setGraph(DirectedGraph<Vertex, Edge> graph);
	
	public abstract List<Feedback> match();
	
	protected final Feedback feedbackNotFound(String feedback) {
		String feedbackStr = feedback;
		
		feedbackStr = feedbackStr.replaceAll("([A-Z0-9\\_]+)\\.\\:([a-zA-Z]+)", "");
		feedbackStr = feedbackStr.replaceAll(":[a-zA-Z]+", "");
		feedbackStr = feedbackStr.replace("[", "").replace("]", "");
		
		return new ConstraintFeedback(FeedbackType.Incorrect, feedbackStr + ".", name);
	}
	
	protected final String replaceIncorrectFeedback(String feedback) {
		do {
			int init = feedback.indexOf("[");
			int end = feedback.indexOf("]");
			
			feedback = feedback.substring(0, init) + feedback.substring(end + 1, feedback.length());
		} while (feedback.contains("["));
		return feedback;
	}

	public String getName() {
		return name;
	}
	
}
