package xin.ui.cpptutor.obj;

import java.util.List;
import java.util.Map;

public class MatchingResult {
	private Map<Feedback, List<Feedback>> feedback;
	private List<Solution> solutions;
	public Map<Feedback, List<Feedback>> getFeedback() {
		return feedback;
	}
	public void setFeedback(Map<Feedback, List<Feedback>> feedback) {
		this.feedback = feedback;
	}
	public List<Solution> getSolutions() {
		return solutions;
	}
	public void setSolutions(List<Solution> solutions) {
		this.solutions = solutions;
	}
    
    public String toString() {
        String res = "*******Matching Result********\n";
        
        for (Map.Entry<Feedback, List<Feedback>> entry : feedback.entrySet()) {
            res += "key: " + entry.getKey() + "\n";
            
            for (Feedback l : entry.getValue()) {
                res +=  l.toString() + " ";
            }
            res += "\n";
        }
        
        res += "^^^solutions^^^\n";
        
        for (Solution s : solutions) {
            res += s + "\n";
        }
        return res;
    }
}
