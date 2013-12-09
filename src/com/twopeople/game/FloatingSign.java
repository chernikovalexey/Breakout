package com.twopeople.game;

import java.awt.*;

/**
 * Created by Alexey
 * At 11:10 PM on 11/20/13
 */

public class FloatingSign extends UIElement {
    private String sign;
    private int dx, dy;

    private int fontSize = 16;
    private long lastStateChange = System.currentTimeMillis();

    public FloatingSign(String sign, int x, int y, int dx, int dy) {
        this.sign = sign;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void update() {
        int speed = 8;

        double angle = Math.atan2(dy - y, dx - x);

        x += speed * Math.cos(angle);
        y += speed * Math.sin(angle);

        if (Geom.getDistBetweenPoints(x, y, dx, dy) < 5) {
            remove = true;
        }

        long currentStateChange = System.currentTimeMillis();
        if (currentStateChange - lastStateChange > 35) {
            lastStateChange = currentStateChange;

            opacity -= 0.0175f;
            fontSize -= 1;

            if (opacity < 0 || fontSize < 0) {
                remove = true;
            }
        }
    }

    @Override
    public void render(Graphics g) {
        if (fontSize > 0) {
            g.setFont(Game.getFont(2 + fontSize));
            g.setColor(Color.white);
            ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
            g.drawString(sign, x, y);
        }
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
}