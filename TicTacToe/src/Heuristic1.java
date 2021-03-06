

/**
 * 
 * @author Jonathan Koenes, Adam Bartz, Stephen Bush
 *
 */
public class Heuristic1 implements IHeuristic {

	private static double VALUE_OF_1ST_SPACE = 1.0; 
	private static double VALUE_OF_2ND_SPACE = 3.0; 
	private static double VALUE_OF_3RD_SPACE = 9.0; 
	private final int DEBUG = 0;

	
	public double evaluateState(Node root, char sym) {
		
		if ( DEBUG >= 1 ) System.out.println("Evaluating..."+ root);
		
		if ( root.getChar() != 'n' ) {
			if ( DEBUG >= 1 ) System.out.println("Not Available");		
			return 0;
		}
		
		double value = 0.0;
		int count[] = {0,0,0,0};
		int adjCount[] = {0,0,0,0};
		int width[] = {0,0,0,0};
		int indexes[] = {0,1,2,3,3,2,1,0};
		int inARow = 0;
		if ( DEBUG >= 1 ) System.out.println("Root: "+root+" Searching for "+sym);
		
		Node[] neighbors = root.getNeighbors();
		Node cur;
		for ( int i = 0; i < 8; i++ ) {
			inARow = 0;
					
			// Check one space out
			if ( neighbors.length <= i ) continue;
			cur = neighbors[i];
			if ( cur == null ) continue;
			if ( DEBUG >= 2 ) System.out.println("["+i+"]  Starting At: "+cur);
			
			if ( cur.getChar() == sym ) {
				if ( DEBUG >= 2 ) System.out.println("1 -> "+sym+" to Index "+indexes[i]);
				count[indexes[i]]++;
				if ( inARow == 0 ) {
					adjCount[indexes[i]]++;
					inARow++;
				}
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
			if ( DEBUG >= 2 ) System.out.println("    Checking: "+cur);
			
			if ( cur.getChar() == sym ) {
				if ( DEBUG >= 2 ) System.out.println("1 -> "+sym+" to Index "+indexes[i]);
				count[indexes[i]]++;
				if ( inARow == 1 ) {
					adjCount[indexes[i]]++;
					inARow++;
				}
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
			if ( DEBUG >= 2 ) System.out.println("    Checking: "+cur);
			
			if ( cur.getChar() == sym ) {
				if ( DEBUG >= 2 ) System.out.println("1 -> "+sym+" to Index "+indexes[i]);
				count[indexes[i]]++;
				if ( inARow == 2 ) {
					adjCount[indexes[i]]++;
					inARow++;
				}
			}
			else if ( cur.getChar() == 'n' ) {  }
			else {
				continue;
			}
			width[indexes[i]]++;
			
		}
		
		// Check for Immediate wins
		for ( int i = 0; i < adjCount.length; i++ ) {
			if ( adjCount[i] >= 3 ) value += 9000.0;
		}
		
		// Sum up the counts
		for ( int i = 0; i < count.length; i++ ) {
			if ( DEBUG >= 2 ) System.out.println("Summing... "+width[i]+","+count[i]);
			if ( width[i] < 3 ) continue;
			if ( count[i] >= 1 ) value += VALUE_OF_1ST_SPACE;
			if ( count[i] >= 2 ) value += VALUE_OF_2ND_SPACE;
			if ( count[i] >= 3 ) value += VALUE_OF_3RD_SPACE;
		}
		if ( DEBUG >= 1 ) System.out.println("Got "+value);		
		return value;
		
	}


	@Override
	public double evaluateState(Node[] state, char inChar) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getParameter() {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public void setParameter() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String[] getStatus() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
