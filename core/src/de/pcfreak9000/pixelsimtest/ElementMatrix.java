package de.pcfreak9000.pixelsimtest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.RandomXS128;

public class ElementMatrix {
    
    static {
        Pixmap p = new Pixmap(1, 1, Format.RGB565);
        p.setColor(Color.WHITE);
        p.drawPixel(0, 0);
        WHITE = new Texture(p);
        p.dispose();
    }
    private static final Texture WHITE;
    
    public static final int SIZE = 150;
    
    private ElementState[][] matrix = new ElementState[SIZE][SIZE];
    
    private List<Integer> indizes = new ArrayList<>();
    private List<Integer> indizes2 = new ArrayList<>();
    
    private Element base;
    
    private RandomXS128 random = new RandomXS128();
    
    public ElementMatrix(Element base) {
        this.base = base;
        for (int i = 0; i < SIZE; i++) {
            indizes.add(i);
        }
        Collections.shuffle(indizes);
        for (int i = 0; i < SIZE; i++) {
            indizes2.add(i);
        }
        Collections.shuffle(indizes2);
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                createState(i, j, base);
                //matrix[i][j] = base.createElementState(i, j);
            }
        }
    }
    
    private int frame = 0;
    float maxtemp, mintemp;
    
    public void update() {
        //Collections.shuffle(indizes);
        //Collections.shuffle(indizes2);
        maxtemp = Float.NEGATIVE_INFINITY;
        mintemp = Float.POSITIVE_INFINITY;
        for (int j : indizes2) {
            for (int i : indizes) {
                ElementState t = matrix[i][j];
                if (t != null) {
                    t.update(this, frame);
                    maxtemp = Math.max(maxtemp, t.getTemperature());
                    mintemp = Math.min(mintemp, t.getTemperature());
                }
            }
        }
        frame++;
    }
    
    public double random() {
        return random.nextDouble();
    }
    
    public Element base() {
        return base;
    }
    
    public void switchStates(ElementState s0, ElementState s1) {
        if (s0 == s1) {
            return;
        }
        switchStates(s0.x, s0.y, s1.x, s1.y);
    }
    
    public void switchStates(int x0, int y0, int x1, int y1) {
        ElementState s0 = matrix[x0][y0];
        ElementState s1 = matrix[x1][y1];
        if (s0.x != x0 || s0.y != y0 || s1.x != x1 || s1.y != y1) {
            throw new RuntimeException();
        }
        if (s0.isFixed() || s1.isFixed()) {
            throw new RuntimeException();
        }
        matrix[x0][y0] = s1;
        s1.x = x0;
        s1.y = y0;
        matrix[x1][y1] = s0;
        s0.x = x1;
        s0.y = y1;
    }
    
    public void killState(int x, int y) {
        matrix[x][y] = base.createElementState(x, y);
    }
    
    public void createState(int x, int y, Element e) {
        matrix[x][y] = e.createElementState(x, y);
    }
    
    public ElementState convertState(int x, int y, Element e) {
        ElementState current = matrix[x][y];
        ElementState newstate = e.createElementState(x, y);
        newstate.timepart = current.timepart;
        newstate.setHeat(current.getTemperature() * newstate.getSpecificHeat());
        matrix[x][y] = newstate;
        return newstate;
    }
    
    public ElementState getState(int x, int y) {
        if (checkBounds(x, y)) {
            return matrix[x][y];
        }
        return null;
    }
    
    public boolean hasElement(int x, int y) {
        ElementState s = getState(x, y);
        if (s == null) {
            return false;
        }
        return s.getElement() != base;
    }
    
    public void createCircle(int x, int y, int radius, Element e) {
        for (int i = -radius; i <= radius; i++) {
            for (int j = -radius; j <= radius; j++) {
                int ax = x + i;
                int ay = y + j;
                if (i * i + j * j < radius * radius) {
                    if (checkBounds(ax, ay) && !hasElement(ax, ay)) {
                        createState(ax, ay, e);
                    }
                }
            }
        }
    }
    
    public boolean checkBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < SIZE && y < SIZE;
    }
    
    public void render(SpriteBatch batch) {
        // System.out.println(maxtemp);
        // System.out.println(mintemp);
        Color c = new Color();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                ElementState t = getState(i, j);
                if (t.getColor() != null) {
                    batch.setColor(t.getColor());
                    float temp = t.getTemperature();
                    float rel = temp / (maxtemp - mintemp);
                    c.set(rel, 0, 1 - rel, 1);
                    c.add(t.getColor());
                    //batch.setColor(c);
                    //batch.setColor(rel * 0.5f + act.r * 0.5f, 0 + act.g * 0.5f, (1 - rel) * 0.5f + act.b * 0.5f, 1);
                    //                    Vector2 v = t.getVelocity();
                    //                    if (v.len2() > 100) {
                    //                        batch.setColor(t.getColor());
                    //                    } else if (v.len2() > 0 && v.len2() <= 100) {
                    //                        batch.setColor(Color.CORAL);
                    //                    } else {
                    //                        batch.setColor(Color.RED);
                    //                    }
                    batch.draw(WHITE, t.getX(), t.getY(), 1, 1);
                }
            }
        }
    }
}
