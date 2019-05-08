package game.environment;


import game.Drawable;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class EnvObject implements Drawable {

    private Point2D coord;
    private Image image;
    private int z;

    private static final int DEPTH_DECREASE = 60;

    EnvObject(double x, double y, Image image) {
        setCoord(x, y);
        this.image = image;
        this.z = (int) this.getY();

    }

    private void setCoord(double x, double y) {
        this.coord = new Point2D(x, y);
    }

    public Point2D getCoord() {
        return coord;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getZ() {
        return this.z + (int) this.image.getHeight() - DEPTH_DECREASE;
    }

    public Image getImage() {
        return image;
    }

    public double getX() {
        return coord.getX();
    }

    public double getY() {
        return coord.getY();
    }

    public void draw(GraphicsContext gc) {
        gc.drawImage(this.getImage(), this.getX(), this.getY());
    }

}
