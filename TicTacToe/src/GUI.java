import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * CSCI 446 AI, Semester Project - Polar Tic-Tac-Toe
 * 
 * @author Adam Bartz, Jonathan Koenes, Stephen Bush
 */

public class GUI extends JPanel{
	
	private TicTacToe myGame;
        private int p1X, p1Y = -1;
        private boolean blnXY = false;
	
	public GUI(TicTacToe myGame) {
		setPreferredSize(new Dimension (660, 660));
		addMouseListener(new MSMouseListener());
		this.myGame = myGame;
	}
	
	public void paint(Graphics g) {
            BufferedImage myImage = null;
            // draw grid
            try {
                java.net.URL myImageURL = TicTacToe.class.getResource("images/grid.png");
                myImage = ImageIO.read(myImageURL);
            } catch (IOException e) {
                    System.out.println("Image: grid error");
            }
            g.drawImage(myImage, 0, 0, this);
            // draw choice
            if (p1X >= 0 && p1Y >= 0) {
                try {
                    java.net.URL myImageURL;
                    // toggle between O & X placement
                    if (!blnXY)
                        myImageURL = TicTacToe.class.getResource("images/o.png");
                    else
                        myImageURL = TicTacToe.class.getResource("images/x.png");                        
                    myImage = ImageIO.read(myImageURL);
                } catch (IOException e) {
                        System.out.println("Image: O error");
                }
                //*************temp*************
                p1X = p1X - (myImage.getWidth() / 2);
                p1Y = p1Y - (myImage.getHeight() / 2);
                //*************temp*************
                g.drawImage(myImage, p1X, p1Y, this);
                blnXY = !blnXY;     // toggle XY placement
            }
        }
	
	private class MSMouseListener implements MouseListener {
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
		@Override
		public void mouseClicked(MouseEvent e) {
			p1X = e.getX();		// get x mouse coord
			p1Y = e.getY();		// get y mouse coord
			System.out.println("X: " + p1X + "  Y: " + p1Y);
			repaint();
		}
	}

}