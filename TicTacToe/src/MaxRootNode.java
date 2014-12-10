import java.util.LinkedList;


public class MaxRootNode extends RootNode {

	
	public MaxRootNode(Node inMove, RootNode inPar,MinMaxTree t) {
		super(inMove,inPar,t);
		chr = t.getChar();
		
		if ( chr == 'x' ) chr = 'o';
		else chr = 'x';

		if ( inMove != null ) {
			inMove.setXO(chr);
			value = evaluateHeuristic(inMove);
			inMove.setXO('n');
			if ( DEBUG_LEVEL >= 1 ) System.out.println(inMove+", "+value);
		}

		alpha = Double.NEGATIVE_INFINITY;
		//alpha = value;
	}
	
	@Override
	public void expandNode(Node[] choices) {
		
		MinRootNode tempNode;
		for ( Node n : choices ) {
			tempNode = new MinRootNode(n,this,tree);
			children.add(tempNode);
		}
		
	}
	
	public void resolveNode() {
		//alpha = value;
		alpha = Double.MIN_VALUE;

		if ( DEBUG_LEVEL >= 2 ) System.out.println("Resolving: A-"+alpha);
		for ( RootNode n : children ) {
			if ( DEBUG_LEVEL >= 3 ) System.out.println(n.beta);
			System.out.println(">> TEST "+n.beta +" > "+alpha);
			if ( n.beta > alpha ) alpha = n.beta;
		}
		if ( children.size() == 0 ) {
			System.out.println(">> TEST == "+alpha);			
			alpha = value;
		}

		if ( parent != null ) {
			parent.resolveNode();
			
		}
		
		if ( tree.isPruning() && parent != null && alpha > parent.beta ) {
			if ( DEBUG_LEVEL >= 2 ) System.out.println("Pruned A-"+alpha);			
			pruned = true;
		}
		
		if ( DEBUG_LEVEL >= 2 ) System.out.println("Resolved: A-"+alpha);
	}

	/*
	public double evaluateHeuristic(Node in) {
		IHeuristic h = new Heuristic3();
		
		System.out.println("Evaluating Max >> "+move);
		
		char opp;
		if ( chr == 'x' ) opp = 'o';
		else opp = 'x';

		double mine = h.evaluateState(in, chr);
		System.out.println("M:"+mine);
		double enem = h.evaluateState(in, opp);
		System.out.println("E:"+enem);
		
		//if ( mine >= 9000 ) return 9000;
		//if ( enem >= 9000 ) enem = enem % 9000;
		//return (mine/enem); 
		//return (mine-enem); 
		//return (mine+enem); 

		
		if ( mine >= 9000 ) mine = mine % 9000;
		if ( enem >= 9000 ) return -9000;
		//return (enem/mine); 
		return (enem-mine); 
		//return (mine+enem);
		//return (enem+(mine*0.95)); 


		
	}
	/* */

}
