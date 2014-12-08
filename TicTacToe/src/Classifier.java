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
	private int[][] testData = new int[9][3];				// {#}{mySymbolVal, enSymbolVal, Win=1/Loss=0}
	private int[] classifications = new int[48];
	private char[] board = new char[48];
	private int k = 1;								// k-nearest neighbor
	private boolean priority = false;						// true: aggressive, false: defensive
	
	@Override
	public double evaluateState(Node[] state, char enChar) {
		// set up training data
		setupTestData();
		enSymbol = enChar;
		        
		int classification = 0;
		for (Node myNode: state) {
			int temp = classify(myNode);
			classifications[myNode.getId()] = temp; 		// save for status
			board[myNode.getId()] = myNode.getChar();
			
			// assume win over loss where		win > loss 		and 	possible win > possible loss
			if (priority) {
				if (classification != 2) {			// if classify as win, do not replace
					if (temp == 2)				
						classification = temp;
					else if (classification != -2) {
						if (temp == -2)
							classification = temp;
						else if (classification != 1) {
							classification = temp;
						}
					}
				}
			
			// assume loss over win where		loss > win		and		possible loss > possible loss
			} else {
				if (classification != -2) {			// if classify as loss, do not replace
					if (temp == -2)				
						classification = temp;
					else if (classification != 2) {
						if (temp == 2)
							classification = temp;
						else if (classification != -1) {
							classification = temp;
						}
					}
				}
			}
		}
		
		return classification;
	}
			
	private int classify(Node myNode) {
		int [] myVal = calcValue(myNode);
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
		int[] votes = {0, 0, 0, 0};				// win, possible win, possible lose, lose
		for (double[] values: classifiers) {
			switch ((int) values[1]) {
				case 2:
					votes[0]++;
					break;
				case 1:
					votes[1]++;
					break;
				case -1:
					votes[2]++;
					break;
				case -2:
					votes[3]++;
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
		switch (group) {
			case 0:				// win
				group = 2;
				break;
			case 1:				// possible win
				group = 1;
				break;
			case 2:				// possible lose
				group = -1;
				break;
			case 3:				// lose
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
	private int[] calcValue(Node myNode) {
		int[] value = {0, 0, 0};			// {mysymbol, ensymbol, class}
		Node[] neighbors = myNode.getNeighbors();
		
		for (int index = 0; index < 4; index++) {
			String myValA = findVal(neighbors[index], index, 3);			// find symbols
			String myValB = findVal(neighbors[7 - index], index, 3);		// search opposite side
			int[] temp = getVal(myValA, myValB);							// get number of each symbol
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
	private int[] getVal(String valueA, String valueB) {
		Boolean horizontal = false;								// is it checking horizontal, where possible spaces are 6 	
		char sym1, sym2;										// closest 'x' or 'o' neighbor symbols to cell
		int[] myVal = new int[2];								// {my symbol, enemy symbol}
		
		if (valueA.length() == 3 && valueB.length() == 3)
			horizontal = true;
		
		// find first 'x' or 'o' symbol
		String temp = valueA.replaceAll("n", "");	// remove empty cells
		if (temp.length() == 0)
			sym1 = '0';
		else
			sym1 = temp.charAt(temp.length() - 1);				// grab first 'x' or 'o' symbol 'left' of selected node					
		temp = valueB.replaceAll("n", "");			// remove empty cells
		if (temp.length() == 0)
			sym2 = '0';
		else
			sym2 =	temp.charAt(0);									// grab first  'x' or 'o' symbol 'right' of selected node		
		
		
		// if an 'x' and 'o' both occupy a diagonal or vertical then these directions are blocked
		temp = valueA + valueB;
		if (!horizontal && temp.contains(String.valueOf(sym1)) && temp.contains(String.valueOf(sym2))) {
			myVal[0] = 0;
			myVal[1] = 0;
			
		// else check for values
		} else {
			if (!horizontal) {
				temp = valueA.replaceAll("n","");
				temp += valueB.replaceAll("n","");
				if (sym1 == enSymbol || sym2 == enSymbol)
					myVal[1] = temp.length();
				else
					myVal[0] = temp.length();
			} else {
				String[] leftside = valueA.split(otherSymbol(sym1));	// split so can tell if there are blockers									
				String[] rightside = valueB.split(otherSymbol(sym2));	// split so can tell if there are blockers			

				valueA = leftside[leftside.length - 1];					// get unblocked part
				valueB = rightside[0];									// get unblocked part
				
				// if one side is empty or both sides have the same symbols add together
				if (sym1 == sym2 || sym1 == '0' || sym2 == '0') {
					temp = valueA + valueB;
					// if there are enough cells to connect four then get value
					if (temp.length() == 4) {
						temp = temp.replaceAll("n", "");
						if (sym1 == enSymbol || sym2 == enSymbol)
							myVal[1] = temp.length();
						else
							myVal[0] = temp.length();
					// otherwise blocked
					} else {
						myVal[0] = 0;
						myVal[1] = 0;
					}
				// else closest symbols differ on each side
				} else {
					
					// count enemy symbol
					if (sym1 == enSymbol) {		// on left side
						int index = valueB.indexOf(otherSymbol(enSymbol));
						if (index > 0)
							temp = valueA + valueB.substring(0, index - 1);
						else
							temp = valueA;
					} else {					// on right side
						rightside = valueA.split(otherSymbol(enSymbol));
						if (rightside.length > 0)
							temp = rightside[0] + valueB;
						else
							temp = valueB;
					}
					// if not blocked then count
					if (temp.length() == 4) {
						temp = temp.replaceAll("n", "");
						myVal[1] = temp.length();
					}
					
					// count my symbol
					if (sym1 != enSymbol) {		// on left side
						int index = valueB.indexOf(enSymbol);
						if (index > 0)
							temp = valueA + valueB.substring(0, index - 1);
						else
							temp = valueA;
					} else {					// on right side
						rightside = valueA.split(Character.toString(enSymbol));
						if (rightside.length > 0)
							temp = rightside[0] + valueB;
						else
							temp = valueB;
					}
					// if not blocked then count
					if (temp.length() == 4) {
						temp = temp.replaceAll("n", "");
						myVal[0] = temp.length();
					}
				}
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
	 * find the symbols from the neighbor nodes and return them in a string for easy counting
	 * 
	 * @param myNode
	 * @param index
	 * @param limit
	 * @return
	 */
	private String findVal(Node myNode, int index, int limit) {
		String val = "";
		if (myNode == null)
			return val;
		
		if (limit > 0) {
			val = String.valueOf(myNode.getChar());
			limit--;
			val += findVal(getNeighbor(myNode, index), index, limit);
		}
		
		return val;
	}

	/**
	 * return a specific neighbor (helps for counting neighbors in a line, or the same direction)
	 * @param myNode
	 * @param index
	 * @return
	 */
	private Node getNeighbor(Node myNode, int index) {
		Node[] neighbors = myNode.getNeighbors();

		return neighbors[index];
	}
			
	public void setKNeighbor(int val) {
		k = val;
	}
	
	// win = 2, possible win = 1, possible lose = -1, lose = -2
	private void setupTestData() {
		// 15 empty nodes
		testData[0][0] = 0;			// mySymbol = 0
		testData[0][1] = 0;			// enSymbol = 0
		testData[0][2] = 1;			// possible win

		// 3 enemy symbols
		testData[1][0] = 0;			// mySymbol
		testData[1][1] = 3;			// enSymbol
		testData[1][2] = -2;		// lose
		// 2 enemy symbols
		testData[2][0] = 0;			// mySymbol
		testData[2][1] = 2;			// enSymbol
		testData[2][2] = -1;		// possible lose

		// 3 my symbols
		testData[3][0] = 3;			// mySymbol
		testData[3][1] = 0;			// enSymbol
		testData[3][2] = 2;			// win
		// 2 my symbols
		testData[4][0] = 2;			// mySymbol
		testData[4][1] = 0;			// enSymbol
		testData[4][2] = 1;			// possible win

		// 3 my symbols, 3 enemy symbols
		testData[5][0] = 3;			// mySymbol
		testData[5][1] = 3;			// enSymbol
		testData[5][2] = 2;			// win
		// 3 my symbols, 2 enemy symbols
		testData[6][0] = 3;			// mySymbol
		testData[6][1] = 2;			// enSymbol
		testData[6][2] = 2;			// win
		
		// 2 my symbols, 3 enemy symbols
		testData[7][0] = 2;			// mySymbol
		testData[7][1] = 3;			// enSymbol
		testData[7][2] = -2;		// lose
		// 2 my symbols, 2 enemy symbols
		testData[8][0] = 2;			// mySymbol
		testData[8][1] = 2;			// enSymbol
		testData[8][2] = -1;		// possible lose
	}

	@Override
	public double evaluateState(Node inNode, char inChar) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getParameter() {
		if (priority)
			return 1;
		else
			return 0;
	}

	@Override
	public void setParameter() {
		// change priority
	    int reply = JOptionPane.showConfirmDialog(null, "Do you want the classifier to be aggressive (defensive = No)?", "Parameter Setting", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
        	priority = true;
        	System.out.println("Classifier.setParameter: Aggressive Mode");
        } else
            System.out.println("Classifier.setParameter: Defensive Mode");

	}

	@Override
	public String[] getStatus() {
		String[] status = new String[96];
		
		for (int counter = 0; counter < 48; counter++)
			status[counter] = String.valueOf(board[counter]);
		for (int counter = 48; counter < 96; counter++)
			status[counter] = String.valueOf(classifications[counter - 48]);
			
		return status;
	}
}