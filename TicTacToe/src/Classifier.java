import java.util.ArrayList;

import javax.swing.JOptionPane;

/**
 * This class contains 2 public methods:
 * 		winOrLose(Node)
 * 			returns: 	true  -> if the given Node returns a win/possible win
 * 						false -> if the given Node returns a loss/possible loss
 * 
 * 		bestChoice(Node[])
 * 			returns:	Node  -> best chance of win given the Node[] (choices of nodes)
 * 				order of priority: loss, win, possible loss, possible win
 *
 * @author Jonathan Koenes, Adam Bartz, Stephen Bush
 */
public class Classifier implements IHeuristic{

	private char enSymbol;									// enemy symbol
	private int[][] testData = new int[11][3];				// {#}{mySymbolVal, enSymbolVal, Win=1/Loss=0}
	private int[] classifications = new int[48];
	private int k = 1;										// k-nearest neighbor
	private int priority = 1;						// 1: aggressive, -1: defensive
	String[] status;
	
	@Override
	public double evaluateState(Node[] state, char enChar) {
		// set up training data
		setupTestData();
		if (priority > 0)
			enSymbol = enChar;
		else
			enSymbol = otherSymbol(enChar).charAt(0);

		int classification = 0;
		for (Node myNode: state) {
			int temp = classify(state, myNode);
			classifications[myNode.getId()] = temp * priority; 		// save for status
			
			if (classification != 3) {			// if classify as win, do not replace
				if (temp == 3) {				
					classification = temp;
				} else if (classification != 2) {
					if (temp == 2) {
						classification = temp;
					} else if (classification != -2) {
						if (temp == -2) {
							classification = temp;
						} else if (classification != 1) {
							if (temp == 1) {
								classification = temp;
							} else if (temp == -1)
								classification = temp;
						}
					}
				}
			}
		}
		
		return classification;
	}
			
	private int classify(Node[] state, Node myNode) {
		int [] myVal = calcValue(state, myNode);
		ArrayList<double[]> classifiers = new ArrayList<double[]>();
		double distance = 0;
		
		for (int[] neighbor: testData) {
			// calculate the distance for test case
			double x = neighbor[0] - (double) myVal[0];
			double y = neighbor[1] - (double) myVal[1];
			x = Math.sqrt(x * x);
			y = Math.sqrt(y * y);
			distance = x + y;
			// add to nearest neighbors
			double[] values = {distance, neighbor[2]};
			classifiers.add(values);
			// sort with farthest neighbor at end of array
			for(int counter = 0; counter < classifiers.size(); counter++) {
				double[] check = classifiers.get(counter);
				if (counter != classifiers.size() - 1 && distance < check[0]) {
					values = classifiers.get(counter);
					distance = values[0];
					classifiers.set(counter, classifiers.get(classifiers.size() - 1));
					classifiers.set(classifiers.size() - 1, values);
				}
			}
			// if too many neighbors trim farthest one
			if (classifiers.size() > k)
				classifiers.remove(classifiers.size() - 1);
		}
		
		// vote
		int[] votes = {0, 0, 0, 0, 0};				// win, close win, possible win, possible loss, close loss
		for (double[] values: classifiers) {
			switch ((int) values[1]) {
				case 3:
					votes[0]++;
					break;
				case 2:
					votes[1]++;
					break;
				case 1:
					votes[2]++;
					break;
				case -1:
					votes[3]++;
					break;
				case -2:
					votes[4]++;
					break;
				default:
					System.err.println("Classifier-classify: can't determine class value");
			}
		}
		
		// set class
		int group = 0;
		if (votes[group] < votes[1])
			group = 1;
		if (votes[group] < votes[2])
			group = 2;
		if (votes[group] < votes[3])
			group = 3;
		if (votes[group] < votes[4])
			group = 4;
		switch (group) {
			case 0:				// win
				group = 3;
				break;
			case 1:				// close win
				group = 2;
				break;
			case 2:				// possible win
				group = 1;
				break;
			case 3:				// possible loss
				group = -1;
				break;
			case 4:				// close loss
				group = -2;				
				break;
			default:
		}
		return group;
	}
		
	/**
	 * get the value for mySymbol and enSymbol, keep track of only highest in each win/loss row (4-in-a-row) 
	 * 
	 * @param myNode
	 * @return
	 */
	private int[] calcValue(Node[] state, Node myNode) {
		int[] value = {0, 0, 0};			// {mysymbol, ensymbol, class}
		
		for (int index = 0; index < 4; index++) {
			String myVal = findVal(state, myNode, index);			// find symbols
			int[] temp = getVal(myVal);								// get number of each symbol
			if (temp[0] > value[0])
				value[0] = temp[0];
			if (temp[1] > value[1])
				value[1] = temp[1];
		}
		
		return value;
	}

	/**
	 * count the number of symbols and return those values
	 * @param values
	 * @return
	 */
	private int[] getVal(String value) {
		int[] myVal = new int[2];								// count for {my symbol, enemy symbol}
			
		String[] path;
		
		for (int counter = 0; counter < 2; counter++) {
			if (counter == 0)
				path = value.split(String.valueOf(enSymbol));		// count the possibility for my symbols
			else
				path = value.split(otherSymbol(enSymbol));	// count the possibility for enemy symbols

			for (int counter2 = 0; counter2 < path.length; counter2++) {
				if (path[counter2].length() >= 4)
					myVal[counter] = path[counter2].replaceAll("n", "").length();
				else
					myVal[counter] = 0;
			}
		}
		
		return myVal;
	}
	
	private String otherSymbol(char symbol) {
		if (symbol == 'x')
			return "o";
		else
			return "x";
	}
	
	/**
	 * find the symbols from the possible 4-in-a-row cells connected to this cell
	 * 			 and return them in a string for easy counting
	 * 
	 * @param state
	 * @param myNode
	 * @param index
	 * @return
	 */
	private String findVal(Node[] state, Node myNode, int index) {
		String val;
		int id = myNode.getId();
		int ring = id % 4;			// ring = right value
		int left = 0;
		int mod = 0;
		int size = 4;
		int[] ids;
		
		// 0 = diagonal TL-BR, 1 = horizontal, 2 = diagonal BL-TR, 3 = vertical
		switch(index) {
		
			// TL-BR
			case 0:
				left = 3 - ring;
				mod = 3;
				break;

			// horizontal
			case 1:
				left = 3;
				mod = 4;
				size = 7;
				break;
			
			// BL-TR
			case 2:
				left = ring;
				mod = 5;
				break;
			
			// vertical
			case 3:
				left = 3 - ring;
				mod = -1;
				
			default:
		}

		ids = new int[size];
		// find left-most
		ids[0] = id - (mod * left);
		if (ids[0] < 0)
			ids[0] = 48 + ids[0];
		val = String.valueOf(state[ids[0]].getChar());
		
		// find next
		for (int counter = 1; counter < size; counter++) {
			ids[counter] = ids[counter - 1] + mod;
			if (ids[counter] > 47)
				ids[counter] = ids[counter] - 48;
			val = val + String.valueOf(state[ids[counter]].getChar());
		}
		
		return val;
	}
			
	public void setKNeighbor(int val) {
		k = val;
	}
	
	// win = 3, close win = 2, possible win = 1, loss = -3, close loss = -2, possible loss = -1
	private void setupTestData() {
		// 15 empty nodes
		testData[0][0] = 0;			// mySymbol = 0
		testData[0][1] = 0;			// enSymbol = 0
		testData[0][2] = 1;			// possible win

		// 3 enemy symbols
		testData[1][0] = 0;			// mySymbol
		testData[1][1] = 3;			// enSymbol
		testData[1][2] = -2;		// close lose
		// 2 enemy symbols
		testData[2][0] = 0;			// mySymbol
		testData[2][1] = 2;			// enSymbol
		testData[2][2] = -1;		// possible lose

		// 4 my symbols
		testData[3][0] = 4;			// mySymbol
		testData[3][1] = 0;			// enSymbol
		testData[3][2] = 3;			// win
		// 3 my symbols
		testData[4][0] = 3;			// mySymbol
		testData[4][1] = 0;			// enSymbol
		testData[4][2] = 2;			// close win
		// 2 my symbols
		testData[5][0] = 2;			// mySymbol
		testData[5][1] = 0;			// enSymbol
		testData[5][2] = 1;			// possible win

		// 4 my symbols, 3 enemy symbols
		testData[6][0] = 4;			// mySymbol
		testData[6][1] = 3;			// enSymbol
		testData[6][2] = 3;			// win
		// 3 my symbols, 3 enemy symbols
		testData[7][0] = 3;			// mySymbol
		testData[7][1] = 3;			// enSymbol
		testData[7][2] = 2;			// close win
		// 3 my symbols, 2 enemy symbols
		testData[8][0] = 3;			// mySymbol
		testData[8][1] = 2;			// enSymbol
		testData[8][2] = 2;			// close win
		
		// 2 my symbols, 3 enemy symbols
		testData[9][0] = 2;			// mySymbol
		testData[9][1] = 3;			// enSymbol
		testData[9][2] = -2;		// close lose
		// 2 my symbols, 2 enemy symbols
		testData[10][0] = 2;			// mySymbol
		testData[10][1] = 2;			// enSymbol
		testData[10][2] = -1;		// possible lose
	}

	@Override
	public double evaluateState(Node inNode, char inChar) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getParameter() {
		return priority;
	}

	@Override
	public void setParameter() {
		// change priority
	    int reply = JOptionPane.showConfirmDialog(null, "Do you want the classifier to be aggressive (defensive = No)?", "Parameter Setting", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
        	priority = 1;
        	System.out.println("Classifier.setParameter: Aggressive Mode");
        } else {
        	priority = -1;
            System.out.println("Classifier.setParameter: Defensive Mode");
        }
	}

	private void formatStatus() {
		status = new String[23];
		ArrayList<int[]> order = new ArrayList<int[]>();
		
		// north quadrant
		order.add(new int[]{35, 39, 43});
		order.add(new int[]{34, 38, 42});
		order.add(new int[]{33, 37, 41});
		order.add(new int[]{32, 36, 40});
		// West and East quadrant
		order.add(new int[]{31, 30, 29, 28,    44, 45, 46, 47});
		order.add(new int[]{27, 26, 25, 24,    0, 1, 2, 3});
		order.add(new int[]{23, 22, 21, 20,    4, 5, 6, 7});
		// South quadrant
		order.add(new int[]{16, 12, 8});
		order.add(new int[]{17, 13, 9});
		order.add(new int[]{18, 14, 10});
		order.add(new int[]{19, 15, 11});
		
		int counter = 0;
		while (order.size() > 0) {			
			int[] currOrder = order.get(0); 
			for (int ocounter = 0; ocounter < currOrder.length; ocounter++) {
				if (classifications[counter] < 0)
					status[counter] = status[counter] + "     " + String.valueOf(classifications[currOrder[ocounter]]);
				else
					status[counter] = status[counter] + "      " + String.valueOf(classifications[currOrder[ocounter]]);
				if (currOrder.length > 3 && ocounter == 3)
					status[counter] = status[counter] + "          ";
			}
			order.remove(0);
			counter++;
		}
	}
	
	@Override
	public String[] getStatus() {
		formatStatus();
		
		return status;
	}
}