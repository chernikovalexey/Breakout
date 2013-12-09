package com.twopeople.game;

import java.awt.*;
import java.util.Random;

public class Brick extends Entity {
    public static int id = -1;
    public static int WIDTH = 32;
    public static int HEIGHT = 16;

    private int line = 0;
    private int type = 0;

    public float velocityQuotient = 0.001789f;
    public int fading = 0;
    private int padding = 0;
    private int maxPadding = 15;

    public Brick(World world, int x, int y, int type) {
        super(world, x, y, WIDTH, HEIGHT);

        this.type = type;

        int rowItems = (Game.WIDTH) / (WIDTH + 5);
        line = id / rowItems + 1;

        id += 1;
    }

    public void update() {
        if (fading != 0) {
            padding += fading == -1 ? 3 : 0;
            opacity += fading * (0.0333f + (fading + 1) * 0.00165f);

            if (opacity <= 0) {opacity = 0; }
            if (opacity >= 1f) {opacity = 1f;}

            if (padding >= maxPadding) {
                super.remove();

                if (world.countBonuses() < 2) {
                    Random random = world.getRandom();

                    if (true || random.nextInt(128) % 5 * 2 == 0) { world.addBonus(this, BonusType.PlatformExtension); }
                }
            }
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.gray);
        Game.setOpacity(g, opacity);
        g.drawImage(Art.bricks[0][type - 1], getX() + padding / 2, getY() + padding, getWidth() - padding, getHeight() - padding, null);
//        g.fillRect((int) getRectBB().getX(), (int) getRectBB().getY(), (int) getRectBB().getWidth(), (int) getRectBB().getHeight());
    }

//    @Override
//    public Rectangle getRectBB() {
//        return new Rectangle(getX() - 1, getY() - 1, getWidth() + 2, getHeight() + 2);
//    }

    @Override
    public void remove() {
        if (fading != -1) {
            fading = -1;
            int gainScore = 10 * type;
            world.addUiElement(new FloatingSign("+" + gainScore, getX(), getY(), Game.WIDTH - 40, 40));
            world.getGame().addScore(gainScore);
        }
    }

    public int getLine() {
        return line;
    }
}