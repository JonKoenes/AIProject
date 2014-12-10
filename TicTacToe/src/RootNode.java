import java.util.LinkedList;


public abstract class RootNode {
	
	double alpha;
	double beta;
	RootNode parent;
	Node move;
	boolean pruned;
	double value;
	LinkedList<RootNode> children;
	MinMaxTree tree;
	char chr;
	int heurType;
	
	static final int DEBUG_LEVEL = 2;

	public RootNode(){}
	public RootNode(Node inMove, RootNode inPar,MinMaxTree t) {
		parent = inPar;
		move = inMove;
		pruned = false;
		tree = t;
		children = new LinkedList<RootNode>();
	}
	
	public abstract void expandNode(Node[] choices);
	
	//public abstract double evaluateHeuristic(Node in);
	
	public double evaluateHeuristic(Node in) {
		//IHeuristic h = new Heuristic3();
		IHeuristic h = tree.getHeuristic();
		
		if ( DEBUG_LEVEL >= 2 ) System.out.println("Evaluating Neutral Heurist >> "+move);
		
		double val = h.evaluateState(tree.getAllNodes(),tree.getChar());
		
		return val;

	}
	
	public abstract void resolveNode();
	
	@Override
	public String toString() {
		String ret = "";
		
		if ( move != null ) ret += move.toString();
		
		if ( alpha != 0 ) {
			ret += "   Alpha: "+alpha;
		}
		else if ( beta != 0 ) {
			ret += "   Beta: "+beta;
		}
		else {
			ret += "   Value: "+value;
		}
		
		return ret;
	}

}
