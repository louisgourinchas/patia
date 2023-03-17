package sokoban;

import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.SpriteAnimation;
import com.codingame.gameengine.module.tooltip.TooltipModule;

public class Pusher {
    private TooltipModule tooltipModule;
    private Cell cell;
    private SpriteAnimation sprite;
    private GraphicEntityModule graphics;

    public Pusher(Cell cell, GraphicEntityModule graphics, Group group, TooltipModule tooltipModule) {
        this.tooltipModule = tooltipModule;
        this.graphics = graphics;
        this.cell = cell;

        this.sprite = graphics.createSpriteAnimation().setLoop(true).setPlaying(true).setDuration(300);
        group.add(sprite);
        updateSprite(2);
    }

    private void updateSprite(int dir) {
        char dirText = "URDL".charAt(dir);
        String[] animation = {dirText + "1.png", dirText + "2.png", dirText + "3.png", dirText + "1.png"};

        this.sprite.setImages(animation);
        graphics.commitEntityState(0, sprite);
        sprite.setX(cell.getX() * Board.SPRITE_SIZE).setY(cell.getY() * Board.SPRITE_SIZE);
        tooltipModule.setTooltipText(sprite, "PUSHER\nx: " + cell.getX() + "\ny: " + cell.getY());
    }

    public Cell getCell() {
        return cell;
    }

    public boolean move(int dir) {
        Cell next = cell.getNeighbor(dir);
        if (next == null || next.isWall()) return false;
        if (next.hasBox()) {
            Cell boxNext = next.getNeighbor(dir);
            if (boxNext == null || !boxNext.isFree()) return false;
            next.getBox().moveTo(boxNext);
        }
        this.cell = next;
        this.updateSprite(dir);
        return true;
    }
}
