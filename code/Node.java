package code;

public class Node implements Comparable<Node> {

	State state;
	Node parent;
	String operator;
	int depth;
	int pathCost;
	int hCost;

	public Node(State state, Node parent, String operator, int depth) {
		// super();
		this.state = state;
		this.parent = parent;
		this.operator = operator;
		this.depth = depth;
		//this.pathCost = pathCost;
		//this.hCost = 0;
	}

	public Node() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override

	public int compareTo(Node node) {
		return this.pathCost - node.pathCost;
	}

}
