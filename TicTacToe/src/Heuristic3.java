import java.util.LinkedList;




public class Heuristic3 implements IHeuristic {

	private static double VALUE_OF_1ST_SPACE = 1.0; 
	private static double VALUE_OF_2ND_SPACE = 3.0; 
	private static double VALUE_OF_3RD_SPACE = 9.0;
	private final int DEBUG = 0;
	
	private static LinkedList<Node> list = null;


	public double evaluateState(Node root, char sym) {
		char opp;
		if ( sym == 'x' ) opp = 'o';
		else opp = 'x';

		double mine = evaluateSide(root, sym);
		if ( DEBUG >= 2 ) System.out.println("M:"+mine);
		double enem = evaluateSide(root, opp);
		if ( DEBUG >= 2 ) System.out.println("E:"+enem);
		
		if ( mine >= 9000 ) {
			return 9000;
		}
		if ( enem >= 9000 ) {
			if ( sym == opp ) return -9000;
		}
		
		return mine;
	}
	
	public double evaluateSide(Node root, char sym) {
		if ( list == null ) list = getAllNodes(root);
		double ret = 0;
		
		IHeuristic h = new Heuristic1();
		
		for ( Node n : list ) {
			ret += h.evaluateState(n, sym);
		}
		
		return ret;
	}
	
	private LinkedList<Node> getAllNodes(Node in) {
		LinkedList<Node> list = new LinkedList<Node>();
		list.add(in);
		int i = 0;
		while ( i < list.size() ) {
			for ( Node n : list.get(i).getNeighbors() ) {
				if ( n != null && !list.contains(n) ) list.addLast(n);
			}
			i++;
		}
		
		return list;
	}

	@Override
	public double evaluateState(Node[] state, char inChar) {
		
		double sum = 0.0;
		double temp = 0.0;
		for ( Node n : state ) {
			temp = evaluateState(n,inChar);
			if ( temp == 9000 ) return 9000;
			if ( temp == -9000 ) return -9000;
			
			sum += temp;
		}
		
		return sum;
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
