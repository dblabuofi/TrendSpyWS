package xin.ui.cpptutor.obj;

import xin.ui.cpptutor.obj.Vertex;

public class PatternVertex extends Vertex {
	private String approxLabel;
	private String correctFeedback, incorrectFeedback;

	public PatternVertex(int id) {
		super(id);
	}

	public String getApproxLabel() {
		return approxLabel;
	}

	public void setApproxLabel(String approxLabel) {
		this.approxLabel = approxLabel;
	}

	public String getCorrectFeedback() {
		return correctFeedback;
	}

	public void setCorrectFeedback(String correctFeedback) {
		this.correctFeedback = correctFeedback;
	}

	public String getIncorrectFeedback() {
		return incorrectFeedback;
	}

	public void setIncorrectFeedback(String incorrectFeedback) {
		this.incorrectFeedback = incorrectFeedback;
	}
	public String toString() {
//        return approxLabel + " $ " + correctFeedback + " $ " + incorrectFeedback;
        return "" + id;
    }
}
