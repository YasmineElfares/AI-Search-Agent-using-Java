package code;

import java.util.Comparator;

class GR1Comparator implements Comparator<Node> {

	// Overriding compare()method of Comparator
	// for descending order of cgpa
	public int compare(Node n1, Node n2) {
		if (n1.hCost < n2.hCost)
			return 1;
		else if (n1.hCost > n2.hCost)
			return -1;
		return 0;
	}

}