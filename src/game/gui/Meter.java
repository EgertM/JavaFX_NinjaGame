package game.gui;

import game.character.player.Player;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleBinaryOperator;

public class Meter {
    private static final double METER_INITIAL_WIDTH = 150;
    private final Rectangle innerRectangle;
    private Timeline regeneration;
    private int denominator;
    private List<Timeline> meterChangers;
    private final Rectangle outerRectangle;

    Meter(double y, Color innerColor, int denominator) {
        outerRectangle = new Rectangle();
        outerRectangle.setX(800);
        outerRectangle.setY(y);
        outerRectangle.setWidth(METER_INITIAL_WIDTH);
        outerRectangle.setHeight(25);
        outerRectangle.setStrokeType(StrokeType.OUTSIDE);
        outerRectangle.setStroke(Color.BLACK);
        outerRectangle.setStrokeWidth(3);
        outerRectangle.setFill(Color.TRANSPARENT);

        innerRectangle = new Rectangle();
        innerRectangle.setX(800);
        innerRectangle.setY(y);
        innerRectangle.setWidth(METER_INITIAL_WIDTH);
        innerRectangle.setHeight(25);
        innerRectangle.setStrokeType(StrokeType.INSIDE);
        innerRectangle.setStroke(innerColor);
        innerRectangle.setFill(innerColor);

        ((Group) GUIController.get().getGameScene().getRoot()).getChildren().addAll(outerRectangle, innerRectangle);

        this.denominator = denominator;
        meterChangers = new ArrayList<>();
    }

    public static double getMeterInitialWidth() {
        return METER_INITIAL_WIDTH;
    }

    public void remove() {
        ((Group) GUIController.get().getGameScene().getRoot()).getChildren().removeAll(outerRectangle, innerRectangle);
    }

    public void stop() {
        regeneration.stop();
        for (Timeline meterChanger : meterChangers) {
            meterChanger.stop();
        }
    }

    public void setRegeneration(double time, int n) {
        regeneration = new Timeline(new KeyFrame(Duration.millis(time), event -> {
            increase(n);
            if (innerRectangle.getWidth() >= METER_INITIAL_WIDTH / denominator)
                Player.get().setDisableDashing(false);
        }));
        regeneration.setCycleCount(Timeline.INDEFINITE);
        regeneration.play();
    }

    private void changeMeter(DoubleBinaryOperator change) {
        final double newWidth = Math.max(0, Math.min(change.applyAsDouble(innerRectangle.getWidth(), METER_INITIAL_WIDTH / denominator), METER_INITIAL_WIDTH));

        Timeline meterChanger = new Timeline();
        meterChanger.getKeyFrames().add(new KeyFrame(Duration.millis(10), event -> {
            regeneration.pause();
            innerRectangle.setWidth(innerRectangle.getWidth() - 1);
            if (innerRectangle.getWidth() <= newWidth) {
                innerRectangle.setWidth(newWidth);
                meterChanger.stop();
                regeneration.play();
            }
        }));
        meterChanger.setCycleCount(Timeline.INDEFINITE);
        meterChanger.play();
        meterChangers.add(meterChanger);
    }

    private void increase(int n) {
        changeMeter((a, b) -> a + (b*denominator)/n);
    }

    public void increase() {
        increase(1);
    }

    public void decrease() {
        changeMeter((a, b) -> a - b);
    }

    public double getLength() {
        return innerRectangle.getWidth();
    }

    public boolean isLengthSmallerThanFraction() {
        return innerRectangle.getWidth() <= METER_INITIAL_WIDTH/denominator;
    }
}
