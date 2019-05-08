package game.character.player;


import game.GameController;
import game.character.Character;
import game.environment.Tree;
import game.gui.AnimatedImage;
import game.gui.GUIController;
import game.gui.SpriteStorage;
import game.gui.scoreboard.Scoreboard;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.MotionBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.util.List;

public class Player implements Character {
    public static final int TREE_TRUNK_RADIUS = 50;
    public static final double TREE_VIEW_ANGLE = 0.26;
    private Point2D head;
    private double distance;
    private Image image;
    private double width;
    private double height;
    private Image dashImage;
    private Image activeImage;
    private int z;
    private boolean dashing;
    private int speed;
    private static final int DASH_SPEED = 20;
    private int dashDist;
    private Point2D dashEndPos;
    private int flip;
    private int offset;
    private double rotationInRadians = 0;
    private boolean moving = false;
    private MotionBlur mb = null;
    private IntegerProperty score = new SimpleIntegerProperty(0);
    private MediaPlayer dashPlayer;
    private AnimatedImage animation;
    private static final Point2D STARTING_MOUSE_POS = new Point2D(320, 240);
    private Point2D dashStartPos = STARTING_MOUSE_POS;
    private Point2D lastMousePos = STARTING_MOUSE_POS;
    private static final int PLAYER_DISTANCE_FROM_MOUSE = 80;
    private static final int PLAYER_NO_DASHING_DISTANCE = 10;
    private static Player player = null;
    private long time = 0;
    private boolean dashingDisabled;

    private static final int DEFAULT_SPEED = 4;
    private static final int DEFAULT_DASH_DISTANCE = 200;
    private static final Point2D STARTING_POS = new Point2D(400, 400);
    private static final int DEFAULT_RADIUS = 32;
    private static final float MOTION_BLUR_RADIUS = 16.0f;
    private Timeline toggleToMenuTimeline;


    private void initializePlayer() {
        setDashing(false);
        setSpeed(DEFAULT_SPEED);
        setDashDist(DEFAULT_DASH_DISTANCE);
        setFlip(1);
        setOffset(0);
        setHead(STARTING_POS.getX(), STARTING_POS.getY());
        score.set(0);
        setZ((int) this.getY());
        this.activeImage = this.image;
    }

    public Player() {
        setDashing(false);
        setSpeed(DEFAULT_SPEED);
        setDashDist(DEFAULT_DASH_DISTANCE);
        setFlip(1);
        setOffset(0);
        setHead(10, 10);
        score.set(0);
        setZ((int) this.getY());
        player = this;
    }

    private Player(double r, Image image, Image dashImage) {
        Media dashSound = new Media(new File("res/dash.mp3").toURI().toString());
        dashPlayer = new MediaPlayer(dashSound);
        animation = new AnimatedImage(SpriteStorage.get().getPlayerAnim(), 10);
        this.dashImage = dashImage;
        setRadius(r);
        this.image = image;
        width = this.image.getWidth();
        height = this.image.getHeight();
        initializePlayer();
    }

    public void reset() {
        initializePlayer();
    }

    public Timeline getToggleToMenuTimeline() {
        return toggleToMenuTimeline;
    }

    public static Player get() {
        if (player == null) {
            player = new Player(DEFAULT_RADIUS, SpriteStorage.get().getPlayerImg(), SpriteStorage.get()
                    .getPlayerDashImg());
        }
        return player;
    }

    public void incrementTime() {
        time++;
    }

    public double getRotationInRadians() {
        return rotationInRadians;
    }

    public void setRotationInRadians(double rotationInRadians) {
        this.rotationInRadians = rotationInRadians;
    }

    public static int getPlayerDistanceFromMouse() {
        return PLAYER_DISTANCE_FROM_MOUSE;
    }

    public Point2D getLastMousePos() {
        return lastMousePos;
    }

    public void setLastMousePos(Point2D lastMousePos) {
        this.lastMousePos = lastMousePos;
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

    public Point2D getDashEndPos() {
        return this.dashEndPos;
    }

    public Point2D getHead() {
        return this.head;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeed() {
        return this.speed;
    }

    private static int getDashSpeed() {
        return DASH_SPEED;
    }

    private void setDashDist(int dashDist) {
        this.dashDist = dashDist;
    }

    private int getDashDist() {
        return this.dashDist;
    }

    public boolean isMoving() {
        return moving;
    }

    private boolean isColliding(List<Tree> trees) {
        for (Tree tree : trees) {
            if (getHead().distance(tree.getTrunk()) < TREE_TRUNK_RADIUS) {
                double angle = Math.abs(getRotation(tree.getTrunk()) - getRotationInRadians());
                if (angle / Math.PI < TREE_VIEW_ANGLE) return true;
            }
        }
        return false;
    }

    public boolean isDashing() {
        return this.dashing;
    }

    private void playSound() {
        if (dashPlayer == null) return;
        dashPlayer.stop();
        dashPlayer.play();
    }

    public void setDashing(boolean dashing) {
        this.dashing = dashing;
        if (dashing) {
            activeImage = dashImage;
            dashStartPos = head;
            moving = true;
            initMotionBlur();
            playSound();
        } else activeImage = image;
    }

    private void initMotionBlur() {
        mb = new MotionBlur();
        mb.setRadius(MOTION_BLUR_RADIUS);
        mb.setAngle(Math.toDegrees(rotationInRadians));
    }

    public MotionBlur getMotionBlur() {
        return mb;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public int getZ() {
        return this.z;
    }

    public void setHead(double x, double y) {
        head = new Point2D(x, y);
    }

    private Image getImage() {
        return this.activeImage;
    }

    private void setRadius(double r) {
    }

    public void setMouseDist(double mouseX, double mouseY) {
        distance = Math.sqrt(Math.pow((mouseX - getX()), 2) + Math.pow((mouseY - getY()), 2));
    }

    private double getDistance() {
        return distance;
    }

    public void setX(double x) {
        setHead(x, head.getY());
    }

    public void setY(double y) {
        setHead(head.getX(), y);
    }

    public double getX() {
        return head.getX();
    }

    public double getY() {
        return head.getY();
    }

    public Point2D getFeet() {
        double x = head.getX() + width / 4;
        double y = head.getY() + height / 2;
        return new Point2D(x, y);
    }

    private double getRotation(Point2D point) {
        double rotationInRadians;
        if (point.getX() > getX()) {
            rotationInRadians = Math.atan((point.getY() - getY()) / (point.getX() - getX()));
        } else {
            rotationInRadians = Math.atan((point.getY() - getY()) / (point.getX() - getX())) + Math.PI;
        }
        return rotationInRadians;
    }

    private void setRotation() {
        rotationInRadians = getRotation(lastMousePos);
    }

    public void startDashing(Point2D dashMouse) {
        setLastMousePos(dashMouse);
        setRotation();
        setDashEndPos(new Point2D(getX() + getDashDist() * Math.cos(getRotationInRadians()),
                getY() + getDashDist() * Math.sin(getRotationInRadians())));
        setDashing(true);
        GUIController.get().getDashoMeter().decrease();
    }

    private void updateNoDashMoving() {
        if (getDistance() > PLAYER_DISTANCE_FROM_MOUSE) {
            setHead(getX() + Math.cos(rotationInRadians) * getSpeed(), getY() + Math.sin(rotationInRadians)
                    * getSpeed());
            moving = true;
        } else {
            moving = false;
            setHead(getX(), getY());
        }
    }

    private void updateDashMoving() {
        setHead(getX() + Math.cos(rotationInRadians) * getDashSpeed(), getY() + Math.sin(rotationInRadians)
                * getDashSpeed());
    }

    private void updateMoving() {
        if (isColliding(Tree.getTrees())) {
            setDashing(false);
            setMouseDist(lastMousePos.getX(), lastMousePos.getY());
            setRotation();
            return;
        }
        if (!isDashing()) {
            setMouseDist(lastMousePos.getX(), lastMousePos.getY());
            setRotation();
            updateNoDashMoving();
        } else {
            updateDashMoving();
        }
    }

    public void updateDirection() {
        if (getX() < lastMousePos.getX() && getFlip() == 1 && !isDashing()) {
            setFlip(-1);
            setOffset(1);
        } else if (getX() > lastMousePos.getX() && getFlip() == -1 && !isDashing()) {
            setFlip(1);
            setOffset(0);
        }
    }

    public void update() {
        if (isDashing() && getDashEndPos().distance(getHead()) < PLAYER_NO_DASHING_DISTANCE) {
            setDashing(false);
        }
        updateMoving();
        updateDirection();
        setZ((int) getY());
    }

    private Image blur(Image image) {
        ImageView blurred = new ImageView(image);
        blurred.setEffect(mb);
        SnapshotParameters blurParams = new SnapshotParameters();
        blurParams.setFill(Color.TRANSPARENT);
        return blurred.snapshot(blurParams, null);

    }

    public void draw(GraphicsContext gc) {
        double xCoord = getHead().getX();
        double yCoord = getHead().getY();
        if (!moving || dashing) {
            Image playerImage = (moving ? blur(getImage()) : getImage());
            gc.drawImage(playerImage,
                    xCoord + getOffset() * getImage().getWidth(),
                    yCoord, getFlip() * getImage().getWidth(), getImage().getHeight());
            if (moving) PlayerDashLine.draw(dashStartPos, head);
        } else {
            gc.drawImage(animation.getFrame(time), xCoord + getOffset() * getImage().getWidth(),
                    yCoord, getFlip() * getImage().getWidth(), getImage().getHeight());
        }
    }

    public void kill() {
        GUIController guiController = GUIController.get();
        GameController.get().pause();
        guiController.setPauseScreenText("You died");
        guiController.getDashoMeter().stop();
        guiController.getHealthMeter().stop();
        Scoreboard.get().postScores();
        Scoreboard.get().getScores();
        toggleToMenuTimeline = new Timeline(new KeyFrame(Duration.seconds(5)));
        toggleToMenuTimeline.setOnFinished(event -> guiController.getPrimaryStage()
                .setScene(guiController.getMenuScene()));
        toggleToMenuTimeline.play();
    }

    public void addToScore() {
        score.set(score.get() + 1);
    }

    public int getScore() {
        return score.get();
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    public void setDisableDashing(boolean dashingDisabled) {
        this.dashingDisabled = dashingDisabled;
    }

    public boolean isDashingDisabled() {
        return dashingDisabled;
    }
}
