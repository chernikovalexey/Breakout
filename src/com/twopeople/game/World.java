package com.twopeople.game;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class World {
    private Game game;

    private ArrayList<Entity> bricks = new ArrayList<Entity>();
    private ArrayList<Entity> bonuses = new ArrayList<Entity>();
    private ArrayList<UIElement> uiElements = new ArrayList<UIElement>();
    private Racket racket;
    private Ball ball;

    private Random random = new Random();

    private int level = 1;
    private int maxLevel = 0;

    private boolean racketFadeIn = false;
    private long lastRacketStateChange = 0;
    private boolean bricksFadeIn = false;
    private long lastBricksStateChange = 0;

    public World(Game game) {
        this.game = game;
        reset();

        maxLevel = new File("res/levels").listFiles().length;
    }

    public void reset() {
        racket = new Racket(this, Game.WIDTH / 2 - Racket.WIDTH / 2, Game.HEIGHT + Game.BAR_HEIGHT);
        ball = new Ball(this, 200, Game.HEIGHT - 200);

        racketFadeIn = true;
        level = 1;

        bonuses.clear();
        if (!bricksPresent()) {
            loadLevel();
        }
    }

    public void update() {
        updateList(bricks);
        updateList(bonuses);
        racket.update();
        ball.update();

        Iterator<UIElement> i2 = uiElements.iterator();
        while (i2.hasNext()) {
            UIElement element = i2.next();
            element.update();
            if (element.shouldRemove()) {
                i2.remove();
            }
        }

        if (bricksFadeIn) {
            long currentStateChange = System.currentTimeMillis();
            if (currentStateChange - lastBricksStateChange > 5) {
                lastBricksStateChange = currentStateChange;
                boolean requiresStop = false;

                for (Entity entity : bricks) {
                    Brick br = (Brick) entity;
                    br.fading = 1;

                    br.velocityQuotient += 0.0004888f * br.getLine();

                    float maxQuotient = 1.865f * (bricksHeight / br.getLine());
                    if (br.velocityQuotient >= maxQuotient) {br.velocityQuotient = maxQuotient;}

                    int rq = Game.BAR_HEIGHT - 10 + br.getLine() * (Brick.HEIGHT + 5);
                    int yo = (int) (5 * bricksHeight * br.velocityQuotient);
                    if (br.getY() + yo >= rq) {
                        yo = rq - br.getY();
                    } else {
                        requiresStop = true;
                    }

                    br.move(0, yo);
                }

                bricksFadeIn = requiresStop;
            }
        }

        if (racketFadeIn) {
            long currentStateChange = System.currentTimeMillis();
            if (currentStateChange - lastRacketStateChange > 10) {
                lastRacketStateChange = currentStateChange;

                racket.move(0, -1);
                if (racket.getY() <= Game.HEIGHT + Game.BAR_HEIGHT - Racket.HEIGHT * 2) {
                    racket.move(0, 1);
                    racketFadeIn = false;
                }
            }
        }

        if (level == maxLevel && !bricksPresent()) {
            game.win();
        }
    }

    private void updateList(ArrayList<Entity> entities) {
        Iterator<Entity> i = entities.iterator();
        while (i.hasNext()) {
            Entity entity = i.next();
            entity.update();
            if (entity.shouldRemove()) {
                i.remove();
            }
        }
    }

    public void render(Graphics g) {
        Game.setOpacity(g, 1f);

        renderList(bricks, g);
        renderList(bonuses, g);
        racket.render(g);
        if (!game.isGameOver()) { ball.render(g); }

        Iterator<UIElement> i2 = uiElements.iterator();
        while (i2.hasNext()) {
            i2.next().render(g);
        }
    }

    private void renderList(ArrayList<Entity> entities, Graphics g) {
        Iterator<Entity> i = entities.iterator();
        while (i.hasNext()) {
            i.next().render(g);
        }
    }

    public void addBrick(int x, int y, int type) {
        bricks.add(new Brick(this, x, y, type));
    }

    public void addBonus(int x, int y, BonusType type) {
        bonuses.add(new Bonus(this, x, y, type));
    }

    public void addBonus(Entity entity, BonusType type) {
        addBonus(entity.getX() + entity.getWidth() / 2 - Bonus.WIDTH / 2, entity.getY(), type);
    }

    public void addUiElement(UIElement element) {
        uiElements.add(element);
    }

    public Game getGame() {
        return this.game;
    }

    public boolean bricksPresent() {
        return bricks.size() > 0;
    }

    public Racket getRacket() {
        return this.racket;
    }

    public Ball getBall() {
        return this.ball;
    }

    public Random getRandom() {
        return random;
    }

    public int countBonuses() {
        return bonuses.size();
    }

    public ArrayList<Entity> getCollidingEntities(Entity entity) {
        ArrayList<Entity> colliders = new ArrayList<Entity>();
        ArrayList<Entity> entities = new ArrayList<Entity>();
        entities.addAll(bricks);
        entities.addAll(bonuses);
        for (Entity e : entities) {
            if (e.getCollisionSide(entity) != Entity.CollisionSide.None) {
                colliders.add(e);
            }
        }
        return colliders;
    }

    private int bricksHeight = 0;

    public void loadLevel() {
        try {
            Brick.id = 0;
            String currentLine;
            String types = "1234567";
            BufferedReader br = new BufferedReader(new FileReader("res/levels/level" + level + ".txt"));

            int line = 0;
            int y = 0;
            while ((currentLine = br.readLine()) != null) {
                char[] chars = currentLine.toCharArray();
                for (int i = 0, len = chars.length; i < len; ++i) {
                    int type = types.indexOf(chars[i]);
                    if (type > -1) {
                        y = 11 + Game.BAR_HEIGHT + line * Brick.HEIGHT + line * 5;
                        addBrick((i - 1) * Brick.WIDTH + i * 5, y, type + 1);
                    }
                }

                ++line;
            }

            y += Brick.HEIGHT;
            bricksHeight = y;

            for (Entity entity : bricks) {
                Brick brick = (Brick) entity;
                int invertedLine = ((bricksHeight / Brick.HEIGHT) - brick.getLine());
                brick.move(0, -y / 6 - brick.getLine() * invertedLine * Brick.HEIGHT / 64);
                brick.setOpacity(0.0789f);
            }

            bricksFadeIn = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}