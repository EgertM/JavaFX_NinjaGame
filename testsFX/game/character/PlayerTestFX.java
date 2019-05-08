package game.character;

import game.GameController;
import game.Main;
import game.character.player.Player;
import game.gui.GUIController;
import javafx.geometry.Point2D;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PlayerTestFX extends ApplicationTest {
    @Override
    public void start(Stage stage) {
        Main game = new Main();
        game.start(stage);
        clickOn("#play");
    }

    @Test
    public void testPlayerDashing() throws TimeoutException {
        Point2D location = new Point2D(600, 600);
        WaitForAsyncUtils.waitFor(2, TimeUnit.SECONDS, () -> !GameController.get().isPaused());
        clickOn(location);
        Point2D dashEndPos = Player.get().getDashEndPos();
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () -> !Player.get().isDashing());
        Assert.assertTrue(Player.get().getHead().distance(dashEndPos) <= Player.getPlayerDistanceFromMouse());
    }

    @Test
    public void testPlayerDying() throws TimeoutException {
        WaitForAsyncUtils.waitFor(2, TimeUnit.SECONDS, () -> !GameController.get().isPaused());
        WaitForAsyncUtils.waitFor(30, TimeUnit.SECONDS, () -> GUIController.get().getHealthMeter().getLength() <= 0);
        Assert.assertTrue(GUIController.get().getHealthMeter().getLength() <= 0);
    }
}
