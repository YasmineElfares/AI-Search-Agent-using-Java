package code;

import java.util.Comparator;

class AS2Comparator implements Comparator<Node> {

	// Overriding compare()method of Comparator
	// for descending order of cgpa
	public int compare(Node n1, Node n2) {
		int cost1 = n1.hCost + n1.pathCost;
		int cost2 = n2.hCost + n2.pathCost;
		if (cost1 > cost2)
			return 1;
		else if (cost1 < cost2)
			return -1;
		return 0;
	}

}