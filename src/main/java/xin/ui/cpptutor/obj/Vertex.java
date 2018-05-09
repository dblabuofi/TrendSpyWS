package xin.ui.cpptutor.obj;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class Vertex implements Serializable{
	protected int id;
	private VertexType type;
	private String label, assignedVariable;
	private Set<String> readingVariables;
    private String parent;
    private String lineNum;
    
	public Vertex(int id) {
		this.id = id;
		readingVariables = new HashSet<>();
	}

    public String getLineNum() {
        return lineNum;
    }

    public void setLineNum(String lineNum) {
        this.lineNum = lineNum;
    }
	
    public void setParent(String p) {
        this.parent = p;
    }
    
    public String getParent() {
        return parent;
    }
    
	public int getId() {
		return id;
	}
	
	public String getAssignedVariable() {
		return assignedVariable;
	}

	public void setAssignedVariable(String assignedVariable) {
		this.assignedVariable = assignedVariable;
	}

	public VertexType getType() {
		return type;
	}

	public void setType(VertexType type) {
		this.type = type;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Set<String> getReadingVariables() {
		return readingVariables;
	}

	@Override
	public String toString() {
		return id + "-" + type + "-" + label + " $$ASSIGNED: " + assignedVariable + " $$READING: " + readingVariables;
	}
	public String getHardCode() {
		String hardCode = label.replaceAll("[a-zA-Z]", "");
		hardCode = hardCode.replaceAll(" ", "");
		return hardCode;
	}
	public LinkedHashSet<String> getVarSet() {
		LinkedHashSet<String> variables = new LinkedHashSet<String>();
		String labelCopy = new String(label);
		String alphaOnly = labelCopy.replaceAll("[^\\p{Alpha}]+",",");
	    alphaOnly = alphaOnly.replaceAll("length", "");
	    alphaOnly = alphaOnly.replaceAll(",,", ",");
	    alphaOnly = alphaOnly.replaceAll(" ", "");
	    String[] vars = alphaOnly.split(",");
	    for(String var: vars) {
	    	if(readingVariables.contains(var)) {
	    		variables.add(var);
	    	}
	    }
		return variables;
	}
	
}
