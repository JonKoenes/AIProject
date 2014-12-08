
import static java.awt.Component.LEFT_ALIGNMENT;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;



/**
 * CSCI 446 AI, Semester Project - Polar Tic-Tac-Toe
 * 
 * @author Jonathan Koenes, Adam Bartz, Stephen Bush
 */
public class TicTacToe implements ItemListener {

	private TicTacToe myGame;
    private JFrame frame;
	private IPlayer p1, p2;																				// of
																				// Players
	private GUI myGUI;
	private JCheckBox chkSquares = new JCheckBox("View Selection Squares");
	private JCheckBox chkStatus = new JCheckBox("Show Status");
	private boolean blnStatus = false;
	private JTextArea myStatusReport;

	private int turn;
        private static volatile boolean newGame = false, locked = false;
	private Node[] allnodes, playedNodes, playableNodes;
        private Node[][] gameBoard;
        private Thread runningGame;

	public TicTacToe() {
            frame = new JFrame("PolarTic-Tac-Toe by " );
		

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // close program on
                myGame = this;
														// close window

		playerSelection();

		frame.setLayout(new GridBagLayout()); // layout to include GUI
		GridBagConstraints layoutConstraints = new GridBagConstraints();
		frame.setResizable(false);

		// create buttons
		JButton cmdNewGame = new JButton("New Game");
		cmdNewGame.setMnemonic(KeyEvent.VK_N);
		JButton cmdQuitGame = new JButton("Quit Game");
		cmdQuitGame.setMnemonic(KeyEvent.VK_Q);
		chkSquares.setMnemonic(KeyEvent.VK_V);
		chkSquares.addItemListener(this);
		chkStatus.setMnemonic(KeyEvent.VK_S);
		chkStatus.addItemListener(this);

		// stats pane
		JPanel statsPane = new JPanel();
		statsPane.setLayout(new GridBagLayout());
		GridBagConstraints layoutConstraintsStats = new GridBagConstraints();
		JLabel lblStats = new JLabel("Statistics");
		layoutConstraintsStats.gridwidth = 2;
		layoutConstraintsStats.anchor = GridBagConstraints.NORTH;
		layoutConstraintsStats.fill = GridBagConstraints.HORIZONTAL;
		statsPane.add(lblStats, layoutConstraintsStats);
		statsPane.setBorder(BorderFactory.createLineBorder(Color.black));
		statsPane.setPreferredSize(new Dimension(300, 660));
		myStatusReport = new JTextArea();
		myStatusReport.setColumns(25);
		myStatusReport.setLineWrap(true);
		myStatusReport.setRows(39);
		//myStatusReport.setEditable(false);
		layoutConstraintsStats.gridy = 1;
		layoutConstraintsStats.gridwidth = 1;
		layoutConstraintsStats.fill = GridBagConstraints.BOTH;		
		statsPane.add(myStatusReport, layoutConstraintsStats);
		JScrollPane myScrollPane = new JScrollPane(myStatusReport);
		myScrollPane.setAlignmentX(LEFT_ALIGNMENT);
		layoutConstraintsStats.gridx = 1;
		statsPane.add(myScrollPane, layoutConstraintsStats);
		
		// buttons pane
		JPanel buttonsPane = new JPanel();
		buttonsPane.setLayout(new GridBagLayout());
		GridBagConstraints layoutConstraintsButtons = new GridBagConstraints();
		layoutConstraintsButtons.fill = GridBagConstraints.HORIZONTAL;
		buttonsPane.add(cmdNewGame, layoutConstraintsButtons);
		layoutConstraintsButtons.gridx = 1;
		buttonsPane.add(cmdQuitGame, layoutConstraintsButtons);
		layoutConstraintsButtons.gridx = 2;
		buttonsPane.add(chkSquares, layoutConstraintsButtons);
		layoutConstraintsButtons.gridx = 3;
		buttonsPane.add(chkStatus, layoutConstraintsButtons);

		// add to layout
		layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
		layoutConstraints.gridx = 0;
		myGUI = new GUI(this);
		frame.add(myGUI, layoutConstraints);
		layoutConstraints.fill = GridBagConstraints.BOTH;
		layoutConstraints.gridx = 1;
		frame.add(statsPane, layoutConstraints);
		layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
		layoutConstraints.gridy = 1;
		// this.add(lstPane, layoutConstraints);
		layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
		layoutConstraints.gridx = 0;
		frame.add(buttonsPane, layoutConstraints);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		// New Game button listener
		cmdNewGame.addActionListener(new NewButton());
			
		// Quit Game button listener
		cmdQuitGame.addActionListener(new CloseButton());

	}

	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		if (source == chkSquares) {
			if (chkSquares.isSelected())
				myGUI.SetViewSelectionSquares(true);
			else
				myGUI.SetViewSelectionSquares(false);
		} else if (source == chkStatus) {
			blnStatus = chkStatus.isSelected();
		}
	}

	public void playerSelection() {
		Object[] options = { "Human", "AI: H1", "AI: H2", "AI: Classifier", "AI: NeuralNet" };
		String choice = null;
		while (choice == null) {
			choice = (String) JOptionPane.showInputDialog(null,
					"Player 1 Selection", "Select:", JOptionPane.QUESTION_MESSAGE,
					null, options, options[0]);

			if (choice == null) {
			    int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Quit?", JOptionPane.YES_NO_OPTION);
		        if (reply == JOptionPane.YES_OPTION)
		          System.exit(0);

			} else if (choice.equals("Human")) {
				p1 = new HumanPlayer();
			} else {
				char type = '1';
				if (choice.equals("AI: H1"))					// heuristic 1
					type = '1';
				else if (choice.equals("AI: H2"))				// heuristic 2
					type = '2';
				else if (choice.equals("AI: Classifier"))		// classifier
					type = 'c';
				else if(choice.equals("AI: NeuralNet"))											// neural net
					type = 'n';
				p1 = new AIPlayer('x', type);
			}
		}

		choice = null;
		while (choice == null) {
			choice = (String) JOptionPane.showInputDialog(null,
					"Player 2 Selection", "Select:", JOptionPane.QUESTION_MESSAGE,
					null, options, options[0]);
			
			if (choice == null) {
			    int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?", "Quit?", JOptionPane.YES_NO_OPTION);
		        if (reply == JOptionPane.YES_OPTION)
		          System.exit(0);
			
			} else if (choice.equals("Human")) {
				p2 = new HumanPlayer();
			} else {
				char type = '1';
				if (choice.equals("AI: H1"))					// heuristic 1
					type = '1';
				else if (choice.equals("AI: H2"))				// heuristic 2
					type = '2';
				else if (choice.equals("AI: Classifier"))		// classifier
					type = 'c';
				else if(choice.equals("AI: NeuralNet"))											// neural net
					type = 'n';
				p2 = new AIPlayer('o', type);
			}
		}
	}

	private void playGame() {            
            
            runningGame = new Thread(new Runnable() {
                
                @Override
                public void run() {
                
                    turn = 1;
                    newGame = false;
                    allnodes = makeNodes();
                    setNeighbors();
                    printboard();
                    playedNodes = new Node[48];
                    playableNodes = allnodes;

                    myGUI.setNodes(allnodes);
                    myGUI.setPlayedNodes(playedNodes);
                    myGUI.repaint();
                    myGUI.clearSubs();
                    myGUI.addSubscriber(p1);
                    myGUI.addSubscriber(p2);
                    // myGUI.addSubscriber(this);
                    Resolution.setRules();

                    Node justPlayed = null;

                    while (!newGame && !winCheck() ) {

                            if (turn == 1) {
                                    justPlayed = p1.play(playableNodes, allnodes);
                                    System.out.println(turn + " just played");
                                    justPlayed.setValue(true);
                                    justPlayed.setXO('x');
                                    turn = 2;
                            } else if (turn == 2) {
                                    justPlayed = p2.play(playableNodes, allnodes);
                                    System.out.println(turn + " just played");
                                    justPlayed.setValue(true);
                                    justPlayed.setXO('o');
                                    turn = 1;
                            }                      
                            update(justPlayed);
                            if (blnStatus)
                            	loadStatus();
                    }
                }
            });
            runningGame.start();
	}

	private boolean winCheck() {
		String winner = Resolution.resolutionWinCheck();
		if(winner.equals("X")){
			JOptionPane.showMessageDialog(null, "X wins the game!", "Winner", 0);
			return true;
		}
		else if(winner.equals("O")){
			JOptionPane.showMessageDialog(null, "O wins the game!", "Winner", 0);
			return true;
		}
		else{
			for(int i = 0; i < playedNodes.length; i++){
				if(playedNodes[i] == null){
					return false;
				}
			}	
			
			JOptionPane.showMessageDialog(null, "It's a cat game!", "Tie", 0);
		}
		return true;
	}
        
        public void update(Node lastPlayed){
        	Resolution.addFact(lastPlayed);
            int playedNodesIndex = 0;
            playedNodes = new Node[48];
            
            for(int i = 0; i < allnodes.length; i++){
                if(!(allnodes[i].getChar() == 'n')){
                    playedNodes[playedNodesIndex] = allnodes[i];
                    playedNodesIndex++;
                }
            }
            
            int playableIndex = 0;
            playableNodes = new Node[48];
            
            for(int i = 0; i < playedNodesIndex; i++){
                Node[] neighbors = playedNodes[i].getNeighbors();
                for(int j = 0; j < neighbors.length; j++){
                  	if ( neighbors[j] == null ) continue;
                    if(!neighbors[j].getValue()){
                        boolean inlist = false;
                        for(int k = 0; k < playableIndex; k++){
                            if(playableNodes[k].getId() == neighbors[j].getId()){
                                inlist = true;
                                k = playableIndex;
                            }
                        }
                        if(!inlist){
                            playableNodes[playableIndex] = neighbors[j];
                            playableIndex++;
                        }
                    }
                }
            }
            
            myGUI.setNodes(playableNodes);
            myGUI.setPlayedNodes(playedNodes);
            myGUI.repaint();
        }

	private Node[] makeNodes() {
		Node[] ret = new Node[48];
                gameBoard = new Node[12][4];
		int pos = 0;
		for (int i = 0; i < 12; i++) {
			for (int j = 1; j <= 4; j++) {
				ret[pos] = new Node(j, i, pos);
                                gameBoard[i][j - 1] = ret[pos];
				pos++;
			}
		}
		return ret;
	}
        
        public void setNeighbors(){
            for(int i = 0; i < gameBoard.length; i++){
                for(int j = 0; j < gameBoard[i].length; j++){
                    
                    if(j - 1 >= 0){
                    	gameBoard[i][j].addNeighbor(gameBoard[(i+1)%12][(j+3)%4]);
                    	gameBoard[i][j].addNeighbor(gameBoard[i][(j+3)%4]);
                    	gameBoard[i][j].addNeighbor(gameBoard[(i+11)%12][(j+3)%4]);
                    }
                    //*
                    else {
                    	gameBoard[i][j].addNeighbor(null);
                    	gameBoard[i][j].addNeighbor(null);
                    	gameBoard[i][j].addNeighbor(null);
                    }
                    /* */
                    //middle
                    gameBoard[i][j].addNeighbor(gameBoard[(i+1)%12][j]);
                    gameBoard[i][j].addNeighbor(gameBoard[(i+11)%12][j]);
                        
                    if(j + 1 < 4){
                    	gameBoard[i][j].addNeighbor(gameBoard[(i+1)%12][(j+1)%4]);
                    	gameBoard[i][j].addNeighbor(gameBoard[i][(j+1)%4]);
                    	gameBoard[i][j].addNeighbor(gameBoard[(i+11)%12][(j+1)%4]);
                    }
                    //*
                    else {
                    	gameBoard[i][j].addNeighbor(null);
                    	gameBoard[i][j].addNeighbor(null);
                    	gameBoard[i][j].addNeighbor(null);
                    }
                    /* */         
                }
            }
        }
        
        private void loadStatus() {
        	try {
        		BufferedReader myBuffRead = new BufferedReader(new FileReader("data/status.txt"));
        		String line;
        		while ((line = myBuffRead.readLine()) != null) {
        			myStatusReport.append(line);
        		}
        		myBuffRead.close();
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
        }

        public void printboard()
        {
            for(int i = 0; i < gameBoard.length; i++){
                for(int j = 0; j < gameBoard[i].length; j++){
                    System.out.print(gameBoard[i][j].getId() + " ");
                }
                System.out.println();
            }
        }

	public static void main(String[] args) {
		TicTacToe myGame = new TicTacToe();
		myGame.playGame();
	}
        
        private class NewButton implements ActionListener{
        @Override
            public void actionPerformed(ActionEvent arg0) 
            {
                System.out.println("New Game");
                newGame = true;
               
                runningGame.stop();
                Resolution.reset();
                
                myGame.playerSelection();
                myGame.playGame();
            }
        }
        
        private class CloseButton implements ActionListener{
            @Override
            public void actionPerformed(ActionEvent arg0) {
		System.exit(0);
            }
        }
        
   
}
