import java.util.ArrayList;

/**
 * 
 * @author Jonathan Koenes, Adam Bartz, Stephen Bush
 *
 */
public class Resolution {
	
	private static ArrayList<String> facts = new ArrayList<String>();
	private static ArrayList<String> rules = new ArrayList<String>();
	private static ArrayList<String> clauses = new ArrayList<String>();
	
	public static String resolutionWinCheck(){
		
		boolean answers = backChain("win(x)");
		if(answers){
			return "X";
		}
		
		answers = backChain("win(o)");
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
		
		//System.out.println("Backchaining on " + goal);
		
		//break apart multiple list into separate clauses and backchain separately
		if(goal.contains("&")){
			String[] split = goal.split("&");
			for(int i = 0; i < split.length; i++){
				//if an individual piece of the goal fails, then this chain fails
				if(!backChain(split[i])){
					return false;
				}
				
			}
			return true;
		}
		//go through facts to see if the goal is a fact
				for(int i = 0; i < facts.size(); i++){
					if(goal.equals(facts.get(i))){
						return true;
					}
				}
		
		//go through clauses and find a predicate that matches current goal, and if found, unify and apply that unification string
		String current;
		for(int i = 0; i < clauses.size(); i++){
			 current = clauses.get(i);
			//first, check if the clause contains a predicate, if so, attempt unification
			if(current.contains("|")){
				String predicate = current.substring(current.indexOf("|") + 1, current.length());
				
				String[][] theta = new String[10][2];
				theta = unify(predicate, goal, theta);
				
				//if unify succeeded, then we apply the unification, and backchain with it as our new goal
				if(theta[0][0] == null || !theta[0][0].equals("failure")){
					String newGoal = applyUnification(current, goal, theta);
					boolean goalReached = backChain(newGoal);
				
					//if we reached the goal, we found a successful chain, otherwise we just move on to the next clause
					if(goalReached){
						return true;
					}
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
	
	//adds a fact to the knowledge base when a new node is played
	public static void addFact(Node newFact){
		//make a new clause for the new fact
		String newClause;
		if(newFact != null){
			newClause = makeFact(newFact);
			//System.out.println("Added New Fact " + newClause);
			facts.add(newClause);
		
			//add new clauses when our fact is unified with the given rules for a win
			for(int i = 0; i < rules.size(); i++){
				
				String[][] theta = new String[10][2];
				String ruleClause = rules.get(i);
				
				ruleClause = ruleClause.substring(0, ruleClause.indexOf("&"));
				
				theta = unify(newClause, ruleClause, theta);
				
				if(theta[0][0] == null || !theta[0][0].equals("failure")){
					String unified = applyUnification(newClause, rules.get(i), theta);
					//System.out.println("Adding Clause " + unified);
					clauses.add(unified);
				}
			}
		}
		
	}
	
	//the defninition of the unification algorithm
	private static String[][] unify(String sen1, String sen2, String[][] currentSub){
		//System.out.println("Unifying " + sen1 + " and " + sen2);
		if(currentSub[0][0] != null && currentSub[0][0].equals("failure")){
			return currentSub;
		}
		else if(sen1.equals(sen2)){
			return currentSub;
		}
		else if(isVariable(sen1)){
			return unifyVariable(sen1,sen2,currentSub);
		}
		else if(isVariable(sen2)){
			return unifyVariable(sen2,sen1,currentSub);
		}
		else if(isCompound(sen1) && isCompound(sen2)){
			return unify(args(sen1), args(sen2), unify(operator(sen1),operator(sen2),currentSub));
		}
		else if(isList(sen1) && isList(sen2)){
			return unify(restArgs(sen1), restArgs(sen2), unify(first(sen1),first(sen2),currentSub));
		}
		else{
			currentSub[0][0] = "failure";
			return currentSub;
		}
	}
	
	private static String[][] unifyVariable(String variable, String sentence, String[][] currentSub){
		for(int i = 1; i < currentSub.length; i++){
			if(currentSub[i][0] != null){
				if(currentSub[i][0].equals(variable)){
					return currentSub;
				}
				else if(currentSub[i][0].equals(sentence)){
					return currentSub;
				}
			}
		}
		if(occursCheck(variable, sentence)){
			currentSub[0][0] = "failure";
			return currentSub;
		}
		else{
			for(int i = 1; i < currentSub.length; i++){
				if(currentSub[i][0] == null || currentSub[i][0].equals("")){
					if(isVariable(variable)){
						currentSub[i][0] = variable;
						currentSub[i][1] = sentence;
					}
					else{
						currentSub[i][1] = variable;
						currentSub[i][0] = sentence;
					}
					i = currentSub.length;
				}
			}
		}
		return currentSub;
	}
	
	private static boolean occursCheck(String test1, String test2){
		if(test1.contains(test2) || test2.contains(test1)){
			return true;
		}
		return false;
	}
	private static boolean isVariable(String test){
		if(test.equals("player") || test.equals("ring") || test.equals("radial")){
			return true;
		}
		return false;
	}
	
	private static boolean isCompound(String test){
		if(test.contains("at(") || test.contains("win(")){
			return true;
		}
		return false;
	}
	private static boolean isList(String test){
		if(test.contains(",")){
			return true;
		}
		return false;
		
	}
	
	private static String args(String compound){
		int start = compound.indexOf("(") + 1;
		int end = compound.indexOf(")");
		return compound.substring(start, end);
		
	}
	
	private static String operator(String compound){
		int start = compound.indexOf("(");
		return compound.substring(0, start);
	}
	
	private static String first(String list){
		int start = list.indexOf(",");
		return list.substring(0, start);
	}
	
	private static String restArgs(String list){
		int start = list.indexOf(",");
		return list.substring(start + 1, list.length());
	}
	
	private static String applyUnification(String sen1, String sen2, String[][] substitution){
		String newSentence = "";
		for(int i = 1; i < substitution.length; i++){
			if(substitution[i][0] != null){
				sen1 = sen1.replaceAll(substitution[i][0], substitution[i][1]);
				sen2 = sen2.replaceAll(substitution[i][0], substitution[i][1]);
			}
		}
		
		//clean up the clauses
		if(sen1.length() < sen2.length()){
			sen2 = sen2.replace(sen1, "");
			sen2 = sen2.replace("&&", "&");
			sen2 = sen2.replace("&|", "|");
			if(sen2.charAt(0) == '&'){
				sen2 = sen2.substring(1,sen2.length());
			}
			if(sen2.charAt(sen2.length() - 1) == '|'){
				sen2 = sen2.substring(0, sen2.length() - 1);
			}
			newSentence = sen2;
		}
		else{
			sen1 = sen1.replace(sen2, "");
			sen1 = sen1.replace("&&", "&");
			sen1 = sen1.replace("&|", "|");
			if(sen1.charAt(0) == '&'){
				sen1 = sen1.substring(1,sen1.length());
			}
			if(sen1.charAt(sen1.length() - 1) == '|'){
				sen1 = sen1.substring(0, sen1.length() - 1);
			}
			newSentence = sen1;
		}
		newSentence = doMath(newSentence);
		return newSentence;
	}
	
	//simply does the calculation when something like "3+2" or "1-3" is found in a statement
	private static String doMath(String sentence){
		for(int i = 0; i < sentence.length(); i++){
			int num = ((int) sentence.charAt(i)) - 48;
			
			//if a digit is found
			if(num >=0 && num < 10){
				//System.out.println(sentence + " " + num + "@" + i);
				int num2 = ((int) sentence.charAt(i+1)) - 48;
				
				//double digit
				if(num2>=0 && num2<10){
					num = (num*10) + num2;
					if(sentence.charAt(i+2) == '-' || sentence.charAt(i+2) == '+'){
						num2 = Integer.parseInt(sentence.substring(i+3, i+4));
						if(sentence.charAt(i+2) == '-'){
							num = ((num - num2) + 12) % 12;
						}
						else{
							num = (num + num2) % 12;
						}
						sentence = sentence.replace(sentence.substring(i, i+4), "" + num);
					}
				}
				
				
				//single digit
				if(sentence.charAt(i+1) == '-' || sentence.charAt(i+1) == '+'){
					num2 = Integer.parseInt(sentence.substring(i+2, i+3));
					if(sentence.charAt(i+1) == '-'){
						num = ((num - num2) + 12) % 12;
					}
					else{
						num = (num + num2) % 12;
					}
					sentence = sentence.replace(sentence.substring(i, i+3), "" + num);
				}
				
				
			}
			
			
		}
		return sentence;
	}
	
	public static void setRules(){
		
		System.out.println("Setting up rules");
		//vertical wins across a radial
		rules.add("at(player,ring,radial)&at(player,ring-1,radial)&at(player,ring-2,radial)&at(player,ring-3,radial)|win(player)");
//		rules.add("at(player,ring,radial)&at(player,ring+1,radial)&at(player,ring-1,radial)&at(player,ring-2,radial)|win(player)");
//		rules.add("at(player,ring,radial)&at(player,ring+2,radial)&at(player,ring+1,radial)&at(player,ring-1,radial)|win(player)");
//		rules.add("at(player,ring,radial)&at(player,ring+3,radial)&at(player,ring+2,radial)&at(player,ring+1,radial)|win(player)");
		
		//horizontal wins across a ring
		rules.add("at(player,ring,radial)&at(player,ring,radial-1)&at(player,ring,radial-2)&at(player,ring,radial-3)|win(player)");
//		rules.add("at(player,ring,radial)&at(player,ring,radial+1)&at(player,ring,radial-1)&at(player,ring,radial-2)|win(player)");
//		rules.add("at(player,ring,radial)&at(player,ring,radial+2)&at(player,ring,radial+1)&at(player,ring,radial-1)|win(player)");
//		rules.add("at(player,ring,radial)&at(player,ring,radial+3)&at(player,ring,radial+2)&at(player,ring,radial+1)|win(player)");
		
		//spiral wins across rings and radials 
		rules.add("at(player,ring,radial)&at(player,ring-1,radial-1)&at(player,ring-2,radial-2)&at(player,ring-3,radial-3)|win(player)");
//		rules.add("at(player,ring,radial)&at(player,ring+1,radial+1)&at(player,ring-1,radial-1)&at(player,ring-2,radial-2)|win(player)");
//		rules.add("at(player,ring,radial)&at(player,ring+2,radial+2)&at(player,ring+1,radial+1)&at(player,ring-1,radial-1)|win(player)");
//		rules.add("at(player,ring,radial)&at(player,ring+3,radial+3)&at(player,ring+2,radial+2)&at(player,ring+1,radial+1)|win(player)");
//		
		//the other spiral
		rules.add("at(player,ring,radial)&at(player,ring-1,radial+1)&at(player,ring-2,radial+2)&at(player,ring-3,radial+3)|win(player)");
//		rules.add("at(player,ring,radial)&at(player,ring+1,radial-1)&at(player,ring-1,radial+1)&at(player,ring-2,radial+2)|win(player)");
//		rules.add("at(player,ring,radial)&at(player,ring+2,radial-2)&at(player,ring+1,radial-1)&at(player,ring-1,radial+1)|win(player)");
//		rules.add("at(player,ring,radial)&at(player,ring+3,radial-3)&at(player,ring+2,radial-2)&at(player,ring+1,radial-1)|win(player)");
	}
	
	public static void reset(){
		rules = new ArrayList<String>();
		clauses = new ArrayList<String>();
		facts = new ArrayList<String>();
	}

}
