package com.twopeople.game;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Alexey
 * At 2:05 PM on 11/20/13
 */

public class World {
    private Game game;

    private ArrayList<Entity> entities = new ArrayList<Entity>();
    private ArrayList<UIElement> uiElements = new ArrayList<UIElement>();
    private Racket racket;
    private Ball ball;

    private int level = 1;

    public World(Game game) {
        this.game = game;
        racket = new Racket(this, Game.WIDTH / 2 - Racket.WIDTH / 2, Game.HEIGHT + Game.BAR_HEIGHT - Racket.HEIGHT);
        ball = new Ball(this, 200, Game.HEIGHT - 200);

        loadLevel();
    }

    public void update(int delta) {
        Iterator<Entity> i = entities.iterator();
        while (i.hasNext()) {
            Entity entity = i.next();
            entity.update(delta);
            if (entity.shouldRemove()) {
                i.remove();
            }
        }
        racket.update(delta);
        ball.update(delta);

        Iterator<UIElement> i2 = uiElements.iterator();
        while (i2.hasNext()) {
            UIElement element = i2.next();
            element.update(delta);
            if (element.shouldRemove()) {
                i2.remove();
            }
        }

        if (!bricksPresent() && ball.isAttached()) {
            ++level;
            loadLevel();
        }
    }

    public void render(Graphics g) {
        g.drawImage(Art.background, 0, 0, Game.WIDTH, Game.HEIGHT + Game.BAR_HEIGHT, null);

        Iterator<Entity> i = entities.iterator();
        while (i.hasNext()) {
            i.next().render(g);
        }
        racket.render(g);
        ball.render(g);

        Iterator<UIElement> i2 = uiElements.iterator();
        while (i2.hasNext()) {
            i2.next().render(g);
        }
    }

    public void addBrick(int x, int y) {
        entities.add(new Brick(this, x, y));
    }

    public void addBonus(int x, int y, int w, int h, BonusType type) {
        entities.add(new Bonus(this, x, y, w, h, type));
    }

    public void addBonus(Entity entity, BonusType type) {
        addBonus(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight(), type);
    }

    public void addUiElement(UIElement element) {
        uiElements.add(element);
    }

    public Game getGame() {
        return this.game;
    }

    public boolean bricksPresent() {
        return entities.size() > 0;
    }

    public Racket getRacket() {
        return this.racket;
    }

    public Ball getBall() {
        return this.ball;
    }

    public ArrayList<Entity> getCollidingBricks(Entity entity) {
        ArrayList<Entity> colliders = new ArrayList<Entity>();
        for (Entity e : entities) {
            if (e.getCollisionSide(entity) != Entity.CollisionSide.None) {
                colliders.add(e);
            }
        }
        return colliders;
    }

    public void loadLevel() {
        try {
            String currentLine;

            BufferedReader br = new BufferedReader(new FileReader("res/levels/level" + level + ".txt"));

            int line = 0;
            while ((currentLine = br.readLine()) != null) {
                String[] chars = currentLine.split("");
                for (int i = 0, len = chars.length; i < len; ++i) {
                    if (chars[i].equals("1")) {
                        addBrick((i - 1) * Brick.WIDTH + i * 5, 11 + Game.BAR_HEIGHT + line * Brick.HEIGHT + line * 5);
                    }
                }

                ++line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}