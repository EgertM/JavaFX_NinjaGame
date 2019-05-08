package game;

import javafx.scene.canvas.GraphicsContext;

public interface Drawable {
    int getZ();

    void draw(GraphicsContext gc);
}
