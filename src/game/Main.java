package game;

import game.character.enemy.Enemy;
import game.character.enemy.EnemyArcher;
import game.character.player.Player;
import game.character.player.PlayerArrow;
import game.character.player.PlayerController;
import game.environment.Tree;
import game.gui.GUIController;
import game.gui.SpriteStorage;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main extends Application {

    // The main method starts the application
    public static void main(String[] args) {
        launch(args);
    }

    // The Application class method start() has to be overridden
    private static SpriteStorage spriteStorage = SpriteStorage.get();
    private static Player player = Player.get();




    @Override
    public void start(Stage primaryStage) {

        GUIController guiController = GUIController.get();
        GameController gameController = GameController.get();
        gameController.setPlayer(player);
        guiController.setPrimaryStage(primaryStage);
        primaryStage.setMaxWidth(guiController.getScreenWidth());
        primaryStage.setMaxHeight(guiController.getScreenHeight());
        primaryStage.setResizable(false);
        guiController.makeGUI();

        Image arrow = spriteStorage.getArrow();

        PlayerArrow playerArrow = new PlayerArrow(player.getX(), player.getY(), arrow);
        List<Enemy> enemies = Enemy.getEnemies();
        List<EnemyArcher> enemyArchers = EnemyArcher.getEnemies();

        List<Drawable> drawOrder = new ArrayList<>(Arrays.asList(player, playerArrow));
        GameController.setDrawOrder(drawOrder);
        Tree.makeTrees();

        PlayerController playerController = new PlayerController(player);
        playerController.setHandlers(guiController.getGameScene());

        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                if (!GameController.get().isPaused()) {
                    guiController.drawBackground();
                    player.incrementTime();
                    player.setMouseDist(player.getLastMousePos().getX(), player.getLastMousePos().getY());
                    player.update();
                    Enemy.updateEnemies(enemies);
                    EnemyArcher.updateEnemies(enemyArchers);
                    playerArrow.setZ((int) player.getY());
                    guiController.mainDraw(GameController.getDrawOrder());
                }
            }
        }.start();
        primaryStage.show();
    }

}
