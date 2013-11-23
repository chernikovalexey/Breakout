package com.twopeople.game;

import java.awt.*;

/**
 * Created by Alexey
 * At 11:09 PM on 11/20/13
 */

public class UIElement {
    protected int x, y;
    protected float opacity = 1f;
    protected boolean remove = false;

    public UIElement() {
    }

    public void update(int delta) {
    }

    public void render(Graphics g) {
    }

    public boolean shouldRemove() {
        return this.remove;
    }
}