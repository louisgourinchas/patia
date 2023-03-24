Class breakdown:  

- Agent.java: now automatically tries to read it's actions from the 'action.txt' file, defaults to "DUU" if that fails.  
- Coordinates.java: storage of an int tuple, to be able to compare positions.  
- SokobanMain.java: invokes each part in order to be able to use the solver (levelTranslator and solverRunner).  
- levelTranslator.java: goes to read the json file and translates it to a pddl test file.   
- solverRunner.java: runs the solver on the pddl test file and translates the result into an action stringfor the Agent.  
