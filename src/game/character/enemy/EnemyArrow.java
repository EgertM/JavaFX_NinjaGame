package game.character.enemy;

import game.Drawable;
import game.GameController;
import game.character.player.Player;
import game.gui.GUIController;
import game.gui.Meter;
import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.Arrays;

public class EnemyArrow implements Drawable {

    public static final int COLLISION_RADIUS = 32;
    private Point2D coord;
    private Image image;
    private double rotation;
    private static final int SPEED = 20;
    private int z;

    EnemyArrow(double x, double y, Image image, Point2D destination) {
        setCoord(x, y);
        this.image = image;
        double flipDirection = 0;
        if (destination.getX() < coord.getX()) {
            flipDirection = 1;
        }
        this.rotation = Math.atan((destination.getY() - coord.getY()) / (destination.getX() - coord.getX()));
        this.rotation += flipDirection * Math.PI;

    }

    public Image getImage() {
        return image;
    }

    public void setZ(int z) {
        this.z = (z - 1);
    }

    public int getZ() {
        return this.z;
    }

    private void setCoord(double x, double y) {
        this.coord = new Point2D(x, y);
    }

    private boolean isOutOfBounds() {
        return (this.coord.getX() > GUIController.get().getScreenWidth()
                || this.coord.getX() < 0
                || this.coord.getY() > GUIController.get().getScreenHeight()
                || this.coord.getY() < 0);
    }

    private Image prepareArrow(Image arrow) {
        ImageView arrowIV = new ImageView(arrow);
        arrowIV.setRotate(Math.toDegrees(rotation)); //+ Math.PI/2));
        SnapshotParameters arrowParams = new SnapshotParameters();
        arrowParams.setFill(Color.TRANSPARENT);
        this.coord = new Point2D(this.coord.getX() + SPEED * Math.cos(rotation),
                this.coord.getY() + SPEED * Math.sin(rotation));
        return arrowIV.snapshot(arrowParams, null);

    }

    private void remove() {
        if (GameController.getDrawOrder().contains(this)) {
            GameController.removeFromDrawOrder(Arrays.asList(this));
        }
    }

    private boolean isColliding(Player player) {
        return (this.coord.distance(player.getHead()) < COLLISION_RADIUS);
    }

    private void handleCollide() {
        Player player = Player.get();
        if (this.isColliding(player)) {
            if (player.isDashing()) {
                this.remove();
                setCoord(0, 0);

            } else {
                Meter healthMeter = GUIController.get().getHealthMeter();
                healthMeter.decrease();
                if (healthMeter.getLength() <= 1) player.kill();
            }
        }

    }

    public void draw(GraphicsContext gc) {
        if (this.isOutOfBounds()) {
            GameController.getDrawOrder().remove(this);
        } else {
            gc.drawImage(this.prepareArrow(this.getImage()),
                    this.coord.getX(),
                    this.coord.getY());
            this.handleCollide();
        }


    }

}
