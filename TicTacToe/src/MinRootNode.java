
public class MinRootNode extends RootNode {

	public MinRootNode(Node inMove, RootNode inPar,MinMaxTree t) {
		super(inMove,inPar,t);
		chr = t.getChar();
		
		
		inMove.setXO(chr);
		value = evaluateHeuristic(inMove);

		System.out.println(inMove);
		System.out.println(value);
		
		inMove.setXO('n');

		//beta = Double.MAX_VALUE;
		beta = value;
	}
	
	@Override
	public void expandNode(Node[] choices) {
		
		MaxRootNode tempNode;
		for ( Node n : choices ) {
			tempNode = new MaxRootNode(n,this,tree);
			children.add(tempNode);
			
			if ( tree.isPruning() && parent != null && beta < parent.alpha ) {
				pruned = true;
				return;
			}
		}
				
		resolveNode();
	}
	
	public void resolveNode() {
		//beta = value;
		beta = Double.MAX_VALUE;

		System.out.println("Resolving: B-"+beta);
		for ( RootNode n : children ) {
			System.out.println(n.alpha);
			if ( n.alpha < beta ) beta = n.alpha;
		}
		
		System.out.println("Resolved: B-"+beta);
		if ( parent != null ) parent.resolveNode();
	}	
	
	/*
	public double evaluateHeuristic(Node in) {
		IHeuristic h = new Heuristic3();

		System.out.println("Evaluating Min >> "+move);
		
		char opp;
		if ( chr == 'x' ) opp = 'o';
		else opp = 'x';

		double mine = h.evaluateState(in, chr);
		System.out.println("M:"+mine);
		double enem = h.evaluateState(in, opp);
		System.out.println("E:"+enem);
		
		//if ( mine >= 9000 ) mine = mine % 9000;
		//if ( enem >= 9000 ) return -9000;
		//return (enem/mine); 
		//return (enem-mine); 
		//return (mine+enem); 

		if ( mine >= 9000 ) return 9000;
		if ( enem >= 9000 ) enem = enem % 9000;
		//return (mine/enem); 
		return (mine-enem); 
		//return (mine+enem); 
		//return (mine+(enem*0.95)); 

		
	}
	/* */
	
	


	

}
