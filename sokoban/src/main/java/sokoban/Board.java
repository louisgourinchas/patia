package sokoban;

import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.tooltip.TooltipModule;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private int width;
    private int height;
    private Cell[][] grid;
    private ArrayList<Box> boxes = new ArrayList<>();
    private Pusher pusher;
    public static final int SPRITE_SIZE = 128;
    public static final int BORDER_SIZE = 20;

    private GraphicEntityModule graphics;

    public Board(List<String> input, GraphicEntityModule graphics, TooltipModule tooltipModule) {
        this.graphics = graphics;
        height = input.size();
        width = 0;
        for (int y = 0; y < height; y++) width = Math.max(width, input.get(y).length());

        Group group = graphics.createGroup();
        double scaleX = ((double) graphics.getWorld().getWidth() - 2 * BORDER_SIZE) / (SPRITE_SIZE * width);
        double scaleY = ((double) graphics.getWorld().getHeight() - 2 * BORDER_SIZE) / (SPRITE_SIZE * height);
        double scale = Math.min(scaleX, scaleY);
        group.setScale(scale);
        double w = width * SPRITE_SIZE * scale;
        double h = height * SPRITE_SIZE * scale;
        group.setX((int) ((graphics.getWorld().getWidth() - w) / 2));
        group.setY((int) ((graphics.getWorld().getHeight() - h) / 2));

        grid = new Cell[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                char c = ' ';
                if (input.get(y).length() > x) c = input.get(y).charAt(x);
                grid[x][y] = new Cell(x, y, c, graphics, group, tooltipModule);
                if (grid[x][y].hasBox()) boxes.add(grid[x][y].getBox());
                if (c == '@' || c == '+') pusher = new Pusher(grid[x][y], graphics, group, tooltipModule);
            }
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[x][y].initNeighbors(grid);
            }
        }
        pusher.getCell().visit();

        //BufferedGroup bufferedGroup = graphics.createBufferedGroup().setZIndex(-1);
        Group bufferedGroup = graphics.createGroup().setZIndex(-1);
        group.add(bufferedGroup);
        int xMin = 0, yMin = 0, xMax = width, yMax = height;
        while (group.getX() + xMin * SPRITE_SIZE * scale > 0) xMin--;
        while (group.getY() + yMin * SPRITE_SIZE * scale > 0) yMin--;
        while (group.getX() + (xMax + 1) * SPRITE_SIZE * scale < graphics.getWorld().getWidth()) xMax++;
        while (group.getY() + (yMax + 1) * SPRITE_SIZE * scale < graphics.getWorld().getHeight()) yMax++;

        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                Sprite sprite;
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    sprite = grid[x][y].getSprite(graphics);
                } else {
                    sprite = graphics.createSprite().setImage("outside.png");
                }
                sprite.setX(x * SPRITE_SIZE).setY(y * SPRITE_SIZE);
                bufferedGroup.add(sprite);

            }
        }
    }

    public boolean isWin() {
        return boxes.stream().allMatch(b -> b.isSolved());
    }

    public ArrayList<String> getInput(boolean firstTurn) {
        ArrayList<String> result = new ArrayList<>();
        if (firstTurn) {
            result.add(width + " " + height + " " + (boxes.size()));
            for (int y = 0; y < height; y++) {
                String line = "";
                for (int x = 0; x < width; x++) {
                    line += grid[x][y].getMapChar();
                }
                result.add(line);
            }
        }
        result.add(pusher.getCell().getInput());
        for (Box box : boxes) result.add(box.getCell().getInput());
        return result;
    }

    public boolean playAction(String action) {
        action = action.toUpperCase();
        int dir = "URDL".indexOf(action);
        if (dir == -1) return false;
        return pusher.move(dir);
    }
}
