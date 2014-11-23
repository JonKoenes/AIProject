
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

	private double calculateHeuristicValue3(Node root, char sym) {
		
		double value = 0.0;
		int count[] = {0,0,0,0};
		int width[] = {0,0,0,0};
		int indexes[] = {0,1,2,3,3,2,1,0};
		System.out.println("Root: "+root+" Searching for "+sym);
		
		Node[] neighbors = root.getNeighbors();
		Node cur;
		for ( int i = 0; i < 8; i++ ) {
					
			// Check one space out
			if ( neighbors.length <= i ) continue;
			cur = neighbors[i];
			if ( cur == null ) continue;
			System.out.println("["+i+"]  Starting At: "+cur);
			
			if ( cur.getChar() == sym ) {
				System.out.println("1 -> "+sym+" to Index "+indexes[i]);
				count[indexes[i]]++;
			}
			else if ( cur.getChar() == 'n' ) {  }
			else {
				continue;
			}
			width[indexes[i]]++;

			// Check two spaces out
			if ( cur.getNeighbors().length <= i ) continue;
			cur = cur.getNeighbors()[i];
			if ( cur == null ) continue;
			System.out.println("    Checking: "+cur);
			
			if ( cur.getChar() == sym ) {
				System.out.println("1 -> "+sym+" to Index "+indexes[i]);
				count[indexes[i]]++;
			}
			else if ( cur.getChar() == 'n' ) {  }
			else {
				continue;
			}
			width[indexes[i]]++;
			
			// Check three spaces out
			if ( cur.getNeighbors().length <= i ) continue;
			cur = cur.getNeighbors()[i];
			if ( cur == null ) continue;
			System.out.println("    Checking: "+cur);
			
			if ( cur.getChar() == sym ) {
				System.out.println("1 -> "+sym+" to Index "+indexes[i]);
				count[indexes[i]]++;
			}
			else if ( cur.getChar() == 'n' ) {  }
			else {
				continue;
			}
			width[indexes[i]]++;
			
		}
		// Sum up the counts
		for ( int i = 0; i < count.length; i++ ) {
			System.out.println("Summing... "+width[i]+","+count[i]);
			if ( width[i] < 3 ) continue;
			if ( count[i] == 1 ) value += VALUE_OF_1ST_SPACE;
			if ( count[i] == 2 ) value += VALUE_OF_2ND_SPACE;
			if ( count[i] >= 3 ) value += VALUE_OF_3RD_SPACE;
		}
		System.out.println("Got "+value);		
		return value;
	}
	
	private double calculateHeuristicValue4(Node root, char sym) {
		
		double value = 0.0;
		int count[] = {0,0,0,0};
		int width[] = {0,0,0,0};
		int indexes[] = {0,1,2,3,3,2,1,0};
		System.out.println("Root: "+root+" Searching for "+sym);
		
		Node[] neighbors = root.getNeighbors();
		Node cur;
		for ( int i = 0; i < 8; i++ ) {
					
			// Check one space out
			if ( neighbors.length <= i ) continue;
			cur = neighbors[i];
			if ( cur == null ) continue;
			System.out.println("["+i+"]  Starting At: "+cur);
			
			if ( cur.getChar() == sym ) {
				System.out.println("1 -> "+sym+" to Index "+indexes[i]);
				count[indexes[i]]++;
			}
			else if ( cur.getChar() == 'n' ) {  }
			else {
				continue;
			}
			width[indexes[i]]++;

			// Check two spaces out
			if ( cur.getNeighbors().length <= i ) continue;
			cur = cur.getNeighbors()[i];
			if ( cur == null ) continue;
			System.out.println("    Checking: "+cur);
			
			if ( cur.getChar() == sym ) {
				System.out.println("1 -> "+sym+" to Index "+indexes[i]);
				count[indexes[i]]++;
			}
			else if ( cur.getChar() == 'n' ) {  }
			else {
				continue;
			}
			width[indexes[i]]++;
			
			// Check three spaces out
			if ( cur.getNeighbors().length <= i ) continue;
			cur = cur.getNeighbors()[i];
			if ( cur == null ) continue;
			System.out.println("    Checking: "+cur);
			
			if ( cur.getChar() == sym ) {
				System.out.println("1 -> "+sym+" to Index "+indexes[i]);
				count[indexes[i]]++;
			}
			else if ( cur.getChar() == 'n' ) {  }
			else {
				continue;
			}
			width[indexes[i]]++;
			
		}
		// Sum up the counts
		double best = 0.0;
		for ( int i = 0; i < count.length; i++ ) {
			System.out.println("Summing... "+width[i]+","+count[i]);
			if ( width[i] < 3 ) continue;
			if ( count[i] == 1 ) value += VALUE_OF_1ST_SPACE;
			if ( count[i] == 2 ) value += VALUE_OF_2ND_SPACE;
			if ( count[i] >= 3 ) value += VALUE_OF_3RD_SPACE;
			if ( value > best ) best = value;
		}
		System.out.println("Got "+value);		
		return best;
	}
}
