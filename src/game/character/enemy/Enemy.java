package game.character.enemy;

import game.GameController;
import game.character.Character;
import game.character.player.Player;
import game.gui.GUIController;
import game.gui.Meter;
import game.gui.SpriteStorage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enemy extends AbstractEnemy implements Character {
    private static final int DEFAULT_SPEED = 3;
    private Point2D coord;
    private Image image;
    private volatile boolean dashing;
    private int speed;
    private int dashSpeed;
    private int dashDist;
    private Point2D dashEndPos;
    private int flip;
    private int offset;
    private double movementDirection;
    private static boolean isSomeoneDashing = false;
    private static List<Enemy> enemies = new ArrayList<>();

    private static Media[] clangSounds;
    private boolean preparingToDash;
    private int orbitingCounter = 0;
    private int enemySpawnCounter;

    private static final int DISTANCE_FROM_PLAYER = 140;
    private static final int ORBITING_DISTANCE = 110;
    private static final int DEFAULT_DASH_SPEED = 17;
    private static final int DEFAULT_DASH_DISTANCE = 150;
    private static final int CLANG_SOUNDS_LENGTH = 6;
    private static final int COLLISION_ORBIT = 32;
    private static final double DASH_END_POINT_MULTIPLIER = 2.5;
    private static final int TIME_BEFORE_DASHING = 200;
    private static final int ORBITING_COUNTER_LIMIT = 20;
    private static final int RESOLUTION = 64;
    private static final double SPEED_MULTIPLIER = 0.1;
    private static final int SCREEN_EDGE_WIDTH = 50;

    public Enemy() {
        setDashing(false);
        setSpeed(DEFAULT_SPEED);
        setDashSpeed(DEFAULT_DASH_SPEED);
        setDashDist(DEFAULT_DASH_DISTANCE);
    }

    private Enemy(double x, double y, Image image) {
        clangSounds = new Media[CLANG_SOUNDS_LENGTH];
        for (int i = 1; i < CLANG_SOUNDS_LENGTH + 1; i++) {
            clangSounds[i - 1] = new Media(new File("res/clang_" + i + ".mp3").toURI().toString());
        }
        setCoord(x, y);
        this.image = image;
        setZ((int) this.getY());
        setDashing(false);
        setSpeed(DEFAULT_SPEED);
        setDashSpeed(DEFAULT_DASH_SPEED);
        setDashDist(DEFAULT_DASH_DISTANCE);
    }

    public void kill() {
        this.setDashing(false);
        isSomeoneDashing = false;
        GameController.getDrawOrder().remove(this);
        Enemy.getEnemies().remove(this);

    }

    private void playClang() {
        MediaPlayer clangPlayer = new MediaPlayer(clangSounds[(int) (System.currentTimeMillis() % clangSounds.length)]);
        clangPlayer.stop();
        clangPlayer.play();
    }

    private boolean isColliding(Player player) {
        return (this.getCoord().distance(player.getHead()) < COLLISION_ORBIT);
    }

    private void setMovementDirection(double dir) {
        this.movementDirection = dir;
    }

    private double getMovementDirection() {
        return this.movementDirection;
    }

    public void setFlip(int flip) {
        this.flip = flip;
    }

    public int getFlip() {
        return this.flip;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return this.offset;
    }

    private void setDashEndPos(Point2D dashEndPos) {
        this.dashEndPos = dashEndPos;
    }

    private Point2D getDashEndPos() {
        return this.dashEndPos;
    }

    public Point2D getCoord() {
        return this.coord;
    }

    public void setCoord(double x, double y) {
        this.coord = new Point2D(x, y);
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return this.speed;
    }

    private void setDashSpeed(int dashSpeed) {
        this.dashSpeed = dashSpeed;
    }

    private int getDashSpeed() {
        return this.dashSpeed;
    }

    private void setDashDist(int dashDist) {
        this.dashDist = dashDist;
    }

    public boolean isDashing() {
        return this.dashing;
    }

    private void setDashing(boolean dashing) {
        this.dashing = dashing;
        isSomeoneDashing = false;
    }

    public void setZ(int z) {
    }

    public int getZ() {
        return (int) this.coord.getY();
    }

    private Image getImage() {
        return image;
    }

    public double getY() {
        return coord.getY();
    }

    public double getX() {
        return coord.getX();
    }

    public static boolean isSomeoneDashing() {
        return isSomeoneDashing;
    }

    private double getMoveDirection(Point2D coord) {
        if (coord.getX() > this.getX()) {
            return Math.atan((coord.getY() - this.getY()) / (coord.getX() - this.getX()));
        } else {
            return Math.atan((coord.getY() - this.getY())
                    / (coord.getX() - this.getX())) + Math.PI;
        }
    }

    private void updateMoveDirection(Point2D coord) {
        this.setMovementDirection(getMoveDirection(coord));
    }

    private void setNextCoord(double speed) {
        this.setCoord(this.getX() + Math.cos(this.getMovementDirection()) * speed, this.getY()
                + Math.sin(this.getMovementDirection()) * speed);
    }

    private void dash(Point2D coord) {
        updateMoveDirection(coord);
        setNextCoord(getDashSpeed());
    }

    private Point2D getDashEndPoint(Point2D playerPosition) {
        Point2D vector = new Point2D(playerPosition.getX() - this.getCoord().getX(), playerPosition.getY()
                - this.getCoord().getY());
        return this.getCoord().add(vector.multiply(DASH_END_POINT_MULTIPLIER));
    }

    private void addEnemies() {
        Player player = Player.get();
        if (++enemySpawnCounter >= player.getScore()) {
            enemySpawnCounter = 0;
            Enemy.makeEnemies((int) (Math.log(player.getScore()) / Math.log(5)) + 1,
                    GUIController.get().getScreenWidth(), GUIController.get().getScreenHeight());
        }
        Enemy.makeEnemies(1, GUIController.get().getScreenWidth(), GUIController.get().getScreenHeight());
    }

    private void handleCollide(Player player) {
        if (this.isColliding(player)) {
            if (this.isDashing() && !player.isDashing()) {
                Meter healthMeter = GUIController.get().getHealthMeter();
                healthMeter.decrease();
                if (healthMeter.getLength() <= 0) player.kill();
            } else if (!this.isDashing() && player.isDashing()) {
                kill();
                player.addToScore();
                addEnemies();
            } else if (this.isDashing() && player.isDashing()) {
                playClang();
            }
        }
    }

    private void updateDashing() {
        if (getDashEndPos() != null && getDashEndPos().distance(getCoord()) < 10) {
            setDashing(false);
        } else {
            dash(dashEndPos);
        }
        handleCollide(Player.get());
    }

    private void initializeDashing() {
        Timeline wait = new Timeline(new KeyFrame(Duration.millis(TIME_BEFORE_DASHING)));
        wait.setOnFinished(event -> {
            setDashing(true);
            setPreparingToDash(false);
        });
        setPreparingToDash(true);
        wait.play();
        isSomeoneDashing = true;
        orbitingCounter = 0;
        setDashEndPos(getDashEndPoint(Player.get().getHead()));
    }

    private void move(int i) {
        Player player = Player.get();

        if (this.getCoord().distance(player.getHead()) < ORBITING_DISTANCE) {
            this.setMovementDirection(this.getMovementDirection() + Math.PI);
            setNextCoord(DEFAULT_SPEED);
            orbitingCounter = 0;
        } else if (this.getCoord().distance(player.getHead()) < DISTANCE_FROM_PLAYER) {
            this.setMovementDirection(this.getMovementDirection() + Math.pow(-1, i) * Math.PI / 2);
            setNextCoord(DEFAULT_SPEED + i * SPEED_MULTIPLIER);
            orbitingCounter += 1;
            if (!isSomeoneDashing && orbitingCounter > ORBITING_COUNTER_LIMIT) initializeDashing();
        } else {
            setNextCoord(getSpeed());
            orbitingCounter = 0;
        }
    }


    private void update(int i) {
        Player player = Player.get();

        if (isDashing()) {
            updateDashing();
        } else if (isPreparingToDash()) {
            handleCollide(player);
        } else {
            updateDirection();
            updateMoveDirection(player.getHead());
            move(i);
            handleCollide(player);
        }
    }

    public static void updateEnemies(List enemyList) {
        for (int i = 0; i < enemyList.size(); i++) {
            Enemy enemy = (Enemy) enemyList.get(i);
            enemy.update(i);
        }
    }

    public void draw(GraphicsContext gc) {
        gc.drawImage(this.getImage(),
                this.getX() + this.getOffset() * RESOLUTION,
                this.getY(), RESOLUTION * this.getFlip(), RESOLUTION);
    }

    public static void makeEnemies(int amount, int screenWidth, int screenHeight) {
        Random randomNumberGenerator = new Random();
        Image enemy = SpriteStorage.get().getEnemy();
        Timeline spawningTimeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            int i = (int) Math.pow(-1, randomNumberGenerator.nextInt(10));
            int j = (int) Math.pow(-1, randomNumberGenerator.nextInt(10));
            int x = i * randomNumberGenerator.nextInt(SCREEN_EDGE_WIDTH) + i * screenWidth;
            int y = j * randomNumberGenerator.nextInt(SCREEN_EDGE_WIDTH) + j * screenHeight;
            Enemy newEnemy = new Enemy(x, y, enemy);
            enemies.add(newEnemy);
            GameController.getDrawOrder().add(newEnemy);
        }));
        spawningTimeline.setCycleCount(amount);
        spawningTimeline.play();
    }

    public static List<Enemy> getEnemies() {
        return enemies;
    }

    public static void resetEnemies() {
        GameController.getDrawOrder().removeAll(enemies);
        enemies.clear();
    }

    private void setPreparingToDash(boolean preparingToDash) {
        this.preparingToDash = preparingToDash;
    }

    public boolean isPreparingToDash() {
        return preparingToDash;
    }
}
