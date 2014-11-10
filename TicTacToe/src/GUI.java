
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;



/**
 * CSCI 446 AI, Semester Project - Polar Tic-Tac-Toe
 * 
 * @author Adam Bartz, Jonathan Koenes, Stephen Bush
 */

public class GUI extends JPanel {

	private TicTacToe myGame;
	private int p1X, p1Y = -1;
	private boolean blnXY = false;
	private boolean showSquares = false;
	private final int[] gridCoord = { 328, 225, 154, 84, 14 }; // x, y1, y2, y3,
																// y4 (y1 is
																// inner ring &
																// y4 is outer
																// ring)
	private final int _SquareSelect = 40; // size of square space at each
											// intersection can select for move
	private final int _RingIntersect = 12; // how many intersections for each
											// ring
	private IPlayer[] subscribers;
	private Node[] playableNodes, playedNodes;

	public GUI(TicTacToe myGame) {
		setPreferredSize(new Dimension(660, 660));
		addMouseListener(new MSMouseListener());
		this.myGame = myGame;
		subscribers = new IPlayer[3];
                playedNodes = new Node[1];
	}

	public Node[] getPlayableNodes() {
		return playableNodes;
	}
        
        public void setPlayedNodes(Node[] played){
            playedNodes = played;
        }

	public void paint(Graphics g) {
		// clear previous image
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		// draw grid
		BufferedImage myImage = null;
		try {
			java.net.URL myImageURL = TicTacToe.class
					.getResource("images/grid.png");
			myImage = ImageIO.read(myImageURL);
		} catch (IOException e) {
			System.out.println("Image: grid error");
		}
		g.drawImage(myImage, 0, 0, this);
                
                for(int i = 0; i < playedNodes.length; i++){
                    if(playedNodes[i] != null){
                        java.net.URL myImageURL;
                        
                        if(playedNodes[i].getChar() == 'x'){                           
                        myImageURL = TicTacToe.class.getResource("images/x.png");                           
                        }
                        else{
                            myImageURL = TicTacToe.class.getResource("images/o.png");
                        }
                    
                        try{
                            myImage = ImageIO.read(myImageURL);
                        }
                        catch(IOException e){
                            System.out.println("Image error");
                        }
                        int[] coord = playedNodes[i].getCoord();
                        int[] rectXY = createSelectionSquares(coord[0], coord[1]);
                        g.drawImage(myImage, rectXY[0] - _SquareSelect/2, rectXY[1]- _SquareSelect/2, this);
                        
                    }
                }
		// draw choice
		// if (p1X >= 0 && p1Y >= 0) {
		// try {
		// java.net.URL myImageURL;
		// // toggle between O & X placement
		// if (!blnXY)
		// myImageURL = TicTacToe.class.getResource("images/o.png");
		// else
		// myImageURL = TicTacToe.class.getResource("images/x.png");
		// myImage = ImageIO.read(myImageURL);
		// } catch (IOException e) {
		// System.out.println("Image: O error");
		// }
		// //*************temp*************
		// p1X = p1X - (myImage.getWidth() / 2);
		// p1Y = p1Y - (myImage.getHeight() / 2);
		// //*************temp*************
		// g.drawImage(myImage, p1X, p1Y, this);
		// blnXY = !blnXY; // toggle XY placement
		// }
		// ************TEMP************
		// create selection zones
		if (showSquares) {

			for (int row = 0; row < playableNodes.length; row++) {
                            if(playableNodes[row] != null){
				int[] coord = playableNodes[row].getCoord();
				int[] rectXY = createSelectionSquares(coord[0], coord[1]);
				g.setColor(Color.GREEN);
				g.drawRect(rectXY[0], rectXY[1], _SquareSelect, _SquareSelect);
                            }
			}
		}

		// ************TEMP************
	}

	private class MSMouseListener implements MouseListener {
		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			p1X = e.getX(); // get x mouse coord
			p1Y = e.getY(); // get y mouse coord
			//System.out.println("X: " + p1X + "  Y: " + p1Y);

			Node updatee = null;
			for (int i = 0; i < playableNodes.length && playableNodes[i] != null; i++) {
				int[] coord = playableNodes[i].getCoord();
				int[] rectXY = createSelectionSquares(coord[0], coord[1]);
				if (p1X >= rectXY[0] && p1X < rectXY[0] + _SquareSelect
						&& p1Y >= rectXY[1] && p1Y < rectXY[1] + _SquareSelect) {
					updatee = playableNodes[i];
				}
			}
			if (updatee != null) {
				for (int i = 0; i < subscribers.length; i++) {
					if (subscribers[i] != null) {
						//System.out.println("Sent and update to " + i);
						subscribers[i].update(updatee);
					}
				}
			}

			repaint();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			
		}
	}

	public int[] createSelectionSquares(int ring, int row) {
		int[] rectXY = new int[2];
		int radius = gridCoord[0] - gridCoord[ring];
		final double _ToRadians = Math.PI / 180;

		int angle = (360 / _RingIntersect) * row;
		int quadrantX = 1, quadrantY = 1;
		int currRingY = 0, currRingX = 0; // intersection (x, y) coordinates
		// find (x, y) of next intersection on current ring
		if (angle % 180 == 0) {
			quadrantY = 0;
			currRingX = radius;
			if (angle == 180)
				quadrantX = -1;
		} else if (angle % 90 == 0) {
			quadrantX = 0;
			currRingY = -radius;
			if (angle == 90)
				quadrantY = -1;
		} else {
			// find b
			currRingX = (int) (Math.cos(angle * _ToRadians) * radius);
			// find a
			currRingY = (int) (Math.sin(angle * _ToRadians) * radius);
		}
		// find actual coordinates
		rectXY[0] = gridCoord[0] + (currRingX * quadrantX)
				- (_SquareSelect / 2);
		rectXY[1] = gridCoord[0] + (currRingY * quadrantY)
				- (_SquareSelect / 2);

		return rectXY;
	}

	public void setNodes(Node[] nodes) {
		playableNodes = nodes;
	}

	public void SetViewSelectionSquares(boolean newValue) {
		showSquares = newValue;
		repaint();
	}

	public void addSubscriber(IPlayer person) {
		for(int i = 0; i < subscribers.length; i++){
                    if(subscribers[i] == null){
                        subscribers[i] = person;
                        i = subscribers.length;
                    }
                }
	}
        
        public void clearSubs(){
            subscribers = new IPlayer[3];
        }
}