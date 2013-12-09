package com.twopeople.game;

import java.awt.*;

public class Racket extends Entity {
    public static int WIDTH = 60;
    public static int HEIGHT = 12;

    private int glowPosition = 0;

    public Racket(World world, int x, int y) {
        super(world, x, y, WIDTH, HEIGHT);
    }

    @Override
    public void update() {
        InputHandler input = world.getGame().getInput();

        int speed = 8;

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

        BonusManager bm = world.getGame().getBonusManager();
        if (bm.isInactive(BonusType.PlatformExtension)) {
            bm.activate(BonusType.PlatformExtension);
            setWidth((int) (getWidth() * 1.5));
        } else if (bm.isJustRemoved(BonusType.PlatformExtension)) {
            setWidth(WIDTH);
        }

        if (bm.isInactive(BonusType.PlatformShrink)) {
            bm.activate(BonusType.PlatformShrink);
            setWidth((int) (getWidth() / 1.5));
        } else if (bm.isJustRemoved(BonusType.PlatformShrink)) {
            setWidth(WIDTH);
        }

        glowPosition = 0;
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.cyan);
        Game.setOpacity(g, 1f);
        g.drawImage(Art.racket[0][0], getX(), getY(), getWidth(), getHeight(), null);
    }

    @Override
    public Rectangle getRectBB() {
        return new Rectangle(getX() - 2, getY(), getWidth() + 4, getHeight());
    }

    // todo
    public void setGlowPosition(int p) {
        System.out.println(p);
        glowPosition = p;
    }
}