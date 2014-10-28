
import javax.swing.JPanel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author f93x834
 */
public class Node {
    
    private int ring, radial;
    private JPanel panel;
    private boolean value;
    
    public Node(int ring, int radial, JPanel panel){
        this.ring = ring;
        this.radial = radial;
        this.panel = panel;
    }
    
    public Node(int ring, int radial){
        this.ring = ring;
        this.radial = radial;
        panel = null;
    }
    
    public JPanel getPanel(){
        return panel;
    }
    
    public int[] getCoord(){
        int[] ret = {ring, radial};
        return ret;
    }
    
    public void setValue(boolean value){
        this.value = value;
    }
    
    public boolean getValue(){
        return value;
    }
    
}
