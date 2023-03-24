package sokoban;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.commons.lang3.StringUtils;

public class levelTranslator {
    
    public static Map<String, Coordinates> translateLevel(String filename){

        //starter values for future developements.
        int squareId = 0;
        String[][] squareNames;

        String objects = "(:objects\n\tp1 - player\n\t";
        String problem = "(:INIT\n\t";
        String goal = "(:goal\n\t(AND";

        Map<String, Coordinates> coordMap = new HashMap<String, Coordinates>();

        //create target file
        createTarget();

        //write header (same every time)
        writeToGoal("(define (problem sokoban-9-9)\n(:domain sokoban)\n", false);

        //get json file content
        String boardString = readLevel(filename);
        List<String> boardLines =  Arrays.asList(boardString.split("\\n"));

        //pad smaller lines with extra empty spaces.
        int maxlength = 0;
        for (String s : boardLines){
            if (s.length()>maxlength)
                maxlength = s.length();
        }
        for (int i=0;i<boardLines.size();i++){
            boardLines.set(i, StringUtils.rightPad(boardLines.get(i), maxlength));
        }

        //iterate on each board square
        squareNames = new String[boardLines.size()][boardLines.get(0).length()];

        /*for each square, we have to define:
            -it's presence in the declared objects
            -it's status (wall, box, destination, box on destinatino, player, player on destination, empty space)
            -it's neighbors (links)
            because knowing the name of a future neighbor in advance is hard, we will do the links in a second iteration.
            note: we do not take into account what is 'inside' or 'outside' the walls.
        */
        for (int i=0;i<boardLines.size();i++){
            for (int j=0;j<boardLines.get(i).length();j++){
                
                //make a name, declare it in objects, remember it for links
                String name = generateName(squareId);
                squareId++;
                objects = objects + name + " ";
                squareNames[i][j] = name;

                //append its status to the problem for general info, or to goal  for destinations
                switch(boardLines.get(i).charAt(j)){
                    //wall
                    case '#':
                        problem = problem + "(iswall " + name + ")\n\t";
                        break;
                    //box
                    case '$':
                        problem = problem + "(iscrate " + name + ")\n\t";
                        break;
                    //destination. /!\ in our model, destinations matter only for the goal, they are empty spaces
                    case '.':
                        problem = problem + "(isempty " + name + ")\n\t";
                        goal = goal + " (iscrate " + name + ")";
                        break;
                    //box already on a destination
                    case '*':
                        problem = problem + "(iscrate " + name + ")\n\t";
                        goal = goal + " (iscrate " + name + ")";
                        break;
                    //player
                    case '@':
                        problem = problem + "(ison p1 " + name + ")\n\t";
                        break;
                    //player on a destination
                    case '+':
                        problem = problem + "(ison p1 " + name + ")\n\t";
                        goal = goal + " (iscrate " + name + ")";
                        break;
                    //empty space
                    case ' ':
                        problem = problem + "(isempty " + name + ")\n\t";
                        break;
                    default:
                        break;
                }
            }
        }

        //we finished maping all the names in squareNames, adding links
        for (int i=0;i<squareNames.length;i++){
            for (int j=0;j<squareNames[i].length;j++){
                //check if the neighbor in each cardinal direction is possible
                if(i-1>=0)
                    problem = problem + "(Vlink " + squareNames[i][j] + " " + squareNames[i-1][j] + ")\n\t";
                if(i+1<squareNames.length)
                    problem = problem + "(Vlink " + squareNames[i][j] + " " + squareNames[i+1][j] + ")\n\t";
                if(j-1>=0)
                    problem = problem + "(Hlink " + squareNames[i][j] + " " + squareNames[i][j-1] + ")\n\t";
                if(j+1<squareNames[i].length)
                    problem = problem + "(Hlink " + squareNames[i][j] + " " + squareNames[i][j+1] + ")\n\t";
                    
                //For future purposes, we build a Map associating names to coordinates.
                coordMap.put(squareNames[i][j], new Coordinates(i, j));
            }
        }

        //finalize objects, problems and goal.
        objects = objects + "- pos\n)\n";
        problem = problem + ")\n";
        goal = goal + ")\n)\n"; 

        writeToGoal(objects, true);
        writeToGoal(problem, true);
        writeToGoal(goal+")\n", true);//add the final closing parenthesis
        
        //for future use in the translation from pddl command to agent action.
        return coordMap;
    }

    private static String readLevel(String filename){

        String board = null;
        JSONParser parser = new JSONParser();

        try {     
            
            //stolen from SoloGameRunner to get accurate path to file
            String path = new File(System.getProperty("user.dir")).toPath().resolve("config/" + filename).toString();

            //the way the test jsons are formed, there should be only one object o
            JSONObject level = (JSONObject) parser.parse(new FileReader(path));
        
            board = (String) level.get("testIn");
            //System.out.println("\n\n\n" + board + "\n\n\n");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return board;
    }

    private static void createTarget(){

        String targetPath = new File(System.getProperty("user.dir")).toPath().resolve("src/pddl/tempTest.pddl").toString();
        
        File file = new File(targetPath);
        //boolean succes = false;

        try {
            file.createNewFile();
            //succes = true;
        } catch (IOException e) {
            System.out.println("An error occurred while trying to create temp pddl file.");
            e.printStackTrace();
        }

        //return succes;
    }

    //generates names in format A,B,C,[...],Y,Z,AA,AB,[...],ZZ,AAA...
    private static String generateName(int id){
        return id < 0 ? "" : generateName((id / 26) - 1) + (char)(65 + id % 26);
    }

    private static void writeToGoal(String content, boolean append){
        String targetPath = new File(System.getProperty("user.dir")).toPath().resolve("src/pddl/tempTest.pddl").toString();

        try {
            FileWriter writer = new FileWriter(targetPath, append);
            writer.write(content);
            writer.close();
          } catch (IOException e) {
            System.out.println("An error occurred while trying to write the header for temp pddl file");
            e.printStackTrace();
          }
    }
}
