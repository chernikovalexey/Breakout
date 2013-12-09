package com.twopeople.game;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Game extends Canvas implements Runnable {
    public static int WIDTH = 634;
    public static int HEIGHT = 560;
    public static int BAR_HEIGHT = 32;

    private static int frameCount = 0;
    private static int fps = 0;

    private boolean isRunning = false;

    private static ArrayList<Font> fonts = new ArrayList<Font>();

    private BufferedImage buffer = new BufferedImage(WIDTH, HEIGHT + BAR_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    private InputHandler input = new InputHandler(this);
    private World world = new World(this);
    private BonusManager bonusManager = new BonusManager();

    private int lifes = 0;
    private int score = 0;
    private boolean won = true;

    private int animDir = -1;
    private float tryAgainOpacity = 1f;

    static {
        fonts.add(loadFont("HelveticaLight", 15));
        fonts.add(loadFont("HelveticaLight", 16));
        fonts.add(loadFont("HelveticaLight", 48));

        for (int i = 1; i <= 16; ++i) {
            fonts.add(loadFont("HelveticaMedium", i));
        }
    }

    public Game() {
        Dimension d = new Dimension(WIDTH - 10, HEIGHT - 10 + BAR_HEIGHT);
        setMinimumSize(d);
        setPreferredSize(d);
        setMaximumSize(d);
    }

    public void init() {
        createBufferStrategy(3);
        requestFocus();
    }

    public void start() {
        isRunning = true;
        (new Thread(this, "Breakout Thread")).start();
    }

    public void stop() {
        isRunning = false;
    }

    float localTime = 0f;

    @Override
    public void run() {
        init();

        final double GAME_HERTZ = 30.0;
        final double TIME_BETWEEN_UPDATES = 1000000000 / GAME_HERTZ;
        final int MAX_UPDATES_BEFORE_RENDER = 5;
        double lastUpdateTime = System.nanoTime();
        double lastRenderTime = System.nanoTime();
        final double TARGET_FPS = 60;
        final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;

        int lastSecondTime = (int) (lastUpdateTime / 1000000000);

        while (isRunning) {
            double now = System.nanoTime();
            int updateCount = 0;

            while (now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER) {
                update();
                lastUpdateTime += TIME_BETWEEN_UPDATES;
                updateCount++;
            }

            if (now - lastUpdateTime > TIME_BETWEEN_UPDATES) {
                lastUpdateTime = now - TIME_BETWEEN_UPDATES;
            }

            render();
            lastRenderTime = now;

            int thisSecond = (int) (lastUpdateTime / 1000000000);
            if (thisSecond > lastSecondTime) {
                //System.out.println("NEW SECOND " + thisSecond + " " + fps);
                fps = frameCount;
                frameCount = 0;
                lastSecondTime = thisSecond;
            }

            while (now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES) {
                Thread.yield();

                try {
                    Thread.sleep(1);
                } catch (Exception e) {}

                now = System.nanoTime();
            }
        }
    }

    private void update() {
        input.update();
        if (!isGameOver()) {
            world.update();
            bonusManager.update();
        } else {
            float delta = 0.0375f;
            tryAgainOpacity += delta * animDir;
            if (tryAgainOpacity > 1f || tryAgainOpacity < 0.2f) {
                animDir *= -1;
                tryAgainOpacity += delta * animDir;
            }

            if (input.r.isDown()) {
                won = false;
                lifes = 3;
                score = 0;
                world.reset();
            }
        }
    }

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        Graphics g = bs.getDrawGraphics();
        g.fillRect(0, 0, getWidth(), getHeight());

        Graphics dg = buffer.getGraphics();

        Graphics2D g2d = (Graphics2D) dg;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        dg.drawImage(Art.bg2, 0, 0, Art.bg2.getWidth(), HEIGHT + BAR_HEIGHT, null);

        Game.setOpacity(dg, 0.156789f);

        dg.setColor(new Color(115, 164, 46));
        dg.fillRect(0, 0, WIDTH, HEIGHT + BAR_HEIGHT);

        setOpacity(dg, 1f);

        world.render(dg);

        if (!isGameOver()) {
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.19f));

            dg.setColor(new Color(0, 0, 0));
            dg.fillRect(0, 0, WIDTH, BAR_HEIGHT);
            dg.setColor(new Color(204, 204, 204));
            dg.fillRect(0, BAR_HEIGHT, WIDTH, 1);

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            dg.setFont(getFont(0));
            dg.setColor(new Color(204, 204, 204));
            dg.drawString("Lifes: " + lifes, 10, BAR_HEIGHT / 2 + 6);

            String scoreSign = "Score: " + score;
            int signWidth = (int) getFont(0).getStringBounds(scoreSign, new FontRenderContext(g2d.getTransform(), true, true)).getWidth();

            dg.drawString(scoreSign, WIDTH - 10 - signWidth, BAR_HEIGHT / 2 + 6);
        } else {
            setOpacity(dg, 0.254f);

            dg.setColor(new Color(0, 0, 0));
            dg.fillRect(0, 0, WIDTH, HEIGHT + BAR_HEIGHT);

            String finSign = won ? "Congratulations!" : "Gave Over!";
            String tryAgainSign = (won ? "Play" : "Try") + " again by pressing R";

            int finWidth = getStringWidth(finSign, dg, getFont(2));
            int tryAgainWidth = getStringWidth(tryAgainSign, dg, getFont(1));

            setOpacity(dg, 1f);
            dg.setColor(Color.white);
            dg.setFont(getFont(2));
            dg.drawString(finSign, WIDTH / 2 - finWidth / 2, HEIGHT / 2 - 48);
            dg.setFont(getFont(1));
            setOpacity(dg, tryAgainOpacity);
            dg.drawString(tryAgainSign, WIDTH / 2 - tryAgainWidth / 2, HEIGHT / 2 - 16);
        }

        g.drawImage(buffer, 0, 0, buffer.getWidth(), buffer.getHeight(), null);

        g.dispose();
        bs.show();
    }

    public InputHandler getInput() {
        return this.input;
    }

    public BonusManager getBonusManager() {
        return this.bonusManager;
    }

    public static int getFps() {
        return fps;
    }

    public static Font getFont(int n) {
        return fonts.get(n);
    }

    // Game logic

    public void addLifes(int lifes) {
        this.lifes += lifes;
    }

    public void decrementLifes() {
        --lifes;
        if (lifes < 0) {
            lifes = 0;
        }
    }

    public void addScore(int score) {
        this.score += score;
    }

    public boolean isGameOver() {
        return lifes == 0 || won;
    }

    public void win() {
        won = true;
    }

    //

    public static void main(String[] args) {
        JFrame frame = new JFrame("Breakout");
        Game game = new Game();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(game, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        game.start();
    }

    public static Font loadFont(String name, int size) {
        try {
            InputStream stream = new BufferedInputStream(new FileInputStream("res/fonts/" + name + ".ttf"));
            Font b_font = Font.createFont(Font.TRUETYPE_FONT, stream);
            Font font = b_font.deriveFont(Font.PLAIN, size);
            stream.close();
            return font;
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void setOpacity(Graphics g, float opacity) {
        ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
    }

    public static int getStringWidth(String str, Graphics g, Font font) {
        return (int) font.getStringBounds(str, new FontRenderContext(((Graphics2D) g).getTransform(), true, true)).getWidth();
    }
}