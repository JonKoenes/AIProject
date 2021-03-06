
import javax.swing.JPanel;



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jonathan Koenes, Adam Bartz, Stephen Bush
 */
public class Node {

	private int ring, radial;
        int id;
	private char character = 'n';
	private boolean played;
        private Node[] neighbors = new Node[8];
        private int neighNum = 0;  
        
	public Node(int ring, int radial,int id) {
		this.ring = ring;
		this.radial = radial;
                this.id = id;
	}
        
        public int getId(){
            return id;
        }
        
        public void setXO(char xo){
            character = xo;
        }
        public char getChar(){
            return character;
        }

	public int[] getCoord() {
		int[] ret = { ring, radial };
		return ret;
	}

	public void setValue(boolean value) {
		played = value;
	}

	public boolean getValue() {
		return played;
	}
    
    public void addNeighbor(Node neighbor){
        neighbors[neighNum] = neighbor;
        neighNum++;
    }
    
    public Node[] getNeighbors(){
        Node[] ret = new Node[neighNum];
        for(int i = 0; i < ret.length; i++){
            ret[i] = neighbors[i];
        }
        return ret;
    }
    
    @Override
    public String toString() {
    	String r = "";
    	
    	
    	r += "("+ring+","+radial+") : "+getChar();
    	
        return r;
    }

}
