package xin.ui.cpptutor.obj;

import java.io.Serializable;

public class Edge implements Serializable {
	private Vertex fromVertex, toVertex;
	private EdgeType type;

	public Edge(Vertex fromVertex, Vertex toVertex, EdgeType type) {
		this.fromVertex = fromVertex;
		this.toVertex = toVertex;
		this.type = type;
	}

	@Override
	public String toString() {
		return fromVertex + "-" + type + "->" + toVertex;
	}

	public Vertex getFromVertex() {
		return fromVertex;
	}

	public Vertex getToVertex() {
		return toVertex;
	}

	public EdgeType getType() {
		return type;
	}
	
}
