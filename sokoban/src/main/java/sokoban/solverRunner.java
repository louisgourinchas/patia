package sokoban;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

public class solverRunner {

    public static void start(Map<String, Coordinates> coordMap) throws IOException {

        //we do not take into account the host OS or anything else, really.
        Process process = Runtime.getRuntime().exec("java -cp pddl4j-4.0.0.jar fr.uga.pddl4j.planners.statespace.HSP src/pddl/domain.pddl src/pddl/tempTest.pddl");
        
        //could be improved: use StringBuilder
        String agentActions = "";

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";

        /*  iterate over the lines that are results of the solver:
        *   we are interested only in lines ending with [0], format XX: (<command>) [0]
        *   and in those lines, only the command interests us
        */
        while ((line = reader.readLine()) != null) {
            //if line is a command
            if (line.endsWith("[0]"))
                //isolate the command from the rest of the string
                line = line.substring(line.lastIndexOf("(")+1,line.lastIndexOf(")")).trim();
                String action = toAction(line, coordMap);
                if (action.length()<2)
                    agentActions += action;
                System.out.println("Read command: " + line + " associated action: " + action + "\n");
        }

        //Save the action String to a file
        //System.out.println("Action string is:\n" + agentActions);
        String targetPath = new File(System.getProperty("user.dir")).toPath().resolve("src/pddl/action.txt").toString();

        try {
            FileWriter writer = new FileWriter(targetPath, false);
            writer.write(agentActions);
            writer.close();
          } catch (IOException e) {
            System.out.println("An error occurred while trying to write the agent actions to file");
            e.printStackTrace();
          }
    }

    //TODO handle degenerate cases better than just "Error"
    private static String toAction(String command, Map<String, Coordinates> coordMap){

        String[] args = command.split(" ");

        switch(args[0].toLowerCase()) {
            case "vpush": //we know the command is valid from the solver, so we do not have to do more checks than for a move
            case "vmove":
                //if the player's starting position is "above" it's destination: go down, else go up
                if (coordMap.get(args[2].toUpperCase()).getX() > coordMap.get(args[3].toUpperCase()).getX())
                    return "U";
                else
                    return "D";
            case "hpush": //we know the command is valid from the solver, so we do not have to do more checks than for a move
            case "hmove":
                //if the player's starting position is to the right of it's destination: go to the left, else go to the right
                if (coordMap.get(args[2].toUpperCase()).getY() > coordMap.get(args[3].toUpperCase()).getY())
                    return "L";
                else
                    return "R";
            default:
                //we cannot go here
                break;
        }

        return "Error";
    }
    
}
