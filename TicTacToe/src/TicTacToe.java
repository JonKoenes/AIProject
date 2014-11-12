
import java.awt.Color;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;



/**
 * CSCI 446 AI, Semester Project - Polar Tic-Tac-Toe
 * 
 * @author Adam Bartz, Jonathan Koenes, Stephen Bush
 */
public class TicTacToe implements ItemListener {

	private TicTacToe myGame;
        private JFrame frame;
	private JList lstP1, lstP2; // list box of options for player types
	private String strP1, strP2; // p1, p2 selected type
	private IPlayer p1, p2;
	private String[] strPTypes = { "Human", "Computer: H1", "Computer: H2" }; // types
																				// of
																				// Players
	private String strP1Type = "", strP2Type = "";
	private GUI myGUI;
	private JCheckBox chkSquares = new JCheckBox("View Selection Squares");
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

		// populate lists
		lstP1 = new JList(strPTypes);
		lstP2 = new JList(strPTypes);
		// set default settings
		lstP1.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		lstP2.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		lstP1.setLayoutOrientation(JList.VERTICAL_WRAP);
		lstP2.setLayoutOrientation(JList.VERTICAL_WRAP);
		lstP1.setVisibleRowCount(2);
		lstP2.setVisibleRowCount(2);
		// add mouse listeners
		lstP1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					strP1Type = lstP1.getSelectedValue().toString();
				}
			}
		});
		lstP2.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					strP2Type = lstP2.getSelectedValue().toString();
				}
			}
		});
		// add scroll panes
		JScrollPane lstScrlP1 = new JScrollPane(lstP1);
		lstScrlP1.setAlignmentX(LEFT_ALIGNMENT);
		JScrollPane lstScrlP2 = new JScrollPane(lstP2);
		lstScrlP2.setAlignmentX(LEFT_ALIGNMENT);
		// add panels to house lists
		// JPanel lstPane = new JPanel();
		// lstPane.setLayout(new BoxLayout(lstPane, BoxLayout.PAGE_AXIS));
		// JLabel lblP1 = new JLabel("Player 1 Type: " + strP1Type);
		// lblP1.setLabelFor(lstP1);
		// JLabel lblP2 = new JLabel("Player 2 Type: " + strP2Type);
		// lblP2.setLabelFor(lstP2);
		// lstPane.add(lblP1);
		// lstPane.add(lstScrlP1);
		// lstPane.add(lblP2);
		// lstPane.add(lstScrlP2);
		// stats pane
		JPanel statsPane = new JPanel();
		statsPane.setLayout(new GridBagLayout());
		GridBagConstraints layoutConstraintsStats = new GridBagConstraints();
		JLabel lblStats = new JLabel("Statistics");
		layoutConstraintsStats.anchor = GridBagConstraints.NORTH;
		statsPane.add(lblStats, layoutConstraintsStats);
		statsPane.setBorder(BorderFactory.createLineBorder(Color.black));
		statsPane.setPreferredSize(new Dimension(300, 660));
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

		setList1Values("Human"); // default value
		setList2Values("Human"); // default value
		frame.setVisible(true);

		// New Game button listener
		cmdNewGame.addActionListener(new NewButton());
			
		// Quit Game button listener
		cmdQuitGame.addActionListener(new CloseButton());

	}

	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();
		if (source == chkSquares)
			if (chkSquares.isSelected())
				myGUI.SetViewSelectionSquares(true);
			else
				myGUI.SetViewSelectionSquares(false);
	}

	private void setList1Values(String newVal) {
		strP1 = newVal;
		lstP1.setSelectedValue(strP1, true);
	}

	public void playerSelection() {
		Object[] options = { "Human", "AI" };
		String choice = (String) JOptionPane.showInputDialog(null,
				"Player 1 Selection", "Select:", JOptionPane.QUESTION_MESSAGE,
				null, options, options[0]);
		if (choice.equals("Human")) {
			p1 = new HumanPlayer();
		} else {
			p1 = new AIPlayer();
		}

		choice = (String) JOptionPane.showInputDialog(null,
				"Player 2 Selection", "Select:", JOptionPane.QUESTION_MESSAGE,
				null, options, options[0]);
		if (choice.equals("Human")) {
			p2 = new HumanPlayer();
		} else {
			p2 = new AIPlayer();
		}
	}

	private void setList2Values(String newVal) {
		strP2 = newVal;
		lstP2.setSelectedValue(strP2, true);
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

                    Node justPlayed = null;

                    while (!newGame && !winCheck() ) {

                            if (turn == 1) {
                                    justPlayed = p1.play(playableNodes);
                                    System.out.println(turn + " just played");
                                    justPlayed.setValue(true);
                                    justPlayed.setXO('x');
                                    turn = 2;
                            } else if (turn == 2) {
                                    justPlayed = p2.play(playableNodes);
                                    System.out.println(turn + " just played");
                                    justPlayed.setValue(true);
                                    justPlayed.setXO('o');
                                    turn = 1;
                            }                      
                            update(justPlayed);
                    }
                }
            });
            runningGame.start();
	}

	private boolean winCheck() {
		return false;
	}
        
        public void update(Node lastPlayed){
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
