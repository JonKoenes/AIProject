import java.util.Random;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

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
	private char HType = '0';

	
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
		
		
		switch (type) {
		
			case 'T':
			case 't':
				// change priority
				String[] options = {"Heuristic #3","Neural Network","Classifier"};
			    int reply = JOptionPane.showOptionDialog(null, "Select the type of Heuristic to use with the MinMaxTree", "Heuristic Type", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
			    if (reply == 0) {
		        	System.out.println("Heuristic Set: Heuristic #3");
		        	HType = '3'; 
		        } else if (reply == 1) {
		        	HType = 'n'; 
		        	System.out.println("Heuristic Set: Neural Network");
		        } else if (reply == 2) {
		        	HType = 'c'; 
		        	System.out.println("Heuristic Set: Classifier");
		        }
			    break;

		
			case 'n':
				heur = new NeuralNet();
				heur.setParameter();
				break;
			
			case 'c':
				heur = new Classifier();
				heur.setParameter();
				break;

			case '2':
				heur = new Heuristic2();
				RATIO_OF_OPP_TO_PLAYER = 1.0;
				break;
				
			case '1':
			default:
				heur = new Heuristic1();
				RATIO_OF_OPP_TO_PLAYER = 0.95;
				break;
		}
	}
	
	@Override
	public Node play(Node[] choices, Node[] state) {
		Node best = choices[0];
		

		
		//* Method #2 -- MinMaxTree
		if ( type == 't') {
			MinMaxTree tree = new MinMaxTree(gameBoard,mySym,false,HType);
			best = tree.evaluateTree(5, 10000);
			return best;
		}
		/* */
		
		//* Method #2 -- MinMaxTree
		if ( type == 'T') {
			MinMaxTree tree = new MinMaxTree(gameBoard,mySym,true,HType);
			best = tree.evaluateTree(5, 10000);
			return best;
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
		String[] myStatus = null;
		
		double temp,temp1,temp2;
		int parameter = 0;

		for (Node n : choices) {
			if ( n == null ) { 
				continue;
			}
		
			// run classifier
			if (type == 'c') {
				parameter = heur.getParameter();
				
				if (parameter > 0)
					state[n.getId()].setXO(mySym);
				else
					state[n.getId()].setXO(enSym);
				
				temp = heur.evaluateState(state, enSym);

				Boolean exchange = false;
				if (bestVal == 0)
					exchange = true;
				else {
					if (bestVal != 3) {			// keep track of win states
						if (temp == 3)			// prevent loss unless have a win in priority	
							exchange = true;
						else if (bestVal != 2) {
							if (temp == 2)
								exchange = true;
							else if (bestVal != -1) {
								if (temp == -1)
									exchange = true;
								else if (bestVal != 1) {
									if (temp == 1)
										exchange = true;
									else if (temp == -2)
										exchange = true;
								}
							}
						}
					}
				}
				if (exchange) {
					bestVal = temp;
					best = n;
				}
				myStatus = heur.getStatus();
				state[n.getId()].setXO('n');
			
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
			
			
			else{
				temp1 = heur.evaluateState(n,mySym);
				temp2 = heur.evaluateState(n,enSym);
				temp = temp1+(temp2*RATIO_OF_OPP_TO_PLAYER);
				if (temp > bestVal ) {
					best = n;
					bestVal = temp;
				}
			}

			// save status
			if (myStatus != null && myStatus.length > 0) {
				try {
					PrintWriter output = new PrintWriter("data/status.txt");
					for (String getStatus: myStatus) {
						output.println(getStatus + "\n");;
					}
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
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
