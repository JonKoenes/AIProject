import java.util.ArrayList;


public class Resolution {
	
	private static ArrayList<String> facts = new ArrayList<String>();
	private static ArrayList<String> rules = new ArrayList<String>();
	private static ArrayList<String> clauses = new ArrayList<String>();
	
	public static String resolutionWinCheck(){
		
		boolean answers = backChain("win(X)");
		if(answers){
			return "X";
		}
		
		answers = backChain("win(O)");
		if(answers){
			return "O";
		}
		
		return "F";
	}
	
	private static boolean backChain(String goal){
		//return true for "noise" values
		if(goal == null || goal.equals(" ")){
			return true;
		}
		
		
		//go through clauses and find a predicate that matches current goal, and if found, unify and apply that unification string
		String current;
		for(int i = 0; i < clauses.size(); i++){
			 current = clauses.get(i);
			//first, check if the clause contains a predicate, if so, attempt unification
			if(current.contains("|")){
				String predicate = current.substring(current.indexOf("|"), current.length());
				
				String[][] theta = new String[10][2];
				theta = unify(predicate, goal, theta);
				
				//if unify succeeded, then we apply the unification, and backchain with it as our new goal
				if(!theta[0][0].equals("failure")){
					String newGoal = applyUnification(current, goal, theta);
					boolean goalReached = backChain(newGoal);
				
					//if we reached the goal, we found a successful chain, otherwise we just move on to the next clause
					if(goalReached){
						return true;
					}
				}	
			}
		}
		
		
		
		//go through facts to see if the goal is a fact
		for(int i = 0; i < facts.size(); i++){
			if(goal.equals(facts.get(i))){
				return true;
			}
		}
		
		//break apart multiple list into separate clauses and backchain separately
				if(goal.contains("&")){
					String[] split = goal.split("&");
					for(int i = 0; i < split.length; i++){
						//if an individual piece of the goal fails, then this chain fails
						if(!backChain(split[i])){
							return false;
						}
						
					}
				}
		
		//all other cases return false, as the backchain did not result in achieving the goal
		return false;
	}
	
	private static String makeFact(Node newFact){
		//makes predicate logic sentence for a node
		String owner = "" + newFact.getChar();
		int[] coord = newFact.getCoord();
		String radial = "" + coord[1];
		String ring = "" + coord[0];
		return "at(" + owner + "," + ring + "," + radial + ")";
	}
	
	public static void addFact(Node newFact){
		//make a new clause for the new fact
		String newClause;
		if(newFact != null){
			newClause = makeFact(newFact);
			facts.add(newClause);
		}
		//add new clauses when our fact is unified with the given rules for a win
		
		for(int i = 0; i < rules.size(); i++){
			
			String[][] unification = new String[10][2];
			unification = unify(newClause, rules.get(i), unification);
			
			if(unification[0][0] != null){
				String unified = applyUnification(newClause, rules.get(i), unification);
				clauses.add(unified);
			}
		}
		
	}
	
	private static String[][] unify(String sen1, String sen2, String[][] currentSub){
		
	}
	
	private static String applyUnification(String sen1, String sen2, String[][] substitution){
		String newSentence = "";
		
		return newSentence;
	}
	
	public static void setRules(){
		//vertical wins across a radial
		rules.add("!at(player,ring,radial)&!at(player,ring-1,radial)&!at(player,ring-2,radial)&!at(player,ring-3,radial)|win(player)");
		rules.add("!at(player,ring,radial)&!at(player,ring+1,radial)&!at(player,ring-1,radial)&!at(player,ring-2,radial)|win(player)");
		rules.add("!at(player,ring,radial)&!at(player,ring+2,radial)&!at(player,ring+1,radial)&!at(player,ring-1,radial)|win(player)");
		rules.add("!at(player,ring,radial)&!at(player,ring+3,radial)&!at(player,ring+2,radial)&!at(player,ring+1,radial)|win(player)");
		
		//horizontal wins across a ring
		rules.add("!at(player,ring,radial)&!at(player,ring,radial-1)&!at(player,ring,radial-2)&!at(player,ring,radial-3)|win(player)");
		rules.add("!at(player,ring,radial)&!at(player,ring,radial+1)&!at(player,ring,radial-1)&!at(player,ring,radial-2)|win(player)");
		rules.add("!at(player,ring,radial)&!at(player,ring,radial+2)&!at(player,ring,radial+1)&!at(player,ring,radial-1)|win(player)");
		rules.add("!at(player,ring,radial)&!at(player,ring,radial+3)&!at(player,ring,radial+2)&!at(player,ring,radial+1)|win(player)");
		
		//spiral wins across rings and radials 
		rules.add("!at(player,ring,radial)&!at(player,ring-1,radial-1)&!at(player,ring-2,radial-2)&!at(player,ring-3,radial-3)|win(player)");
		rules.add("!at(player,ring,radial)&!at(player,ring+1,radial+1)&!at(player,ring-1,radial-1)&!at(player,ring-2,radial-2)|win(player)");
		rules.add("!at(player,ring,radial)&!at(player,ring+2,radial+2)&!at(player,ring+1,radial+1)&!at(player,ring-1,radial-1)|win(player)");
		rules.add("!at(player,ring,radial)&!at(player,ring+3,radial+3)&!at(player,ring+2,radial+2)&!at(player,ring+1,radial+1)|win(player)");
		
		//the other spiral
		rules.add("!at(player,ring,radial)&!at(player,ring-1,radial+1)&!at(player,ring-2,radial+2)&!at(player,ring-3,radial+3)|win(player)");
		rules.add("!at(player,ring,radial)&!at(player,ring+1,radial-1)&!at(player,ring-1,radial+1)&!at(player,ring-2,radial+2)|win(player)");
		rules.add("!at(player,ring,radial)&!at(player,ring+2,radial-2)&!at(player,ring+1,radial-1)&!at(player,ring-1,radial+1)|win(player)");
		rules.add("!at(player,ring,radial)&!at(player,ring+3,radial-3)&!at(player,ring+2,radial-2)&!at(player,ring+1,radial-1)|win(player)");
	}

}
