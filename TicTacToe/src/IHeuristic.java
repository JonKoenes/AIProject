

/**
 * 
 * @author Jonathan Koenes, Adam Bartz, Stephen Bush
 *
 */
public interface IHeuristic {

	public double evaluateState(Node inNode, char inChar);
	
	public double evaluateState(Node[] state, char inChar);
	
	public int getParameter();
	
	public void setParameter();
	
	public String[] getStatus();
}
