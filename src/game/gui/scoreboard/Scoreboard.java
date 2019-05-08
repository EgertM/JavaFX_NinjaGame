package game.gui.scoreboard;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.scope.FacebookPermissions;
import com.restfb.scope.ScopeBuilder;
import com.restfb.types.NamedFacebookType;
import com.restfb.types.User;
import game.character.player.Player;
import game.gui.GUIController;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.TreeMap;

public class Scoreboard {
    private String appID = "422302361542637";
    private String appSecret = "16c58b6c8fa0fce635703e9c55e7ea15";

    private TreeMap<Integer, String> scoresGlobal = new TreeMap<>();

    private static final String SUCCESS_URL = "https://www.facebook.com/connect/login_success.html";

    private final WebView browser = new WebView();
    private final WebEngine webEngine = browser.getEngine();
    private User player = null;
    private Connection<NamedFacebookType> playerFriends;
    private static Scoreboard scoreboard = new Scoreboard();
    private boolean loggedIn = false;
    private String code;

    public static Scoreboard get() {
        return scoreboard;
    }

    public WebView getBrowser() {
        return browser;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void showLogin() {
        DefaultFacebookClient facebookClient = new DefaultFacebookClient(Version.LATEST);
        ScopeBuilder scopes = new ScopeBuilder();
        scopes.addPermission(FacebookPermissions.USER_FRIENDS);
        scopes.addPermission(FacebookPermissions.PUBLIC_PROFILE);
        String loadUrl = facebookClient.getLoginDialogUrl(appID, SUCCESS_URL, scopes);
        webEngine.load(loadUrl + "&display=popup&response_type=code");
        webEngine.getLoadWorker().stateProperty().addListener(
                (ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) -> {
                    if (newValue != Worker.State.SUCCEEDED) {
                        return;
                    }

                    String myUrl = webEngine.getLocation();

                    if ("https://www.facebook.com/dialog/close".equals(myUrl)) {
                        GUIController.get().setMenu();
                    }

                    if (myUrl.startsWith(SUCCESS_URL)) {
                        int pos = myUrl.indexOf("code=");
                        code = myUrl.substring(pos + "code=".length());
                        FacebookClient.AccessToken token = facebookClient.obtainUserAccessToken(appID,
                                appSecret, SUCCESS_URL, code);
                        this.player = createPlayer(token);
                        loggedIn = true;
                        getScores();
                        postScores();
                        GUIController.get().setMenu();
                    }
                });
    }

    private User createPlayer(FacebookClient.AccessToken token) {
        FacebookClient client = new DefaultFacebookClient(token.getAccessToken(), Version.LATEST);
        player = client.fetchObject("me", User.class, Parameter.with("fields", "first_name,last_name,name"));
        this.playerFriends = client.fetchConnection("me/friends", NamedFacebookType.class);
        return player;
    }

    public void postScores() {
        try {
            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append("http://dreamlo.com/lb/eAVMHLIhu0esxJWTsbeXzQRUQwgGsID0yZo7Vi8UhL3w/add");
            urlBuilder.append("/").append(this.player.getName().replace(" ", "_"));
            urlBuilder.append("/").append(Player.get().getScore());
            URL post = new URL(urlBuilder.toString());
            URLConnection yc = post.openConnection();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            yc.getInputStream()));
            in.close();
        } catch (IOException | NullPointerException ignored) {
        }
    }

    public void getScores() {
        try {
            URL get = new URL("http://dreamlo.com/lb/5ad43e72d6024519e0c8051c/quote/10");
            URLConnection yc = get.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;
            scoresGlobal.clear();
            if (playerFriends != null) {
                while ((inputLine = in.readLine()) != null) {
                    String[] line = inputLine.split(",");
                    for (NamedFacebookType playerFromList : playerFriends.getData()) {
                        String[] playerName = playerFromList.getName().split(" ");
                        if ((line[0].substring(1, line[0].length() - 1).contains(playerName[0])
                                && line[0].substring(1, line[0].length() - 1).contains(playerName[1]))
                                || (line[0].substring(1, line[0].length() - 1).contains(player.getFirstName())
                                && line[0].substring(1, line[0].length() - 1).contains(player.getLastName()))) {
                            scoresGlobal.put(Integer.parseInt(line[1].substring(1, line[1].length() - 1)),
                                    line[0].substring(1, line[0].length() - 1));
                        }
                    }
                }
            }
            in.close();
        } catch (IOException ignored) {
        }
    }

    public User getPlayer() {
        return this.player;
    }

    public TreeMap<Integer, String> getScoresGlobal() {
        return scoresGlobal;
    }

}
