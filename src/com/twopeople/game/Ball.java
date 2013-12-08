package com.twopeople.game;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Alexey
 * At 2:16 PM on 11/20/13
 */

public class Ball extends Entity {
    public static int WIDTH = 16;
    public static int HEIGHT = 16;

    // In degrees
    private int rotation = -45;
    private boolean attached = true;
    private int spriteState = 3;
    private int animDir = 1;

    private long lastStateChange = System.currentTimeMillis();

    public Ball(World world, int x, int y) {
        super(world, x, y, WIDTH, HEIGHT);
        setRound(true);
        setNormalSpeed(2);
        setSpeed(2);
        setMaxSpeed(3);
    }

    @Override
    public void update() {
        InputHandler input = world.getGame().getInput();
        Racket racket = world.getRacket();

        if (attached) {
            setX(racket.getX() + racket.getWidth() / 2 - WIDTH / 2);
            setY(racket.getY() - HEIGHT);

            if (input.space.isClicked()) {
                attached = false;
            }
        } else {
            int speed = 10;
            double angle = getAngle();
            int dx = (int) (speed * Math.cos(angle));
            int dy = (int) (speed * Math.sin(angle));

            move(dx, dy);

            setWidth(WIDTH);
            setHeight(HEIGHT);

            if (getX() <= 0 || getX() >= Game.WIDTH - WIDTH) {
                move(-dx, 0);
                rotation = 180 - rotation;
            }

            ArrayList<Entity> collidingBricks = world.getCollidingEntities(this);
            if (collidingBricks.size() > 0) {
                for (Entity entity : collidingBricks) {
                    if (entity instanceof Brick) {
                        CollisionSide cs2 = entity.getCollisionSide(this);

                        if (cs2 == CollisionSide.Left || cs2 == CollisionSide.Right) {
                            move(-dx, 0);
                            rotation = 180 - rotation;
                        }

                        if (cs2 == CollisionSide.Top || cs2 == CollisionSide.Bottom) {
                            move(0, -dy);
                            rotation = -rotation;
                        }

                        entity.remove();
                        break;
                    }
                }
            }

            CollisionSide cs = racket.getCollisionSide(this);

            if (cs == CollisionSide.Left || cs == CollisionSide.Right) {
                move(-dx, 0);
                rotation = 180 - rotation;
            }

            if (cs == CollisionSide.Top && !world.bricksPresent()) {
                attached = true;
            } else {
                if (getY() <= Game.BAR_HEIGHT || cs == CollisionSide.Top) {
                    if (cs == CollisionSide.Top) {
                        racket.setGlowPosition((getX() - racket.getX()) / (Racket.WIDTH / 3));
                    }
                    move(0, -dy);
                    rotation = -rotation;
                }

                if (getY() >= Game.HEIGHT + Game.BAR_HEIGHT) {
                    world.getGame().decrementLifes();
                    attached = true;
                }
            }
        }

        long currentStateChange = System.currentTimeMillis();

        if (currentStateChange - lastStateChange > 85) {
            lastStateChange = currentStateChange;
            spriteState += animDir;

            if (spriteState < 3 || spriteState > 7) {
                animDir *= -1;
                spriteState += animDir;
            }
        }
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(Art.ball[spriteState][0], getX(), getY(), getWidth(), getHeight(), null);
    }

    public double getAngle() {
        return Math.toRadians(-rotation);
    }

    public boolean isAttached() {
        return this.attached;
    }
}