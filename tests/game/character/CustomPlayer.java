package game.character;

import game.character.player.Player;
import javafx.event.EventType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class CustomPlayer extends Player {
    private boolean isKill = false;

    @Override
    public void kill() {
        isKill = true;
    }

    boolean isKill() {
        return isKill;
    }

    public void setKill(boolean kill) {
        isKill = kill;
    }

    public static MouseEvent getMouseEvent(int x, int y, EventType<MouseEvent> type, MouseButton mouseButton) {
        return new MouseEvent(type, x, y, x, y, mouseButton, 1, true, true, true, true, true, true, true, true, true, true, null);
    }
}
