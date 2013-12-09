package com.twopeople.game;

import java.awt.*;
import java.util.ArrayList;

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

//            if (input.left.isDown()) { move(-1, 0); }
//            if (input.right.isDown()) { move(1, 0); }
//            if (input.up.isDown()) { move(0, -1); }
//            if (input.down.isDown()) { move(0, 1); }
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

                        System.out.println(cs2);

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
                System.out.println("Weird moving direction ...");
                rotation = 180 - rotation;
            }

            if (cs == CollisionSide.Top && !world.bricksPresent()) {
                attached = true;
            } else {
                if (getY() <= Game.BAR_HEIGHT || cs == CollisionSide.Top) {
                    //                    System.out.println("Fuuuuu");
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

        if (currentStateChange - lastStateChange > 75) {
            lastStateChange = currentStateChange;
            spriteState += animDir;

            if (spriteState < 1 || spriteState > 2) {
                animDir *= -1;
                spriteState += animDir;
            }
        }
    }

    @Override
    public void render(Graphics g) {
        g.drawImage(Art.ball[spriteState][0], getX(), getY(), getWidth(), getHeight(), null);
        //        g.fillOval(getRoundBB().x - getRoundBB().radius, getRoundBB().y - getRoundBB().radius, getRoundBB().radius * 2, getRoundBB().radius * 2);
    }

    public double getAngle() {
        return Math.toRadians(-rotation);
    }

    public boolean isAttached() {
        return this.attached;
    }
}