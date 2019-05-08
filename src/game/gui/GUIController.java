package game.gui;

import game.Drawable;
import game.GameController;
import game.Main;
import game.character.player.Player;
import game.gui.scoreboard.Scoreboard;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;


import java.io.File;
import java.util.Comparator;
import java.util.List;

public final class GUIController {
    private boolean music = true;
    private VBox vb;
    private static final int SCREEN_WIDTH = 1024;
    private static final int SCREEN_HEIGHT = 768;
    private MediaView musicMediaView;
    private StackPane menuStack; //menu stack
    private StackPane browserStack; //browser stack
    private StackPane scoreboardStack; // stack in scores´ pane
    private AnchorPane scoreViewAll; // scores main pane
    private StackPane instructionsView;
    private VBox instructionsTextField;
    private Button instructionsButton;
    private Button backButton;
    private Button scores;
    private Button facebookAuth;
    private Stage primaryStage;
    private Scene gameScene;
    private Scene menuScene;
    private MediaPlayer musicplayer;
    private Canvas canvas;
    private GraphicsContext gc;
    private Scene browser;
    private Scene scoreboard;
    private Scene instructionsScene;
    private Scoreboard playerScores;

    private Label pauseScreenTextLabel;
    private Meter healthMeter;
    private Meter dashoMeter;

    private static GUIController guiController = null;

    private GUIController() {
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Ninja game");
    }

    public static GUIController get() {
        if (guiController == null) {
            guiController = new GUIController();
        }
        return guiController;
    }

    public int getScreenWidth() {
        return SCREEN_WIDTH;
    }

    public int getScreenHeight() {
        return SCREEN_HEIGHT;
    }

    public GraphicsContext getGc() {
        return gc;
    }

    public Scene getGameScene() {
        return gameScene;
    }

    public Scene getMenuScene() {
        return menuScene;
    }

    private void makeMenu() {
        menuStack = new StackPane();
        menuStack.setId("menuScene");
    }

    private void makeBrowser() {
        browserStack = new StackPane();
    }

    private void makeScoreboard() {
        scoreViewAll = new AnchorPane();
        scoreboardStack = new StackPane();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }


    private void addMusic() {
        Media menuMusic = new Media(new File("res/gamemusic.mp3").toURI().toString());
        musicplayer = new MediaPlayer(menuMusic);
        musicMediaView = new MediaView(musicplayer);
        musicplayer.setAutoPlay(music);
        musicplayer.setCycleCount(MediaPlayer.INDEFINITE);
    }

    private void makeVBox() {
        vb = new VBox();
        vb.setPrefSize(700, 700);
        vb.setAlignment(Pos.TOP_CENTER);
        vb.setPadding(new Insets(100, 50, 150, 50));
        vb.setSpacing(10);
    }

    private void setHeading() {
        Label lbl = new Label("NINJA GAME");
        lbl.setFont(Font.font("Amble CN", FontWeight.BOLD, 80));
        lbl.setPadding(new Insets(20, 20, 100, 20));
        lbl.setId("heading");
        vb.getChildren().add(lbl);
    }

    private void addMenuButtons() {
        Button playButton = new Button("PLAY");
        playButton.setId("play");

        playButton.setOnMouseClicked(event -> GameController.get().initializeGame());

        Button toggleSoundButton = new Button("TOGGLE MUSIC");
        toggleSoundButton.setId("sound");

        toggleSoundButton.setOnMouseClicked(event -> {
            if (music) {
                music = false;
                musicplayer.setVolume(0);
            } else {
                music = true;
                musicplayer.setVolume(100);
            }
        });

        instructionsButton = new Button("INSTRUCTIONS");
        instructionsButton.setId("instructions");

        scores = new Button("SCORES");
        scores.setId("scores");

        facebookAuth = new Button("FACEBOOK");
        facebookAuth.setId("facebook");
        vb.setId("menuStack");
        vb.getChildren().addAll(playButton, toggleSoundButton, instructionsButton, scores, facebookAuth);
    }

    private void playMusicAndVideo() {
        menuStack.getChildren().addAll(vb, musicMediaView);
    }

    private void fbAuth() {
        facebookAuth.setOnMouseClicked(event -> {
            Scoreboard scoreboard = Scoreboard.get();
            if (!scoreboard.isLoggedIn()) {
                browserStack.getChildren().addAll(scoreboard.getBrowser());
                primaryStage.setScene(browser);
                scoreboard.showLogin();
                playerScores = scoreboard;
            }
        });
    }

    private void createScoreTable() { //creates scoreTableScene
        scores.setOnMouseClicked(event -> {
            StringBuilder text = new StringBuilder();
            int counterPlacement = 1;
            if (playerScores != null && playerScores.getScoresGlobal() != null) {
                for (Integer score : playerScores.getScoresGlobal().descendingKeySet()) {
                    String name = playerScores.getScoresGlobal().get(score);
                    String[] names = name.split("_");

                    if (counterPlacement < 10) {
                        text.append("  ").append(Integer.toString(counterPlacement)).append(".  ").append(names[0])
                                .append(" ").append(names[1]).append(" : ").append(score).append("\n");
                    } else {
                        text.append(Integer.toString(counterPlacement)).append(".  ").append(name).append("  : ")
                                .append(score).append("\n");
                    }
                    counterPlacement += 1;
                }
                Label temp = new Label();
                temp.setText(text.toString());
                temp.setId("scoreLabel");
                temp.setFont(new Font("Serif", 30));

                scoreViewAll.getChildren().clear();
                scoreboardStack.getChildren().clear();
                scoreViewAll.getChildren().addAll(backButton, scoreboardStack);
                scoreboardStack.getChildren().addAll(temp);

                scoreboardStack.setTranslateX(280);
                scoreboardStack.setTranslateY(100);

                String cssURL = "gui/style/ScoreboardStyle.css";
                String cssScores = Main.class.getResource(cssURL).toExternalForm();
                scoreboard.getStylesheets().add(cssScores);
                primaryStage.setScene(scoreboard);

                scoreboardStack.setId("scoreStack");
                scoreViewAll.setId("scoreScene");
            } else {

                Label info = new Label("PLEASE LOG IN TO FACEBOOK TO SEE SCORES!");

                VBox infoBox = new VBox(10);
                infoBox.getChildren().addAll(info);
                infoBox.setAlignment(Pos.CENTER);

                Scene infoScene = new Scene(infoBox, 300, 300);

                Stage infoWindow = new Stage();
                infoWindow.setTitle("ERROR");
                infoWindow.setScene(infoScene);

                infoWindow.setX(primaryStage.getX() + 200);
                infoWindow.setY(primaryStage.getY() + 200);

                infoWindow.show();
            }

        });
    }


    private void makeSceneAndCanvas() {
        // A Group is created
        Group root = new Group();
        // creates a new gameScene with a size of 640x480px
        gameScene = new Scene(root);
        canvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        root.getChildren().add(canvas);
    }

    private void makeInstructions() {
        instructionsButton.setOnMouseClicked(event -> {
            Label instructions = new Label(
                    "The goal of the game is to kill as many ninjas as "
                            + "\n" + "possible by dashing through them. It is as simple "
                            + "\n" + "as that. Enemy ninjas can dash too, so be careful."
                            + "\n" + "Controls:"
                            + "\n" + "\tDash -> click to dash in the direction of"
                            + "\n" + "\tyour mouse"
                            + "\n" + "\tMove -> ninja moves towards your mouse"
                            + "\n" + "\tP -> pauses the game"
                            + "\n" + "\tQ -> quits the game");
            instructions.setId("instructions");
            instructionsView = new StackPane();
            HBox topPart = new HBox();
            topPart.setId("topBox");
            instructionsScene = new Scene(topPart, SCREEN_WIDTH, SCREEN_HEIGHT);

            topPart.getChildren().addAll(backButton, instructionsView);
            instructionsView.setPadding(new Insets(0, 300, 0, 0));
            instructionsTextField = new VBox();
            instructionsTextField.setMaxWidth(800);
            instructionsTextField.setMaxHeight(450);
            instructionsTextField.setAlignment(Pos.TOP_CENTER);
            instructionsTextField.getChildren().addAll(instructions);

            instructionsView.getChildren().addAll(instructionsTextField);
            instructionsView.setId("instructionsBack");
            instructionsTextField.setId("instructionsBox");
            instructionsView.setId("instructionsScene");

            instructionsView.setPadding(new Insets(0, 100, 0, 100));

            String cssURL = "gui/style/InstructionsStyle.css";
            String cssInstructions = Main.class.getResource(cssURL).toExternalForm();
            instructionsScene.getStylesheets().add(cssInstructions);

            primaryStage.setScene(instructionsScene);

        });
    }

    private void setKeyPressHandlers() {
        gameScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.Q) {
                Timeline toggleToMenuTimeline = Player.get().getToggleToMenuTimeline();
                if (toggleToMenuTimeline != null) {
                    toggleToMenuTimeline.stop();
                }

                primaryStage.setScene(menuScene);
            } else if (event.getCode() == KeyCode.P) {
                if (GameController.get().isPaused()) {
                    GameController.get().unPause();
                } else {
                    GameController.get().pause();
                    this.setPauseScreenText("Game paused");
                }
            }
        });
    }

    private void backButtonClick() {
        backButton.setOnMouseClicked(event -> {
            primaryStage.setScene(menuScene);
        });
    }

    private void browser() {
        browser = new Scene(browserStack, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    private void scores() {
        scoreboard = new Scene(scoreViewAll, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    private void makeBackButton() {
        backButton = new Button("<-");
        backButton.setId("backButton");
    }

    private void setCSS() {
        menuScene = new Scene(menuStack, SCREEN_WIDTH, SCREEN_HEIGHT);
        String cssURL = "gui/style/Buttons.css";
        String cssButtons = Main.class.getResource(cssURL).toExternalForm();
        menuScene.getStylesheets().add(cssButtons);
        primaryStage.setScene(menuScene);
    }

    public void setMenu() {
        primaryStage.setScene(menuScene);
    }

    private void setFont() {
        gc = canvas.getGraphicsContext2D();
        Font theFont = Font.font("Helvetica", FontWeight.BOLD, 24);
        gc.setFont(theFont);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
    }

    public void makeGUI() {
        this.makeMenu();
        this.addMusic();
        this.makeVBox();
        this.setHeading();
        this.addMenuButtons();
        this.playMusicAndVideo();
        this.setCSS();
        this.makeSceneAndCanvas();
        this.setKeyPressHandlers();
        this.setFont();
        this.createScoreTable();
        this.makeBrowser();
        this.browser();
        this.fbAuth();
        this.makeScoreboard();
        this.makeBackButton();
        this.backButtonClick();
        this.makeInstructions();
        this.scores();

    }

    public void mainDraw(List drawOrder) {
        if (GameController.getToBeRemoved().size() != 0) {
            drawOrder.removeAll(GameController.getToBeRemoved());
            GameController.resetRemovable();
        }
        drawOrder.sort(Comparator.comparingInt(Drawable::getZ));

        for (int i = 0; i < drawOrder.size(); i++) {
            ((Drawable) drawOrder.get(i)).draw(gc);
        }
    }

    public void addScoreLabel() {
        Label scoreLabel = new Label();
        scoreLabel.setLayoutX(SCREEN_WIDTH / 2);
        scoreLabel.setLayoutY(50);
        scoreLabel.setStyle("-fx-font-weight: 900; -fx-font-size: 36px; -fx-text-fill: white;");
        scoreLabel.textProperty().bind(Player.get().scoreProperty().asString());
        ((Group) gameScene.getRoot()).getChildren().add(scoreLabel);
    }

    private void makePauseScreenTextLabel() {
        pauseScreenTextLabel = new Label();
        pauseScreenTextLabel.setTextAlignment(TextAlignment.CENTER);
        pauseScreenTextLabel.setAlignment(Pos.CENTER);

        pauseScreenTextLabel.setStyle("-fx-text-fill: white; -fx-font-size: 54px; -fx-font-weight: 900");
        ((Group) gameScene.getRoot()).getChildren().add(pauseScreenTextLabel);
    }

    public void setPauseScreenText(String text) {
        if (pauseScreenTextLabel == null) {
            makePauseScreenTextLabel();
        }
        pauseScreenTextLabel.setLayoutX(SCREEN_WIDTH / 2 - (100 / 7) * text.length());
        pauseScreenTextLabel.setLayoutY(SCREEN_HEIGHT / 2 - (100 / 7) * text.length());
        pauseScreenTextLabel.setText(text);
    }

    public void drawBackground() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                gc.drawImage(SpriteStorage.get().getBackground(), i * 256, j * 256, 256, 256);
            }
        }
    }

    public void createMeters() {
        healthMeter = new Meter(60, Color.RED, 2);
        healthMeter.setRegeneration(10, 2000);
        dashoMeter = new Meter(100, Color.BLUE, 4);
        dashoMeter.setRegeneration(10, 500);
    }

    public Meter getDashoMeter() {
        return dashoMeter;
    }

    public Meter getHealthMeter() {
        return healthMeter;
    }
}
