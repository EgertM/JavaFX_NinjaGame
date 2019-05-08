package game.character;

import game.Drawable;


public interface Character extends Drawable {
    int getFlip();
    void setFlip(int flip);

    int getOffset();
    void setOffset(int offset);

    int getSpeed();
    void setSpeed(int speed);

    int getZ();

    double getX();
    double getY();

    void updateDirection();

    void kill();
}
