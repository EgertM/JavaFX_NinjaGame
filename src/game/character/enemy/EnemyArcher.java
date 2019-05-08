package game.character.enemy;

import game.GameController;
import game.character.Character;
import game.character.player.Player;
import game.gui.GUIController;
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

public class EnemyArcher extends AbstractEnemy implements Character {
    private static final int DEFAULT_SPEED = 3;
    public static final int COLLISION_RADIUS = 32;
    public static final double SHOOT_END_POS_VECTOR_FACTOR = 2.5;
    public static final int DURATION_BEFORE_SHOOTING = 500;
    public static final double SPEED_MULTIPLIER = 0.1;
    public static final int ORBITING_COUNTER_LIMIT = 20;
    public static final int DRAWING_MULTIPLIER = 64;
    public static final int SCREEN_WIDTH_EDGE = 50;
    private Point2D coord;
    private Image image;
    private volatile boolean shooting;
    private int speed;
    private Point2D shootEndPos;
    private int flip;
    private int offset;
    private double movementDirection;
    private static boolean isSomeoneShooting = false;
    private static List<EnemyArcher> enemies = new ArrayList<>();
    private static final int DISTANCE_FROM_PLAYER = 240;
    private static final int ORBITING_DISTANCE = 180;
    private MediaPlayer shootPlayer;

    private boolean preparingToShoot;
    private int orbitingCounter = 0;
    private int enemySpawnCounter;

    private void playSound() {
        if (shootPlayer == null) return;
        shootPlayer.stop();
        shootPlayer.play();
    }

    private EnemyArcher(double x, double y, Image image) {
        setCoord(x, y);
        this.image = image;
        setShooting(false);
        setSpeed(DEFAULT_SPEED);

        Media shootSound = new Media(new File("res/bowShoot.mp3").toURI().toString());
        shootPlayer = new MediaPlayer(shootSound);
    }

    public void kill() {
        this.setShooting(false);
        isSomeoneShooting = false;
        GameController.getDrawOrder().remove(this);
        EnemyArcher.getEnemies().remove(this);

    }

    private boolean isColliding(Player player) {
        return (this.getCoord().distance(player.getHead()) < COLLISION_RADIUS);
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

    private void setShootEndPos(Point2D shootEndPos) {
        this.shootEndPos = shootEndPos;
    }

    public Point2D getShootEndPos() {
        return this.shootEndPos;
    }

    public Point2D getCoord() {
        return this.coord;
    }

    private void setCoord(double x, double y) {
        this.coord = new Point2D(x, y);
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return this.speed;
    }

    private boolean isShooting() {
        return this.shooting;
    }

    private void setShooting(boolean shooting) {
        this.shooting = shooting;
        isSomeoneShooting = false;
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

    private void updateMoveDirection(Point2D coord) {
        if (coord.getX() > this.getX()) {
            this.setMovementDirection(Math.atan((coord.getY() - this.getY()) / (coord.getX() - this.getX())));
        } else if (coord.getX() < this.getX()) {
            this.setMovementDirection(Math.atan((coord.getY() - this.getY()) / (coord.getX() - this.getX())) + Math.PI);
        }
    }

    private void setNextCoord(double speed) {
        this.setCoord(this.getX() + Math.cos(this.getMovementDirection()) * speed, this.getY()
                + Math.sin(this.getMovementDirection()) * speed);
    }

    private void shoot(Point2D coord) {
        EnemyArrow shotArrow = new EnemyArrow(this.getX(), this.getY(), SpriteStorage.get().getEnemyArrow(), coord);
        GameController.getDrawOrder().add(shotArrow);
        this.playSound();
    }

    private Point2D getShootEndPoint(Point2D playerPosition) {
        Point2D vector = new Point2D(playerPosition.getX() - this.getCoord().getX(), playerPosition.getY()
                - this.getCoord().getY());
        return this.getCoord().add(vector.multiply(SHOOT_END_POS_VECTOR_FACTOR));
    }

    private void addEnemies() {
        Player player = Player.get();
        if (++enemySpawnCounter >= player.getScore()) {
            enemySpawnCounter = 0;
            EnemyArcher.makeEnemies((int) (Math.log(player.getScore()) / Math.log(5)) + 1,
                    GUIController.get().getScreenWidth(), GUIController.get().getScreenHeight());
        }
        EnemyArcher.makeEnemies(1, GUIController.get().getScreenWidth(), GUIController.get().getScreenHeight());
    }

    private void handleCollide(Player player) {
        if (this.isColliding(player)) {
            if (player.isDashing()) {
                kill();
                player.addToScore();
                addEnemies();
            }
        }
    }

    private void updateShooting() {
        shoot(shootEndPos);
        setShooting(false);
        handleCollide(Player.get());
    }

    private void initializeShooting() {
        Timeline wait = new Timeline(new KeyFrame(Duration.millis(DURATION_BEFORE_SHOOTING)));
        wait.setOnFinished(event -> {
            setShooting(true);
            setPreparingToShoot(false);
        });
        setPreparingToShoot(true);
        wait.play();
        isSomeoneShooting = true;
        orbitingCounter = 0;
        setShootEndPos(getShootEndPoint(Player.get().getHead()));
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
            if (!isSomeoneShooting && orbitingCounter > ORBITING_COUNTER_LIMIT) initializeShooting();
        } else {
            setNextCoord(getSpeed());
            orbitingCounter = 0;
        }
    }


    private void update(int i) {
        Player player = Player.get();
        if (isShooting()) {
            updateShooting();
        } else if (isPreparingToShoot()) {
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
            game.character.enemy.EnemyArcher enemy = (game.character.enemy.EnemyArcher) enemyList.get(i);
            enemy.update(i);
        }
    }

    public void draw(GraphicsContext gc) {
        gc.drawImage(this.getImage(),
                this.getX() + this.getOffset() * DRAWING_MULTIPLIER,
                this.getY(), DRAWING_MULTIPLIER * this.getFlip(), DRAWING_MULTIPLIER);
    }

    public static void makeEnemies(int amount, int screenWidth, int screenHeight) {
        Random randomNumberGenerator = new Random();
        Image enemyImage = SpriteStorage.get().getEnemyArcher();
        Timeline spawningTimeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> {
            int i = (int) Math.pow(-1, randomNumberGenerator.nextInt(10));
            int j = (int) Math.pow(-1, randomNumberGenerator.nextInt(10));
            int x = i * randomNumberGenerator.nextInt(SCREEN_WIDTH_EDGE) + i * screenWidth;
            int y = j * randomNumberGenerator.nextInt(SCREEN_WIDTH_EDGE) + j * screenHeight;
            EnemyArcher newEnemy = new EnemyArcher(x, y, enemyImage);
            enemies.add(newEnemy);
            GameController.getDrawOrder().add(newEnemy);
        }));
        spawningTimeline.setCycleCount(amount);
        spawningTimeline.play();
    }

    public static List<EnemyArcher> getEnemies() {
        return enemies;
    }

    public static void resetEnemies() {
        GameController.getDrawOrder().removeAll(enemies);
        enemies.clear();
    }

    private void setPreparingToShoot(boolean preparingToShoot) {
        this.preparingToShoot = preparingToShoot;
    }

    private boolean isPreparingToShoot() {
        return preparingToShoot;
    }
}



