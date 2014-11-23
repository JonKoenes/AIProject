
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author f93x834
 */
public class AIPlayer implements IPlayer {
	
	private static double RATIO_OF_OPP_TO_PLAYER = 0.99; 
	private static double VALUE_OF_1ST_SPACE = 1.0; 
	private static double VALUE_OF_2ND_SPACE = 3.0; 
	private static double VALUE_OF_3RD_SPACE = 9.0; 
	private IHeuristic heur;
	private char mySym;
	private char enSym;
	
	public AIPlayer(char sym) {
		mySym = sym;
		if ( mySym == 'x' ) {
			enSym = 'o';
		}
		else {
			enSym = 'x';
		}
		
		
		//* Heuristic #3
		heur = new Heuristic1();
		RATIO_OF_OPP_TO_PLAYER = 1.0;
		/* */
		
		/* Heuristic #4
		heur = new Heuristic2();
		RATIO_OF_OPP_TO_PLAYER = 1.0;
		/* */

	}
	
	@Override
	public Node play(Node[] choices) {
		Node best = choices[0];
		double bestVal = 0;
		
		double temp,temp1,temp2;
		for ( Node n : choices ) {
			if ( n == null ) { 
				continue;
			}
		
			temp1 = heur.evaluateState(n,mySym);
			temp2 = heur.evaluateState(n,enSym);
			temp = temp1+(temp2*RATIO_OF_OPP_TO_PLAYER);
			if ( temp > bestVal ) {
				best = n;
				bestVal = temp;
			}
		}
		System.out.println("AI played >> Bv = "+bestVal);
		return best;
	}

	@Override
	public void update(Node picked) {

	}
}
