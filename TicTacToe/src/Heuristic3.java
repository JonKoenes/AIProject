import java.util.LinkedList;




public class Heuristic3 implements IHeuristic {

	private static double VALUE_OF_1ST_SPACE = 1.0; 
	private static double VALUE_OF_2ND_SPACE = 3.0; 
	private static double VALUE_OF_3RD_SPACE = 9.0;
	private final int DEBUG = 0;
	
	private static LinkedList<Node> list = null;

	
	public double evaluateState(Node root, char sym) {
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
