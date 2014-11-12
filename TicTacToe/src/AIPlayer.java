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

	@Override
	public Node play(Node[] choices) {
		Node best = choices[0];
		double bestVal = 0;
		
		double temp,temp1,temp2;
		for ( Node n : choices ) {
			if ( n == null ) { 
				continue;
			}
			temp1 = calculateHeuristicValue(n,'o');
			temp2 = calculateHeuristicValue(n,'x');
			temp = temp1+temp2*0.95;
			if ( temp > bestVal ) {
				best = n;
				bestVal = temp;
			}
		}
		System.out.println("AI played");
		return best;
	}

	@Override
	public void update(Node picked) {

	}
	
	private double calculateHeuristicValue(Node root, char sym) {
		
		double value = 0.0;
		System.out.println("Root: "+root+" Searching for "+sym);
		
		Node[] neighbors = root.getNeighbors();
		Node cur;
		for ( int i = 0; i < 8; i++ ) {

			// Check one space out
			if ( neighbors.length <= i ) continue;
			cur = neighbors[i];
			if ( cur == null ) continue;
			System.out.println("  Starting At: "+cur);
			
			if ( cur.getChar() == sym ) {
				System.out.println("1 -> "+sym);
				value += 1.0;
			}
			else if ( cur.getChar() == 'n' ) { continue; }
			else {
				continue;
			}

			// Check two spaces out
			if ( cur.getNeighbors().length <= i ) continue;
			cur = cur.getNeighbors()[i];
			if ( cur == null ) continue;
			System.out.println("    Checking: "+cur);
			
			if ( cur.getChar() == sym ) {
				System.out.println("2 -> "+sym);
				value += 3.0;
			}
			else if ( cur.getChar() == 'n' ) { continue; }
			else {
				continue;
			}
			
			// Check three spaces out
			if ( cur.getNeighbors().length <= i ) continue;
			cur = cur.getNeighbors()[i];
			if ( cur == null ) continue;
			System.out.println("    Checking: "+cur);
			
			if ( cur.getChar() == sym ) {
				System.out.println("3 -> "+sym);
				value += 9.0;
			}
			else if ( cur.getChar() == 'n' ) { continue; }
			else {
				continue;
			}
		}
		
		
		return value;
	}
}
