import java.util.Random;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jonathan Koenes, Adam Bartz, Stephen Bush
 */
public class AIPlayer implements IPlayer {
	
	private static double RATIO_OF_OPP_TO_PLAYER = 0.99; 
	private static double VALUE_OF_1ST_SPACE = 1.0; 
	private static double VALUE_OF_2ND_SPACE = 3.0; 
	private static double VALUE_OF_3RD_SPACE = 9.0; 
	private IHeuristic heur;
	private char mySym;
	private char enSym;
	private char type;					// '1' = Heuristic1, '2' = Heuristic2, 'c' = classifier
	private Node[][] gameBoard;
	
	public AIPlayer(char sym, char type, Node[][] board) {
		mySym = sym;
		if ( mySym == 'x' ) {
			enSym = 'o';
		}
		else {
			enSym = 'x';
		}
		this.type = type;
		
		gameBoard = board;
		
		
		//* Heuristic #3
		switch (type) {
			case 'n':
				heur = new NeuralNet();
				heur.setParameter();
				break;
			
			case 'c':
				heur = new Classifier();
				heur.setParameter();
				break;
								
			default:
            heur = new Heuristic1();
            RATIO_OF_OPP_TO_PLAYER = 0.95;
				break;
		}
		/* */
		
		/* Heuristic #4
		heur = new Heuristic2();
		RATIO_OF_OPP_TO_PLAYER = 1.0;
		/* */

	}
	
	@Override
	public Node play(Node[] choices, Node[] state) {
		Node best = choices[0];
		
		
		
		//* Method #2 -- MinMaxTree
		if ( type == 't') {
			MinMaxTree tree = new MinMaxTree(gameBoard,mySym,false);
			best = tree.evaluateTree(5, 10000);
		}
		/* */
		
		//* Method #2 -- MinMaxTree
		if ( type == 'T') {
			MinMaxTree tree = new MinMaxTree(gameBoard,mySym,true);
			best = tree.evaluateTree(5, 10000);
		}
		/* */

		if ( type == 'r') {
			int i = 0;
			for (; i < choices.length && choices[i] != null; i++ ) { }
			return choices[(int)(Math.random()*i)];
		}

		//* Method #1 -- Greedy Heuristic
		best = choices[0];
		double bestVal = 0;
		
		double temp,temp1,temp2;
		for (Node n : choices) {
			if ( n == null ) { 
				continue;
			}
		
			// run classifier
			if (type == 'c') {
				Node[] nextState = state;
				nextState[n.getId()].setXO(mySym);
				temp = heur.evaluateState(nextState, enSym);
				//System.out.println("Classifier: Node ID-" + n.getId() + " classified as group: " + temp);
				int parameter = heur.getParameter();
				// assume win over loss where		win > loss 		and 	possible win > possible loss
				Boolean exchange = false;
				if (parameter > 0) {
					if (bestVal != 2) {			// if classify as win, do not replace
						if (temp == 2)				
							exchange = true;
						else if (bestVal != -2) {
							if (temp == -2)
								exchange = true;
							else if (bestVal != 1) {
								exchange = true;
							}
						}
					}
				
				// assume loss over win where		loss > win		and		possible loss > possible loss
				} else {
					if (bestVal != -2) {			// if classify as loss, do not replace
						if (temp == -2)				
							exchange = true;
						else if (bestVal != 2) {
							if (temp == 2)
								exchange = true;
							else if (bestVal != -1) {
								exchange = true;
							}
						}
					}
				}

				if (exchange) {
					bestVal = temp;
					best = n;
					
					System.out.println("Classifier.status:");
					String[] myStatus = heur.getStatus();
					System.out.println("             " + myStatus[39]);
					System.out.println("     " + myStatus[35] + "               " + myStatus[43]);
					System.out.println("             " + myStatus[38]);
					System.out.println("       " + myStatus[34] + "           " + myStatus[42]);
					System.out.println("  " + myStatus[31] + "      " + myStatus[33] + "   " + myStatus[37] + "   " + myStatus[41] + "      " + myStatus[47]);
					System.out.println("     " + myStatus[30] + "               " + myStatus[46]);
					System.out.println("       " + myStatus[29] + "   " + myStatus[32] + " " + myStatus[36] + " " + myStatus[40] + "   " + myStatus[45]);
					System.out.println("         " + myStatus[28] + "       " + myStatus[44]);
					System.out.println(myStatus[27] + "  " + myStatus[26] + "  " + myStatus[25] + "  " + myStatus[24] + "       " + myStatus[0] + "  " + myStatus[1] + "  " + myStatus[2] + "  " + myStatus[3]);
					System.out.println("         " + myStatus[20] + "       " + myStatus[4]);
					System.out.println("       " + myStatus[21] + "   " + myStatus[16] + " " + myStatus[12] + " " + myStatus[8] + "   " + myStatus[5]);
					System.out.println("     " + myStatus[22] + "               " + myStatus[6]);
					System.out.println("  " + myStatus[23] + "      " + myStatus[17] + "   " + myStatus[13] + "   " + myStatus[9] + "      " + myStatus[7]);
					System.out.println("       " + myStatus[18] + "           " + myStatus[10]);
					System.out.println("             " + myStatus[14]);
					System.out.println("     " + myStatus[19] + "               " + myStatus[11]);
					System.out.println("             " + myStatus[15]);
					System.out.println();
					System.out.println();
					System.out.println("             " + myStatus[39 + 48]);
					System.out.println("     " + myStatus[35 + 48] + "               " + myStatus[43 + 48]);
					System.out.println("             " + myStatus[38 + 48]);
					System.out.println("       " + myStatus[34 + 48] + "           " + myStatus[42 + 48]);
					System.out.println("  " + myStatus[31 + 48] + "      " + myStatus[33 + 48] + "   " + myStatus[37 + 48] + "   " + myStatus[41 + 48] + "      " + myStatus[47 + 48]);
					System.out.println("     " + myStatus[30 + 48] + "               " + myStatus[46 + 48]);
					System.out.println("       " + myStatus[29 + 48] + "   " + myStatus[32 + 48] + " " + myStatus[36 + 48] + " " + myStatus[40 + 48] + "   " + myStatus[45 + 48]);
					System.out.println("         " + myStatus[28 + 48] + "       " + myStatus[44 + 48]);
					System.out.println(myStatus[27 + 48] + "  " + myStatus[26 + 48] + "  " + myStatus[25 + 48] + "  " + myStatus[24 + 48] + "       " + myStatus[0 + 48] + "  " + myStatus[1 + 48] + "  " + myStatus[2 + 48] + "  " + myStatus[3 + 48]);
					System.out.println("         " + myStatus[20 + 48] + "       " + myStatus[4 + 48]);
					System.out.println("       " + myStatus[21 + 48] + "   " + myStatus[16 + 48] + " " + myStatus[12 + 48] + " " + myStatus[8 + 48] + "   " + myStatus[5 + 48]);
					System.out.println("     " + myStatus[22 + 48] + "               " + myStatus[6 + 48]);
					System.out.println("  " + myStatus[23 + 48] + "      " + myStatus[17 + 48] + "   " + myStatus[13 + 48] + "   " + myStatus[9 + 48] + "      " + myStatus[7 + 48]);
					System.out.println("       " + myStatus[18 + 48] + "           " + myStatus[10 + 48]);
					System.out.println("             " + myStatus[14 + 48]);
					System.out.println("     " + myStatus[19 + 48] + "               " + myStatus[11 + 48]);
					System.out.println("             " + myStatus[15 + 48]);
}
				nextState[n.getId()].setXO('n');
			
			// run heuristics
			} 
			//Neural Net Heuristic
			if(type == 'n'){
				if(mySym == 'x'){//maximizer
					n.setXO('x');
					temp = heur.evaluateState(state, mySym);
					if(temp > bestVal){
						best = n;
						bestVal= temp;
					}
					n.setXO('n');
				}
				else{//minimizer
					n.setXO('o');
					temp = heur.evaluateState(state, mySym);
					if(temp < bestVal){
						best = n;
						bestVal= temp;
					}
					n.setXO('n');
				}
			}
			
			
			else if ( type == '1' ){
				temp1 = heur.evaluateState(n,mySym);
				temp2 = heur.evaluateState(n,enSym);
				temp = temp1+(temp2*RATIO_OF_OPP_TO_PLAYER);
				if (temp > bestVal ) {
					best = n;
					bestVal = temp;
				}
			}
		}
		System.out.println("AI played >> Bv = "+bestVal);
		/* */		
		
		return best;
	}

	@Override
	public void update(Node picked) {

	}
	
	public void setBoard(Node[][] board) {
		
		gameBoard = board;
		
	}
}
