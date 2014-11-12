/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author f93x834
 */
public class AIPlayer implements IPlayer {

	@Override
	public Node play(Node[] choices) {
		System.out.println("AI played");
		return choices[0];
	}

	@Override
	public void update(Node picked) {

	}
}
