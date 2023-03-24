package sokoban;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Agent {
    public static void main(String[] args) {
        
        String targetPath = new File(System.getProperty("user.dir")).toPath().resolve("src/pddl/action.txt").toString();
        String solution = "";

        try {
            FileReader reader = new FileReader(targetPath);

            int charCode;
            //charcode 10 is \n
            while((charCode = reader.read()) != -1) {
                solution += (char)charCode;
            }
            reader.close();
          } catch (IOException e) {
            solution = "DUU";
            System.out.println("An error occurred while trying to write the agent actions to file");
            e.printStackTrace();
          }
          
        for (char c : solution.toCharArray()) System.out.println(c);
    }
}
