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
		IHeuristic h = new Heuristic3();

		System.out.println("Evaluating Neutral Heurist >> "+move);
		
		char opp;
		if ( tree.getChar() == 'x' ) opp = 'o';
		else opp = 'x';

		double mine = h.evaluateState(in, tree.getChar());
		System.out.println("M:"+mine);
		double enem = h.evaluateState(in, opp);
		System.out.println("E:"+enem);
		
		//if ( mine >= 9000 ) mine = mine % 9000;
		//if ( enem >= 9000 ) return -9000;
		//return (enem/mine); 
		//return (enem-mine); 
		//return (mine+enem); 

		if ( mine >= 9000 ) {
			if ( tree.getChar() == chr ) return 9000;
			else mine = mine % 9000;
		}
		if ( enem >= 9000 ) {
			if ( tree.getChar() == opp ) return -9000;
			else enem = enem % 9000;
		}
		
		//return (mine/enem); 
		return (mine-enem); 
		//return (mine+enem); 
		//return (mine+(enem*0.95)); 

	}
	
	public abstract void resolveNode();

}
