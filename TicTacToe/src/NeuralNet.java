import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Stack;


public class NeuralNet implements IHeuristic {
	
	//parameters
	private static int numInputs = 96;//96 nodes, 48 for each of either x or o
	private static int numHiddenNodes = 60; //arbitrary, just picked something that sounded good
	private static int numOutputs = 1; //chances of winning given a state and turn
	private static double LR = 0.1; //learning rate
	private static double reward = 0;
	private static double bias = 0.1;
	private static Stack<double[]> gameStack = new Stack<double[]>();
	
	//the weight matrixes
	private static double[][] inHid = new double[numInputs][numHiddenNodes];
	private static double[][] hidOut = new double[numHiddenNodes][numOutputs];
	
	//data arrays
	private static double[] output = new double[numOutputs]; //holds output values
	private static double[] input = new double[numInputs]; //holds the input values
	private static double[] lastOutput = new double[numOutputs];//holds the data from previous prediction
	private static double[] hiddenOutput = new double[numHiddenNodes]; //holds the outputs for the hidden layer
	private static double[] outputError = new double[numOutputs];
	private static double[] hiddenError = new double[numHiddenNodes];
	
	public NeuralNet() {
		loadWeightsFromFile();
	}
	
	//input modeled after TD Gammon
	public static void setInput(Node[] gameboard, char side){
		char otherside = 'o';
		if(side == 'o'){
			otherside = 'x';
		}
		int turn = 0;
		int num = 0;
		//put in the x's
		for(int i = 0; i < 48; i++){
			if(gameboard[i].getChar() == 'x'){
				input[i] = 1;
				turn++;
				num++;
			}
			else{
				input[i] = 0;
			}
		}
//		input[input.length - 4] = num;
		num = 0;
		//put in the o's
		for(int i = 48; i < input.length; i++){
			if(gameboard[i - 48].getChar() == 'o'){
				input[i] = 1;
				turn++;
				num++;
			}
			else{
				input[i] = 0;
			}
		}
//		input[input.length - 3] = -num;
//		//set the turn
//		input[input.length-2] = turn%2;//0 is x, 1 is o
//		input[input.length-1] = Math.abs(input[input.length-2] - 1);
//		input[input.length-1] = 1;//bias
	}
	
	public static void initializeWeights(){
		//input to hidden weights
		for(int i = 0; i < inHid.length; i++){
			for(int j = 0; j < inHid[i].length; j++){
				inHid[i][j] = (Math.random()/10) - 0.5;
			}
		}
		
		//set the bias to one
//		for(int j = 0; j < inHid[0].length -1; j++){
//			inHid[numInputs - 1][j] = 1;
//		}
		
		//hidden to output weights
		for(int i = 0; i < hidOut.length; i++){
			for(int j = 0; j < hidOut[i].length; j++){
				hidOut[i][j] =(Math.random()*10) - 5;
			}
		}
	}
	
	public static void calculateNetwork(){
		double sum = 0;
		//first do the inputs to hidden
		for(int hidNode = 0; hidNode < numHiddenNodes; hidNode++){
			sum = 0;
			for(int inNode = 0; inNode < inHid.length; inNode++){
				sum += (input[inNode] * inHid[inNode][hidNode]);
			}
			sum += bias;
			hiddenOutput[hidNode] = activationFunction(sum);
		}
		
		//now to hidden layer to output
		for(int outNode = 0; outNode < numOutputs; outNode++){
			sum = 0;
			for(int hidNode = 0; hidNode < hidOut.length; hidNode++){
				sum += (hiddenOutput[hidNode] * hidOut[hidNode][outNode]);
			}
			sum += bias;
			output[outNode] = activationFunction(sum);
		}
	}
	
	public static void calculateErrorArrays(){
		//output error
		for(int outNode = 0; outNode < numOutputs; outNode++){
			double sum = 0;
			for(int node = 0; node < hidOut.length; node++){
				sum += (hiddenOutput[node] * hidOut[node][outNode]);
			}
			outputError[outNode] = derivitiveActivationFunction(sum) * (lastOutput[outNode] - output[outNode] + reward);
		}
		//error in hidden layer
		for(int hidNode = 0; hidNode < numHiddenNodes; hidNode++){
			double sum = 0;
			for(int weight = 0; weight < inHid.length; weight++){
				sum += (input[weight] * inHid[weight][hidNode]);
			}
			double sumOurWeights = 0;
			for(int outweight = 0; outweight < output.length; outweight++){
				sumOurWeights += hiddenOutput[outweight] * outputError[outweight];
			}
			hiddenError[hidNode] = derivitiveActivationFunction(sum) * sumOurWeights;
		}
		
	}
	
	public static void calculateWinError(){
		//output error
		for(int outNode = 0; outNode < numOutputs; outNode++){
			double sum = 0;
			for(int weight = 0; weight < hidOut.length; weight++){
				sum += (hiddenOutput[weight] * hidOut[weight][outNode]);
			}
			outputError[outNode] = derivitiveActivationFunction(sum) * (1 - output[outNode] + reward);
		}
		//error in hidden layer
		for(int hidNode = 0; hidNode < numHiddenNodes; hidNode++){
			double sum = 0;
			for(int weight = 0; weight < inHid.length; weight++){
				sum += (input[weight] * inHid[weight][hidNode]);
			}
			double sumOurWeights = 0;
			for(int outweight = 0; outweight < output.length; outweight++){
				sumOurWeights += hiddenOutput[outweight] * outputError[outweight];
			}
			hiddenError[hidNode] = derivitiveActivationFunction(sum) * sumOurWeights;
		}
		
	}
	
	public static void calculateLossError(){
		//output error
		for(int outNode = 0; outNode < numOutputs; outNode++){
			double sum = 0;
			for(int weight = 0; weight < hidOut.length; weight++){
				sum += (hiddenOutput[weight] * hidOut[weight][outNode]);
			}
			outputError[outNode] = derivitiveActivationFunction(sum) * (0 - output[outNode] + reward);
		}
		//error in hidden layer
		for(int hidNode = 0; hidNode < numHiddenNodes; hidNode++){
			double sum = 0;
			for(int weight = 0; weight < inHid.length; weight++){
				sum += (input[weight] * inHid[weight][hidNode]);
			}
			double sumOurWeights = 0;
			for(int outweight = 0; outweight < output.length; outweight++){
				sumOurWeights += hiddenOutput[outweight] * outputError[outweight];
			}
			hiddenError[hidNode] = derivitiveActivationFunction(sum) * sumOurWeights;
		}
		
	}
	
	public static void calculateTieError(){
		//output error
		for(int outNode = 0; outNode < numOutputs; outNode++){
			double sum = 0;
			for(int weight = 0; weight < hidOut.length; weight++){
				sum += (hiddenOutput[weight] * hidOut[weight][outNode]);
			}
			outputError[outNode] = derivitiveActivationFunction(sum) * (0.5 - output[outNode] + reward);
		}
		//error in hidden layer
		for(int hidNode = 0; hidNode < numHiddenNodes; hidNode++){
			double sum = 0;
			for(int weight = 0; weight < inHid.length; weight++){
				sum += (input[weight] * inHid[weight][hidNode]);
			}
			double sumOurWeights = 0;
			for(int outweight = 0; outweight < output.length; outweight++){
				sumOurWeights += hiddenOutput[outweight] * outputError[outweight];
			}
			hiddenError[hidNode] = derivitiveActivationFunction(sum) * sumOurWeights;
		}
		
	}
	
	public static void updateWeights(){
		//update weights to hidden nodes
		for(int node = 0; node < inHid.length -1; node++){
			for(int weight = 0; weight < inHid[node].length; weight++){
				inHid[node][weight] = inHid[node][weight] + LR  * (input[node] * hiddenError[weight]);
			}
		}
		
		//update weights to output
		for(int node = 0; node < hidOut.length; node++){
			for(int weight = 0; weight < hidOut[node].length; weight++){
				hidOut[node][weight] = hidOut[node][weight] + LR * (hiddenOutput[node] + outputError[weight]);
			}
		}
	}
	
	//sigmoidal function
	private static double activationFunction(double sumInputs){
		double result = 0.0;
		 result = 1.0 + Math.exp(-sumInputs);
		 
		 return 1 / result;
		 
	}
	
	//derivitive of sigmoid
	private static double derivitiveActivationFunction(double sumInputs){
		double result = 0.0;
		
		result = sumInputs * (1 - sumInputs);
		
		return result;
	}
	
	//linear activation
	private static double linearActivation(double sumInputs){
		return sumInputs;
	}
	
	//derivitive of linear
	private static double linearDerivitive(double sumInputs){
		return 1;
	}
	
	public static void setReward(double inreward){
		reward = inreward;
	}
	
	public static void storeLastOutput(){
		for(int i = 0; i < numOutputs; i++){
			lastOutput[i] = output[i];
		}
	}
	
	public static void putOnStack(Node[] state, char player){
		double[] ourState = new double[state.length * 2];

		//put in the x's
		for(int i = 0; i < 48; i++){
			if(state[i].getChar() == 'x'){
				ourState[i] = 1;
			}
			else{
				ourState[i] = 0;
			}
		}
		//put in the o's
		for(int i = 48; i < input.length-1; i++){
			if(state[i - 48].getChar() == 'o'){
				ourState[i] = 1;

			}
			else{
				ourState[i] = 0;
			}
		}
		
		
		gameStack.push(ourState);
	}
	
	public static void calculateFromWin(char winner){
		
		double[] winningState = gameStack.pop();
		
		for(int i = 0; i < winningState.length; i++){
			input[i] = winningState[i];
		}
		
		calculateNetwork();
		calculateWinError();
		updateWeights();
		calculateNetwork();
		
		 lastOutput = getResults();
		 
		 double[] current = gameStack.pop();
		 
		 do{
			 for(int i = 0; i < current.length; i++){
				 input[i] = current[i];
			 }
			 calculateNetwork();
			 calculateErrorArrays();
			 updateWeights();
			 calculateNetwork();
			 
			 lastOutput = getResults();
			 
			 current = gameStack.pop();
			 
		 }while(!gameStack.isEmpty());
		
	}
	
	public static void calculateFromLoss(char winner){
		
		double[] losingState = gameStack.pop();
		
		for(int i = 0; i < losingState.length; i++){
			input[i] = losingState[i];
		}
		
		calculateNetwork();
		calculateLossError();
		updateWeights();
		calculateNetwork();
		
		 lastOutput = getResults();
		 
		 double[] current = gameStack.pop();
		 
		 do{
			 for(int i = 0; i < current.length; i++){
				 input[i] = current[i];
			 }
			 calculateNetwork();
			 calculateErrorArrays();
			 updateWeights();
			 calculateNetwork();
			 
			 lastOutput = getResults();
			 
			 current = gameStack.pop();			 
		 }while(!gameStack.isEmpty());
		
	}
	
	public static void calculateFromTie(){
		
		double[] tie = gameStack.pop();
		
		for(int i = 0; i < tie.length; i++){
			input[i] = tie[i];
		}
		
		calculateNetwork();
		calculateTieError();
		updateWeights();
		calculateNetwork();
		
		 lastOutput = getResults();
		 
		 double[] current = gameStack.pop();
		 
		 while(!gameStack.isEmpty()){
			 for(int i = 0; i < current.length; i++){
				 input[i] = current[i];
			 }
			 calculateNetwork();
			 calculateErrorArrays();
			 updateWeights();
			 calculateNetwork();
			 
			 lastOutput = getResults();
			 
			 current = gameStack.pop();
			 
		 }	
		
	}
	
	public static double[] getResults(){
		return output;
	}
	
	public static void writeToFile(){
		File output = new File(".data\\nnsave");
		FileWriter writer;
		try{
			writer = new FileWriter(output);
			
			writer.write(inHid.length + "," + inHid[0].length + "\n");
		
			for(int i = 0; i < inHid.length; i++){
				for(int j = 0; j < inHid[i].length; j++){
					writer.write(inHid[i][j] + ",");
				}
				writer.write("\n");
			}
			
			writer.write(hidOut.length + "," + hidOut[0].length + "\n");
			for(int i = 0; i < hidOut.length; i++){
				for(int j = 0; j < hidOut[i].length; j++){
					writer.write(hidOut[i][j] + ",");
				}
				writer.write("\n");
			}
			
		
		
			writer.close();
		}
		catch(IOException e){
			System.out.println("File for save missing");
		}
	}
	
	public static void loadWeightsFromFile(){
		File input = null;
		Scanner reader = null;
		try {
			input = new File(".data\\nnsave40_110000newLR");
			reader = new Scanner(input);
		} catch ( java.io.FileNotFoundException e ) {
			try {
				input = new File(".data/nnsave40_110000newLR");
				reader = new Scanner(input);
			} catch ( java.io.FileNotFoundException e1 ) {
				e1.printStackTrace();
				System.out.println("shit happened");
			}
		}
		
		try{
			String[] line = reader.nextLine().split(",");
			numInputs = Integer.parseInt(line[0]);
			numHiddenNodes = Integer.parseInt(line[1]);
			
			inHid = new double[numInputs][numHiddenNodes];
			
			for(int i = 0; i < inHid.length; i++){
				line = reader.nextLine().split(",");
				for(int j = 0; j < inHid[i].length; j++){
					inHid[i][j] = Double.parseDouble(line[j]);
				}
			}
			
			line = reader.nextLine().split(",");
			numOutputs = Integer.parseInt(line[1]);
			
			hidOut = new double[numHiddenNodes][numOutputs];
			
			for(int i = 0; i < hidOut.length; i++){
				line = reader.nextLine().split(",");
				for(int j = 0; j < hidOut[i].length; j++){
					hidOut[i][j] = Double.parseDouble(line[j]);
				}
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
			System.out.println("shit happened");
		}
		
		
		
		
		
	}

	@Override
	public double evaluateState(Node inNode, char inChar) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double evaluateState(Node[] state, char inChar) {
		setInput(state, inChar);
		calculateNetwork();
		return output[0];
	}

	@Override
	public int getParameter() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setParameter() {
	}

	@Override
	public String[] getStatus() {
		// TODO Auto-generated method stub
		return null;
	}
}
