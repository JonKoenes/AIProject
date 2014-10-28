/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author f93x834
 */
public class HumanPlayer implements IPlayer {
    
    private boolean notified = false;
    private Node choice = null;
    
    @Override
    public Node play(Node[] choices){
        
        
        boolean done = false;
        notified = false;
        while(!done){
            System.out.print("");
            if(notified){
                done = true;
                System.out.println("I was notified successfully");
            }
        }
        
        return choice;
    }
    
    public void update(Node picked){
        System.out.println("Player was updated");
        notified = true;
        choice = picked;
    }
}
