package com.twopeople.game;

/**
 * Created by Alexey
 * At 2:18 PM on 11/25/13
 */

public class Bonus extends Entity {
    private BonusType type;

    public Bonus(World world, int x, int y, int width, int height, BonusType type) {
        super(world, x, y, width, height);
        this.type = type;
    }
}