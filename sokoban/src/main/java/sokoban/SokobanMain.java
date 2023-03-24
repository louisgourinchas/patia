package sokoban;

import java.io.IOException;
import java.util.Map;

import com.codingame.gameengine.runner.SoloGameRunner;

public class SokobanMain {
    public static void main(String[] args) {

        Map<String, Coordinates> coords;
        
        //Make pddl problem out of json file
        coords = levelTranslator.translateLevel("test1.json");

        //run solver and translate pddl solution to agent action
        try {
            solverRunner.start(coords);
        } catch (IOException e) {
            e.printStackTrace();
        }

        SoloGameRunner gameRunner = new SoloGameRunner();
        //Agent will automatically read action string from file.
        gameRunner.setAgent(Agent.class);
        gameRunner.setTestCase("test1.json");

        gameRunner.start();
    }
}
