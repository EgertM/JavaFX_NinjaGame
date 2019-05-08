package game.character.enemy;

import game.character.player.Player;
import javafx.geometry.Point2D;

public abstract class AbstractEnemy {

    private int flip;
    private int offset;
    private Point2D coord;

    public Point2D getCoord() {
        return coord;
    }

    public void setCoord(Point2D coord) {
        this.coord = coord;
    }

    public void setFlip(int flip) {
        this.flip = flip;
    }

    public int getFlip() {
        return this.flip;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return this.offset;
    }

    public double getX() {
        return coord.getX();
    }

    public void updateDirection() {
        if (Player.get().getX() > this.getX()) {
            this.setFlip(-1);
            this.setOffset(1);
        } else {
            this.setFlip(1);
            this.setOffset(0);
        }
    }
}
