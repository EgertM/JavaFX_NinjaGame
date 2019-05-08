package game.character.player;

import game.gui.GUIController;
import game.gui.Meter;
import javafx.geometry.Point2D;

import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

public class PlayerController {
    private final Player player;

    public PlayerController(Player player) {
        this.player = player;
    }

    public void mouseMoveHandler(MouseEvent e) {
        if (!player.isDashing()) {
            double x1 = e.getX();
            double y1 = e.getY();
            player.setLastMousePos(new Point2D(x1, y1));
            player.setMouseDist(x1, y1);
        }
    }

    public void mouseClickHandler(MouseEvent e) {
        Meter dashoMeter = GUIController.get().getDashoMeter();
        if (dashoMeter.isLengthSmallerThanFraction()) player.setDisableDashing(true);
        if (!dashoMeter.isLengthSmallerThanFraction()) player.setDisableDashing(false);
        if (!player.isDashing() && !player.isDashingDisabled()) {
            player.startDashing((new Point2D(e.getX(), e.getY())));
        }
    }

    public void setHandlers(Scene scene) {
        scene.setOnMouseMoved(this::mouseMoveHandler);
        scene.setOnMouseClicked(this::mouseClickHandler);
    }
}
