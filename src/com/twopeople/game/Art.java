package com.twopeople.game;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Art {
    public static BufferedImage icon = loadImage("res/images/icon32.png");
    public static BufferedImage bg2 = loadImage("res/images/bg2.jpg");
    public static BufferedImage[][] racket = loadSprite("res/images/racket.png", 60, 6);
    public static BufferedImage[][] ball = loadSprite("res/images/ball_1.png", 16, 16);
    public static BufferedImage[][] bricks = loadSprite("res/images/bricks.png", 32, 16);
    public static BufferedImage[][] bonuses = loadSprite("res/images/bonuses.png", 16, 16);

    public static BufferedImage loadImage(String path) {
        BufferedImage image = null;

        try {
            image = ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public static BufferedImage[][] loadSprite(String path, int w, int h) {
        BufferedImage image = loadImage(path);
        int width = image.getWidth() / w;
        int height = image.getHeight() / h;
        BufferedImage[][] sprite = new BufferedImage[width][height];

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                sprite[x][y] = image.getSubimage(x * w, y * h, w, h);
            }
        }

        return sprite;
    }
}