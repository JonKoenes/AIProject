import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

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
public class TicTacToe extends JFrame implements ItemListener {
	
    private static TicTacToe myGame;
    private JList lstP1, lstP2;     // list box of options for player types
    private String strP1, strP2;    // p1, p2 selected type
    private IPlayer p1,p2;
    private String[] strPTypes = {"Human", "Computer: H1", "Computer: H2"};      // types of Players
    private String strP1Type = "", strP2Type = "";
    private GUI myGUI;
    private JCheckBox chkSquares = new JCheckBox("View Selection Squares");
    private int turn;
    private Node[] allnodes,playedNodes,playableNodes;
   
    public TicTacToe () {
        
            super ("Polar Tic-Tac-Toe by ");	// title
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	// close program on close window

            playerSelection();
            

            
            this.setLayout(new GridBagLayout());		// layout to include GUI
            GridBagConstraints layoutConstraints = new GridBagConstraints();
            setResizable(false);

            //create buttons
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
//            JPanel lstPane = new JPanel();
//            lstPane.setLayout(new BoxLayout(lstPane, BoxLayout.PAGE_AXIS));
//            JLabel lblP1 = new JLabel("Player 1 Type: " + strP1Type);
//            lblP1.setLabelFor(lstP1);
//            JLabel lblP2 = new JLabel("Player 2 Type: " + strP2Type);
//            lblP2.setLabelFor(lstP2);
//            lstPane.add(lblP1);
//            lstPane.add(lstScrlP1);
//            lstPane.add(lblP2);
//            lstPane.add(lstScrlP2);
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
            //add to layout
            layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
            layoutConstraints.gridx = 0;
            myGUI = new GUI(this);
            this.add(myGUI, layoutConstraints);
            layoutConstraints.fill = GridBagConstraints.BOTH;
            layoutConstraints.gridx = 1;
            this.add(statsPane, layoutConstraints);
            layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
            layoutConstraints.gridy = 1;
            //this.add(lstPane, layoutConstraints);
            layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
            layoutConstraints.gridx = 0;
            this.add(buttonsPane, layoutConstraints);
            pack();
            setLocationRelativeTo(null);

            setList1Values("Human");    // default value
            setList2Values("Human");    // default value
            setVisible(true);

            // New Game button listener
            cmdNewGame.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                            System.out.println("New Game");
                            myGame = new TicTacToe();
                    }
            });
            // Quit Game button listener
            cmdQuitGame.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                            System.exit(0);
                    }
            });
            
            

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
        public void playerSelection(){
            Object[] options = {"Human", "AI"};
            String choice = (String) JOptionPane.showInputDialog(null,"Player 1 Selection", "Select:", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if(choice.equals("Human")){
                p1 = new HumanPlayer();
            }
            else{
                p1 = new AIPlayer();
            }
            
            choice = (String) JOptionPane.showInputDialog(null,"Player 2 Selection", "Select:", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if(choice.equals("Human")){
                p2 = new HumanPlayer();
            }
            else{
                p2 = new AIPlayer();
            }
        }
        
        private void setList2Values(String newVal) {
            strP2 = newVal;
            lstP2.setSelectedValue(strP2, true);
        }
        private void playGame(){
            turn = 1;
            allnodes = makeNodes();
            playedNodes = new Node[48];
            playableNodes = allnodes;
            
            
            myGUI.setNodes(allnodes);
            myGUI.addSubscriber(p1);
            myGUI.addSubscriber(p2);
            //myGUI.addSubscriber(this);
            
            
            while(!winCheck()){
                
                if(turn == 1){
                    p1.play(playableNodes);
                    turn = 2;
                }
                else if(turn == 2){
                    p2.play(playableNodes);
                    turn = 1;
                }
                
            }
        }
        
        private boolean winCheck(){
            return false;
        }
        
        private Node[] makeNodes(){
            Node[] ret = new Node[48];
            int pos = 0;
            for(int i = 0; i < 12; i++){
                for(int j = 1; j <= 4 ; j++){
                    ret[pos] = new Node(j, i);
                    pos++;
                }
            }
            
            return ret;
        }


        public static void main(String[] args) {
		myGame = new TicTacToe();
                myGame.playGame();
	}
}


