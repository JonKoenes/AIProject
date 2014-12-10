
public class MinRootNode extends RootNode {

	public MinRootNode(Node inMove, RootNode inPar,MinMaxTree t) {
		super(inMove,inPar,t);
		chr = t.getChar();
		
		
		inMove.setXO(chr);
		value = evaluateHeuristic(inMove);

		if ( DEBUG_LEVEL >= 1 ) System.out.println(inMove + ", "+value);
		
		inMove.setXO('n');

		beta = Double.MAX_VALUE;
		//beta = value;
	}
	
	@Override
	public void expandNode(Node[] choices) {
		
		MaxRootNode tempNode;
		for ( Node n : choices ) {
			tempNode = new MaxRootNode(n,this,tree);
			children.add(tempNode);
		}
				
	}
	
	public void resolveNode() {
		//beta = value;
		beta = Double.POSITIVE_INFINITY;

		if ( DEBUG_LEVEL >= 2 ) System.out.println("Resolving: B-"+beta);
		for ( RootNode n : children ) {
			if ( DEBUG_LEVEL >= 3 ) System.out.println(n.alpha);
			System.out.println(">> TEST "+n.alpha +" < "+beta);
			if ( n.alpha < beta ) beta = n.alpha;
		}
		if ( children.size() == 0 ) {
			System.out.println(">> TEST == "+beta);			
			beta = value;
		}
		
		if ( tree.isPruning() && parent != null && beta < parent.alpha ) {
			if ( DEBUG_LEVEL >= 2 ) System.out.println("Pruned B-"+beta);			
			pruned = true;
		}

		if ( DEBUG_LEVEL >= 2 ) System.out.println("Resolved: B-"+beta);
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
