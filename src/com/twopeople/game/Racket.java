package com.twopeople.game;

import java.awt.*;

/**
 * Created by Alexey
 * At 2:16 PM on 11/20/13
 */

public class Racket extends Entity {
    public static int WIDTH = 55;
    public static int HEIGHT = 10;

    public Racket(World world, int x, int y) {
        super(world, x, y, WIDTH, HEIGHT);
    }

    @Override
    public void update(int delta) {
        InputHandler input = world.getGame().getInput();

        int speed = 4;

        //        if (input.hasMouseMoved() && input.isMouseIn()) {
        //            setX(input.getMouseX());
        //        } else {
        if (input.left.isDown()) { move(-speed, 0); }
        if (input.right.isDown()) { move(speed, 0); }
//        //        }

        if (getX() <= 0) {
            setX(0);
        }

        if (getX() >= Game.WIDTH - WIDTH) {
            setX(Game.WIDTH - WIDTH);
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.cyan);
        g.fillRect(getX(), getY(), getWidth(), getHeight());
    }
}