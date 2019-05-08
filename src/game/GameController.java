package game;

import game.character.enemy.Enemy;
import game.character.enemy.EnemyArcher;
import game.character.player.Player;
import game.environment.Tree;
import game.gui.GUIController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public final class GameController {

    private GUIController guiController = null;
    private static GameController gameController = null;
    private Player player = null;
    private static List<Drawable> drawOrder = null;
    private static List<Drawable> toBeRemoved = new ArrayList<>();
    private boolean paused = true;

    private GameController() {
        guiController = GUIController.get();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public static void setDrawOrder(List<Drawable> drawOrderInput) {
        drawOrder = drawOrderInput;
    }

    public static void removeFromDrawOrder(List<Drawable> remove) {
        toBeRemoved = remove;
    }

    public static void resetRemovable() {
        toBeRemoved = new ArrayList<Drawable>();
    }

    public static List<Drawable> getToBeRemoved() {
        return toBeRemoved;
    }

    public static List<Drawable> getDrawOrder() {
        return drawOrder;
    }

    public static GameController get() {
        if (gameController == null) {
            gameController = new GameController();
        }
        return gameController;
    }

    public void initializeGame() {
        unPause();
        guiController.getPrimaryStage().setScene(guiController.getGameScene());
        guiController.setPauseScreenText("");
        player.reset();
        Enemy.resetEnemies();
        EnemyArcher.resetEnemies();
        Tree.makeTrees();
        Timeline waitBeforeSpawningTimeline = new Timeline(new KeyFrame(Duration.seconds(5)));
        waitBeforeSpawningTimeline.play();
        waitBeforeSpawningTimeline.setOnFinished(event -> {
            Enemy.makeEnemies(1, guiController.getScreenWidth(), guiController.getScreenHeight());
            EnemyArcher.makeEnemies(1, guiController.getScreenWidth(), guiController.getScreenHeight());
        });
        guiController.addScoreLabel();
        if (guiController.getDashoMeter() != null) {
            guiController.getHealthMeter().remove();
            guiController.getDashoMeter().remove();
        }
        guiController.createMeters();
    }

    public boolean isPaused() {
        return paused;
    }

    public void pause() {
        paused = true;
    }

    public void unPause() {
        paused = false;
        GUIController.get().setPauseScreenText("");
    }
}
