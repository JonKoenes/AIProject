import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;


/**
 * CSCI 446 AI, Semester Project - Polar Tic-Tac-Toe
 * 
 * @author Adam Bartz, Jonathan Koenes, Stephen Bush
 */
public class TicTacToe extends JFrame {
	
	public TicTacToe () {
		super ("Polar Tic-Tac-Toe by ");	// title
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		// close program on close window
		
		this.setLayout(new GridBagLayout());		// layout to include GUI
		GridBagConstraints layoutConstraints = new GridBagConstraints();
		layoutConstraints.fill = GridBagConstraints.HORIZONTAL;		// stretch buttons to fill grid cells
		
		//setResizable(false);

		//create buttons
		JButton cmdNewGame = new JButton("New Game");
		cmdNewGame.setMnemonic(KeyEvent.VK_N);
		JButton cmdQuitGame = new JButton("Quit Game");
		cmdQuitGame.setMnemonic(KeyEvent.VK_Q);
		
		//add to layout
		layoutConstraints.gridwidth = 2;
		this.add(new GUI(this), layoutConstraints);
		layoutConstraints.gridy = 1;
		layoutConstraints.gridwidth = 1;
		this.add(cmdNewGame, layoutConstraints);
		this.add(cmdQuitGame, layoutConstraints);
		pack();
		setLocationRelativeTo(null);
		
		setVisible(true);

		// New Game button listener
		cmdNewGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("New Game");
			}
		});
		// Quit Game button listener
		cmdQuitGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

	}
	
	public static void main(String[] args) {
		TicTacToe myGame = new TicTacToe();
	}
}
