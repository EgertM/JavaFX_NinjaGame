package game.character.player;

import game.gui.GUIController;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PlayerDashLine {
    public static final int DRAWING_START = 12;
    public static final int DRAWING_LIMIT = 52;
    public static final int DRAWING_INCREASE = 6;
    public static final int COORDINATE_INCREASE = 32;
    private static GraphicsContext gc = GUIController.get().getGc();
    private static final int LINE_WIDTH = 1;

    public static void draw(Point2D startPos, Point2D endPos) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(LINE_WIDTH);
        for (int i = DRAWING_START; i <= DRAWING_LIMIT; i += DRAWING_INCREASE) {
            gc.strokeLine(startPos.getX() + COORDINATE_INCREASE, startPos.getY() + i, endPos.getX()
                    + COORDINATE_INCREASE, endPos.getY() + i);
        }
    }
}
