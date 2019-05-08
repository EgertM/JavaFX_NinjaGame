package game.character;

import game.character.enemy.Enemy;
import game.character.player.Player;
import game.character.player.PlayerController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import org.junit.Assert;
import org.junit.Test;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollisionTest {
    Player player = new CustomPlayer();
    PlayerController playerController = new PlayerController(player);

    @Test(expected = NullPointerException.class)
    public void testPlayerDashing() {
        player.setHead(10, 10);
        Enemy enemy = new Enemy();
        enemy.setCoord(20, 20);
        List<Enemy> enemies = new ArrayList<>(Collections.singleton(enemy));
        playerController.mouseClickHandler(CustomPlayer.getMouseEvent((int) enemy.getX(), (int) enemy.getY(), MouseEvent.MOUSE_MOVED, MouseButton.PRIMARY));
        player.update();
        while (player.isDashing()) {
            // Enemy.kill will trigger a NullPointerException and because of that we can be sure that the enemy was killed;
            player.update();
            Enemy.updateEnemies(enemies);
        }
    }
}
