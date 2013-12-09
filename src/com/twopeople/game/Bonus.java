package com.twopeople.game;

import java.awt.*;

public class Bonus extends Entity {
    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;

    private BonusType type;

    private boolean inAnimation = false;
    private int skippedTicks = 0;
    private int padding = 0;

    public Bonus(World world, int x, int y, BonusType type) {
        super(world, x, y, WIDTH, HEIGHT);
        this.type = type;
        setRound(true);
    }

    @Override
    public void update() {
        int speed = 6;

        if (world.getCollidingEntities(this).size() == 0 && !inAnimation) {
            move(0, speed);
        }

        if (world.getRacket().collidesWith(this)) {
            inAnimation = true;

            Game game = world.getGame();
            game.getBonusManager().add(type);

            if (type == BonusType.ExtraLife) {
                game.addLifes(1);
            }
        }

        if (inAnimation) {
            double angle = Math.atan2(40 - getY(), Game.WIDTH - 40 - getX());

            if (++skippedTicks > 12) {
                padding += 1;
                opacity -= 0.01f;
            }

            speed = 48;

            move((int) (speed * Math.cos(angle)), (int) (speed * Math.sin(angle)));

            if (Geom.getDistBetweenPoints(getX(), getY(), Game.WIDTH - 40, 40) <= 20 || opacity < 0f) {
                remove();
            }
        }

        if (getY() >= Game.HEIGHT + Game.BAR_HEIGHT) {
            remove();
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.pink);
        ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g.drawImage(Art.bonuses[0][0], getX() + padding / 2, getY() + padding / 2, getWidth() - padding, getHeight() - padding, null);
    }

    @Override
    public Circle getRoundBB() {
        return new Circle(getX() + getWidth() / 2, getY() + getHeight() / 2 + 5, (getWidth() + getHeight()) / 4);
    }
}