Readme.txt
AI Project by Jonathan Koenes, Adam Bartz, and Stephen Bush

Don't run the .jar, it is completely recommended that you import it into an IDE before running, as there tends to be file lookup issues

Files and Description:
AIPlayer 	- Contains the AI playing logic, given heurstics it can use, as well as other things
Classifier 	- Contains the classifier heuristic in it's entirety
GUI 		- Contains all the logic for the GUI painting
Heuristic1 	- Contains the logic for heuristic 1
Heuristic2 	- Contains the logic for heuristic 2
Heuristic3 	- Contains the logic for heuristic 3 
HumanPlayer - Contains the logic for having a human player, including waiting for updates from the GUI about clicks
IHeuristic	- Contains the interface for the Heurstic classes
IPlayer 	- Contains the interface for the two players
MaxRootNode	- Contains the logic for a maximizer node in the Minimax tree
MinMaxTree	- Contains the logic for managing and possibly pruning Min and Max nodes in the Minimax tree
MinRootNode	- Contains the logic for a minimzer node in the Minimax tree
NeuralNet	- Contains in its entirety the neural net training logic, as well as loading and using neural nets
Node		- Contains the logic and information pertaining to a played spot, including the player and location of the spot on the board
Resolution	- Contains in its entirety the resolution logic win checker, including lists for rules, facts, and knowledge
RootNode 	- Contains superclass information for the MaxRootNode and MaxRootNode classes
TicTacToe 	- Contains the main method, as well as the game engine for managing players, moves, and available moves, including providing information for GUI painting

Known Bugs:
Very rare Casper shows up when the GUI is clicked. Only once, the GUI updated the player, who then gave a play, but the node that was played disappeared. Only happened once. Never been solved. 
All output goes to terminal, but this is not a bug. Simply ran out of time to implement a nice GUI status updater.