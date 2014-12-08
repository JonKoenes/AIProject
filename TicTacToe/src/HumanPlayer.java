/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jonathan Koenes, Adam Bartz, Stephen Bush
 */
public class HumanPlayer implements IPlayer {

	private boolean notified = false;
	private Node choice = null;
	private Node[][] gameBoard;
	

	@Override
	public Node play(Node[] choices, Node[] state) {

		boolean done = false;
		notified = false;
		while (!done) {
			System.out.print("");
			if (notified) {
				done = true;
				//System.out.println("I was notified successfully");
			}
		}

		return choice;
	}

	@Override
	public void update(Node picked) {
		System.out.println("Player was updated");
		System.out.print("");
		notified = true;
		choice = picked;
	}
	
	public void setBoard(Node[][] board) { }
}
