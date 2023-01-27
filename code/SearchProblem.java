package code;

public class SearchProblem {

	String[] operators; // left, right, up, down, pickup, retrieve, drop
	State initialState;
	String[] stateSpace;
	// String goalTest;
	String pathCostFunction;

	public SearchProblem(String[] operators, State initialState, String[] stateSpace, String pathCostFunction) {
		super();
		this.operators = operators;
		this.initialState = initialState;
		this.stateSpace = stateSpace;
		this.pathCostFunction = pathCostFunction;
	}

	public boolean goalTest(State state, int initialCapacity) {

		// You reach your goal when there are no living passengers who are not rescued,
		// are no undamaged boxes which have not been retrieved, and the rescue boat is
		// not
		// carrying any passengers.
		if (initialCapacity != state.remainingCapacity) {
			return false;
		}

		for (int i = 0; i < state.ships.length; i++) {
			if (state.ships[i][2] > 0 || state.ships[i][3] > 0) {
				return false;
			}

		}
		return true;
	}

	// public static int pathCostFunction()

}
