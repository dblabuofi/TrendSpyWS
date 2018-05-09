package xin.ui.cpptutor.obj;

import xin.ui.cpptutor.obj.Pattern.PatternType;


public class PatternFeedback extends Feedback {
	private String patternType;
	
	public PatternFeedback(FeedbackType type, String text, String patternType) {
		super(type, text);
		this.patternType = patternType;
	}

	public String getPatternType() {
		return patternType;
	}

}
