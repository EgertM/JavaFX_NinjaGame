package game.character;

import game.character.player.Player;
import game.character.player.PlayerController;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.junit.Test;
import org.junit.Assert;

public class PlayerTest {

    private Player player = new Player();
    private PlayerController playerController = new PlayerController(player);

    @Test
    public void testMoving() {
        player.setHead(10, 10);
        playerController.mouseMoveHandler(CustomPlayer.getMouseEvent(200, 200, MouseEvent.MOUSE_MOVED, MouseButton.NONE));
        player.update();
        Assert.assertTrue(player.isMoving());
        while (player.isMoving()) player.update();
        Assert.assertTrue(player.getHead().distance(new Point2D(200, 200)) <= Player.getPlayerDistanceFromMouse());
    }
}
