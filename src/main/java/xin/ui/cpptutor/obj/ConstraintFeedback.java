package xin.ui.cpptutor.obj;

public class ConstraintFeedback extends Feedback {
	private String constraint;

	public ConstraintFeedback(FeedbackType type, String text, String constraint) {
		super(type, text);
		this.constraint = constraint;
	}

	public String getConstraint() {
		return constraint;
	}

}
