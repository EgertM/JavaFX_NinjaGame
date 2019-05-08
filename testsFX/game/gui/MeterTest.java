package game.gui;

import game.GameController;
import game.Main;
import game.character.player.Player;
import javafx.geometry.Point2D;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MeterTest extends ApplicationTest {
    @Override
    public void start(Stage stage) {
        Main game = new Main();
        game.start(stage);
        clickOn("#play");
    }

    @Test
    public void testDashoMeter() throws TimeoutException {
        Point2D location = new Point2D(600, 600);
        WaitForAsyncUtils.waitFor(2, TimeUnit.SECONDS, () -> !GameController.get().isPaused());
        clickOn(location);
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () -> !Player.get().isDashing());
        Assert.assertTrue(GUIController.get().getDashoMeter().getLength() < Meter.getMeterInitialWidth());
    }

    @Test
    public void testHealthMeter() throws TimeoutException {
        WaitForAsyncUtils.waitFor(2, TimeUnit.SECONDS, () -> !GameController.get().isPaused());
        WaitForAsyncUtils.waitFor(30, TimeUnit.SECONDS, () -> GUIController.get().getHealthMeter().getLength() < Meter.getMeterInitialWidth());
        Assert.assertTrue(GUIController.get().getHealthMeter().getLength() < Meter.getMeterInitialWidth());
    }
}
