package com.twopeople.game;

/**
 * Created by Alexey
 * At 2:20 PM on 11/25/13
 */

public enum BonusType {
    PlatformExtension(7.5, 0),
    PlatformShrink(5.5, 1),
    SpeedUp(10.5, 2),
    SpeedDown(9.5, 3),
    ExtraLife(-1.0, 4);

    private int lifetimeInMs; // stored in ms
    private int sprite;

    // taken in s as an argument
    BonusType(double lifetimeInSeconds, int sprite) {
        this.lifetimeInMs = (int) (lifetimeInSeconds * 1000);
        this.sprite = sprite;
    }

    public int getLifetime() {
        return lifetimeInMs;
    }

    public int getSprite() {
        return sprite;
    }
}