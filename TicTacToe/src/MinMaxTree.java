import java.util.LinkedList;


public class MinMaxTree {
	
	private Node[][] gameBoard;
	private boolean usingPruning;
	private char chr;
	private LinkedList<RootNode> queue;
	RootNode root;
	char HeuristicType;
	static final int DEBUG_LEVEL = 0;

	
	public MinMaxTree(Node[][] board, char c,boolean prune,char hType) {
		gameBoard = board;
		HeuristicType = hType;
		chr = c;
		usingPruning = prune;
		queue = new LinkedList<RootNode>();
		root = new MaxRootNode(null,null,this);
		queue.add(root);
	}
	
	public Node evaluateTree(int depth, int time) {
		
		long t = System.currentTimeMillis();
		
		RootNode current,temp;
		int dp;
		while ( true ) {
			current = queue.removeFirst();
			if ( DEBUG_LEVEL >= 1 ) System.out.println("Evaluating.....");

			// Mark the nodes
			temp = current;
			while ( temp != null ) {
				if ( temp.move != null ) temp.move.setXO(temp.chr);
				temp = temp.parent;
			}
			
			// Expand the tree
			current.expandNode(getPlayableNodes());
			
			// Un-mark the nodes
			temp = current;
			while ( temp != null ) {
				if ( temp.move != null ) temp.move.setXO('n');
				temp = temp.parent;
			}
						
			// Update the queue
			if ( !current.pruned ) {
				for ( RootNode n : current.children ) queue.add(n);
			}
			
			// Evaluate the break conditions
			if ( queue.size() == 0 ) break;

			temp = queue.peekFirst();
			dp = 1;
			while ( temp.parent != null ) {
				dp++;
				temp = temp.parent;
			}
			if ( dp > depth ) break;

			if ( (System.currentTimeMillis()-t) > time ) break;
		}
		
		// Get the return node
		Node best = null;
		double bestValue = Double.NEGATIVE_INFINITY;
		for ( RootNode n : root.children ) {
			//if ( root.alpha == n.beta ) return n.move;
			if ( DEBUG_LEVEL >= 2 ) System.out.println(n.beta+" > "+bestValue);
			if ( n.beta > bestValue ) {
				best = n.move;
				bestValue = n.beta;
			}
		}
		
		if ( DEBUG_LEVEL >= 1 ) printTree();
		
		return best;
	}
	
	public char getChar() { return chr; }
	public boolean isPruning() { return usingPruning; }
	
	public Node[] getPlayableNodes() {
		LinkedList<Node> list = new LinkedList<Node>();
		
		for (Node[] n1 : gameBoard ) {
			for (Node n2 : n1 ) {
				if ( n2.getChar() != 'n' ) continue;
				
				for ( Node n3 : n2.getNeighbors() ) {
					if ( n3 != null && n3.getChar() != 'n' ) {
						list.add(n2);
						break;
					}
				}
			}
		}
		
		Node[] ret = new Node[list.size()];
		for ( int i = 0; i < ret.length; i++ ) {
			ret[i] = list.removeFirst();
		}
		
		return ret;
	}
	
	public IHeuristic getHeuristic() {
		
		IHeuristic ret = null;
		
		switch (HeuristicType) {
		case '1':
			ret = new Heuristic1();
			break;

		case '2':
			ret = new Heuristic2();
			break;

		case '3':
			ret = new Heuristic3();
			break;
			
		case 'c':
			ret = new Classifier();
			break;

		case 'n':
			ret = new NeuralNet();
			break;
		}
		
		return ret;
		
	}
	
	public void printTree() {
		String[] tree = new String[10];
		for ( int i = 0; i < tree.length; i++ ) tree[i] = "|";

		
		printTreeRecurse(0,root,tree);
		
		tree[0] += "|";
		
		
		for ( int i = 0; i < tree.length; i++ ) {
			if ( tree[i].length() > 1 ) System.out.println(tree[i]);
		}
		
	}
	
	private void printTreeRecurse(int depth, RootNode current, String[] tree) {
	
		for ( RootNode n : current.children ) {
			printTreeRecurse(depth+1,n,tree);
		}
		
		tree[depth] += " ["+current.value+"] ";
		tree[depth+1] += "|";
		
	}
	
	public Node[] getAllNodes() {
		Node[] list = new Node[48];
		int index = 0;
		
		for ( int i = 0; i < gameBoard.length; i++ )
			for ( int i2 = 0; i2 < gameBoard[0].length; i2++ ) {
				list[index] = gameBoard[i][i2];
				index++;
			}
				
		
		
		return list;
	}


	

}
