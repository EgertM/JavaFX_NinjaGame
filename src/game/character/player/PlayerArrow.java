package game.character.player;

import game.Drawable;
import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class PlayerArrow implements Drawable {

    private Point2D coord;
    private Image image;
    private double rotation;
    private double distanceFromPlayer;
    private int z;

    private static final int DISTANCE_FROM_PLAYER = 32;

    public PlayerArrow(double x, double y, Image image) {
        setCoord(x, y);
        this.image = image;
        this.rotation = 0;
        setDistanceFromPlayer(DISTANCE_FROM_PLAYER);
    }

    public void setDistanceFromPlayer(double dist) {
        this.distanceFromPlayer = dist;
    }

    public double getDistanceFromPlayer() {
        return this.distanceFromPlayer;
    }

    public void setCoord(double x, double y) {
        this.coord = new Point2D(x, y);
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

    private static Image prepareArrow(Image arrow, Player player) {
        ImageView arrowIV = new ImageView(arrow);
        arrowIV.setRotate(Math.toDegrees(player.getRotationInRadians() + Math.PI / 2));
        SnapshotParameters arrowParams = new SnapshotParameters();
        arrowParams.setFill(Color.TRANSPARENT);
        return arrowIV.snapshot(arrowParams, null);

    }

    public void draw(GraphicsContext gc) {
        Player player = Player.get();
        gc.drawImage(PlayerArrow.prepareArrow(this.getImage(), player),
                player.getFeet().getX() + this.getDistanceFromPlayer() * Math.cos(player.getRotationInRadians()),
                player.getFeet().getY() + this.getDistanceFromPlayer() * Math.sin(player.getRotationInRadians()));
    }

}
