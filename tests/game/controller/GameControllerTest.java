package game.controller;

import game.GameController;
import org.junit.Assert;
import org.junit.Test;

public class GameControllerTest {
    @Test
    public void testPausing() {
        GameController gameController = GameController.get();
        gameController.pause();
        Assert.assertTrue(gameController.isPaused());
    }
}
