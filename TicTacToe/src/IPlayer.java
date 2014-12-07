/**
 * 
 * @author Jonathan Koenes, Adam Bartz, Stephen Bush
 *
 */
public interface IPlayer {
		
	public Node play(Node[] choices, Node[] state);

	public void update(Node picked);

}
