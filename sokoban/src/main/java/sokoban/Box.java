package sokoban;

import com.codingame.gameengine.module.entities.GraphicEntityModule;
import com.codingame.gameengine.module.entities.Group;
import com.codingame.gameengine.module.entities.Sprite;
import com.codingame.gameengine.module.tooltip.TooltipModule;

public class Box {
    private Cell cell;
    private Sprite sprite;
    private TooltipModule tooltipModule;

    public Box(Cell cell, GraphicEntityModule graphics, Group group, TooltipModule tooltipModule) {
        this.tooltipModule = tooltipModule;
        this.cell = cell;
        this.sprite = graphics.createSprite();
        group.add(sprite);
        updateSprite();
    }

    public void updateSprite() {
        sprite.setImage(isSolved() ? "crateSolved.png" : "crateUnsolved.png")
                .setX(cell.getX()*Board.SPRITE_SIZE)
                .setY(cell.getY()*Board.SPRITE_SIZE);
        tooltipModule.setTooltipText(sprite, "BOX\nx: " + cell.getX() + "\ny: " + cell.getY());
    }

    public boolean isSolved() {
        return cell.isDropzone();
    }

    public Cell getCell() {
        return cell;
    }

    public void moveTo(Cell boxNext) {
        cell.setBox(null);
        boxNext.setBox(this);
        this.cell = boxNext;
        updateSprite();
    }
}
