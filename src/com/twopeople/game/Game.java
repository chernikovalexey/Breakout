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

/**
 * Created by Alexey
 * At 1:39 PM on 11/20/13
 */

public class Game extends Canvas implements Runnable {
    public static int WIDTH = 640;
    public static int HEIGHT = 560;
    public static int BAR_HEIGHT = 32;

    private static int fps = 0;

    private boolean isRunning = false;

    private static ArrayList<Font> fonts = new ArrayList<Font>();

    private BufferedImage buffer = new BufferedImage(WIDTH, HEIGHT + BAR_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    private InputHandler input = new InputHandler(this);
    private World world = new World(this);

    private int lifes = 3;
    private int score = 0;

    static {
        fonts.add(loadFont("HelveticaLight", 15));
        fonts.add(loadFont("HelveticaLight", 16));
        fonts.add(loadFont("HelveticaLight", 18));

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
        createBufferStrategy(2);
        requestFocus();
    }

    public void start() {
        isRunning = true;
        (new Thread(this, "Breakout Thread")).start();
    }

    public void stop() {
        isRunning = false;
    }

    @Override
    public void run() {
        init();

        int frames = 0;

        long time = System.currentTimeMillis();
        long framesTime = System.currentTimeMillis();

        while (isRunning) {
            update((int) (System.currentTimeMillis() - time - 8));
            render();

            ++frames;
            time = System.currentTimeMillis();

            if (System.currentTimeMillis() - framesTime >= 1000) {
                framesTime = System.currentTimeMillis();
                fps = frames;
                frames = 0;
                //System.out.println(getFps() + " fps");
            }

            try {
                Thread.sleep(8);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update(int delta) {
        input.update();
        world.update(delta);
    }

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        Graphics g = bs.getDrawGraphics();

        Graphics dg = buffer.getGraphics();
        dg.setColor(Color.black);
        dg.fillRect(0, 0, getWidth(), getHeight());

        Graphics2D g2d = (Graphics2D) dg;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        world.render(dg);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        dg.setColor(new Color(255, 255, 255));
        dg.fillRect(0, 0, WIDTH, BAR_HEIGHT);
        dg.setColor(new Color(204, 204, 204));
        dg.fillRect(0, BAR_HEIGHT, WIDTH, 1);

        //        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        dg.setFont(getFont(0));
        dg.setColor(Color.black);
        dg.drawString("Lifes: " + lifes, 10, BAR_HEIGHT / 2 + 6);

        String scoreSign = "Score: " + score;
        int signWidth = (int) getFont(0).getStringBounds(scoreSign, new FontRenderContext(g2d.getTransform(), true, true)).getWidth();

        dg.drawString(scoreSign, WIDTH - 10 - signWidth, BAR_HEIGHT / 2 + 6);

        g.drawImage(buffer, 0, 0, buffer.getWidth(), buffer.getHeight(), null);

        bs.show();
        g.dispose();
    }

    public InputHandler getInput() {
        return this.input;
    }

    public static int getFps() {
        return fps;
    }

    public static Font getFont(int n) {
        return fonts.get(n);
    }

    // Game logic

    public void decrementLifes() {
        --lifes;
        if (lifes < 0) {
            lifes = 0;
        }
    }

    public void addScore(int score) {
        this.score += score;
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
}