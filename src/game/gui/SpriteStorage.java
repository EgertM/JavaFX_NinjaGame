package game.gui;

import javafx.scene.image.Image;

public final class SpriteStorage {
    private Image arrow = new Image("file:res/arrow.png");
    private Image background = new Image("file:res/tiles.jpg");
    private Image background2 = new Image("file:res/grass_diffuse.jpg");
    private Image tree = new Image("file:res/tree_1.png");
    private Image enemy = new Image("file:res/enemy.png");
    private Image playerImg = new Image("file:res/player.png");
    private Image playerDashImg = new Image("file:res/playerdash.png");
    private Image player0 = new Image("file:res/player_0.png");
    private Image player1 = new Image("file:res/player_1.png");
    private Image enemyBowman = new Image("file:res/enemybowman.png");
    private Image enemyArrow = new Image("file:res/enemyArrow.png");
    private static SpriteStorage spriteStorage = null;

    private SpriteStorage() {
    }

    public static SpriteStorage get() {
        if (spriteStorage == null) {
            spriteStorage = new SpriteStorage();
        }
        return spriteStorage;
    }

    public Image getArrow() {
        return arrow;
    }

    public Image getEnemyArcher() {
        return enemyBowman;
    }

    public Image getEnemyArrow() {
        return enemyArrow;
    }

    public Image getBackground() {
        return background2;
    }

    public Image getTree() {
        return tree;
    }

    public Image getEnemy() {
        return enemy;
    }

    public Image getPlayerImg() {
        return playerImg;
    }

    public Image[] getPlayerAnim() {
        return new Image[]{player0, player1};
    }

    public Image getPlayerDashImg() {
        return playerDashImg;
    }
}
