package xin.ui.cpptutor.obj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.Serializable;
import org.jgrapht.graph.DirectedMultigraph;
import xin.ui.cpptutor.obj.Edge;
import xin.ui.cpptutor.obj.Feedback.FeedbackType;
import xin.ui.cpptutor.obj.Pattern.PatternType;
import xin.ui.cpptutor.obj.Vertex;
import xin.ui.cpptutor.obj.VertexType;


public class PatternGraph implements Serializable {
	private DirectedGraph<PatternVertex, Edge> graph = new DefaultDirectedGraph<>(Edge.class);
//	private DirectedGraph<PatternVertex, Edge> graph = new DirectedMultigraph<>(Edge.class);
//	private PatternType type;
	private String type;
	private String description;
	private String missingFeedback;

    public PatternGraph() {
    }
    
	public PatternGraph(String type, String description, String missingFeedback) {
		this.type = type;
		this.description = description;
		this.missingFeedback = missingFeedback;
	}
//	public PatternType getType() {
//		return type;
//	}
	public String getType() {
		return type;
	}
	
	public DirectedGraph<PatternVertex, Edge> getGraph() {
		return graph;
	}

	public String getDescription() {
//		return "<strong>" + description + "</strong>";
		return description;
	}
	
	public String getMissingFeedback() {
		return missingFeedback;
	}
	
	public PatternVertex findNodeById(int id) {
		PatternVertex ret = null;
		for (Iterator<PatternVertex> it = graph.vertexSet().iterator(); ret == null && it.hasNext(); ) {
			PatternVertex current = it.next();
			if (current.getId() == id)
				ret = current;
		}
		return ret;
	}
	
	public MatchingResult match(DirectedGraph<Vertex, Edge> graph, Map<VertexType, List<Vertex>> verticesByType, int times) {
		MatchingResult ret = new MatchingResult();
		ret.setSolutions(computeSolutions(graph, verticesByType));
		ret.setFeedback(getFeedback(ret.getSolutions(), times));
		return ret;
	}
	
	private void computeSolutions(DirectedGraph<Vertex, Edge> graph, Map<PatternVertex, List<Vertex>> searchSpace, int depth, Solution current, List<Solution> solutions) {
		if (depth == searchSpace.size())
			solutions.add(current.clone());
		else {
			// Pick a random node not yet processed.
			PatternVertex u = Sets.difference(searchSpace.keySet(), current.getNodeMapping().keySet()).iterator().next();
			
			for (Vertex v : searchSpace.get(u)) {
				List<Map<String, String>> listOfVarMappings = new ArrayList<>();
				
				// No duplicates.
				boolean check = !current.getNodeMapping().containsValue(v);
				for (Iterator<PatternVertex> nodesInCurrentIt = current.getNodeMapping().keySet().iterator(); check && nodesInCurrentIt.hasNext(); ) {
					PatternVertex otherU = nodesInCurrentIt.next();
					
					check = check && (!getGraph().containsEdge(u, u) || graph.containsEdge(v, v)) && 
								(!getGraph().containsEdge(u, otherU) || graph.containsEdge(v, current.getNodeMapping().get(otherU))) &&
								(!getGraph().containsEdge(otherU, u) || graph.containsEdge(current.getNodeMapping().get(otherU), v));
				}
				
				if (check) {
					Map<String, String> varMappings = new HashMap<>();
					Set<String> pendingUVars = new HashSet<>(u.getReadingVariables());
					Set<String> pendingVVars = new HashSet<>(v.getReadingVariables());
					
					// Check current variables.
					for (Iterator<String> currentUVarIt = current.getVarMapping().keySet().iterator(); check && currentUVarIt.hasNext(); ) {
						String currentUVar = currentUVarIt.next();
						String currentVVar = current.getVarMapping().get(currentUVar);
						
						if (u.getAssignedVariable() != null && u.getAssignedVariable().equals(currentUVar))
							check = check && v.getAssignedVariable() != null && v.getAssignedVariable().equals(currentVVar);
						
						if (u.getReadingVariables().contains(currentUVar))
							check = check && v.getReadingVariables().contains(currentVVar);
					}
					
					if (check) {
						pendingUVars.removeAll(current.getVarMapping().keySet());
						pendingVVars.removeAll(current.getVarMapping().values());
						
						if (u.getAssignedVariable() != null && !current.getVarMapping().containsKey(u.getAssignedVariable()) && 
								v.getAssignedVariable() != null && !current.getVarMapping().containsValue(v.getAssignedVariable())) {
							varMappings.put(u.getAssignedVariable(), v.getAssignedVariable());
							
							// Already checked.
							pendingUVars.remove(u.getAssignedVariable());
							pendingVVars.remove(v.getAssignedVariable());
						}
						
						// Compute all permutations.
						List<String> listOfPending = new ArrayList<>(pendingUVars);
						List<Set<String>> allPermutations = new ArrayList<>();
						for (int i = 0; i < listOfPending.size(); i++)
							allPermutations.add(new HashSet<>(pendingVVars));
						
						for (List<String> permutation: Sets.cartesianProduct(allPermutations)) {
							Map<String, String> newVarMappings = new HashMap<>(varMappings);
							for (int i = 0; i < permutation.size(); i++)
								newVarMappings.put(listOfPending.get(i), permutation.get(i));
							listOfVarMappings.add(newVarMappings);
						}
					}
				}
				
				if (check)
					for (Map<String, String> newVarMapping : listOfVarMappings) {
						current.getVarMapping().putAll(newVarMapping);
						
						String uLabel = Variable.substitute(u.getLabel(), current, null, "", "");
						
						// Check perfect matching. xin
//                        System.out.println(uLabel);
//                        int index = uLabel.indexOf("[");
//                        while (index != -1) {
//                            if (index != 0 && uLabel.charAt(index - 1) != '\\') {
//                                uLabel = uLabel.substring(0, index) + "\\\\" + uLabel.substring(index);
//                            }
//                            index = uLabel.indexOf("[", index + 1);
//                        }
//                        index = uLabel.indexOf("]");
//                        while (index != -1) {
//                            if (index != 0 && uLabel.charAt(index - 1) != '\\') {
//                                uLabel = uLabel.substring(0, index) + "\\\\" + uLabel.substring(index);
//                            }
//                            index = uLabel.indexOf("]", index + 1);
//                        }
//                        System.out.println(uLabel);

						
                        
                        try {
                            java.util.regex.Pattern p = java.util.regex.Pattern.compile(uLabel);
                            Matcher m = p.matcher(v.getLabel());
                            boolean matching = m.find();
                            boolean removeApprox = false;

                            // Let's try approximate matching
                            if (!matching && u.getApproxLabel() != null) {
                                String uApproxLabel = Variable.substitute(u.getApproxLabel(), current, null, "", "");
                                matching = v.getLabel().matches(uApproxLabel);

                                p = java.util.regex.Pattern.compile(uApproxLabel);
                                m = p.matcher(v.getLabel());
                                matching = m.find();

                                if (matching) {
                                    current.getApproxNodes().add(u);
                                    removeApprox = true;
                                }
                            }

                            if (matching) {
                                current.getNodeMapping().put(u, v);
                                computeSolutions(graph, searchSpace, depth + 1, current, solutions);
                                current.getNodeMapping().remove(u);
                            }
                            if (removeApprox)
                                current.getApproxNodes().remove(u);
                        } catch (Exception e) {
                            System.out.println("Please do not do this to me!! " + uLabel);
                        }
						
						
						for (String uVar : newVarMapping.keySet())
							current.getVarMapping().remove(uVar);
					}
			}
		}
	}
	
	private List<Solution> computeSolutions(DirectedGraph<Vertex, Edge> graph, Map<VertexType, List<Vertex>> verticesByType) {
		// Compute search space.
		Map<PatternVertex, List<Vertex>> searchSpace = new HashMap<>();
		boolean emptySearchSpace = false;
		for (PatternVertex v : getGraph().vertexSet()) {
			List<Vertex> matchingVertices = verticesByType.get(v.getType());
			
			if (v.getType() == null)
				matchingVertices = new ArrayList<>(graph.vertexSet());
			
			if (matchingVertices != null)
				searchSpace.put(v, matchingVertices);
			else
				emptySearchSpace = true;
		}
		
		List<Solution> ret = new ArrayList<>();
		if (!emptySearchSpace)
			// Compute solutions.
			computeSolutions(graph, searchSpace, 0, new Solution(), ret);
		
		return ret;
	}
	
	private void generateDetailedFeedBack(Solution solution, Map<Feedback, List<Feedback>> feedback) {
		Feedback main = null;
		if (!solution.getApproxNodes().isEmpty())
			main = new PatternFeedback(FeedbackType.Almost, "You almost got " + getDescription() + ".", type);
		else
			main = new PatternFeedback(FeedbackType.Correct, "You correctly got " + getDescription() + ".", type);
		List<Feedback> detailed = new ArrayList<>();
		feedback.put(main, detailed);
			
		for (PatternVertex u : solution.getNodeMapping().keySet())
			if (solution.getApproxNodes().contains(u) && u.getIncorrectFeedback() != null)
				detailed.add(new PatternFeedback(FeedbackType.Incorrect, Variable.substitute(u.getIncorrectFeedback(), solution, null, "<b>", "</b>"), type));
			else if (!solution.getApproxNodes().contains(u) && u.getCorrectFeedback() != null)
				detailed.add(new PatternFeedback(FeedbackType.Correct, Variable.substitute(u.getCorrectFeedback(), solution, null, "<b>", "</b>"), type));
	}
	
	private Map<Feedback, List<Feedback>> getFeedback(List<Solution> solutions, int times) {
		Map<Feedback, List<Feedback>> feedback = new HashMap<>();
		if (solutions.size() != times)
			feedback.put(new PatternFeedback(FeedbackType.Incorrect, "You incorrectly got " + getDescription() + ".", type), 
					Lists.newArrayList(
							new PatternFeedback(FeedbackType.Incorrect, 
									"We found you were performing this task " + solutions.size() + " time" + 
										(solutions.size() != 1 ? "s" : "") + ", we were expecting to find " + times + ".", type),
							new PatternFeedback(FeedbackType.Incorrect, 
									"Please, " + getMissingFeedback() + ".", type)));
		else if (solutions.size() == times) {
			// Add correct feedback if empty.
			if (solutions.isEmpty())
				feedback.put(new PatternFeedback(FeedbackType.Correct, "You correctly got " + getDescription() + ".", type), new ArrayList<>());
				
			// Generate detailed feedback for those that have matched, e.g., the range is not correct.
			for (Solution current : solutions)
				generateDetailedFeedBack(current, feedback);
		} else
			feedback.put(new PatternFeedback(FeedbackType.Incorrect, "You incorrectly got " + getDescription() + ".", type), 
					Lists.newArrayList(
							new PatternFeedback(FeedbackType.Incorrect, 
									"We did not found that you are performing this task.", type),
							new PatternFeedback(FeedbackType.Incorrect, 
									"Please, " + getMissingFeedback() + ".", type)));
		
		return feedback;
	}
	
}
