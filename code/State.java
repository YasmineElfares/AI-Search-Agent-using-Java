package code;

public class State {

	int posX;
	int posY;
	int remainingCapacity;
	int[][] ships;
	int blackBoxes;
	int savedPassengers;
	// each internal array contains the info of one ship
	// posX,posY,number of people on ship,current health of blackbox
	public State(int posX, int posY, int remainingCapacity, int[][] ships, int blackBoxes, int savedPassengers) {
		super();
		this.posX = posX;
		this.posY = posY;
		this.remainingCapacity = remainingCapacity;
		this.ships = ships;
		this.blackBoxes = blackBoxes;
		this.savedPassengers = savedPassengers;
	}
	



}
