package code;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.StringJoiner;

public class CoastGuard extends SearchProblem {

	static int initialCapacity;
	static int M;
	static int N;
	static int[][] stations;
	static int initPassengers;
	static int expandedNodesCount;
	static State initState;
	static HashSet<String> states;
	static boolean visualizeSol;
	static int printCounter = 0;

	public CoastGuard(String[] operators, State initialState, String[] stateSpace, String pathCostFunction) {
		super(operators, initialState, stateSpace, pathCostFunction);
		// TODO Auto-generated constructor stub
	}

	public static String stringifyState(State state) {

		String stringified = "";

		// each internal array contains the info of one ship
		// posX,posY,number of people on ship,current health of blackbox
		String ships = "";
		for (int i = 0; i < state.ships.length; i++) {
			ships += state.ships[i][0] + "," + state.ships[i][1] + "," + state.ships[i][2] + "," + state.ships[i][3]
					+ ";";

		}

		stringified += state.posX + "," + state.posY + ";" + state.remainingCapacity + ";" + ships + ";"
				+ state.blackBoxes + "," + state.savedPassengers;
		return stringified;

	}

	public static String GenGrid() {

		String output = "";
		int M = (int) ((Math.random() * (10)) + 5);
		int N = (int) ((Math.random() * (10)) + 5);
		int[][] grid = new int[N][M];
		// the maximum number of passengers the coast guard boat can carry.
		int C = (int) ((Math.random() * (70)) + 30);
		int ships = (int) ((Math.random() * ((M * N) - 1)) + 1);
		int stations = (int) ((Math.random() * ((M * N) - 1)) + 1);
		// location of coast guard
		int cgX = (int) ((Math.random() * (N - 1)));
		int cgY = (int) ((Math.random() * (M - 1)));
		output += M + "," + N + ";" + C + ";" + cgX + "," + cgY + ";";
		grid[cgX][cgY] = 3;
		// station = 1, ship = 2, coast guard = 3
		// Generating stations
		for (int i = 0; i < stations; i++) {
			int IX = (int) ((Math.random() * (N - 1)));
			int IY = (int) ((Math.random() * (M - 1)));
			if (grid[IX][IY] == 0) {
				grid[IX][IY] = 1;
				output += IX + "," + IY + ",";
			}
		}
		output = output.substring(0, output.length() - 1) + ";";
		// Generating ships
		for (int i = 0; i < ships; i++) {
			int SX = (int) ((Math.random() * (N - 1)));
			int SY = (int) ((Math.random() * (M - 1)));
			if (grid[SX][SY] == 0) {
				grid[SX][SY] = 2;
				int SPassengers = (int) ((Math.random() * 100));
				output += SX + "," + SY + "," + SPassengers + ",";

			}
		}
		output = output.substring(0, output.length() - 1) + ";";
		return output;
	}

	public static String solve(String grid, String strategy, boolean visualize) {
		initPassengers = 0;
		expandedNodesCount = 0;

		states = new HashSet<String>();
		visualizeSol = visualize;

		String[] gridArray = grid.split(";");
		String[] dimensions = gridArray[0].split(",");
		M = Integer.parseInt(dimensions[0]);
		N = Integer.parseInt(dimensions[1]);
		initialCapacity = Integer.parseInt(gridArray[1]);
		String[] location = gridArray[2].split(",");
		int cgX = Integer.parseInt(location[0]);
		int cgY = Integer.parseInt(location[1]);
		String[] Stations = gridArray[3].split(",");
		String[] ships = gridArray[4].split(",");
		int[][] initShips = new int[ships.length / 3][4];
		int numberOfShips = 0;

		for (int i = 0; i < ships.length; i += 3) {

			initShips[numberOfShips][0] = Integer.parseInt(ships[i]);
			initShips[numberOfShips][1] = Integer.parseInt(ships[i + 1]);
			initShips[numberOfShips][2] = Integer.parseInt(ships[i + 2]);
			initShips[numberOfShips][3] = 20;
			initPassengers += Integer.parseInt(ships[i + 2]);
			numberOfShips++;

		}

		stations = new int[Stations.length / 2][2];
		int numberOfStations = 0;
		for (int i = 0; i < Stations.length; i += 2) {

			stations[numberOfStations][0] = Integer.parseInt(Stations[i]);
			stations[numberOfStations][1] = Integer.parseInt(Stations[i + 1]);
			numberOfStations++;

		}

		State initialState = new State(cgX, cgY, initialCapacity, initShips, 0, 0);
		// System.out.println(initialState.blackBoxes);
		initState = initialState;
		states.add(stringifyState(initialState));
		// posX,posY,number of people on ship,current health of blackbox

		// String[] operators = { "left", "right", "up", "down", "pickup", "retrieve",
		// String[] operators = { "pickup", "drop", "retrieve", "up", "down", "left",
		// "right" };
		// String[] operators = { "up", "down", "right", "left", "pickup", "drop",
		// "retrieve" };
		String[] operators = { "pickup", "retrieve", "drop", "up", "down", "left", "right" };

		String[] test = new String[0];
		SearchProblem problem = new SearchProblem(operators, initialState, test, "");
		switch (strategy) {
		case "BF":
			return GeneralSearch(problem, "BF");
		case "DF":
			return GeneralSearch(problem, "DF");
		case "ID":
			return ID(problem);
		case "GR1":
			return GeneralSearch(problem, "GR1");
		case "GR2":
			return GeneralSearch(problem, "GR2");
		case "UC":
			return GeneralSearch(problem, "UC");
		case "AS1":
			return GeneralSearch(problem, "AS1");
		case "AS2":
			return GeneralSearch(problem, "AS2");
		default:
			return "";
		}
	}

	public static String constructSolution(Node node) {

		// System.out.println("Goal node pathcost: " + node.pathCost + "; Goal node
		// hcost: " + node.hCost);

		// plan;deaths;retrieved;nodes
		ArrayList<String[][]> allMatrices = new ArrayList<String[][]>();
		// System.out.println("node.state.SAVED Passengers: " +
		// node.state.savedPassengers);
		int deaths = initPassengers - node.state.savedPassengers;
		int retrieved = node.state.blackBoxes;
		String plan = "";
		while (node.depth > 0) {
			// System.out.println("node pathcost: " + node.pathCost + "; node hcost: " +
			// node.hCost);

			String[][] matrix = new String[N][M];
			plan = node.operator + "," + plan;
			int passengersOnCG = Math.abs(initialCapacity - node.state.remainingCapacity);
			matrix[node.state.posX][node.state.posY] = "CG" + passengersOnCG;

			for (int i = 0; i < node.state.ships.length; i++) {
				int[] ship = node.state.ships[i];
				matrix[ship[0]][ship[1]] = "Ship(" + ship[2] + "," + ship[3] + ")";
			}
			for (int i = 0; i < stations.length; i++) {
				int[] station = stations[i];
				matrix[station[0]][station[1]] = "Station";
			}
			allMatrices.add(matrix);
			node = node.parent;
		}
		plan = plan.substring(0, plan.length() - 1);

		if (visualizeSol) {
			String[][] rootMatrix = new String[N][M];
			rootMatrix[initState.posX][initState.posY] = "CG" + 0;

			for (int i = 0; i < initState.ships.length; i++) {
				int[] ship = initState.ships[i];
				rootMatrix[ship[0]][ship[1]] = "Ship(" + ship[2] + "," + ship[3] + ")";
			}
			for (int i = 0; i < stations.length; i++) {
				int[] station = stations[i];
				rootMatrix[station[0]][station[1]] = "Station";
			}

			visualize(rootMatrix);

			for (int i = allMatrices.size() - 1; i >= 0; i--) {

				visualize(allMatrices.get(i));
			}
		}
		return plan + ";" + deaths + ";" + retrieved + ";" + expandedNodesCount;

	}

	public static String GeneralSearch(SearchProblem problem, String QingFunction) {
		// QingFn = enqueue 1st, enqueue last, ..
		Queue<Node> Nodes = new LinkedList<>();
		Node root = new Node(problem.initialState, new Node(), "", 0);
		Nodes.add(root);
		boolean isRoot = true;
		Queue<Node> expandedNodes = new LinkedList<Node>();
		while (!Nodes.isEmpty()) {
			Node node = Nodes.remove();
			// if (!isRoot) {
			// node.state = timestep(node.state);
			// }
			// isRoot = false;
			if (problem.goalTest(node.state, initialCapacity)) {
				return constructSolution(node);
			} else {
				expandedNodes = expand(node, problem, QingFunction);
				// expandedNodesCount += expandedNodes.size();
				expandedNodesCount++;

				switch (QingFunction) {
				case "BF":
					Nodes = BF(Nodes, expandedNodes);
					break;
				case "DF":
					Nodes = DF(Nodes, expandedNodes);
					break;
				case "GR1":
					Nodes = GR1(Nodes, expandedNodes);
					break;
				case "UC":
					Nodes = UC(Nodes, expandedNodes);
					break;
				case "GR2":
					Nodes = GR2(Nodes, expandedNodes);

					// if (printCounter < 16) {
					// for (Node elem : Nodes) {
					// System.out.print("hcost: " + elem.hCost + " operator: " + elem.operator + " ;
					// ");
					// }
					// System.out.println("");
					// printCounter++;
					// System.out.println("Peek op: " + Nodes.peek().operator);
					// }

					break;
				case "AS1":
					Nodes = AS1(Nodes, expandedNodes);
					break;
				case "AS2":
					Nodes = AS2(Nodes, expandedNodes);
					break;
				default:
					return "";
				}
			}

		}
		return "Failure";

	}

	public static String ID(SearchProblem problem) {

		for (int i = 1; i < 999; i++) {
			states = new HashSet<String>();
			Queue<Node> Nodes = new LinkedList<>();
			Queue<Node> expandedNodes = new LinkedList<>();
			boolean isRoot = true;
			Node root = new Node(problem.initialState, new Node(), "", 0);
			Nodes.add(root);

			while (!Nodes.isEmpty()) {
				Node node = Nodes.remove();
				// if (!isRoot) {
				// node.state = timestep(node.state);
				// }
				// isRoot = false;
				if (problem.goalTest(node.state, initialCapacity)) {
					return constructSolution(node);
				} else {
					expandedNodes = expand(node, problem, "");
					expandedNodesCount += expandedNodes.size();
					Node tempNode = expandedNodes.peek();
					if (tempNode != null) {
						if (tempNode.depth <= i) {
							Nodes = DF(Nodes, expandedNodes);
						}
					}
				}
			}
		}
		return "Failure";
	}

	public static Queue<Node> expand(Node node, SearchProblem problem, String qingFunction) {
		Queue<Node> expandedNodes = new LinkedList<Node>();

		for (int i = 0; i < problem.operators.length; i++) {
			// operators: left, right, up, down, pickup, retrieve, drop
			State newState;
			String stringifiedState;
			switch (problem.operators[i]) {
			case "left":

				newState = left(node.state);

				if (newState == null) {
					break;
				}
				newState = timestep(newState);
				stringifiedState = stringifyState(newState);
				// if (!states.contains(stringifiedState)) {

				if (states.add(stringifiedState)) {
					Node expandedNode = new Node(newState, node, "left", node.depth + 1);
					expandedNodes.add(expandedNode);
				}
				// System.out.println("-----ba3d left----------------");
				break;
			case "right":
				newState = right(node.state);

				if (newState == null) {
					break;
				}
				newState = timestep(newState);
				stringifiedState = stringifyState(newState);
				if (states.add(stringifiedState)) {
					Node expandedNode = new Node(newState, node, "right", node.depth + 1);
					expandedNodes.add(expandedNode);
				}
				break;

			case "up":
				newState = up(node.state);

				if (newState == null) {
					break;
				}
				newState = timestep(newState);
				stringifiedState = stringifyState(newState);
				if (states.add(stringifiedState)) {
					Node expandedNode = new Node(newState, node, "up", node.depth + 1);
					expandedNodes.add(expandedNode);
				}
				break;

			case "down":
				newState = down(node.state);
				// newState = timestep(newState);
				if (newState == null) {
					break;
				}
				newState = timestep(newState);
				stringifiedState = stringifyState(newState);
				if (states.add(stringifiedState)) {
					Node expandedNode = new Node(newState, node, "down", node.depth + 1);
					expandedNodes.add(expandedNode);
				}
				break;

			case "drop":
				// check if a stations exists in my cell & check if I have sth to drop
				if (initialCapacity != node.state.remainingCapacity) {
					for (int j = 0; j < stations.length; j++) {
						if (stations[j][0] == node.state.posX && stations[j][1] == node.state.posY) {
							// System.out.println("remaining: in drop: " + node.state.remainingCapacity);
							int savedNow = (initialCapacity - node.state.remainingCapacity);

							// System.out.println("savedNow: " + node.state.savedPassengers);
							// System.out.println(node.state.savedPassengers);
							newState = drop(node.state);
							newState.savedPassengers = node.state.savedPassengers + savedNow;
							newState = timestep(newState);
							stringifiedState = stringifyState(newState);
							if (states.add(stringifiedState)) {
								Node expandedNode = new Node(newState, node, "drop", node.depth + 1);
								expandedNodes.add(expandedNode);
							}
							break;
						}
					}
				}
				break;

			case "pickup":
				if (node.state.remainingCapacity > 0) {
					int[][] myShips = node.state.ships;

					for (int j = 0; j < myShips.length; j++) {

						if (myShips[j][0] == node.state.posX && myShips[j][1] == node.state.posY && myShips[j][2] > 0) {

							newState = pickUp(node.state, j);
							newState = timestep(newState);
							stringifiedState = stringifyState(newState);
							if (states.add(stringifiedState)) {
								Node expandedNode = new Node(newState, node, "pickup", node.depth + 1);
								expandedNodes.add(expandedNode);

								break;
							}
						}
					}
				}
				break;

			case "retrieve":
				// check if blackbox isnt completely damaged and no more passengers on ship
				int[][] myShips = node.state.ships;
				for (int j = 0; j < myShips.length; j++) {
					if (myShips[j][0] == node.state.posX && myShips[j][1] == node.state.posY && myShips[j][2] <= 0
							&& myShips[j][3] > 1) {
						newState = retrieve(node.state, j);

						if (newState == null) {
							break;
						}
						newState = timestep(newState);
						// newState.ships[j][3] = 0;
						stringifiedState = stringifyState(newState);
						if (states.add(stringifiedState)) {

							Node expandedNode = new Node(newState, node, "retrieve", node.depth + 1);
							expandedNodes.add(expandedNode);
							break;
						}

					}
				}
				break;

			}

		}
		return expandedNodes;
	}

	public int compare(Node n1, Node n2) {
		if (n1.depth > n2.depth)
			return 1;
		else {
			return 0;
		}
	}

	public static Queue<Node> limitedDF(Queue<Node> queue, Queue<Node> expandedNodes, int limitDepth) {

		// Queue<Node> temp = new LinkedList<Node>();
		Node node = expandedNodes.peek();
		if (node.depth <= limitDepth) {
			expandedNodes.addAll(queue);
			return expandedNodes;
		} else {
			return queue;
		}

	}

	public static Queue<Node> UC(Queue<Node> queue, Queue<Node> expandedNodes) {

		ArrayList<Node> temp = new ArrayList<Node>();
		temp.addAll(queue);
		temp.addAll(expandedNodes);
		// queue.addAll(expandedNodes);
		Collections.sort(temp);
		// Collections.reverse(temp);
		Queue<Node> sortedQueue = new LinkedList<Node>();
		sortedQueue.addAll(temp);
		return sortedQueue;
	}

	public static Queue<Node> DF(Queue<Node> queue, Queue<Node> expandedNodes) {
		expandedNodes.addAll(queue);
		return expandedNodes;
	}

	public static Queue<Node> AS11(Queue<Node> queue, Queue<Node> expandedNodes) {

		PriorityQueue<Node> pq = new PriorityQueue<Node>(5, new ASComparator());

		while (expandedNodes.size() != 0) {
			Node node = expandedNodes.poll();
			pq.add(node);
			int x = node.state.posX;
			int y = node.state.posY;
			int[][] ships = node.state.ships;
			int cost = Integer.MIN_VALUE;
			int dist;
			for (int j = 0; j < ships.length; j++) {
				dist = Math.abs((ships[j][0] - x) + (ships[j][1] - y));
				int passengersOnArrival = (Math.abs(ships[j][2] - dist));
				if (passengersOnArrival > cost) {
					cost = passengersOnArrival;
				}
			}
			node.hCost = cost * -1;
		}
		return pq;
	}

	public static Queue<Node> AS1(Queue<Node> queue, Queue<Node> expandedNodes) {

		PriorityQueue<Node> pq = new PriorityQueue<Node>(new ASComparator());
		pq.addAll(queue);
		while (expandedNodes.size() != 0) {
			Node node = expandedNodes.poll();
			int[][] ships = node.state.ships;
			int safeShips = 0;
			int wrecks = 0;
			int bbLost = wrecks - node.state.blackBoxes;
			int passengersOnShips = 0;

			for (int j = 0; j < ships.length; j++) {
				if (node.state.ships[j][3] <= 0) {
					wrecks++;
				}
				passengersOnShips += node.state.ships[j][2];
				if (ships[j][3] > 0) {
					safeShips++;
				}
			}
			int deaths = initPassengers - node.state.savedPassengers - passengersOnShips;
			int pathCost = deaths + bbLost;
			node.pathCost = pathCost;
			node.hCost = safeShips;
			// System.out
			// .println("SAFE SHIPS: " + safeShips + "; POSX: " + node.state.posX + "; POSY:
			// " + node.state.posY);
			pq.add(node);
		}
		return pq;
	}

	public static Queue<Node> AS2(Queue<Node> queue, Queue<Node> expandedNodes) {

		PriorityQueue<Node> pq = new PriorityQueue<Node>(new AS2Comparator());
		// pq.addAll(queue);
		while (expandedNodes.size() != 0) {
			Node node = expandedNodes.remove();
			int x = node.state.posX;
			int y = node.state.posY;
			int[][] ships = node.state.ships;
			int cost = Integer.MAX_VALUE;
			int dist;
			int wrecks = 0;
			int passengersOnShips = 0;
			for (int j = 0; j < ships.length; j++) {
				if (ships[j][3] <= 0) {
					wrecks++;
				}
				passengersOnShips += ships[j][2];
				dist = Math.abs(ships[j][0] - x) + Math.abs(ships[j][1] - y);
				dist = Math.min(dist, Math.abs(ships[j][2]));
				cost += Math.min(dist, cost);
				//cost += dist;
			}
			int bbLost = wrecks - node.state.blackBoxes;
			int deaths = initPassengers - node.state.savedPassengers - passengersOnShips;
			int pathCost = deaths + bbLost;
			node.pathCost = pathCost;
			node.hCost = cost;
			pq.add(node);
		}
		pq.addAll(queue);
		return pq;
	}

	// Heuristics: number of deaths on ship when coast guard reaches it
	public static Queue<Node> GR11(Queue<Node> queue, Queue<Node> expandedNodes) {

		PriorityQueue<Node> pq = new PriorityQueue<Node>(5, new NodeComparator());

		while (expandedNodes.size() != 0) {
			Node node = expandedNodes.poll();
			pq.add(node);
			int x = node.state.posX;
			int y = node.state.posY;
			int[][] ships = node.state.ships;
			int cost = Integer.MIN_VALUE;
			int dist;
			for (int j = 0; j < ships.length; j++) {
				dist = Math.abs((ships[j][0] - x) + (ships[j][1] - y));
				int passengersOnArrival = (Math.abs(ships[j][2] - dist));
				if (passengersOnArrival > cost) {
					cost = passengersOnArrival;
				}
			}
			node.hCost = cost * -1;
		}
		return pq;
	}

	static Queue<Node> reversequeue(Queue<Node> queue) {
		Stack<Node> stack = new Stack<Node>();
		while (!queue.isEmpty()) {
			stack.add(queue.peek());
			queue.remove();
		}
		while (!stack.isEmpty()) {
			queue.add(stack.peek());
			stack.pop();
		}
		return queue;
	}

	// Heuristic 1: number of ships that are not wrecks (blackbox health > 0)
	public static Queue<Node> GR1(Queue<Node> queue, Queue<Node> expandedNodes) {

		PriorityQueue<Node> pq = new PriorityQueue<Node>(new NodeComparator());
		pq.addAll(queue);
		while (expandedNodes.size() != 0) {
			Node node = expandedNodes.poll();
			int[][] ships = node.state.ships;
			int safeShips = 0;
			for (int j = 0; j < ships.length; j++) {
				if (ships[j][3] > 0) {
					safeShips++;
				}
			}
			node.hCost = safeShips;
			pq.add(node);
		}
		return pq;
	}

	// Heuristic 1: number of ships that are wrecks (blackbox health > 0)
	public static Queue<Node> GR111(Queue<Node> queue, Queue<Node> expandedNodes) {
		PriorityQueue<Node> pq = new PriorityQueue<Node>(new NodeComparator());
		pq.addAll(queue);
		while (expandedNodes.size() != 0) {
			Node node = expandedNodes.poll();
			int[][] ships = node.state.ships;
			int safeShips = 0;
			for (int j = 0; j < ships.length; j++) {
				if (ships[j][3] <= 0) {
					safeShips++;
				}
			}
			node.hCost = safeShips;
			pq.add(node);
		}
		return pq;
	}
	// s1 x, s2 x, s3 x, s4, s5 c = 3 ; c = 2
	// s1 x, s2 , s3 , s4, s5 c = 1 ; c = 4

	public static Queue<Node> AS111(Queue<Node> queue, Queue<Node> expandedNodes) {

		PriorityQueue<Node> pq = new PriorityQueue<Node>(new ASComparator());
		pq.addAll(queue);
		while (expandedNodes.size() != 0) {
			Node node = expandedNodes.poll();
			int[][] ships = node.state.ships;
			int safeShips = 0;
			for (int j = 0; j < ships.length; j++) {
				if (ships[j][3] <= 0) {
					safeShips++;
				}
			}
			node.hCost = safeShips;
			pq.add(node);
		}
		return pq;
	}

	// Heuristic 2: minimum of distance to ship and number of deaths till we reach
	// that ship
	public static Queue<Node> GR2(Queue<Node> queue, Queue<Node> expandedNodes) {

		PriorityQueue<Node> pq = new PriorityQueue<Node>(new NodeComparator());
		// pq.addAll(queue);

		while (expandedNodes.size() != 0) {
			Node node = expandedNodes.remove();
			int x = node.state.posX;
			int y = node.state.posY;
			int[][] ships = node.state.ships;
			int cost = 0;
			int dist;
			for (int j = 0; j < ships.length; j++) {
				dist = Math.abs(ships[j][0] - x) + Math.abs(ships[j][1] - y);
				dist = Math.min(dist, Math.abs(ships[j][2]));
				// cost = Math.min(dist, cost);
				cost += dist;
			}
			node.hCost = cost;
			pq.add(node);
		}
		pq.addAll(queue);
		return pq;
	}

	public static Queue<Node> BF(Queue<Node> queue, Queue<Node> expandedNodes) {
		queue.addAll(expandedNodes);
		return queue;
	}

	public static State pickUp(State currentState, int shipIndex) {

		int[][] newShips = new int[currentState.ships.length][4];
		for (int i = 0; i < currentState.ships.length; i++) {
			newShips[i] = Arrays.copyOf(currentState.ships[i], 4);
		}

		// passengerleft = 20, capacity 2 passengersLeft 4, capacity 10
		int[] ship = currentState.ships[shipIndex];
		int passengersOnShip = ship[2];
		int passengersLeft = 0;
		int remCapacity = 0;

		if (currentState.remainingCapacity < passengersOnShip) {

			passengersLeft = (passengersOnShip - currentState.remainingCapacity);
			remCapacity = 0;
		}

		else {
			// if CG capacity > passengers on ship
			if (currentState.remainingCapacity >= passengersOnShip) {
				passengersLeft = 0;
				remCapacity = (currentState.remainingCapacity - passengersOnShip);
			}
		}
		// System.out.println("Da5alnaa " + remCapacity);
		newShips[shipIndex][2] = passengersLeft;
		State newState = new State(currentState.posX, currentState.posY, remCapacity, newShips, currentState.blackBoxes,
				currentState.savedPassengers);

		return newState;
	}

	public static boolean equalState(State stateA, State stateB) {

		if (stateA.posX == stateB.posX && stateA.posY == stateB.posY
				&& stateA.remainingCapacity == stateB.remainingCapacity && stateA.blackBoxes == stateB.blackBoxes
				&& stateA.ships.equals(stateB.ships) && stateA.savedPassengers == stateB.savedPassengers) {
			return true;
		}
		return false;
	}

	// Drop: The coast guard drops all passengers it is currently carrying at a
	// This can only be done when the coast guard and the station are in the same
	// cell & it resets the remaining capacity of the coast guard boat to 0.
	public static State drop(State currentState) {

		State newState = new State(currentState.posX, currentState.posY, initialCapacity, currentState.ships,
				currentState.blackBoxes, currentState.savedPassengers);
		return newState;
	}

	public static State retrieve(State currentState, int shipIndex) {

		int[][] newShips = new int[currentState.ships.length][4];
		for (int i = 0; i < currentState.ships.length; i++) {
			newShips[i] = Arrays.copyOf(currentState.ships[i], 4);
		}
		newShips[shipIndex][3] = 0;

		State newState = new State(currentState.posX, currentState.posY, currentState.remainingCapacity, newShips,
				(currentState.blackBoxes + 1), currentState.savedPassengers);
		return newState;
	}

	public static State left(State currentState) {

		// (down, right)

		int newX = currentState.posX;
		int newY = currentState.posY;

		if (newY > 0) {
			newY--;
			State newState = new State(newX, newY, currentState.remainingCapacity, currentState.ships,
					currentState.blackBoxes, currentState.savedPassengers);
			return newState;
		} else {
			return null;
		}
	}

	public static State right(State currentState) {

		int newX = currentState.posX;
		int newY = currentState.posY;

		if (newY < M - 1) {
			newY++;

			State newState = new State(newX, newY, currentState.remainingCapacity, currentState.ships,
					currentState.blackBoxes, currentState.savedPassengers);
			return newState;
		} else {
			return null;
		}

	}

	public static State down(State currentState) {

		int newX = currentState.posX;
		int newY = currentState.posY;

		if (newX < N - 1) {
			newX++;
			State newState = new State(newX, newY, currentState.remainingCapacity, currentState.ships,
					currentState.blackBoxes, currentState.savedPassengers);
			return newState;
		} else {
			return null;
		}
	}

	public static State up(State currentState) {

		int newX = currentState.posX;
		int newY = currentState.posY;

		if (newX > 0) {
			newX--;

			State newState = new State(newX, newY, currentState.remainingCapacity, currentState.ships,
					currentState.blackBoxes, currentState.savedPassengers);

			return newState;
		}

		else {
			return null;
		}
	}

	public static State timestep(State currentState) {

		int[][] ships = new int[currentState.ships.length][4];

		for (int i = 0; i < currentState.ships.length; i++) {
			ships[i] = Arrays.copyOf(currentState.ships[i], 4);
		}

		for (int i = 0; i < ships.length; i++) {

			if (ships[i][2] > 0) {
				ships[i][2]--;
			} else {
				if (ships[i][3] > 0)
					ships[i][3]--;
			}
		}
		State newState = new State(currentState.posX, currentState.posY, currentState.remainingCapacity, ships,
				currentState.blackBoxes, currentState.savedPassengers);
		return newState;
	}

	public static void visualize(String[][] grid) {

		for (String[] row : grid) {
			StringJoiner sj = new StringJoiner(" | ");
			for (String col : row) {
				sj.add(col);
			}
			System.out.println(sj.toString());

		}
		System.out.println("--------------------------------------------");
	}

	public static void main(String[] args) {
		
		

		String grid0 = "5,6;50;0,1;0,4,3,3;1,1,90;";
		String grid1 = "6,6;52;2,0;2,4,4,0,5,4;2,1,19,4,2,6,5,0,8;";
		String grid2 = "7,5;40;2,3;3,6;1,1,10,4,5,90;";
		String grid3 = "8,5;60;4,6;2,7;3,4,37,3,5,93,4,0,40;";
		String grid4 = "5,7;63;4,2;6,2,6,3;0,0,17,0,2,73,3,0,30;";
		String grid5 = "5,5;69;3,3;0,0,0,1,1,0;0,3,78,1,2,2,1,3,14,4,4,9;";
		String grid6 = "7,5;86;0,0;1,3,1,5,4,2;1,1,42,2,5,99,3,5,89;";
		String grid7 = "6,7;82;1,4;2,3;1,1,58,3,0,58,4,2,72;";
		String grid8 = "6,6;74;1,1;0,3,1,0,2,0,2,4,4,0,4,2,5,0;0,0,78,3,3,5,4,3,40;";
		String grid9 = "7,5;100;3,4;2,6,3,5;0,0,4,0,1,8,1,4,77,1,5,1,3,2,94,4,3,46;";
		String grid10 = "10,6;59;1,7;0,0,2,2,3,0,5,3;1,3,69,3,4,80,4,7,94,4,9,14,5,2,39;";

		String g0 = "3,4;97;1,2;0,1;3,2,65;";

		Runtime rt = Runtime.getRuntime();
		OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
				.getOperatingSystemMXBean();

		long old_used_mem = rt.totalMemory() - rt.freeMemory();
		long startTime = System.nanoTime();
		 
		solve(grid5, "AS2", false);


		long elapsedTime = System.nanoTime() - startTime;
		long new_used_mem = rt.totalMemory() - rt.freeMemory();
		System.out.println("Total memory used:  " + ((new_used_mem - old_used_mem) / 1000000) + " MegaByte");

		double cpu = ((com.sun.management.OperatingSystemMXBean) bean).getProcessCpuLoad();
		while (cpu <= 0) {
			cpu = ((com.sun.management.OperatingSystemMXBean) bean).getProcessCpuLoad();
		}
		System.out.println("Cpu usage: " + ((cpu) * 100) + "%");
		System.out.println("RunTime: " + (elapsedTime / 1000000.0) + " MilliSecond");

		// System.out.println(solve(grid0, "AS1", false));

		System.out.println("DF: " + solve(grid5, "DF", false));
		System.out.println("BF: " + solve(grid5, "BF", false));
		System.out.println("ID: " + solve(grid5, "ID", false));
		System.out.println("------------------------");
		System.out.println("GR1: " + solve(grid5, "GR1", false));
		System.out.println("AS1: " + solve(grid5, "AS1", false));
		System.out.println("------------------------");
		System.out.println("GR2: " + solve(grid5, "GR2", false));
		System.out.println("AS2: " + solve(grid5, "AS2", false));
		 System.out.println("------------------------");
		// System.out.println("UC: " + solve(grid2, "UC", false));

		HashSet<String> test = new HashSet<String>();
		int[][] ships = { { 1, 2, 3, 4 }, { 5, 6, 7, 8 } };
		int[][] ships2 = { { 1, 2, 3, 4 }, { 5, 6, 7, 8 } };
		test.add("1,0,97,3,2,62,203,2,62,203,2,62,203,2,62,20;,0,0");

		State test1 = new State(0, 0, N, ships, 0, 0);
		State test2 = new State(0, 0, N, ships, 0, 0);
		State test3 = new State(0, 0, N, ships, 0, 0);

		Node testNode1 = new Node(null, null, "", 1);
		Node testNode2 = new Node(null, null, "", 2);
		Node testNode3 = new Node(null, null, "", 3);
		Node testNode4 = new Node(null, null, "", 4);

		Queue<Node> q = new LinkedList<Node>();
		Queue<Node> exp = new LinkedList<Node>();
		testNode1.hCost = 1;
		testNode2.hCost = 2;
		testNode3.hCost = 3;
		testNode4.hCost = 4;
		q.add(testNode2);
		q.add(testNode4);
		exp.add(testNode1);
		exp.add(testNode3);

		Queue<Node> tmp = test();

		while (!tmp.isEmpty()) {
			Node n = tmp.remove();
			// System.out.print("PQ in Main: " + n.hCost + " , ");
		}
		System.out.println();
	}

	public static Queue<Node> test() {

		Node testNode1 = new Node(null, null, "", 1);
		Node testNode2 = new Node(null, null, "", 2);
		Node testNode3 = new Node(null, null, "", 3);
		Node testNode4 = new Node(null, null, "", 4);

		Queue<Node> q = new LinkedList<Node>();
		Queue<Node> exp = new LinkedList<Node>();
		testNode1.hCost = 1;
		testNode2.hCost = 5;
		testNode3.hCost = 3;
		testNode4.hCost = 4;
		q.add(testNode2);
		q.add(testNode4);
		exp.add(testNode1);
		exp.add(testNode3);

		PriorityQueue<Node> pq = new PriorityQueue<Node>(Collections.reverseOrder());
		pq.addAll(q);
		pq.addAll(exp);
		// pq = (Queue<Node>)reversequeue(pq);
		// Collections.reverse(pq);
		for (Node elem : pq) {
			System.out.print(elem.hCost + ",");
		}

		return pq;
	}

}
