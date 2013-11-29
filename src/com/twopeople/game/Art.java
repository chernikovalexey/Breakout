package com.twopeople.game;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Alexey
 * At 10:54 PM on 11/20/13
 */

public class Art {
    public static BufferedImage background = loadImage("res/images/background.png");
    public static BufferedImage[][] ball = loadSprite("res/images/ball.png", 16, 16);
    public static BufferedImage[][] bricks = loadSprite("res/images/bricks.png", 32, 16);

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