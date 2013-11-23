package com.twopeople.game;

import java.awt.*;

/**
 * Created by Alexey
 * At 2:06 PM on 11/20/13
 */

public class Brick extends Entity {
    public static int WIDTH = 32;
    public static int HEIGHT = 16;

    private boolean fadingOut = false;
    private int padding = 0;
    private int maxPadding = 15;
    private float opacity = 1f;

    public Brick(World world, int x, int y) {
        super(world, x, y, WIDTH, HEIGHT);
    }

    public void update(int delta) {
        if (fadingOut) {
            padding += 1;
            opacity -= 0.0875f;
            if (opacity < 0) {
                opacity = 0;
            }
            if (padding >= maxPadding) {
                super.remove();
            }
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.gray);
        ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g.fillRect(getX() + padding / 2, getY() + padding / 2, getWidth() - padding / 2, getHeight() - padding / 2);
    }

    @Override
    public void remove() {
        fadingOut = true;
        world.addUiElement(new FloatingSign("+20", getX(), getY(), Game.WIDTH - 40, 40));
        world.getGame().addScore(20);
    }
}