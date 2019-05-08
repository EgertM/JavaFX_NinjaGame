package game.environment;

import game.Drawable;
import game.GameController;
import game.gui.GUIController;
import game.gui.SpriteStorage;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class Tree extends EnvObject {
    public static final int TREE_WIDTH_FROM_EDGE = 200;
    public static final int TREE_HALF_WIDTH = 70;
    public static final int TREE_LENGTH = 110;
    private static Random random = new Random();
    private static List<Tree> trees = new ArrayList<>();

    private static Point2D getRandomPosition() {
        int x, y;
        final AtomicReference<Point2D> position = new AtomicReference<>(new Point2D(0, 0));
        do {
            x = random.nextInt(GUIController.get().getScreenWidth() - TREE_WIDTH_FROM_EDGE);
            y = random.nextInt(GUIController.get().getScreenHeight() - TREE_WIDTH_FROM_EDGE);
            position.set(new Point2D(x, y));
        } while (!trees.stream().allMatch(tree -> position.get().distance(tree.getCoord()) > TREE_WIDTH_FROM_EDGE));

        return position.get();
    }

    private Tree(Point2D position) {
        super(position.getX(), position.getY(), SpriteStorage.get().getTree());
    }

    private Tree() {
        this(getRandomPosition());
    }

    public static List<Tree> getTrees() {
        return trees;
    }

    public static void makeTrees() {
        List<Drawable> drawOrder = GameController.getDrawOrder();
        drawOrder.removeAll(trees);
        trees.clear();
        int n = random.nextInt(3) + 1;
        for (int i = 0; i < n; i++) {
            trees.add(new Tree());
        }
        drawOrder.addAll(trees);
    }

    public Point2D getTrunk() {
        return super.getCoord().add(TREE_HALF_WIDTH, TREE_LENGTH);
    }
}
