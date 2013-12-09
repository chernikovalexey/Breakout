package com.twopeople.game;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Entity {
    public enum CollisionSide {
        Left, Right, Top, Bottom,
        Unknown, None
    }

    public class Circle {
        public int x, y; // Of centre
        public int radius;

        public Circle(int x, int y, int radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }

        private boolean check(double sx, double sy, double ex, double ey) {
            return Geom.getDistFromPointToSegment((int) sx, (int) sy, (int) ex, (int) ey, x, y) <= radius;
        }

        public CollisionSide getCollisionSide(Rectangle rect) {
            boolean top = check(rect.getX(), rect.getY(), rect.getX() + rect.getWidth(), rect.getY());
            boolean right = check(rect.getX() + rect.getWidth(), rect.getY(), rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight());
            boolean bottom = check(rect.getX(), rect.getY() + rect.getHeight(), rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight());
            boolean left = check(rect.getX(), rect.getY(), rect.getX(), rect.getY() + rect.getHeight());

            // todo refactor
            if (top) {
                return CollisionSide.Top;
            }
            if (bottom) {
                return CollisionSide.Bottom;
            }
            if (right) {
                return CollisionSide.Right;
            }
            if (left) {
                return CollisionSide.Left;
            }

            return CollisionSide.None;
        }
    }

    protected World world;

    private int x, y;
    private int width, height;
    private int normalSpeed, speed, maxSpeed;
    public float opacity = 1f;

    private boolean isRound = false;
    private boolean shouldBeRemoved = false;

    public Entity(World world, int x, int y, int width, int height) {
        this.world = world;
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
    }

    public void update() {
    }

    public void render(Graphics g) {
    }

    private boolean hasMovingCollisions() {
        return world.getCollidingEntities(this).size() > 0 || world.getRacket().collidesWith(this) || world.getBall().collidesWith(this);
    }

    public void move(int dx, int dy) {
        if (dx != 0) {
            setX(getX() + dx);
            /*if (hasMovingCollisions()) {
                setX(getX() - dx);

                if (dx > 0) {
                    move(dx + (dx > 0 ? -1 : 1), 0);
                }
            }*/
        }

        if (dy != 0) {
            setY(getY() + dy);
            /*if (hasMovingCollisions()) {
                setY(getY() - dy);

                if (dy > 0) {
                    move(0, dy + (dy > 0 ? -1 : 1));
                }
            }*/
        }
    }

    public Rectangle getRectBB() {
        return new Rectangle(x, y, width, height);
    }

    public Circle getRoundBB() {
        return new Circle(x + width / 2, y + height / 2, (width + height) / 4);
    }

    public CollisionSide getCollisionSide(Entity entity) {
        CollisionSide side = CollisionSide.None;

        if (entity == this) {
            return side;
        }

        if (entity.isRound() && !isRound()) {
            side = entity.getRoundBB().getCollisionSide(getRectBB());
        } else if (isRound() && !entity.isRound()) {
            side = getRoundBB().getCollisionSide(entity.getRectBB());
        } else if (!entity.isRound() && !isRound()) {
            side = getRectBB().intersects(entity.getRectBB()) ? CollisionSide.Unknown : CollisionSide.None;
        }
        return side;
    }

    public boolean collidesWith(Entity entity) {
        return getCollisionSide(entity) != CollisionSide.None;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isRound() {
        return this.isRound;
    }

    public void setRound(boolean isRound) {
        this.isRound = isRound;
    }

    public int getNormalSpeed() {
        return normalSpeed;
    }

    public void setNormalSpeed(int normalSpeed) {
        this.normalSpeed = normalSpeed;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public boolean shouldRemove() {
        return this.shouldBeRemoved;
    }

    public void remove() {
        this.shouldBeRemoved = true;
    }

    public float getOpacity() {
        return this.opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }
}