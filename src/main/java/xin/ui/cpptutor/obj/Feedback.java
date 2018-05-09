package xin.ui.cpptutor.obj;

public abstract class Feedback {
	private FeedbackType type;
	private String text;
    private String pattern;//which pattern??

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

	public Feedback(FeedbackType type, String text) {
		super();
		this.type = type;
		this.text = text;
	}

	public FeedbackType getType() {
		return type;
	}

	public String getText() {
		return text;
	}
	
	public enum FeedbackType {
		Correct, Incorrect, Almost
	}
	  public String toString() {
        return type+":"+text;
    }
    
}