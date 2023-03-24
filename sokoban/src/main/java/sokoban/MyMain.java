package sokoban;

import com.codingame.gameengine.runner.SoloGameRunner;

public class MyMain {
    public static void main(String[] args) {

        SoloGameRunner gameRunner = new SoloGameRunner();
        //Agent will automatically read action string from file.
        gameRunner.setAgent(Agent.class);
        gameRunner.setTestCase("test1.json");

        gameRunner.start();
    }
}
