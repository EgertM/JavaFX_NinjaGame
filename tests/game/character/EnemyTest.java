package game.character;

import game.character.enemy.Enemy;
import game.character.player.Player;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import org.junit.Assert;
import org.junit.Test;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EnemyTest {
    private Player player = new CustomPlayer();

    @Test
    public void onlyOneEnemyDashingTest() {
        ((CustomPlayer) player).setKill(false);
        player.setHead(10, 10);

        List<Enemy> enemies = IntStream.range(0, 5).mapToObj(i -> {
            Enemy enemy = new Enemy();
            enemy.setCoord(20, 20);
            return enemy;
        }).collect(Collectors.toList());

        while (!Enemy.isSomeoneDashing()) Enemy.updateEnemies(enemies);

        boolean onlyOneDashing = false;
        boolean switched = false;

        for (Enemy enemy : enemies) {
            if (enemy.isDashing() || enemy.isPreparingToDash()) {
                onlyOneDashing = !switched;
                switched = true;
            }
        }

        Assert.assertTrue(onlyOneDashing);
    }
}
